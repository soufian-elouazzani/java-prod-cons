package prodcons.v5;

public class Producer extends Thread{
    int qtMsg;
    ProdConsBuffer buffer;
    Message []messages;
    int prodTime;
    public Producer(int nMsg, ProdConsBuffer buffer, int prodTime){
        this.qtMsg = nMsg;
        this.buffer = buffer;
        this.messages = new Message[nMsg];
        this.prodTime = prodTime;
        for(int i = 0; i < nMsg; i++){
            messages[i] = new Message(getName() + " : " + i);
        }

    }
    public void run(){
        for(int i = 0; i < qtMsg; i++){
            try {
                sleep(prodTime);
                buffer.put(messages[i]);
            } catch (InterruptedException e) {System.out.println(e);}
        }
        System.out.println(getName() + " Finished");
        buffer.prodsFinished();
    }
    
}
