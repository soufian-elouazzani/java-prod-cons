package prodcons.v5;
public class Consumer extends Thread{
    ProdConsBuffer buffer;
    int consTime;
    Message msg;
    int k;
    Message[] b;
    boolean nullProcess = false;
    public Consumer(ProdConsBuffer buffer, int consTime, int k){
        this.buffer = buffer;
        this.consTime = consTime;
        this.k = k;
    }

    public void run(){ // TASK: MAKE THIS RUN STOP
        while(true){
            try {
                b = buffer.get(k);
                for(int i = 0; i < k; i++){
                    if(b[i] == null){
                        nullProcess = true;
                        break;
                    }
                    sleep(consTime*10);
                }
                if(nullProcess){
                    break;
                }


            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }
}
