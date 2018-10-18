package MobileAgents;

import java.util.ArrayList;
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
     * Returning the id of the agent.
     * @return id, unique id of the agent
     */
    public long getId() { return this.id; }
    
    /**
     * Walking the nodes in the graph.
     */
    private void walk() {
        Random rand = new Random();
        List<Node> neighbors;
        while (true) {
            neighbors = this.currentNode.getNeighbors();
            Node node = neighbors.get(rand.nextInt(neighbors.size()));
            
            if (!node.agentPresent()) {
                this.currentNode = neighbors.get(rand.nextInt(neighbors.size()));
                try {
                    System.out.println(node.getName());
                    Thread.sleep(500);
                } catch(InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
    
            if ((this.currentNode.getState().equalsIgnoreCase("yellow") ||
                this.currentNode.getState().equalsIgnoreCase("red")) &&
                !this.currentNode.agentPresent()){
                this.currentNode.createMessage(new Message(this,
                                                           this.currentNode,
                                                           null,
                                                           "clone"));
            }
        }
    }

    /**
     * Creating a new agent if the node below is heated.
     * @return alertAgent
     */
    protected MobileAgent clone() {
        Random rand = new Random();
        long id = rand.nextLong();

        if (id <= 0) {
            id *= -1;
        }

        return new MobileAgent(new LinkedBlockingQueue<>(),
                               id,
                               this.currentNode,
                               false);
    }



    @Override
    public synchronized void sendMessage(Message message) {

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
