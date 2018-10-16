package MapLayoutTest;

import MobileAgents.Message;
import MobileAgents.Node;

import java.io.File;
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
        HashMap<Node, LinkedList<Node>> map = gr.getGraph();
        
        for (Map.Entry<Node, LinkedList<Node>> mapTwo: map.entrySet()) {
            System.out.println("key = " + mapTwo.getKey() + ", values" +
                                   mapTwo.getValue());
        }
    }
}
