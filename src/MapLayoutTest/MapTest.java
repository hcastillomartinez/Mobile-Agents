package MapLayoutTest;

import MobileAgents.Message;
import MobileAgents.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * MapTest.java is the main class used for running the program.
 * Danan High, 10/5/2018
 */
public class MapTest {

    public MapTest() {}

    public static void main(String[] args) {
        BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(1);
        
        String fileName = System.getProperty("user.dir") +
            "/src/MapLayoutTest/GraphTest";
        
        GraphReader gr = new GraphReader(new File(fileName), queue);
        
        HashMap<Node, ArrayList<Node>> map = gr.getGraph();
        
        for (Map.Entry<Node, ArrayList<Node>> mapTwo: map.entrySet()) {
            System.out.print("key = " + mapTwo.getKey().getName() + ", values" +
                                 " = [");
            for (Node n: mapTwo.getValue()) {
                System.out.print(n.getName() + "   ");
            }
            System.out.println("]");
        }
    }
}
