import java.util.Arrays;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;


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
    Message out_aux;
    int count = 0;
    public ProdConsBuffer(int bufSz){
        this.buffer = new Message[bufSz];
        this.nfull = 0;
        this.nempty = bufSz;

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
        count++;
        System.out.println(currentThread().getName() + " : PUT : " + Arrays.toString(buffer));
        notifyAll();
    }

    /**
    * Retrieve a message from the buffer,
    * following a FIFO order (if M1 was put before M2, M1
    * is retrieved before M2)
    **/
    public synchronized Message get() throws InterruptedException {
        while(nempty == buffer.length){
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
        return count;
    }
    
}
