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
 * MainTest.java is the main class used for running the program.
 * Danan High, 10/5/2018
 */
public class MainTest {

    public MainTest() {}

    public static void main(String[] args) {
        ArrayList<Thread> threads = new ArrayList<>();
        String fileName = System.getProperty("user.dir") +
            "/src/MapLayoutTest/GraphTest";
        GraphReader gr = new GraphReader(new File(fileName));
        HashMap<Node, ArrayList<Node>> map = gr.getGraph();
        
        for (Node n: map.keySet()) {
            if (n.isBaseStation()) {
                BlockingQueue<Message> queue = new LinkedBlockingQueue<>(1);
                long id = System.currentTimeMillis();
                MobileAgent mobileAgent = new MobileAgent(queue,
                                                          id,
                                                          n,
                                                          true,
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
