package MapLayoutTest;

import MobileAgents.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * MapTest.java is the main class used for running the program.
 * Danan High, 10/5/2018
 */
public class MapTest {

    public MapTest() {}

    public static void main(String[] args) {
        LayoutMaker layoutMaker = new LayoutMaker(5);
        HashMap<Node, LinkedList<Node>> map = layoutMaker.getLayout();

        for (Map.Entry<Node, LinkedList<Node>> m: map.entrySet()) {
            System.out.print(m.getKey() + " = ");
            for (Node n: m.getValue()) {
                System.out.print(n);
            }
            System.out.println();
        }
    }
}
