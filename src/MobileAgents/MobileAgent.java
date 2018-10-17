package MobileAgents;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * MobileAgent.java is the class that contains the functionality of an agent.
 * It can walk, clone itself and it implements the Runnable and SensorObject
 * interfaces.
 * Danan High, 10/11/2018
 */
public class MobileAgent implements SensorObject, Runnable {
    
    private BlockingQueue<Message> queue;
    private long id;
    private Node currentNode;
    private boolean walker;
    private String nodeStatus;
    
    /**
     * Constructor for the MobileAgent class that has a unique id.
     * @param id, unique identifier
     * @param queue, the list of tasks to perform
     */
    public MobileAgent(BlockingQueue<Message> queue,
                       long id,
                       Node currentNode,
                       boolean walker) {
        this.queue = queue;
        this.id = id;
        this.currentNode = currentNode;
        this.walker = walker;
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
        List<Node> neighbors;
        while (true) {
            try {
                neighbors = this.currentNode.getNeighbors();
                System.out.println(this.currentNode.getName() + " = curNode");
                this.currentNode = neighbors.get(rand.nextInt(neighbors.size()));
                
                if (this.currentNode.getState().equalsIgnoreCase("yellow") ||
                    this.currentNode.getState().equalsIgnoreCase("red")) {
                    this.currentNode.createMessage(new Message(this,
                                                               this.currentNode,
                                                               "clone"));
                }
                Thread.sleep(1000);
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    /**
     * Creating a new agent if the node below is heated.
     * @return alertAgent
     */
    protected MobileAgent clone() {
        return new MobileAgent(new LinkedBlockingQueue<>(),
                               System.currentTimeMillis(),
                               this.currentNode,
                               false);
    }

    @Override
    public synchronized void sendMessage() {
    }
    
    @Override
    public synchronized void getMessages() {
    
    }

    @Override
    public void run() {
        if (this.walker) {
            walk();
        }
    }
}
