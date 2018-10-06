package MapLayout;

import java.util.LinkedList;
import java.util.Random;

public class LayoutMaker {

    private LinkedList<Node> list;
    private int numberOfNodes;

    public LayoutMaker(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
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
            listOfNodes.add(new Node((i % 3) + 1));
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
            System.out.println(n + " = " + n.getEdges());
        }
    }


    /**
     * Function to return the list of the map that was made.
     * @return list of the map
     */
    public LinkedList<Node> getLayout() { return this.list; }

}
