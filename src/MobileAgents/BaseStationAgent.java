package MobileAgents;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * BaseStation class that provides the functionality for the base station.
 * Danan High, 10/11/2018
 */
public class BaseStationAgent extends Node {

    private BlockingQueue<Message> queue;
    private List<MobileAgent> agentList;
    private Point coordinate;
    private String state;
    private String name;

    /**
     * Constructor for the BaseStation class.
     * @param queue
     * @param coordinate
     * @param state
     * @param name
     */
    public BaseStationAgent(BlockingQueue<Message> queue,
                            Point coordinate,
                            String state,
                            String name) {
        super(queue, coordinate, state, name);
        this.queue = queue;
        this.coordinate = coordinate;
        this.state = state;
        this.name = name;
        this.agentList = new ArrayList<>();
    }
    
    /**
     * Overriding the method to send specific messages based off of this node.
     */
    @Override
    public void sendMessage() {
    
    }
    
    /**
     * Getting a message from the queue.
     */
    @Override
    public void getMessages() {
    
    }
    
    /**
     * Overriding run to perform specific tasks for this node.
     */
    @Override
    public void run() {
    
    }
}