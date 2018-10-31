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
     * Setting the current node after a move.
     * sync
     * @param node to change to
     */
    public void setCurrentNode(Node node) {
        this.currentNode = node;
    }

    /**
     * Checks the state of the node that it sits on
     * if red then returns true meaning it's dead.
     * @return
     */
    private boolean state(){
        if(!this.currentNode.getState().equals("red")){
            return true;
        }
        else return false;
    }

    /**
     * Returning the id of the agent.
     * @return id, unique id of the agent
     */
    public long getId() { return this.id; }
    
    /**
     * Setting the walker to stop walking.
     * sync
     */
    private void setWalkerStatus() { this.walker = false; }

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
        } else if (messageDetail.equalsIgnoreCase("state changed")) {
            createMessageForNode("clone");
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
     * Overriding toString() to print out the agent.
     * @return string for the agent
     */
    public String toString() {
        if(state()) return "" + this.getId() + " on " + this.currentNode.getName() + " State: Alive";
        else return "" + this.getId() + " on " + this.currentNode.getName() + " State: Dead";
    }
    
    /**
     * Getting the name of the MobileAgent.
     * @return name of the mobile agent
     */
    @Override
    public String printOutName() {
        return this.toString();
    }

/**
     * Overriding run to perform specific tasks
     */
    @Override
    public void run() {
        long time = System.currentTimeMillis(), present;
    
        while (this.alive) {
            present = System.currentTimeMillis();
//            if (Math.abs(time - present) >= 600) {
                time = present;

                if (this.walker) {
                    if (this.currentNode.getState().equalsIgnoreCase("yellow")) {
                        if (this.walker) {
                            setWalkerStatus();
                        }
                        createMessageForNode("clone");
                    } else {
                        createMessageForNode("is agent present");
                    }
                } else if (this.currentNode.getState().equalsIgnoreCase("red")) {
                    this.alive = false;
                }
                getMessages();
//            }
        }
    }
}






















