package prodcons.v3;

import java.util.concurrent.Semaphore;

public class ProdConsBuffer implements IProdConsBuffer {
    private final Message[] buffer;
    private int in = 0;    // Next put position
    private int out = 0;   // Next get position
    private int nfull = 0; // Number of occupied slots
    private int totmsg = 0; // Total messages produced .

    // Semaphores for synchronization
    private final Semaphore mutex;     // Mutual exclusion
    private final Semaphore notFull;   // Available spaces
    private final Semaphore notEmpty;  // Available messages

    public ProdConsBuffer(int bufSz) {
        buffer = new Message[bufSz];
        mutex = new Semaphore(1);      // Binary semaphore for mutual exclusion
        notFull = new Semaphore(bufSz); // Initially all spaces available
        notEmpty = new Semaphore(0);   // Initially no messages available
    }

    @Override
    public void put(Message m) throws InterruptedException {
        // Wait for available space
        notFull.acquire();

        // Enter critical section
        mutex.acquire();
        try {
            // Put message in buffer
            buffer[in] = m;
            in = (in + 1) % buffer.length;
            nfull++;
            totmsg++;

            System.out.println("Produced: " + m + " [Buffer: " + nfull + "/" + buffer.length + "]");
        } finally {
            mutex.release();
        }

        // Signal that a message is available
        notEmpty.release();
    }

    @Override
    public Message get() throws InterruptedException {
        // Wait for available message
        notEmpty.acquire();

        Message m;
        // Enter critical section
        mutex.acquire();
        try {
            // Get message from buffer
            m = buffer[out];
            buffer[out] = null; // Help garbage collection
            out = (out + 1) % buffer.length;
            nfull--;

            System.out.println("Consumed: " + m + " [Buffer: " + nfull + "/" + buffer.length + "]");
        } finally {
            mutex.release();
        }

        // Signal that a space is available
        notFull.release();

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