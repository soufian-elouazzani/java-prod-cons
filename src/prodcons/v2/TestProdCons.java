package prodcons.v2;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class TestProdCons {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try {
            properties.loadFromXML(
                    TestProdCons.class.getClassLoader().getResourceAsStream("prodcons/v2/options.xml")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        //to INT
        int nProd = Integer.parseInt(properties.getProperty("nProd"));
        int nCons = Integer.parseInt(properties.getProperty("nCons"));
        int bufSz = Integer.parseInt(properties.getProperty("bufSz"));
        int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
        int consTime = Integer.parseInt(properties.getProperty("consTime"));
        int minProd = Integer.parseInt(properties.getProperty("minProd"));
        int maxProd = Integer.parseInt(properties.getProperty("maxProd"));


        System.out.println("nProd = " + nProd);
        System.out.println("nCons = " + nCons);
        System.out.println("bufSz = " + bufSz);
        System.out.println("prodTime = " + prodTime);
        System.out.println("consTime = " + consTime);
        System.out.println("minProd = " + minProd);
        System.out.println("maxProd = " + maxProd);

        /*
        buffer -> tableau taille fixe
         -> durée moyenne = consTime
        producteur -> nombre aléatoire de messages entre minProd et maxProd
        dure moyenne pour produire -> prodTime
        1 production a la fois e 1 consommation a la fois pour chaque producteur/consommateur

         */

        /*
        * For the OBJECTIF 2 I needed to check if all the producers has finished their messages, then I could send this information to ProdsConsBuffer.
        * With this information I changed the get() while loop. Inside the "while" I checked if all the producers has finished their messages and
        * if the buffer was empty. If these 2 conditions were TRUE, then all calls of "get()" would return NULL as a message and the threads, when
        * they receive the "null", they would break the while loop inside their "run" method.
        *
        * It's important to call notifyAll() when each producer has finished their messages because we can have a Consumer blocked in "wait()",
        * so we need to wake up every thread and do them check the conditions inside "get()" again.
        * */

        ProdConsBuffer buffer = new ProdConsBuffer(bufSz, nProd);
        Random random = new Random();
        int contCons = 0;
        int contProd = 0;

        for(int i = 0; i < nProd + nCons; i++){
            int side = random.nextInt(2); //if random = 0 -> create PROD       if random = 1 -> create CONSUMER
            if((side == 0 && contProd < nProd) || contCons == nCons){
                contProd++;
                Producer p = new Producer(random.nextInt((maxProd - minProd) + 1) + minProd, buffer, prodTime);
                p.start();
            } else if (contCons < nCons) {
                contCons++;
                Consumer c = new Consumer(buffer, consTime);
                c.start();
            }

        }
        //create n producers
        //create m consumers


    }
}
