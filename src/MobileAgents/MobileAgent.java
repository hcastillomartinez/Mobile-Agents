package MobileAgents;

import java.util.List;
import java.util.Random;
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
    private Node currentNode;
    private String nodeStatus;
    
    /**
     * Constructor for the MobileAgent class that has a unique id.
     * @param id, unique identifier
     * @param queue, the list of tasks to perform
     */
    public MobileAgent(BlockingQueue<String> queue,
                       long id,
                       Node currentNode) {
        this.queue = queue;
        this.id = id;
        this.currentNode = currentNode;
    }
    
    /**
     * Getting the status of the node to determine if it is on fire.
     */
    private void getNodeStatus() {
        this.nodeStatus = this.currentNode.getState();
    }
    
    /**
     * Walking the nodes in the graph.
     */
    private void walk() {
        Random rand = new Random();
        List<Node> neighbors = this.currentNode.getNeighbors();
        this.currentNode = neighbors.get(rand.nextInt(neighbors.size()));
    }
    

    /**
     * Creating a new agent if the node below is heated.
     * @return alertAgent
     */
    protected MobileAgent clone() {
        return new MobileAgent(this.queue,
                               System.currentTimeMillis(),
                               currentNode);
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
/*
 Mobile agent needs to check the current node if they already have an agent
 Mobile agent needs to check the heat status of the node
 */
