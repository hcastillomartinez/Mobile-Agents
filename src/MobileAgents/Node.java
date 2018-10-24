package MobileAgents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
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
    private int x, y;
    private List<Node> neighbors = new ArrayList<>();
    private List<List<Node>> pathsBack;
    private List<MobileAgent> agentList;
    private MobileAgent agent;
    private boolean baseStation;
    
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
     * Returning if the current node is the base station
     * @return baseStation status
     */
    public boolean isBaseStation() { return baseStation; }

    /**
     * Setting the state of the node.
     * @param stateSet, state of the node
     */
    public void setState(String stateSet) {
        this.state = stateSet;
    }

    /**
     * Returning the agent on the node.
     * @param mobileAgent, mobile agent from this node
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
     * Setting the node to the base station when graph is being read in.
     */
    public void setBaseStation() {
        this.baseStation = true;
        this.agentList = new ArrayList<>();
    }

    /**
     * Returning the status of whether the node has an agent present.
     * @return agentPresent, true if there is an agent and false otherwise
     */
    public boolean agentPresent() {
        if (this.agent == null) {
            return false;
        }
        return true;
    }

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
     */
    @Override
    public synchronized void getMessages() {
        long time = System.currentTimeMillis();
        try {
            while(!this.state.equalsIgnoreCase("red")){
                analyzeMessage(this.queue.take());
                for (Node n: this.neighbors) {
                    if (n.getState().equalsIgnoreCase("red")) {
                        setState("yellow");
                    }
                }
                System.out.println(this.getName() + " = " + this.queue);
                
//                if (Math.abs(time - System.currentTimeMillis()) >= 5000) {
//                    updateState();
//                }
            }
            removeClone(this.agent, this);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    
    /**
     * Analyzing where to send the message.
     * @param message, message to analyze
     */
    @Override
    public synchronized void analyzeMessage(Message message) {
        String messageString = message.getDetailedMessage();

        if (messageString.equalsIgnoreCase("is agent present")) {
            checkNodeForRandomWalk(message);
        } else if (messageString.equalsIgnoreCase("clone")) {
            cloneAgents();
        } else if (messageString.equalsIgnoreCase("send clone home")) {
            sendCloneToBaseStation(message.getClonedAgent(), this);
        } else if (messageString.equalsIgnoreCase("remove clone")) {
            removeClone(message.getClonedAgent(), this);
        } else if (messageString.equalsIgnoreCase("update state")) {
            System.out.println(message.toString());
            updateState();
        }
    }
    
    /**
     * Creating the new Mobile Agent for the node.
     * @param node, node to clone an agent on
     * @return mobileAgent to give to the new node
     */
    private synchronized MobileAgent clone(Node node) {
        long id = (new Random()).nextLong();
        return new MobileAgent(new LinkedBlockingQueue<>(1),
                               Math.abs(id),
                               node,
                               false,
                               true);
    }
    
    /**
     * Cloning agents on surrounding neighbors.
     */
    private synchronized void cloneAgents() {
        for (Node n: this.getNeighbors()) {
            if (!n.getState().equalsIgnoreCase("red") &&
                !n.agentPresent()) {
                n.setAgent(clone(n));
                if (n.getState().equalsIgnoreCase("yellow")) {
                    n.sendMessage(new Message(n,
                                              n,
                                              n.getAgent(),
                                              "clone"));
                }
                sendCloneToBaseStation(n.getAgent(), this);
            }
        }
    }
    
    /**
     * Removing the agent from the base station agent list
     */
    private synchronized void removeClone(MobileAgent mobileAgent,
                                          SensorObject sender) {
        if (this.isBaseStation()) {
            this.agentList.remove(mobileAgent);
        } else {
            Node node = getLowestRankedNode(this.neighbors);
            Message message = new Message(this,
                                          node,
                                          this.agent,
                                          "remove clone");
            node.sendMessage(message);
        }
    }

    /**
     * Sending the cloned mobile agent information home to the base station.
     */
    private synchronized void sendCloneToBaseStation(MobileAgent mobileAgent,
                                                     SensorObject sender) {
        if (this.isBaseStation()) {
            Node node = (Node) sender;
            if (!this.agentList.contains(mobileAgent)) {
                this.agentList.add(mobileAgent);
                node.sendMessage(new Message(this,
                                             sender,
                                             null,
                                             "message received"));
            }
        } else {
            Node node = getLowestRankedNode(this.neighbors);
            Message message = new Message(this,
                                          node,
                                          mobileAgent,
                                          "send clone home");
            node.sendMessage(message);
        }
    }

    /**
     * Updating the state of the node
     */
    private void updateState() {
        if (this.getState().equalsIgnoreCase("yellow")) {
            this.setState("red");
        } else if (this.getState().equalsIgnoreCase("blue")) {
            this.setState("yellow");
        }
    }

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

        if (node.agentPresent()) {
            messageToSend = new Message(node,
                                        mobileAgent,
                                        null,
                                        "agent present");
            mobileAgent.sendMessage(messageToSend);
        } else {
            this.setAgent(null);
            node.setAgent(mobileAgent);
            mobileAgent.setCurrentNode(node);

            messageToSend = new Message(node,
                                        mobileAgent,
                                        null,
                                        "moved");
            mobileAgent.sendMessage(messageToSend);
        }


    }
    
    /**
     * Getting the lowest ranked neighbor.
     * @param list, of the neighbor nodes
     * @return lowest ranked neighbor
     */
    private synchronized Node getLowestRankedNode(List<Node> list) {
        Node lowerRankNode = null;

        for (Node nodeCheck: list) {
            if (lowerRankNode == null) {
                lowerRankNode = nodeCheck;
            } else if (lowerRankNode.getX() > nodeCheck.getX()) {
                lowerRankNode = nodeCheck;
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
        System.out.println(this.getName() + " thread has stopped -------");
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

/* storage for functions that have been deleted but may need are below */
//    /**
//     * Creating a message based upon who the sender was and the node state
//     * @param sensObj, object to check the state
//     */
//    private synchronized void checkState(SensorObject sensObj) {
//        Message message;
//
//        if (sensObj.getClass().equals(MobileAgent.class)) {
//            if (getState().equalsIgnoreCase("yellow")) {
//                ((MobileAgent) sensObj).setWalkerStatus();
//                message = new Message(this,
//                                      sensObj,
//                                     null,
//                                     "yellow");
//                sensObj.sendMessage(message);
//            } else if (getState().equalsIgnoreCase("red")) {
//                ((MobileAgent) sensObj).setWalkerStatus();
//                message = new Message(this,
//                                      sensObj,
//                                      null,
//                                      "red");
//                sensObj.sendMessage(message);
//            }
//        } else {
//            if (getState().equalsIgnoreCase("red")) {
//                message = new Message(this,
//                                      sensObj,
//                                      null,
//                                      "set state yellow");
//                sensObj.sendMessage(message);
//            }
//        }
//    }






















