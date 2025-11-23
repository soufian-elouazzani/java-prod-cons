package prodcons.v6;

public class Consumer extends Thread {
    private final IProdConsBuffer buffer;
    private final int consTime;

    public Consumer(IProdConsBuffer buffer, int consTime) {
        this.buffer = buffer;
        this.consTime = consTime;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message msg = buffer.get();
                // Simulate message processing
                sleep(consTime);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}