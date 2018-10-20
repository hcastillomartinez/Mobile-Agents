package MobileAgents;

import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
     * Returning the id of the agent.
     * @return id, unique id of the agent
     */
    public long getId() { return this.id; }

    /**
     * Function to create the specified message.
     * @param messageCode phrase to send
     */
    private void createMessageForNode(String messageCode){
        Message message = new Message(this,
                                      this.currentNode,
                                      null,
                                      messageCode);
        this.currentNode.createMessage(message);
    }

    /**
     * Walking the nodes in the graph.
     */
    private void walk() {
        Random rand = new Random();
        List<Node> neighbors;
        while (true) {
            neighbors = this.currentNode.getNeighbors();
            Node node = neighbors.get(rand.nextInt(neighbors.size()));

            createMessageForNode("agent status");
        }
    }

    /**
     * Creating a new agent if the node below is heated.
     * @return alertAgent
     */
    protected MobileAgent clone(Node n) {
        Random rand = new Random();
        long id = rand.nextLong();

        if (id <= 0) {
            id *= -1;
        }

        return new MobileAgent(new LinkedBlockingQueue<>(),
                               id,
                               n,
                               false);
    }
    
    /**
     * Cloning the mobile agent on the nodes
     */
    private void cloneMobileAgent(Node node) {
        for (Node n: node.getNeighbors()) {
            n.setAgent(clone(node));
        }
    }

    /**
     * Setting the new currentNode to the neighbor without an agent
     */
    private void setCurrentNode(Node node) {
        this.currentNode = node;
    }

    /**
     * Analyzing the message from the sender
     */
    private void analyzeMessage(Message message) {
        if (message.getDetailedMessage().equalsIgnoreCase("good to clone")){
            cloneMobileAgent((Node) message.getSender());
        }
        if (message.getDetailedMessage().equalsIgnoreCase("move ok")){
            Node node = (Node) message.getSender();
            setCurrentNode(node);
            createMessageForNode("set agent");
            System.out.println(this.currentNode.getName() + " = currentNode"); // checking if random walk with producer consumer threading is working.
        }
        if (message.getDetailedMessage().equalsIgnoreCase("agent present")){
            System.out.println("here agent present"); // checking if the detection system for if there is an agent present is working.
            createMessageForNode("agent status");
        }
    }

    /**
     * Sending messages to the queue for the mobile agent.
     * @param message
     */
    @Override
    public void sendMessage(Message message) {
        try {
            this.queue.put(message);
            getMessages();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Getting the messages from this list to perform tasks.
     */
    @Override
    public void getMessages() {
        try {
            System.out.println(this.queue); // checking the queue of messages from the threads.
            Message message = this.queue.take();
            analyzeMessage(message);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (this.walker) {
            walk();
        }
    }
}












