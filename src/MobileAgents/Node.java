package MobileAgents;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MobileAgents.Node.java stores all of the surrounding edges and paths back to the
 * substation in relation to this node.
 * Danan High, 10/5/2018
 */
public class Node implements SensorObject, Runnable {
    
    private String name;
    private long time;
    private BlockingQueue<Message> queue;
    private String state;
    private int x, y;
    private List<Node> neighbors = new ArrayList<>();
    private List<List<Node>> pathsBack;
    private List<MobileAgent> agentList;
    private MobileAgent agent;
    private boolean baseStation;
    private int level;
    private Object lock = new Object();

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
        this.level=0;
        this.pathsBack = new ArrayList<>();
        this.time = System.currentTimeMillis();
    }

    // class to send the final message
    private class FinalMessage extends Thread {
        private Node node;
        private MobileAgent mobileAgent;
        
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
                Thread.sleep((new Random()).nextInt(5000) + 4000);
                if (this.node.agentPresent()) {
                    this.node.sendOrRemoveClone(this.node.getAgent(),
                                                node,
                                                "remove clone");
                }
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
    public int getLevel(){
        return this.level;
    }

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
        
        if (messageString.equalsIgnoreCase("is agent present")) {
            checkNodeForRandomWalk(message);
        } else if (messageString.equalsIgnoreCase("clone")) {
            cloneAgents();
        } else if (messageString.equalsIgnoreCase("send clone home")) {
            sendOrRemoveClone(message.getClonedAgent(),
                              this,
                              "send clone home");
        } else if (messageString.equalsIgnoreCase("remove clone")) {
            sendOrRemoveClone(message.getClonedAgent(),
                              this,
                              "remove clone");
        } else if (messageString.equalsIgnoreCase("update state")) {
            updateState();
        } else if (messageString.equalsIgnoreCase("check state")) {
            checkState(message.getSender());
        } else if (messageString.equalsIgnoreCase("update to new state")) {
            makeNewState((Node) message.getReceiver());
        }
    }

    /**
     * Creating the new state for the node
     */
    private void makeNewState(Node node) {
        if (node.getState().equalsIgnoreCase("blue")) {
            setState("yellow");
        }
    }
    
    /**
     * Checking the state of the node
     */
    private void checkState(SensorObject sensorObject) {
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
    
    /**
     * Creating the new Mobile Agent for the node.
     * sync
     * @param node, node to clone an agent on
     */
    private void clone(Node node) {
        long id = (new Random()).nextLong();
        MobileAgent mobileAgent = new MobileAgent(new LinkedBlockingQueue<>(1),
                                                  Math.abs(id),
                                                  node,
                                                  false,
                                                  true);
        (new Thread(mobileAgent)).start();
        node.setAgent(mobileAgent);
    }

    /**
     * Cloning agents on surrounding neighbors.
     * sync
     */
    private void cloneAgents() {
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
                sendOrRemoveClone(n.getAgent(),
                                  n,
                                  "send clone home");
            }
        }
    }

    /**
     * Sending the cloned mobile agent information home to the base station.
     * sync
     */
    private void sendOrRemoveClone(MobileAgent mobileAgent,
                                   SensorObject sender,
                                   String command) {
        if (this.isBaseStation() &&
            !getState().equalsIgnoreCase("red")) {
            System.out.println("list = " + this.agentList);
            if (command.equalsIgnoreCase("send clone home")) {
                if (!this.agentList.contains(mobileAgent)) {
                    this.agentList.add(mobileAgent);
                }
            } else if (command.equalsIgnoreCase("remove clone")){
                if (this.agentList.contains(mobileAgent)) {
                    this.agentList.remove(mobileAgent);
                }
            }
        } else {
            Node node = getLowestRankedNode(this.neighbors);
            node.sendMessage(new Message(sender,
                                         node,
                                         mobileAgent,
                                         command));
        }
    }

    /**
     * Analyzing the message from the queue.
     * @param message, message to check
     */
    private void checkNodeForRandomWalk(Message message) {
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
    private Node getLowestRankedNode(List<Node> list) {
        Node lowerRankNode = null;
    
        //it was causing a deadlock issue when trying to send the nodes back
        // or retrieve them.
//        Collections.sort(list,new BestOption());
//        for(Node n: list){
//            if(n.getState().equals("blue") || n.getState().equals("yellow")){
//                lowerRankNode=n;
//                break;
//            }
//        }
    
    
        for (Node n: this.neighbors) {
            if (lowerRankNode == null) {
                lowerRankNode = n;
            } else if (lowerRankNode.getLevel() > n.getLevel()) {
                lowerRankNode = n;
            }
        }
        return lowerRankNode;
    }

    /**
     * Updating the state of the node
     */
    private void updateState() {
        boolean fireFound = false;
        for (Node n: this.neighbors) {
            if (n.getState().equalsIgnoreCase("red") &&
                !fireFound) {
                fireFound = true;
            }
        }
        
        if (fireFound) {
            FinalMessage finalMessage = new FinalMessage(this);
            finalMessage.start();
        }
    }

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























