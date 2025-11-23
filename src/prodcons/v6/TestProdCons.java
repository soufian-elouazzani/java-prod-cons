package prodcons.v6;

import java.util.Properties;

public class TestProdCons {
    public static void main(String[] args) {
        try {
            // Load configuration
            Properties properties = new Properties();
            properties.loadFromXML(
                    TestProdCons.class.getClassLoader().getResourceAsStream("prodcons/v6/options.xml"));

            int nProd = Integer.parseInt(properties.getProperty("nProd"));
            int nCons = Integer.parseInt(properties.getProperty("nCons"));
            int bufSz = Integer.parseInt(properties.getProperty("bufSz"));
            int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
            int consTime = Integer.parseInt(properties.getProperty("consTime"));
            int minProd = Integer.parseInt(properties.getProperty("minProd"));
            int maxProd = Integer.parseInt(properties.getProperty("maxProd"));

            System.out.println("Starting Multi-Copy Synchronous Producer-Consumer Test");
            System.out.println("Producers: " + nProd + ", Consumers: " + nCons +
                    ", Buffer: " + bufSz + ", Copies: 1-5 randomly");

            // Create buffer
            IProdConsBuffer buffer = new ProdConsBuffer(bufSz);

            // Create and start producers
            Thread[] producers = new Thread[nProd];
            for (int i = 0; i < nProd; i++) {
                producers[i] = new Producer(buffer, prodTime, minProd, maxProd);
                producers[i].start();
                // Mix startup
                if (i % 2 == 0 && i < nCons) {
                    new Consumer(buffer, consTime).start();
                }
            }

            // Create and start remaining consumers
            for (int i = 0; i < nCons; i++) {
                // Skip already started consumers
                if (i >= nProd || i % 2 != 0) {
                    new Consumer(buffer, consTime).start();
                }
                // Mix startup
                Thread.sleep(10);
            }

            // Wait for all producers to finish
            for (Thread producer : producers) {
                producer.join();
            }

            System.out.println("All producers finished. Consumers still running...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}