package MobileAgents;

import java.util.concurrent.BlockingQueue;

/**
 * MobileAgent.java is the class that contains the functionality of an agent.
 * It can checkNode, clone itself and it implements the Runnable and SensorObject
 * interfaces.
 * Danan High, 10/11/2018
 */
public class MobileAgent implements SensorObject, Runnable {
    
    private BlockingQueue<Message> queue;
    private long id;
    private Node currentNode;
    private boolean walker, alive;
    
    /**
     * Constructor for the MobileAgent class that has a unique id.
     * @param id, unique identifier
     * @param queue, the list of tasks to perform
     * @param currentNode, node that the mobile agent resides on
     * @param walker, status of if the agent is stationary or mobile
     * @param alive, health status of the agent
     */
    public MobileAgent(BlockingQueue<Message> queue,
                       long id,
                       Node currentNode,
                       boolean walker,
                       boolean alive) {
        this.queue = queue;
        this.id = id;
        this.currentNode = currentNode;
        this.walker = walker;
        this.alive = alive;
    }

    /**
     * Returning the id of the agent.
     * @return id, unique id of the agent
     */
    public long getId() { return this.id; }
    
    /**
     * Setting the current node after a move.
     * sync
     * @param node to change to
     */
    public void setCurrentNode(Node node) {
        this.currentNode = node;
    }
    
    /**
     * Setting the walker to stop walking.
     * sync
     */
    public void setWalkerStatus() { this.walker = false; }

    /**
     * Walking the nodes in the graph.
     */
    private void checkNode() {
    
    }

    /**
     * Function to create the specified message.
     * sync
     * @param messageCode, phrase to send
     */
    private void createMessageForNode(String messageCode){
        Message message;
        if (messageCode.equalsIgnoreCase("is agent status")) {
            message = new Message(this,
                                  this.currentNode,
                                  this,
                                  messageCode);
        } else {
            message = new Message(this,
                                  this.currentNode,
                                  null,
                                  messageCode);
        }
        this.currentNode.sendMessage(message);
    }

    /**
     * Analyzing the message from the sender
     * sync
     * @param message, message to analyze from the sender
     */
    @Override
    public void analyzeMessage(Message message) {
        String messageDetail = message.getDetailedMessage();

        if (messageDetail.equalsIgnoreCase("agent present")) {
            createMessageForNode("is agent status");
        } else if (messageDetail.equalsIgnoreCase("dead")) {
            this.alive = false;
        }
    }

    /**
     * Sending messages to the queue for the mobile agent.
     * @param message to add to the queue
     */
    @Override
    public void sendMessage(Message message) {
        try {
            this.queue.put(message);
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
            analyzeMessage(this.queue.take());
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    
    /**
     * Overriding toString() to print out the agent
     * sync
     * @return string for the agent
     */
    public synchronized String toString() {
        return "" + this.getId() + " " + this.currentNode.getName();
    }

    /**
     * Overriding run to perform specific tasks
     */
    @Override
    public void run() {
        long time = System.currentTimeMillis(), present;
        while (alive) {
            present = System.currentTimeMillis();
            if (Math.abs(time - present) >= 500) {
                System.out.println(toString());
                time = present;
                
                if (currentNode.getState().equalsIgnoreCase("yellow")) {
                    setWalkerStatus();
                    createMessageForNode("clone");
                } else if (currentNode.getState().equalsIgnoreCase("red")) {
                    alive = false;
                }
                
                if (walker) {
                    createMessageForNode("is agent present");
                }
                getMessages();
            }
        }
        System.out.println(toString() + " thread has stopped --------");
    }
}






















