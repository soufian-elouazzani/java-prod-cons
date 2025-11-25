package prodcons.v4;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class TestProdCons {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try {
            properties.loadFromXML(
                    TestProdCons.class.getClassLoader().getResourceAsStream("prodcons/v3/options.xml")
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
        simular tratamento de msg com sleep para o consommateur -> durée moyenne = consTime
        producteur -> nombre aléatoire de messages entre minProd et maxProd
        dure moyenne pour produire -> prodTime
        1 production a la fois e 1 consommation a la fois pour chaque producteur/consommateur

         */

        ProdConsBuffer buffer = new ProdConsBuffer(bufSz);
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
