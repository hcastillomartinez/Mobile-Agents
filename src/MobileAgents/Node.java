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
                node.setAgent(null);
                node.setState("red");
                for (Node n: node.getNeighbors()) {
                    if (!n.getState().equalsIgnoreCase("red")) {
                        n.sendMessage(new Message(node,
                                                  n,
                                                  null,
                                                  "update to new state"));
                    }

                    if (n.agentPresent()) {
                        n.getAgent().sendMessage(new Message(n,
                                                             n.getAgent(),
                                                             null,
                                                             "state changed"));
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
     * @param id, id of the node
     */
    public void setNodeIDForAgent(int id) { nodeID = id; }
    
    /**
     * Returning the name of the node.
     * @return name of the node
     */
    public String retrieveName() { return name; }
    
    /**
     * Returning the name of the node.
     * @return name of the node
     */
    public String getName() { return name; }
    
    /**
     * Sets level of a node
     * @param lvl, int that is level of node
     */
    public void setLevel(int lvl){
        level=lvl;
    }
    
    /**
     * Gets the Level of the node.
     * @return Returns an int
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Returning the list of the neighbor nodes.
     * @return neighbor list
     */
    public List<Node> getNeighbors() { return neighbors; }
    
    /**
     * Returning the x value for the node.
     * @return x, for the node
     */
    public int getX() { return x; }
    
    /**
     * Returning the y value for the node.
     * @return y, for the node
     */
    public int getY() { return y; }
    
    /**
     * Returning if the current node is the base station
     * @return baseStation status
     */
    public boolean isBaseStation() { return baseStation; }
    
    /**
     * Setting the node to the base station when graph is being read in.
     */
    public void setBaseStation() {
        baseStation = true;
        agentList = new CopyOnWriteArrayList<>();
    }
    
    /**
     * Returning the status of the node.
     * @return status, heat of the node
     */
    public String getState() {
        synchronized (state) {
            return state;
        }
    }
    
    /**
     * Setting the state of the node.
     * @param stateSet, state of the node
     */
    public synchronized void setState(String stateSet) {
        state = stateSet;
    }
    
    /**
     * Returning the agent on the node.
     * @param mobileAgent, mobile agent from this node
     * @return boolean, false if not set, true otherwise
     */
    public synchronized boolean setAgent(MobileAgent mobileAgent) {
        if (agent == null) {
            agent = mobileAgent;
            return true;
        } else {
            if (mobileAgent == null) {
                agent = null;
                return true;
            }
            return false;
        }
    }
    
    /**
     * Returning the mobile agent on the node.
     * @return mobile agent on the node
     */
    public synchronized MobileAgent getAgent() { return agent; }
    
    /**
     * Returning the list of the agents from the base station.
     * @return list of agents from the base station
     */
    public synchronized List<MobileAgent> mobileAgents() { return agentList; }
    
    /**
     * Returning the nodeID for creating mobile agent uniqueness.
     * @return nodeID, long node id
     */
    public long getNodeID() { return (long) nodeID;}
    
    /**
     * Returning the status of whether the node has an agent present.
     * @return agentPresent, true if there is an agent and false otherwise
     */
    private synchronized boolean agentPresent() {
        if (agent == null) {
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
        int nodePosition = random.nextInt(getNeighbors().size());
        Node node = getNeighbors().get(nodePosition);
        
        if (node.setAgent(mobileAgent)) {
            agent = null;
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
        for (Node n: neighbors) {
            if (n.getLevel() < level) {
                if (!n.getState().equalsIgnoreCase("red")) {
                    if (!message.getLowerRankedNodes().contains(n)) {
                        return n;
                    }
                }
            }
        }
        
        for (Node n: neighbors) {
            if (n.getLevel() >= level) {
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
     * @param message, message
     */
    private synchronized void sendCloneHome(Message message) {
        if (isBaseStation() &&
            !getState().equalsIgnoreCase("red")) {
            if (message.getDetailedMessage().equalsIgnoreCase("send clone home")) {
                if (!agentList.contains(message.getClonedAgent())) {
                    agentList.add(message.getClonedAgent());
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
        MobileAgent mobileAgent = new MobileAgent(new LinkedBlockingQueue<>(),
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
                                agent,
                                "send clone home"));
        for (Node n: getNeighbors()) {
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
        if (state.equalsIgnoreCase("yellow") &&
            !fireCountdownStarted) {
            fireCountdownStarted = true;
            FinalMessage finalMessage = new FinalMessage(this);
            finalMessage.start();
        }
    }
    
    /**
     * Creating the new state for the node
     * @param node, node to scan
     */
    private synchronized void makeNewState(Node node) {
        if (node.getState().equalsIgnoreCase("blue")) {
            setState("yellow");
        }
    }
    
    
    /******************************************************************/
    /*                                                                */
    /*                       Override Functions                      */
    /*                                                                */
    /******************************************************************/
    
    /**
     * Sending a message to update/perform a task.
     * @param message, message to put in the queue
     */
    @Override
    public void sendMessage(Message message) {
        try {
            queue.put(message);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    
    /**
     * Getting a message from the queue to perform a task.
     */
    @Override
    public void getMessages() {
        try {
            analyzeMessage(queue.take());
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

        if (messageString.equalsIgnoreCase("is agent present")) {
            checkNodeForRandomWalk(message);
        } else if (messageString.equalsIgnoreCase("clone")) {
            cloneAgents();
        } else if (messageString.equalsIgnoreCase("send clone home")) {
            sendCloneHome(message);
        } else if (messageString.equalsIgnoreCase("update to new state")) {
            makeNewState((Node) message.getReceiver());
        }
    }
    
    
    /**
     * Printing out the name of this node.
     * @return name of this node
     */
    @Override
    public String printOutName() { return getName(); }
    
    /**
     * Performing a task on this specific thread.
     */
    @Override
    public void run() {
        if (isBaseStation() && agentList.isEmpty()) {
            agentList.add(agent);
        }
        while(!state.equalsIgnoreCase("red")) {
            updateState();
            getMessages();
        }
    }
}




















