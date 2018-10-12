package MobileAgents;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * MobileAgents.Node.java stores all of the surrounding edges and paths back to the
 * substation in relation to this node.
 * Danan High, 10/5/2018
 */
public class Node implements SensorObject, Runnable {
    
    private String name;
    private Point coordinate;
    private BlockingQueue<Message> queue;
    private String state;
    private List<Node> neighbors;
    private MobileAgent agent;
    
    /**
     * Constructor for the Node class.
     * @param queue, concurrent queue for storing events
     * @param coordinate, location of the node
     * @param state, heat status of the node
     * @param name, name of the node
     */
    public Node(BlockingQueue<Message> queue,
                Point coordinate,
                String state,
                String name) {
        this.neighbors = new ArrayList<>();
        this.queue = queue;
        this.coordinate = coordinate;
        this.state = state;
        this.name = name;
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
     * Returning the location of the node in the graph.
     * @return point location of the node
     */
    public Point getCoordinate() { return this.coordinate; }
    
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
        try {
            // take a look here for how to store messages.
            // Maybe create a map that has all of the nodes and their associated
            // tasks that they are performing in a list?
            //      (key = SensorObj, value = list of messages).
            // will put the SensorObj in the BlockingQueue to take turns?
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
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
    
    }
}



/*
Types of messages to be sent:
    - for node to agent
        - if the node has an agent
        - state of heat on the node
        
    - for node to node
        - tell BaseNode to add an agent
        - tell neighbors to create agents
        
    - for BaseNode from node
        - add new agent to the list of agents
        - update location of the walking agent
 */































