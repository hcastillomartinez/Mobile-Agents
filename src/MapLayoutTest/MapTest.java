package MapLayoutTest;

import MobileAgents.Message;
import MobileAgents.MobileAgent;
import MobileAgents.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * MapTest.java is the main class used for running the program.
 * Danan High, 10/5/2018
 */
public class MapTest {

    public MapTest() {}

    public static void main(String[] args) {
        ArrayList<Thread> threads = new ArrayList<>();
        String fileName = System.getProperty("user.dir") +
            "/src/MapLayoutTest/GraphTest";
        GraphReader gr = new GraphReader(new File(fileName));
        HashMap<Node, ArrayList<Node>> map = gr.getGraph();
        
//        for (Map.Entry<Node, ArrayList<Node>> m: map.entrySet()) {
//            System.out.print("key = " + m.getKey().getName());
//            System.out.print("[");
//            for (Node n: m.getValue()) {
//                System.out.print(n.getName() + "    ");
//            }
//            System.out.println("]");
//        }
        
        for (Node n: map.keySet()) {
            if (n.isBaseStation()) {
                BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
                long id = System.currentTimeMillis();
                MobileAgent mobileAgent = new MobileAgent(queue,
                                                          id,
                                                          n,
                                                          true);
                threads.add(new Thread(mobileAgent));
            }
        }
        
        for (Node n: map.keySet()) {
            threads.add(new Thread(n));
        }
        
        for (Thread t: threads) {
            t.start();
        }
    }
}
