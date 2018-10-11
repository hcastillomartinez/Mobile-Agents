package MobileAgents;

import java.util.LinkedList;

/**
 * MobileAgents.Node.java stores all of the surrounding edges and paths back to the
 * substation in relation to this node.
 * Danan High, 10/5/2018
 */
public class Node {
    private int level;
    private LinkedList<Node> edges;
    private LinkedList<Node> returnPaths;
    private boolean onFire, heatedUp, base, agentPresent;
    private Node agent;


    /**
     * Constructor for the MobileAgents.Node class. Class makes a new list of edges that
     * contains the neighboring nodes, a new list of nodes that lead to a path
     * back to the substation.
     * @param level
     */
    public Node(int level, boolean onFire, boolean heatedUp,
                boolean base, boolean agentPresent) {
        this.onFire = onFire;
        this.heatedUp = heatedUp;
        this.base = base;
        this.agentPresent = agentPresent;
        this.level = level;
        this.agent = null;
        this.edges = new LinkedList<>();
        this.returnPaths = new LinkedList<>();
    }


    /**
     * Returning the edges of this node.
     * @return edges of the node
     */
    public LinkedList<Node> getEdges() { return this.edges; }


    /**
     * Returning the level of the current node.
     * @return level of the node
     */
    public int getLevel() { return this.level; }


    /**
     * Returning the list of the return path nodes.
     * @return returnPaths nodes
     */
    public LinkedList<Node> getReturnPaths() { return this.returnPaths; }


    /**
     * Returning if the node is on fire or not.
     * @return onFire status, true if on fire, and false otherwise
     */
    public boolean isOnFire() { return this.onFire; }


    /**
     * Returning if the node is heated or not.
     * @return heatedUp status, true if heated up, and false otherwise
     */
    public boolean isHeatedUp() { return heatedUp; }

    /**
     * Walking the map
     */
    public void walk() {

    }
}
