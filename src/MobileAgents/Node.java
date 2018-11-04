package MobileAgents;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MobileAgents.Node.java stores all of the surrounding edges and paths back to the
 * substation in relation to this node.
 * Danan High, 10/5/2018
 */
public class Node implements SensorObject, Runnable {

    private String name;
    private BlockingQueue<Message> queue;
    private String state;
    private int x, y, nodeID;
    private List<Node> neighbors = new ArrayList<>();
    private List<MobileAgent> agentList;
    private MobileAgent agent;
    private boolean baseStation, fireCountdownStarted = false;
    private int level;
    
    /**
     * Constructor for the Node class.
     * @param queue, concurrent queue for storing events
     * @param x, x value for the node
     * @param y, y value for the node
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
        this.level = 0;
    }
    
    // class to send the final message
    private class FinalMessage extends Thread {
        private Node node;
        
        private FinalMessage(Node node) {
            this.node = node;
        }
        
        /**
         * Overriding the run method to create a thread that has a delay to
         * make the fire spread after a certain amount of time.
         */
        @Override
        public void run() {
            try {
                Thread.sleep((new Random()).nextInt(5000) + 5000);
                this.node.setAgent(null);
                this.node.setState("red");
                
                for (Node n: this.node.getNeighbors()) {
                    if (!n.getState().equalsIgnoreCase("red")) {
                        n.sendMessage(new Message(this.node,
                                                  n,
                                                  null,
                                                  "update to new state"));
                        if (n.agentPresent()) {
                            n.getAgent().sendMessage(new Message(n,
                                                                 n.getAgent(),
                                                                 null,
                                                                 "state changed"));
                        }
                    }
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    
    
    /******************************************************************/
    /*                                                                */
    /*                    Getting Class Data Functions                */
    /*                                                                */
    /******************************************************************/
    
    /**
     * Setting the node ID.
     */
    public void setNodeIDForAgent(int id) { this.nodeID = id; }
    
    /**
     * Returning the name of the node.
     * @return name of the node
     */
    public String retrieveName() { return this.name; }
    
    /**
     * Returning the name of the node.
     * @return name of the node
     */
    public String getName() { return this.name; }
    
    /**
     * Sets level of a node
     * @param lvl, int that is level of node
     */
    public void setLevel(int lvl){
        this.level=lvl;
    }
    
    /**
     * Gets the Level of the node.
     * @return Returns an int
     */
    public int getLevel() {
        return this.level;
    }
    
    /**
     * Returning the list of the neighbor nodes.
     * @return neighbor list
     */
    public List<Node> getNeighbors() { return this.neighbors; }
    
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
     * Returning if the current node is the base station
     * @return baseStation status
     */
    public boolean isBaseStation() { return baseStation; }
    
    /**
     * Setting the node to the base station when graph is being read in.
     */
    public void setBaseStation() {
        this.baseStation = true;
        this.agentList = new CopyOnWriteArrayList<>();
    }
    
    /**
     * Returning the status of the node.
     * @return status, heat, of the node
     */
    public synchronized String getState() { return this.state; }
    
    /**
     * Setting the state of the node.
     * @param stateSet, state of the node
     */
    public synchronized void setState(String stateSet) {
        this.state = stateSet;
    }
    
    /**
     * Returning the agent on the node.
     * @param mobileAgent, mobile agent from this node
     */
    public synchronized boolean setAgent(MobileAgent mobileAgent) {
        if (this.agent == null) {
            this.agent = mobileAgent;
            return true;
        } else {
            if (mobileAgent == null) {
                this.agent = null;
                return true;
            }
            return false;
        }
    }
    
    /**
     * Returning the mobile agent on the node.
     * @return mobile agent on the node
     */
    public synchronized MobileAgent getAgent() { return this.agent; }
    
    /**
     * Returning the list of the agents from the base station.
     * @return list of agents from the base station
     */
    public synchronized List<MobileAgent> mobileAgents() { return this.agentList; }
    
    /**
     * Returning the nodeID for creating mobile agent uniqueness.
     * @return nodeID, long node id
     */
    public long getNodeID() { return (long) this.nodeID;}
    
    /**
     * Returning the status of whether the node has an agent present.
     * @return agentPresent, true if there is an agent and false otherwise
     */
    private synchronized boolean agentPresent() {
        if (this.agent == null) {
            return false;
        }
        return true;
    }
    
    
    /******************************************************************/
    /*                                                                */
    /*                 Mobile Agent Interaction Functions             */
    /*                                                                */
    /******************************************************************/
    
    /**
     * Analyzing the message from the queue.
     * @param message, message to check
     */
    private synchronized void checkNodeForRandomWalk(Message message) {
        Message messageToSend;
        Random random = new Random();
        MobileAgent mobileAgent = (MobileAgent) message.getSender();
        int nodePosition = random.nextInt(this.getNeighbors().size());
        Node node = this.getNeighbors().get(nodePosition);
        
        if (node.setAgent(mobileAgent)) {
            this.agent = null;
            mobileAgent.setCurrentNode(node);
            messageToSend = new Message(node,
                                        mobileAgent,
                                        null,
                                        "moved");
            mobileAgent.sendMessage(messageToSend);
        } else {
            messageToSend = new Message(node,
                                        mobileAgent,
                                        null,
                                        "agent present");
            mobileAgent.sendMessage(messageToSend);
        }
    }
    
    /**
     * Getting the lowest ranked neighbor.
     * @param message, sender of the message
     * @return lowest ranked neighbor
     */
    private synchronized Node getLowestRankedNode(Message message) {
        for (Node n: this.neighbors) {
            if (n.getLevel() < this.level) {
                if (!n.getState().equalsIgnoreCase("red")) {
                    if (!message.getLowerRankedNodes().contains(n)) {
                        return n;
                    }
                }
            }
        }
        
        for (Node n: this.neighbors) {
            if (n.getLevel() >= this.level) {
                if (!n.getState().equalsIgnoreCase("red")) {
                    if (!message.getLowerRankedNodes().contains(this)) {
                        message.getLowerRankedNodes().addLast(this);
                        return n;
                    }
                }
            }
        }
        return null;
    }
    
    
    /******************************************************************/
    /*                                                                */
    /*                        Cloning Functions                       */
    /*                                                                */
    /******************************************************************/
    
    /**
     * Sending the cloned mobile agent information home to the base station.
     */
    private synchronized void sendCloneHome(Message message) {
        if (this.isBaseStation() &&
            !getState().equalsIgnoreCase("red")) {
            if (message.getDetailedMessage().equalsIgnoreCase("send clone home")) {
                if (!this.agentList.contains(message.getClonedAgent())) {
                    this.agentList.add(message.getClonedAgent());
                }
            }
        } else {
            Node node = getLowestRankedNode(message);
            if (node != null) {
                Message m = new Message(this,
                                        node,
                                        message.getClonedAgent(),
                                        message.getDetailedMessage());
                m.setLowerRankedNodes(message.getLowerRankedNodes());
                node.sendMessage(m);
            }
        }
    }
    
    /**
     * Creating the new Mobile Agent for the node.
     * @param node, node to clone an agent on
     */
    private synchronized void clone(Node node) {
        MobileAgent mobileAgent = new MobileAgent(new LinkedBlockingQueue<>(1),
                                                  node.getNodeID(),
                                                  node,
                                                  false,
                                                  true);
        (new Thread(mobileAgent)).start();
        if (node.setAgent(mobileAgent)) {
            node.sendMessage(new Message(node,
                                         node,
                                         mobileAgent,
                                         "send clone home"));
        }
    }
    
    /**
     * Cloning agents on surrounding neighbors.
     */
    private void cloneAgents() {
        sendMessage(new Message(this,
                                this,
                                this.agent,
                                "send clone home"));
        for (Node n: this.getNeighbors()) {
            if (!n.getState().equalsIgnoreCase("red") &&
                !n.agentPresent()) {
                clone(n);
                if (n.getState().equalsIgnoreCase("yellow")) {
                    n.sendMessage(new Message(this,
                                              n,
                                              n.getAgent(),
                                              "clone"));
                }
            }
        }
    }
    
    
    /******************************************************************/
    /*                                                                */
    /*                        Updating State Functions                */
    /*                                                                */
    /******************************************************************/
    
    /**
     * Updating the state of the node
     */
    private synchronized void updateState() {
        if (this.getState().equalsIgnoreCase("yellow") &&
            !this.fireCountdownStarted) {
            FinalMessage finalMessage = new FinalMessage(this);
            finalMessage.start();
            this.fireCountdownStarted = true;
        }
    }
    
    /**
     * Creating the new state for the node
     */
    private synchronized void makeNewState(Node node) {
        if (node.getState().equalsIgnoreCase("blue")) {
            setState("yellow");
        }
    }
    
    /**
     * Checking the state of the node
     */
    private synchronized void checkState(SensorObject sensorObject) {
        if (state.equalsIgnoreCase("blue")) {
            sensorObject.sendMessage(new Message(this,
                                                 sensorObject,
                                                 null,
                                                 "state blue"));
        } else if (state.equalsIgnoreCase("yellow")) {
            sensorObject.sendMessage(new Message(this,
                                                 sensorObject,
                                                 null,
                                                 "state yellow"));
        } else {
            sensorObject.sendMessage(new Message(this,
                                                 sensorObject,
                                                 null,
                                                 "state red"));
        }
    }
    
    
    /******************************************************************/
    /*                                                                */
    /*                       Override Functions                      */
    /*                                                                */
    /******************************************************************/
    
    /**
     * Sending a message to update/perform a task.
     * @param message, message
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
     * Getting a message from the queue to perform a task.
     * sync
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
     * Analyzing where to send the message.
     * @param message, message to analyze
     */
    @Override
    public void analyzeMessage(Message message) {
        String messageString = message.getDetailedMessage();
        System.out.println(message.toString());
        
        if (messageString.equalsIgnoreCase("is agent present")) {
            checkNodeForRandomWalk(message);
        } else if (messageString.equalsIgnoreCase("clone")) {
            cloneAgents();
        } else if (messageString.equalsIgnoreCase("send clone home")) {
            sendCloneHome(message);
        } else if (messageString.equalsIgnoreCase("remove clone")) {
            sendCloneHome(message);
        } else if (messageString.equalsIgnoreCase("check state")) {
            checkState(message.getSender());
        } else if (messageString.equalsIgnoreCase("update to new state")) {
            makeNewState((Node) message.getReceiver());
        }
    }
    
    /**
     * Printing out the name of this node.
     * @return name of this node
     */
    @Override
    public String printOutName() { return this.getName(); }
    
    /**
     * Performing a task on this specific thread.
     */
    @Override
    public void run() {
        while(!this.state.equalsIgnoreCase("red")) {
            updateState();
            getMessages();
        }
    }
}




















