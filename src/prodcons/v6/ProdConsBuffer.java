package prodcons.v6;

import java.util.concurrent.Semaphore;

public class ProdConsBuffer implements IProdConsBuffer {
    private final Message[] buffer;
    private int in = 0;
    private int out = 0;
    private int nfull = 0;
    private int totmsg = 0;

    // Semaphores for synchronization
    private final Semaphore mutex;
    private final Semaphore notFull;
    private final Semaphore notEmpty;

    public ProdConsBuffer(int bufSz) {
        buffer = new Message[bufSz];
        mutex = new Semaphore(1);
        notFull = new Semaphore(bufSz);
        notEmpty = new Semaphore(0);
    }

    @Override
    public void put(Message m, int n) throws InterruptedException {
        // Create a new synchronized message with n copies
        Message multiMessage = new Message(
                m.getContent(), m.getProducerId(), m.getMessageNumber(), n
        );

        // Wait for space in buffer
        notFull.acquire();

        // Enter critical section to add message
        mutex.acquire();
        try {
            buffer[in] = multiMessage;
            in = (in + 1) % buffer.length;
            nfull++;
            totmsg++;

            System.out.println("PRODUCER " + Thread.currentThread().getId() +
                    " put: " + multiMessage + " [Buffer: " + nfull + "/" + buffer.length + "]");
        } finally {
            mutex.release();
        }

        // Signal that a new message is available
        notEmpty.release();

        // WAIT until all copies of this message are consumed
        System.out.println("PRODUCER " + Thread.currentThread().getId() +
                " waiting for " + n + " copies to be consumed...");
        multiMessage.waitForCompletion();
        System.out.println("PRODUCER " + Thread.currentThread().getId() + " released!");
    }

    @Override
    public Message get() throws InterruptedException {
        // Wait for a message to be available
        notEmpty.acquire();

        Message m;
        boolean isLastConsumer = false;

        // Enter critical section to get message
        mutex.acquire();
        try {
            m = buffer[out];
            isLastConsumer = m.consumeOneCopy();

            System.out.println("CONSUMER " + Thread.currentThread().getId() +
                    " got: " + m + " [Last: " + isLastConsumer + "]");

            if (isLastConsumer) {
                // Remove message from buffer and free space
                buffer[out] = null;
                out = (out + 1) % buffer.length;
                nfull--;
                notFull.release(); // Free one space
            }
        } finally {
            mutex.release();
        }

        // If not the last consumer, wait until all copies are consumed
        if (!isLastConsumer) {
            System.out.println("CONSUMER " + Thread.currentThread().getId() +
                    " waiting for other copies...");
            m.waitForAllCopies();
            System.out.println("CONSUMER " + Thread.currentThread().getId() + " released!");
        }

        return m;
    }

    @Override
    public int nmsg() {
        return nfull;
    }

    @Override
    public int totmsg() {
        return totmsg;
    }
}