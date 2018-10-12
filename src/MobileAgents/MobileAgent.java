package MobileAgents;

import java.util.concurrent.BlockingQueue;

/**
 * MobileAgent.java is the class that contains the functionality of an agent.
 * It can walk, clone itself and it implements the Runnable and SensorObject
 * interfaces.
 * Danan High, 10/11/2018
 */
public class MobileAgent implements SensorObject, Runnable {
    
    private BlockingQueue<String> queue;
    private long id;
    
    /**
     * Constructor for the MobileAgent class that has a unique id.
     * @param id, unique identifier
     * @param queue, the list of tasks to perform
     */
    public MobileAgent(BlockingQueue<String> queue, long id) {
        this.queue = queue;
        this.id = id;
    }

    /**
     * Creating a new agent if the node below is heated.
     * @return alertAgent
     */
    protected MobileAgent clone() {
        return new MobileAgent(this.queue,
                               System.currentTimeMillis());
    }

    @Override
    synchronized public void sendMessage() {
    
    }
    
    @Override
    synchronized public void getMessages() {
    
    }

    @Override
    public void run() {
    
    }
}
