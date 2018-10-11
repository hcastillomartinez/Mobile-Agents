package MapLayoutTest;

import MobileAgents.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


/**
 * LayoutMaker.java creates the random map to be used to check and run the
 * mobile agents program.
 * Danan High, 10/5/2018
 */
public class LayoutMaker {

    private LinkedList<Node> list;
    private HashMap<Node, LinkedList<Node>> map;
    private int numberOfNodes;

    public LayoutMaker(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
        this.map = new HashMap<>();
        this.list = new LinkedList<>();
        makeLayout();
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
                listOfNodes.add(new Node((i % 3) + 1,
                                         true,
                                         false,
                                         false,
                                         false));

            }else if (i == 1) {
                listOfNodes.add(new Node((i % 3) + 1,
                                         false,
                                         false,
                                         true,
                                         true));
            } else {
                listOfNodes.add(new Node((i % 3) + 1,
                                         false,
                                         false,
                                         false,
                                         false));
            }
        }

        for (Node n: listOfNodes) {
            randHolder = rand.nextInt(listOfNodes.size());
            for (int j = 0; j < randHolder; j++) {
                Node randomNode = listOfNodes.get((j + rand.nextInt(50)) % numberOfNodes);
                if (!randomNode.equals(n)) {
                    if (!n.getEdges().contains(randomNode)) {
                        n.getEdges().add(randomNode);
                    }
                }
            }
        }

        for (Node n: listOfNodes) {
            for (Node node: n.getEdges()) {
                if (!node.getEdges().contains(n)) {
                    node.getEdges().add(n);
                }
            }
        }

        for (Node n: listOfNodes) {
            if (!map.containsKey(n)) {
                map.put(n, n.getEdges());
            }
        }
    }


    /**
     * Function to return the list of the map that was made.
     * @return list of the map
     */
    public HashMap<Node, LinkedList<Node>> getLayout() { return this.map; }

}
