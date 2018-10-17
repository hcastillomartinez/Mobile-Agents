package MobileAgents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * MobileAgents.Node.java stores all of the surrounding edges and paths back to the
 * substation in relation to this node.
 * Danan High, 10/5/2018
 */
public class Node implements SensorObject, Runnable {
    
    private String name;
    private BlockingQueue<Message> queue;
    private String state;
    private List<Node> neighbors = new ArrayList<>();
    private List<MobileAgent> agentList;
    private MobileAgent agent;
    private boolean baseStation;
    
    /**
     * Constructor for the Node class.
     * @param queue, concurrent queue for storing events
     * @param state, heat status of the node
     * @param name, name of the node
     */
    public Node(BlockingQueue<Message> queue,
                String state,
                String name) {
        this.queue = queue;
        this.state = state;
        this.name = name;
        this.baseStation = false;
        this.agent = null;
    }
    
    /**
     * Returning the name of the node.
     * @return name of the node
     */
    public String getName() { return this.name; }

    /**
     * Returning the list of the neighbor nodes.
     * @return neighbor list
     */
    public List<Node> getNeighbors() { return this.neighbors; }

    /**
     * Returning the status of the node.
     * @return status, heat, of the node
     */
    public String getState() { return this.state; }
    
    /**
     * Setting the node to the base station when graph is being read in.
     */
    public void setBaseStation() {
        this.baseStation = true;
        this.agentList = new ArrayList<>();
    }
    
    /**
     * Returning if the current node is the base station
     * @return baseStation status
     */
    public boolean isBaseStation() { return baseStation; }

    /**
     * Setting the state of the node.
     */
    public void setState(String stateSet) {
        this.state = stateSet;
    }

    /**
     * Returning the agent on the node.
     */
    public void setAgent(MobileAgent mobileAgent) {
        this.agent = mobileAgent;
    }
    
    /**
     * Returning the status of whether the node has an agent present.
     * @return agentPresent, true if there is an agent and false otherwise
     */
    private boolean agentPresent() {
        if (this.agent == null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Creating a message to add to this node
     */
    public void createMessage(Message message) {
        try {
            this.queue.put(message);
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Sending a message to update/perform a task.
     */
    @Override
    public synchronized void sendMessage() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    
    /**
     * Getting a message from the queue to perform a task.
     */
    @Override
    public synchronized void getMessages() {
        try {
            Message message = this.queue.take();
            analyzeMessage(message);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    
    /**
     * Analyzing the message from the queue.
     */
    private void analyzeMessage(Message m) {
        if (m.getSender().getClass().equals(MobileAgent.class)) {
            if (m.getDetailedMessage().equalsIgnoreCase("clone")) {
                MobileAgent mobileAgent = (MobileAgent) m.getSender();
                
                for (Node n: this.neighbors) {
                    if (!n.agentPresent()) {
                        n.setAgent(mobileAgent.clone());
                    }
                }
            }
        }
    }

    /**
     * Performing a task on this specific thread.
     */
    @Override
    public void run() {
        getMessages();
    }
}



/*
Types of messages to be sent:
    - for node to agent
        - if the node has an agent
        - state of heat on the node
        
    - for node to node
        - tell BaseNode to add an agent
        - tell neighbors to create agentList
        
    - for BaseNode from node
        - add new agent to the list of agentList
        - update location of the walking agent
 */































