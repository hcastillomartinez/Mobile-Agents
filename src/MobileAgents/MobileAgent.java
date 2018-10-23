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
     * Setting the current node after a move.
     * @param node to change to
     */
    public synchronized void setCurrentNode(Node node) {
        this.currentNode = node;
    }
    
    /**
     * Setting the walker to stop walking.
     */
    public synchronized void setWalkerStatus() { this.walker = false; }

    /**
     * Walking the nodes in the graph.
     */
    private void checkNode() {
        while (!this.currentNode.getState().equalsIgnoreCase("red")) {
            if (this.walker) {
                createMessageForNode("is agent present");
            }
            createMessageForNode("check state");
            getMessages();
        }
    }

    /**
     * Function to create the specified message.
     * @param messageCode phrase to send
     */
    private synchronized void createMessageForNode(String messageCode){
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
     */
    private synchronized void analyzeMessage(Message message) {
        String messageDetail = message.getDetailedMessage();
        Node node = (Node) message.getSender();

        if (messageDetail.equalsIgnoreCase("yellow")) {
            createMessageForNode("clone");
        } else if (messageDetail.equalsIgnoreCase("moved")) {
            System.out.println(this.currentNode.getName() + " = curNode");
        } else if (messageDetail.equalsIgnoreCase("agent present")) {
            createMessageForNode("is agent status");
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
            Thread.sleep(500);
            analyzeMessage(this.queue.take());
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Overriding run to perform specific tasks
     */
    @Override
    public void run() { checkNode(); }
}

/*
KISS = Keep It Simple Stupid
    really applies for concurrency!!!
 */
/*
10/23/2018
Do not know the state of the object before looking at the object (lock).
Will look at it and then proceed, or wait if lock has already been acquired.
 */






















