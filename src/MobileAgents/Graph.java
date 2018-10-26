package MobileAgents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    private Map<Node,List<Node>> graph;

    /**
     * Constructor for the Graph class that sets map in
     * the graph to be a HashMap and
     * @param root A Node Object
     */
    public Graph(Node root) {
        graph = new HashMap<>();
        graph.put(root,root.getNeighbors());
    }
    
    /**
     * Adding a node to the map.
     */
    public void addNode(Node...node) {
        for(Node n: node){
            graph.put(n,n.getNeighbors());
        }
    }
    
    /**
     * Adding neighbors to the nodes in the map.
     */
    public void addNeighbor(Node key, Node nb) {
        List<Node> neighbor = graph.get(key);
        neighbor.add(nb);
    }
    
    /**
     * Returning the list of the neighbor nodes.
     * @return list of the neighbors
     */
    public List<Node> neighbors(Node key){
        return graph.get(key);
    }
    
    /**
     * Printing out the graph.
     */
    public void printGraph() {
        Set<Node> keys = graph.keySet();
        for(Node n: keys){
            try{
                List<Node> neighbor = n.getNeighbors();
                System.out.println("root: " + n.retrieveName() + " children: "
                                       + neighborString(neighbor));
            } catch(NullPointerException np) {
                np.printStackTrace();
            }

        }
    }
    
    /**
     * Returning the names of the neighbors.
     * @return names of neighbors in the graph
     */
    public String neighborString(List<Node> temp) {
        String names = "";
        for(Node n: temp){
            names += " " + n.retrieveName();
        }
        return names;
    }
}
