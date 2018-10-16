package MapLayoutTest;

import MobileAgents.Message;
import MobileAgents.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * GraphReader.java is the class that reads in the file that contains the
 * graph layout. Class assumes that there is only one station and one fire.
 * Danan High, 10/15/2018
 */
public class GraphReader {
    
    private boolean fireStarted = false, baseStationAssigned = false;
    private File file;
    private HashMap<Node, ArrayList<Node>> graph;
    private BlockingQueue<Message> messages;
    private LinkedList<String> beginNode = new LinkedList<>();
    private LinkedList<String> endNode = new LinkedList<>();
    
    /**
     * Constructor for the GraphReader class
     * @param file, name of the file
     */
    public GraphReader(File file, BlockingQueue<Message> messages) {
        this.file = file;
        this.messages = messages;
        this.graph = new HashMap<>();
        readInGraph();
    }
    
    /**
     * Function to build the graph from the text file.
     */
    private void readInGraph() {
        Scanner scanner;
        String nextLine;
        int x = 0, y = 0, endX = 0, endY = 0, stationX = 0, stationY = 0,
            fireX = 0, fireY = 0;
        
        try {
            scanner = new Scanner(this.file);
            while (scanner.hasNext()) {
                nextLine = scanner.next();
                x = scanner.nextInt();
                y = scanner.nextInt();
               
                while (scanner.hasNextInt()) {
                    endX = scanner.nextInt();
                    endY = scanner.nextInt();
                }
    
                if (nextLine.equalsIgnoreCase("station") &&
                    !this.baseStationAssigned) {
                    stationX = x;
                    stationY = y;
                    this.baseStationAssigned = true;
                } else if (nextLine.equalsIgnoreCase("node")) {
                    placeNodeInGraph(makeNodeName(x, y), x, y);
                } else if (nextLine.equalsIgnoreCase("fire") &&
                    !this.fireStarted) {
                    fireX = x;
                    fireY = y;
                    this.fireStarted = true;
                } else if (nextLine.equalsIgnoreCase("edge")) {
                    beginNode.push(makeNodeName(x, y));
                    endNode.push(makeNodeName(endX, endY));
                }
            }
            
            for (Node n: this.graph.keySet()) {
                if (n.getName().equalsIgnoreCase(makeNodeName(stationX,
                                                              stationY))) {
                    n.setBaseStation();
                }
                
                if (n.getName().equalsIgnoreCase(makeNodeName(fireX,
                                                              fireY))) {
                    n.setState("red");
                }
            }
            
            for (int i = 0; i < beginNode.size(); i++) {
                placeEdgesInGraph(beginNode.get(i), endNode.get(i));
            }
            
            scanner.close();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        }
    }
    
    /**
     * Setting the nodes to their default states
     */
    
    /**
     * Checking if the map contains the nodes, and if not adding it to the
     * graph.
     */
    private void placeNodeInGraph(String name,
                                  int x,
                                  int y) {
        boolean inKeys = false;
        for (Node n: this.graph.keySet()) {
            if (n.getName().equalsIgnoreCase(name)) {
                inKeys = true;
            }
        }
        
        if (!inKeys) {
            this.graph.put(new Node(this.messages,
                                    "green",
                                    makeNodeName(x, y)),
                           new ArrayList<>());
        }
    }
    
    /**
     * Placing the edges in the graph.
     */
    private void placeEdgesInGraph(String beginNodeName, String endNodeName) {
        Node startNode = null, connectingNode = null;
        for (Node n: this.graph.keySet()) {
            if (n.getName().equalsIgnoreCase(beginNodeName)) {
                startNode = n;
            }
            
            if (n.getName().equalsIgnoreCase(endNodeName)) {
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
     * Making a string to make the name for the nodes
     */
    private String makeNodeName(int xSpot, int ySpot) {
        return "" + xSpot + " " + ySpot + "";
    }
    
    /**
     * Returning the built graph
     * @return graph of the layout
     */
    public HashMap<Node, ArrayList<Node>> getGraph() {
        return this.graph; }
}
