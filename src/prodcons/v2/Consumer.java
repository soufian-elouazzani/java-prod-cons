package prodcons.v2;
public class Consumer extends Thread{
    ProdConsBuffer buffer;
    int consTime;
    Message msg;
    public Consumer(ProdConsBuffer buffer, int consTime){
        this.buffer = buffer;
        this.consTime = consTime;
    }

    public void run(){
        while(true){
            try {
                msg = buffer.get();
                if(msg == null){
                    break;
                }
                sleep(consTime);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
