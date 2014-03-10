package Communications;

/**
* MCManager
* @author Emery Berg
*/

public class MCManager {
    private static MCSend sender;
    private MCReceive listener;
    
    public void connect(String group, int port, long ID) {
        sender = new MCSend(port, group);
        listener = new MCReceive(port, group, ID);        
        listener.start();
    }
    
    public static MCSend getSender() { return sender; }
}