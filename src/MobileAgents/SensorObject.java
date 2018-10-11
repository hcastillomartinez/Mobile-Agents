package MobileAgents;

public interface SensorObject {
    /**
     * Sending a message
     * @return message for the node
     */
    String sendMessage();
    
    /**
     * Getting the next message in the queue.
     * @return message
     */
    String getMessage(SensorObject sensorObject);
}
