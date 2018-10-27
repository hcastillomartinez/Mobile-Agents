package MobileAgents;

/**
 * Message.java is the structure for the message to be sent among the
 * SensorObjects.
 * Danan High, 10/11/2018
 */
public class Message {
    
    private SensorObject sender;
    private SensorObject receiver;
    private MobileAgent clonedAgent;
    private String detailedMessage;
    
    /**
     * Constructor for the Message class that holds the originator of the
     * message and the detailed message itself.
     * @param sender, node or agent from who the message is from
     * @param receiver, node or agent who receives the message
     * @param detailedMessage, message from the object
     */
    public Message(SensorObject sender,
                   SensorObject receiver,
                   MobileAgent clonedAgent,
                   String detailedMessage) {
        this.sender = sender;
        this.receiver = receiver;
        this.clonedAgent = clonedAgent;
        this.detailedMessage = detailedMessage;
    }
    
    /**
     * Returning the detailed message
     * @return detailedMessage, from the node
     */
    public String getDetailedMessage() {
        return this.detailedMessage;
    }
    
    /**
     * Returning the node who created the message.
     * @return sensorObject, who created the message
     */
    public SensorObject getSender() { return this.sender; }

    /**
     * Returning the node who gets the message.
     * @return receiver, who gets the message
     */
    public SensorObject getReceiver() {
        return this.receiver;
    }
    
    /**
     * Returning the agent from the message to clone
     * @return clonedAgent to place at the baseStation
     */
    public MobileAgent getClonedAgent() { return clonedAgent; }
    
    /**
     * Overriding toString to return the string of the message sender, receiver,
     * and message that has been sent.
     * @return string representation of the class
     */
    @Override
    public String toString() {
        return ("sender = " + sender.toString() + ", receiver = " +
            receiver.toString() + " : " + detailedMessage + " " + clonedAgent);
    }
}
