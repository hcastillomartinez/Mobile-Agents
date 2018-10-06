package MapLayout;

import java.util.LinkedList;

/**
 * MapLayout.Node.java stores all of the surrounding edges and paths back to the
 * substation in relation to this node.
 * Danan High, 10/5/2018
 */
public class Node {
    private int level;
    private LinkedList<Node> edges;
    private LinkedList<Node> returnPaths;


    /**
     * Constructor for the MapLayout.Node class. Class makes a new list of edges that
     * contains the neighboring nodes, a new list of nodes that lead to a path
     * back to the substation.
     * @param level
     */
    public Node(int level) {
        this.level = level;
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
     * Walking the map
     */
    public void walk() {

    }
}
