package MapLayoutTest;

import MobileAgents.Message;
import MobileAgents.Node;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * GraphReader.java is the class that reads in the file that contains the
 * graph layout.
 * Danan High, 10/15/2018
 */
public class GraphReader {
    
    private File file;
    private HashMap<Node, LinkedList<Node>> graph;
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
        String nodeName = "";
        String nextLine;
        int x = 0, y = 0, endX = 0, endY = 0, place = 0, stationX = 0,
            stationY = 0, fireX = 0, fireY = 0;
        try {
            scanner = new Scanner(this.file);
            while (scanner.hasNext()) {
                nextLine = scanner.next();
                for (int i = 0; i < nextLine.length(); i++) {
                    if (Character.isAlphabetic(nextLine.charAt(i))) {
                        nodeName += nextLine.charAt(i);
                    } else if (Character.isDigit(nextLine.charAt(i))) {
                        if (place == 0) {
                            x = Integer.parseInt(makeString(nextLine
                                                                .charAt(i)));
                        } else if (place == 1) {
                            y = Integer.parseInt(makeString(nextLine
                                                                .charAt(i)));
                        } else if (place == 2) {
                            endX = Integer.parseInt(makeString(nextLine
                                                                   .charAt(i)));
                        } else {
                            endY = Integer.parseInt(makeString(nextLine
                                                                   .charAt(i)));
                        }
                        place++;
                    }
                }
                
                if (nodeName.equalsIgnoreCase("station")) {
                    stationX = x;
                    stationY = y;
                } else if (nodeName.equalsIgnoreCase("node")) {
                    placeNodeInGraph(makeNodeName(x, y), x, y);
                } else if (nodeName.equalsIgnoreCase("fire")) {
                    fireX = x;
                    fireY = y;
                } else if (nodeName.equalsIgnoreCase("edge")) {
                    beginNode.push(makeNodeName(x, y));
                    endNode.push(makeNodeName(x, y));
                }
                place = 0;
                nodeName = "";
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
                           new LinkedList<>());
        }
    }
    
    /**
     * Placing the edges in the graph.
     */
    private void placeEdgesInGraph(String beginNode, String endNode) {
        Node startNode = null, connectingNode = null;
        for (Node n: this.graph.keySet()) {
            if (n.getName().equalsIgnoreCase(beginNode)) {
                startNode = n;
            }
            
            if (n.getName().equalsIgnoreCase(endNode)) {
                connectingNode = n;
            }
        }
        
        if (!connectingNode.getNeighbors().isEmpty()) {
            if (!startNode.getNeighbors().contains(connectingNode)) {
                startNode.getNeighbors().add(connectingNode);
            }
        }
        
        if (!startNode.getNeighbors().isEmpty()) {
            if (!connectingNode.getNeighbors().contains(startNode)) {
                connectingNode.getNeighbors().add(startNode);
            }
        }
    }
    
    /**
     * Making a string to parse the integer from the line of characters.
     */
    private String makeString(char c) {
        return "" + c + "";
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
    public HashMap<Node, LinkedList<Node>> getGraph() {
        System.out.println("here");
        return this.graph; }
}
