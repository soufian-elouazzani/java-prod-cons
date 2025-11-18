package prodcons.v5;
import java.util.Arrays;

import static java.lang.Thread.currentThread;

/*
nfull et nempty
Méthode             Pre-Action              Garde               Post-Action

put(Message m)          -               nfull != bufSz          buffer[in] = m;
                                                                in = (in + 1) % buffer.length;
                                                                nfull++;
                                                                nempty--;
                                                                count++;


get()                   -               nempty != bufSz         out_aux = buffer[out];
                                                                buffer[out] = null;
                                                                out = (out + 1) % buffer.length;
                                                                nempty++;
                                                                nfull--;
 */


public class ProdConsBuffer implements IProdConsBuffer {
    /**
    * Put the message m in the buffer
    **/
    Message []buffer;
    int nfull;
    int nempty;
    int in = 0;
    int out = 0;
    int totalProd;
    int prodsFin = 0;
    Message out_aux;
    int countMessagesProduced = 0;
    int countMessagesConsumed;
    boolean consumerCollecting = false;
    public ProdConsBuffer(int bufSz){
        this.buffer = new Message[bufSz];
        this.nfull = 0;
        this.nempty = bufSz;

    }

    public void setTotalProd(int n){
        totalProd = n;
    }

    public synchronized void put(Message m) throws InterruptedException {
        while(nfull == buffer.length){
            try{
                wait();
            }catch(InterruptedException e){
                System.out.println(e);
            }
        }
        buffer[in] = m;
        in = (in + 1) % buffer.length;
        nfull++;
        nempty--;
        countMessagesProduced++;
        System.out.println(currentThread().getName() + " : PUT : " + Arrays.toString(buffer));
        notifyAll();
    }

    /**
    * Retrieve a message from the buffer,
    * following a FIFO order (if M1 was put before M2, M1
    * is retrieved before M2)
    **/
    //I'm not using this method in OBJECTIF 5 but I added the condition "consumerCollecting" just to be aligned with the method get(int k)
    public synchronized Message get() throws InterruptedException {
        while(nempty == buffer.length || consumerCollecting){ 
            if(prodsFin == totalProd && nmsg() == 0){ 
                return null; 
            }
           try {
               wait();
           }catch(InterruptedException e){
               System.out.println(e);
           }
        }
        
        out_aux = buffer[out];
        buffer[out] = null;
        out = (out + 1) % buffer.length;
        nempty++;
        nfull--;
        System.out.println(currentThread().getName() + " : GET : " + Arrays.toString(buffer));
        notifyAll();
        return out_aux;
    }

    /*add some flag to say "consumer in execution"?
     * if consumer collecting messages, the others need to wait to the consumer C collect consecutive messages
     * if collecting = true, then others wait()    
     * OK
    */

    public synchronized Message[] get(int k) throws InterruptedException{
        Message[] assistReturn = new Message[k];

        while(consumerCollecting){
            wait();
        }

        consumerCollecting = true;

        for(int i = 0; i < k; i++){    
            while(nempty == buffer.length){    
                if(prodsFin == totalProd){
                    consumerCollecting = false; // there is nothing to collect
                    notifyAll();
                    System.out.println(currentThread().getName() + " IS GOING TO PROCESS THE MESSAGES ...");
                    System.out.println(currentThread().getName() + " ENDING");
                    return assistReturn;
                }
                
                try {
                    wait(); // give space in the SC to the producers put messages in the buffer
                } catch (InterruptedException e) {
                    consumerCollecting = false;
                    System.out.println(e);
                }
            }


            assistReturn[i] = buffer[out];
            buffer[out] = null;
            out = (out + 1) % buffer.length;
            nempty++;
            nfull--;
            System.out.println(currentThread().getName() + " has the messages: " + Arrays.toString(assistReturn));
            notifyAll();
        }
        consumerCollecting = false;//finished the consuming for THIS consumer
        notifyAll();//wake up all threads to let them compete for the SC
        System.out.println(currentThread().getName() + " IS GOING TO PROCESS THE MESSAGES ...");
        return assistReturn;
    }


    /**
    * Returns the number of messages currently available in
    * the buffer
    **/
    public int nmsg(){
        return nfull;
    }
    
    
    /**
    * Returns the total number of messages that have
    * been put in the buffer since its creation
    **/
    public int totmsg(){
        return countMessagesProduced;
    }

    //Producer's announcement if he finishes all his messages
    public synchronized void prodsFinished(){//try to connect with producer - OK
        prodsFin++;
        notifyAll(); //wake up every thread because we need to pass through our condition on WHILE with all the threads.
    }
    
}
