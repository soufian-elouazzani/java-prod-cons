package prodcons.v6;

public class Producer extends Thread {
    private final IProdConsBuffer buffer;
    private final int prodTime;
    private final int minProd;
    private final int maxProd;

    public Producer(IProdConsBuffer buffer, int prodTime, int minProd, int maxProd) {
        this.buffer = buffer;
        this.prodTime = prodTime;
        this.minProd = minProd;
        this.maxProd = maxProd;
    }

    @Override
    public void run() {
        try {
            int nMessages = minProd + (int)(Math.random() * (maxProd - minProd + 1));

            for (int i = 0; i < nMessages; i++) {
                // Random number of copies between 1 and 5 for testing
                int nCopies = 1 + (int)(Math.random() * 5);

                Message msg = new Message(
                        "Msg-" + getId() + "-" + i,
                        getId(),
                        i,
                        nCopies
                );

                buffer.put(msg, nCopies);
                sleep(prodTime);
            }
            System.out.println("PRODUCER " + getId() + " finished after " + nMessages + " messages");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}