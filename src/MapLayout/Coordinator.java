package MapLayout;

import MobileAgents.Message;
import MobileAgents.MobileAgent;
import MobileAgents.Node;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Starts Threads and Launches GUI
 */
public class Coordinator extends Application {
    ArrayList<Thread> threads = new ArrayList<>();
    String fileName = System.getProperty("user.dir") +
            "/Resource/GraphTestTwo";
    GraphReader gr = new GraphReader(new File(fileName));
    HashMap<Node, ArrayList<Node>> map = gr.getGraph();

    Display display=new Display(map.keySet());

    /**
     * Goes through Nodes in graph and sets the start of
     * of simulation, starts all of the Threads here.
     */
    public void beginSim(){
        for (Node n: map.keySet()) {
            if (n.isBaseStation()) {
                BlockingQueue<Message> queue = new LinkedBlockingQueue<>(1);
                long id = (new Random()).nextLong();
                MobileAgent mobileAgent = new MobileAgent(queue,
                                                          Math.abs(id),
                                                          n,
                                                          true,
                                                          true);
                n.setAgent(mobileAgent);
                mobileAgent.setCurrentNode(n);
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

    /**
     * As program is launched Display sets everything
     * and simulation is started.
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage){
        display.createGUI(primaryStage);
        beginSim();
    }

    /**
     * When GUI is closed manually whole program
     * is killed.
     */
    @Override
    public void stop(){
        System.exit(0);
    }
    
    public static void main(String[]args){
        launch(args);
    }

}
