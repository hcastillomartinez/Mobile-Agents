package MapLayoutTest;

import MobileAgents.MobileAgent;
import MobileAgents.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * LayoutMaker.java creates the random map to be used to check and run the
 * mobile agents program.
 * Danan High, 10/5/2018
 */
public class LayoutMaker {

    private LinkedList<Node> list;
    private HashMap<Node, List<Node>> map;
    private int numberOfNodes;

    public LayoutMaker(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
        this.map = new HashMap<>();
        this.list = new LinkedList<>();
        makeLayout();
        makeStart();
    }


    /**
     * Function to make the layout of the nodes.
     */
    private void makeLayout() {
        LinkedList<Node> listOfNodes = new LinkedList<>();
        Random rand = new Random();
        int randHolder;

        for (int i = 0; i < numberOfNodes; i++) {
            if (i == 0) {
                listOfNodes.add(new Node(new LinkedBlockingQueue<>(1),
                                         i,
                                         0,
                                         "blue",
                                         "" + i + " " + (i + 2) + ""));

            }else if (i == numberOfNodes) {
                listOfNodes.add(new Node(new LinkedBlockingQueue<>(1),
                                         i,
                                         0,
                                         "red",
                                         "" + i + " " + (i + 2) + ""));
            } else {
                listOfNodes.add(new Node(new LinkedBlockingQueue<>(1),
                                         i,
                                         i + 2,
                                         "blue",
                                         "" + i + " " + (i + 2) + ""));
            }
        }

        for (Node n: listOfNodes) {
            randHolder = rand.nextInt(listOfNodes.size());
            for (int j = 0; j < randHolder; j++) {
                Node randomNode = listOfNodes.get((j + rand.nextInt(50)) % numberOfNodes);
                if (!randomNode.equals(n)) {
                    if (!n.getNeighbors().contains(randomNode)) {
                        n.getNeighbors().add(randomNode);
                    }
                }
            }
        }

        for (Node n: listOfNodes) {
            for (Node node: n.getNeighbors()) {
                if (!node.getNeighbors().contains(n)) {
                    node.getNeighbors().add(n);
                }
            }
        }

        for (Node n: listOfNodes) {
            if (!map.containsKey(n)) {
                map.put(n, n.getNeighbors());
            }
        }
    }


    /**
     * Making the base station and fire start location
     */
    private void makeStart() {
        for (Node n: this.map.keySet()) {
            if (n.getState().equalsIgnoreCase("red")) {
                for (Node node: n.getNeighbors()) {
                    node.setState("yellow");
                }
            } else if (n.getX() == 0 && n.getY() == 0) {
                n.setBaseStation();
            }
        }
    }

    /**
     * Function to return the list of the map that was made.
     * @return list of the map
     */
    public HashMap<Node, List<Node>> getLayout() { return this.map; }

}
