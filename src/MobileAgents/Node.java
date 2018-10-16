package MobileAgents;

import java.awt.*;
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
     * Setting the state of the node.
     */
    public void setState(String stateSet) {
        this.state = stateSet;
    }
    
    /**
     * Returning the status of whether the node has an agent present
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
     * Sending a message to update/perform a task.
     */
    @Override
    synchronized public void sendMessage() {
    
    }
    
    /**
     * Getting a message from the queue to perform a task.
     */
    @Override
    synchronized public void getMessages() {
        try {
            Message tempMessage = this.queue.take();
            // take a look back here
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Performing a task on this specific thread.
     */
    @Override
    public void run() {
        sendMessage();
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































