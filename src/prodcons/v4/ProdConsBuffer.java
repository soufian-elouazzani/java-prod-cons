package prodcons.v4;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProdConsBuffer implements IProdConsBuffer {
    private final Message[] buffer;
    private int in = 0;    // Next put position
    private int out = 0;   // Next get position
    private int nfull = 0; // Number of occupied slots
    private int totmsg = 0; // Total messages produced .

    private final ReentrantLock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    public ProdConsBuffer(int bufSz) {
        buffer = new Message[bufSz];
        lock = new ReentrantLock();           // Single lock
        notFull = lock.newCondition();        // "Buffer has space" condition
        notEmpty = lock.newCondition();       // "Buffer has messages" condition   // Initially no messages available
    }

    @Override
    public void put(Message m) throws InterruptedException {
        lock.lock();           // Acquire the single lock
        try {
            while (nfull >= buffer.length) {  // Buffer full?
                notFull.await();              // Wait for space
            }
            // modify buffer
            buffer[in] = m;
            in = (in + 1) % buffer.length;
            nfull++;
            totmsg++;

            notEmpty.signal();  // Signal that a message is available
        } finally {
            lock.unlock();      // Always release lock
        }

    }

    @Override
    public Message get() throws InterruptedException {
        lock.lock();           // Acquire the single lock
        try {
            while (nfull <= 0) {     // Buffer empty?
                notEmpty.await();    // Wait for message
            }
            // modify buffer
            Message m = buffer[out];
            buffer[out] = null;
            out = (out + 1) % buffer.length;
            nfull--;

            notFull.signal();  // Signal that space is available
            return m;
        } finally {
            lock.unlock();     // Always release lock
        }
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