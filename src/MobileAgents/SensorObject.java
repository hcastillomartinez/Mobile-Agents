package MobileAgents;

/**
 * SensorObject.java is an interface that creates a basis for all of the
 * objects in the graph. A SensorObject can send and get a message.
 * Danan High, 10/11/2018
 */
public interface SensorObject {
    /**
     * Sending a message
     * @return message for the node
     */
    void sendMessage();
    
    /**
     * Getting the next message in the queue.
     * @return message
     */
    void getMessages();
}
