package prodcons.v4;

public class Consumer extends Thread{
    ProdConsBuffer buffer;
    int consTime;
    public Consumer(ProdConsBuffer buffer, int consTime){
        this.buffer = buffer;
        this.consTime = consTime;
    }

    public void run(){
        while(true){
            try {
                buffer.get();
                sleep(consTime*50);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
