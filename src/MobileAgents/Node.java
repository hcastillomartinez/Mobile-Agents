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
    private int x, y;
    private List<Node> neighbors = new ArrayList<>();
    private List<List<Node>> pathsBack;
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
                int x,
                int y,
                String state,
                String name) {
        this.queue = queue;
        this.x = x;
        this.y = y;
        this.state = state;
        this.name = name;
        this.baseStation = false;
        this.agent = null;
        this.pathsBack = new ArrayList<>();
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
     * Returning the x value for the node.
     * @return x, for the node
     */
    public int getX() { return this.x; }
    
    /**
     * Returning the y value for the node.
     * @return y, for the node
     */
    public int getY() { return this.y; }
    
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
     * Returning the mobile agent on the node.
     * @return mobile agent on the node
     */
    public MobileAgent getAgent() { return this.agent; }
    
    /**
     * Returning the list of the agents from the base station.
     * @return list of agents from the base station
     */
    public List<MobileAgent> mobileAgents() { return this.agentList; }
    
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
    public synchronized void sendMessage(Message message) {
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
            analyzeMessage(queue.take());
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Returning the status of whether the node has an agent present.
     * @return agentPresent, true if there is an agent and false otherwise
     */
    public boolean agentPresent() {
        if (this.agent == null) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Analyzing where to send the message.
     */
    private synchronized void analyzeMessage(Message m) {
        if (m.getDetailedMessage().equalsIgnoreCase("clone")) {
            checkForCloneMessage(m);
        }
        if (m.getDetailedMessage().equalsIgnoreCase("insert clone")) {
            checkForAddCloneMessage(m);
        }
    }
    
    /**
     * Analyzing the message from the queue for a message that has the nodes
     * sending the new data about the clone to the base station.
     */
    private void checkForAddCloneMessage(Message m) {
        if (m.getSender().getClass().equals(Node.class)) {
            MobileAgent mobileAgent = m.getClonedAgent();
            
            if (isBaseStation()) {
                if (!mobileAgents().contains(mobileAgent)) {
                    mobileAgents().add(mobileAgent);
                    System.out.println("BaseStationList = " + mobileAgents());
                }
            } else {
                Node node = getLowestRankedNode(this.getNeighbors());
                node.createMessage(new Message(this,
                                               node,
                                               mobileAgent,
                                               "insert clone"));
            }
        }
    }

    /**
     * Analyzing the message from the queue.
     */
    private void checkForCloneMessage(Message m) {
        if (m.getSender().getClass().equals(MobileAgent.class)) {
            if (m.getDetailedMessage().equalsIgnoreCase("clone")) {
                MobileAgent mobileAgent = (MobileAgent) m.getSender();
                
                System.out.println("-------------");
                for (Node n: this.getNeighbors()) {
                    System.out.println(n.getName());
                    if (!n.agentPresent()) {
                        n.setAgent(mobileAgent.clone());
                        Node lowerNode = getLowestRankedNode(n.getNeighbors());
                        lowerNode.createMessage(new Message(n,
                                                            lowerNode,
                                                            n.getAgent(),
                                                            "insert clone"));
                    }
                }
            }
        }
    }
    
    /**
     * Getting the lowest ranked neighbor.
     * @return lowest ranked neighbor
     */
    private synchronized Node getLowestRankedNode(List<Node> list) {
        Node lowerRankNode = null;
        for (Node nodeCheck: list) {
            if (lowerRankNode == null) {
                lowerRankNode = nodeCheck;
            } else {
                if (lowerRankNode.getX() > nodeCheck.getX()) {
                    lowerRankNode = nodeCheck;
                }
            }
        }
        return lowerRankNode;
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
/*
put list of base agent on the gui
 */































