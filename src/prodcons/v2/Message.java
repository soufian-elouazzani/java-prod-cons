package prodcons.v2;
public class Message {
    String info;

    public Message(String s){
        this.info = s;
    }
    @Override
    public String toString() { //Just to understand what's happening on "buffer"
        return "Msg(" + this.info + ")";
    }
}