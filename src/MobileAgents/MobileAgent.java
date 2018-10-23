package MobileAgents;

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
     * Setting the current node after a move.
     * @param node to change to
     */
    public synchronized void setCurrentNode(Node node) {
        this.currentNode = node;
    }

    /**
     * Walking the nodes in the graph.
     */
    private void walk() {
        while (true) {
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
     * Creating a new agent if the node below is heated.
     * @return alertAgent
     */
    protected synchronized MobileAgent clone(Node n) {
        Random rand = new Random();
        long id = rand.nextLong();
        
        if (id < 0) { id *= -1; }

        MobileAgent mobileAgent = new MobileAgent(new LinkedBlockingQueue<>(1),
                                                  id,
                                                  n,
                                                  false);
        n.sendMessage(new Message(this,
                                  n,
                                  mobileAgent,
                                  "send clone home"));
        return mobileAgent;
    }
    
    /**
     * Cloning the mobile agent on the nodes
     */
    private synchronized void cloneMobileAgent(Node node) {
        this.walker = false;
        
        if (!node.agentPresent()) {
            node.setAgent(this);
        }
        
        for (Node n: node.getNeighbors()) {
            if (!n.agentPresent() && !n.getState().equalsIgnoreCase("re")) {
                n.setAgent(clone(n));
                System.out.println(n.getName() + " added agent " +
                                       n.getAgent().getId());
            } else {
                System.out.println(n.getName() + " this node has agent " +
                                       n.getAgent().getId());
            }
        }
        System.out.println(node.getName() + " home has added agent " +
                               node.getAgent().getId());
    }

    /**
     * Analyzing the message from the sender
     */
    private synchronized void analyzeMessage(Message message) {
        String messageDetail = message.getDetailedMessage();
        Node node = (Node) message.getSender();

        if (messageDetail.equalsIgnoreCase("good to clone")) {
            cloneMobileAgent(node);
        } else if (messageDetail.equalsIgnoreCase("moved")) {
            System.out.println(this.currentNode.getName() + " = curNode");
        } else if (messageDetail.equalsIgnoreCase("agent present")) {
            createMessageForNode("is agent status");
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
            Thread.sleep(1000);
            analyzeMessage(this.queue.take());
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Overriding run to perform specific tasks
     */
    @Override
    public void run() {
        if (this.walker) {
            walk();
        }
    }
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






















