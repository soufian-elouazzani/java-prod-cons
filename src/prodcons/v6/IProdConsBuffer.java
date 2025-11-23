package prodcons.v6;


public interface IProdConsBuffer {
    /**
     * Put n instances of the message m in the prodcons buffer
     * The current thread is blocked until all
     * instances of the message have been consumed
     * Any consumer of m is also blocked until all the instances of
     * the message have been consumed
     */
    public void put(Message m, int n) throws InterruptedException;

    /**
     * Retrieve a message from the prodcons buffer, following a fifo order
     */
    public Message get() throws InterruptedException;

    /**
     * Returns the number of messages currently available in the buffer
     */
    public int nmsg();

    /**
     * Returns the total number of messages that have been put in the buffer since its creation
     */
    public int totmsg();
}