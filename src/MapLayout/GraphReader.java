package MapLayout;

import MobileAgents.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * GraphReader.java is the class that reads in the file that contains the
 * graph layout. Class looks for only one station and one fire.
 * Danan High, 10/15/2018
 */
public class GraphReader {
    
    private boolean fireStarted = false, baseStationAssigned = false;
    private File file;
    private HashMap<Node, ArrayList<Node>> graph;
    private LinkedList<String> beginNode = new LinkedList<>();
    private LinkedList<String> endNode = new LinkedList<>();
    
    /**
     * Constructor for the GraphReader class.
     * @param file, name of the file
     */
    public GraphReader(File file) {
        this.file = file;
        this.graph = new HashMap<>();
        readInGraph();
        setLevels();
//        printGraph();
    }
    
    /**
     * Function to build the graph from the text file.
     */
    private void readInGraph() {
        Scanner scanner;
        String nextLine;
        int nodeX, nodeY, edgeX = 0, edgeY = 0, stationX = 0, stationY = 0,
            fireX = 0, fireY = 0;
        
        try {
            scanner = new Scanner(this.file);
            while (scanner.hasNext()) {
                nextLine = scanner.next();
                nodeX = scanner.nextInt();
                nodeY = scanner.nextInt();
               
                // checking for edge ints
                if (scanner.hasNextInt()) {
                    edgeX = scanner.nextInt();
                    edgeY = scanner.nextInt();
                }
    
                // checking next line string
                if (nextLine.equalsIgnoreCase("station") &&
                    !this.baseStationAssigned) {
                    stationX = nodeX;
                    stationY = nodeY;
                    this.baseStationAssigned = true;
                } else if (nextLine.equalsIgnoreCase("node")) {
                    placeNodeInGraph(makeNodeName(nodeX, nodeY), nodeX, nodeY);
                } else if (nextLine.equalsIgnoreCase("fire") &&
                    !this.fireStarted) {
                    fireX = nodeX;
                    fireY = nodeY;
                    this.fireStarted = true;
                } else if (nextLine.equalsIgnoreCase("edge")) {
                    beginNode.push(makeNodeName(nodeX, nodeY));
                    endNode.push(makeNodeName(edgeX, edgeY));
                }
            }
            
            for (int i = 0; i < beginNode.size(); i++) {
                placeEdgesInGraph(beginNode.get(i), endNode.get(i));
            }
            
            setStartingNodes(stationX, stationY, fireX, fireY);
    
            scanner.close();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
    }

    /**
     * Setting the fire and base station nodes.
     */
    private void setStartingNodes(int baseX,
                                  int baseY,
                                  int fireStartX,
                                  int fireStartY) {
        for (Node n: this.graph.keySet()) {
            if (n.retrieveName().equalsIgnoreCase(makeNodeName(baseX,
                                                               baseY))) {
                n.setBaseStation();
            }
        
            if (n.retrieveName().equalsIgnoreCase(makeNodeName(fireStartX,
                                                               fireStartY))) {
                n.setState("red");
                for (Node node: n.getNeighbors()) {
                    node.setState("yellow");
                }
            }
        }
    }
    
    /**
     * Checking if the map contains the nodes, and if not adding it to the
     * graph.
     */
    private void placeNodeInGraph(String name, int newNodeX, int newNodeY) {
        boolean inKeys = false;
        for (Node n: this.graph.keySet()) {
            if (n.retrieveName().equalsIgnoreCase(name)) {
                inKeys = true;
            }
        }
        
        if (!inKeys) {
            // look back here and check out the node for check time
            this.graph.put(new Node(new LinkedBlockingQueue<>(),
                                    newNodeX,
                                    newNodeY,
                                    "blue",
                                    name),
                           new ArrayList<>());
        }
    }
    
    /**
     * Placing the edges in the graph.
     */
    private void placeEdgesInGraph(String beginNodeName, String endNodeName) {
        Node startNode = null, connectingNode = null;
        for (Node n: this.graph.keySet()) {
            if (n.retrieveName().equalsIgnoreCase(beginNodeName)) {
                startNode = n;
            }
            
            if (n.retrieveName().equalsIgnoreCase(endNodeName)) {
                connectingNode = n;
            }
        }
        
        if (startNode != null && connectingNode != null) {
            if (!startNode.getNeighbors().contains(connectingNode)) {
                startNode.getNeighbors().add(connectingNode);
                this.graph.get(startNode).add(connectingNode);
            }
            
            if (!connectingNode.getNeighbors().contains(startNode)) {
                connectingNode.getNeighbors().add(startNode);
                this.graph.get(connectingNode).add(startNode);
            }
        }
    }
    
    /**
     * Making a string to make the name for the nodes.
     */
    private String makeNodeName(int xSpot, int ySpot) {
        return "" + xSpot + " " + ySpot + "";
    }

//    public void printGraph(){
//        for(Node n:graph.keySet()){
//            System.out.print("root: "+n.getName()+"");
//            for(Node a:n.getNeighbors()){
//                System.out.print(" "+a.getName());
//            }
//            System.out.println();
//        }
//    }
    
    /**
     * Returning the built graph.
     * @return graph of the layout
     */
    public HashMap<Node, ArrayList<Node>> getGraph() { return this.graph; }

    /**
     * Continually checks the nodes for their level and until all levels have been
     * set is when it stops.
     */
    private void setLevels(){
        while(levelDone()==false){
            for(Node n: this.graph.keySet()){
                setChildrenLevel(n);
            }
        }
    }

    /**
     * Checks the nodes in the graph and checks if they
     * all have their levels set yet
     * @return Returns true when done
     */
    private boolean levelDone(){
        for(Node n: this.graph.keySet()){
            if(n.getLevel()==0 && !n.isBaseStation())return false;
        }
        return true;
    }

    /**
     * Goes through children of a node and if their levels have not be
     * set yet, it does.
     * @param root, Node of which children are being updated
     */
    private void setChildrenLevel(Node root){
        Collection<Node> collection=root.getNeighbors();
        //roots level hasn't been set yet
        if(root.getLevel()==0 && !root.isBaseStation()){
            return;
        }
        else{
            for(Node n: collection){
                //checks if children have had their level set yet
                if(n.getLevel()==0 && !n.isBaseStation()){
                    n.setLevel(root.getLevel()+1);
                }
            }
        }
    }
}
