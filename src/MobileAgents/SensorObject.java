package MobileAgents;

/**
 * SensorObject.java is an interface that creates a basis for all of the
 * objects in the graph. A SensorObject can send and get a message.
 * Danan High, 10/11/2018
 */
public interface SensorObject {
    /**
     * Sending a message
     * @param message the message to send
     */
    void sendMessage(Message message);
    
    /**
     * Getting the next message in the queue.
     */
    void getMessages();

    /**
     * Function used for analyzing the messages
     * @param message the message to analyze
     */
    void analyzeMessage(Message message);

    /**
     * Getting the name of the SensorNode.
     * @return name of the node
     */
    String printOutName();
}
