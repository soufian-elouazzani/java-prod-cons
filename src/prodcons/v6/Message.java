package prodcons.v6;

public class Message {
    private final String content;
    private final long producerId;
    private final int messageNumber;
    private final int totalCopies;

    private int consumedCopies = 0;
    private boolean allConsumed = false;
    private final Object lock = new Object();

    public Message(String content, long producerId, int messageNumber, int totalCopies) {
        this.content = content;
        this.producerId = producerId;
        this.messageNumber = messageNumber;
        this.totalCopies = totalCopies;
    }

    /**
     * Called by a consumer to take one copy of this message
     * @return true if this is the last consumer, false otherwise
     */
    public boolean consumeOneCopy() {
        synchronized(lock) {
            if (consumedCopies < totalCopies) {
                consumedCopies++;
                boolean isLast = (consumedCopies == totalCopies);
                if (isLast) {
                    allConsumed = true;
                    lock.notifyAll(); // Wake up everyone waiting
                }
                return isLast;
            }
            return false;
        }
    }

    /**
     * Called by consumers to wait until all copies are consumed
     */
    public void waitForAllCopies() throws InterruptedException {
        synchronized(lock) {
            while (!allConsumed) {
                lock.wait();
            }
        }
    }

    /**
     * Called by producer to wait until all copies are consumed
     */
    public void waitForCompletion() throws InterruptedException {
        synchronized(lock) {
            while (!allConsumed) {
                lock.wait();
            }
        }
    }

    // Getters
    public String getContent() { return content; }
    public long getProducerId() { return producerId; }
    public int getMessageNumber() { return messageNumber; }
    public int getTotalCopies() { return totalCopies; }
    public int getConsumedCopies() { return consumedCopies; }

    @Override
    public String toString() {
        return "Message{" + content + "} from Producer-" + producerId +
                " [" + consumedCopies + "/" + totalCopies + " copies]";
    }
}
