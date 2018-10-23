package MapLayoutTest;

import MobileAgents.Message;
import MobileAgents.MobileAgent;
import MobileAgents.Node;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Coordinator extends Application {
    ArrayList<Thread> threads = new ArrayList<>();
    String fileName = System.getProperty("user.dir") +
            "/src/MapLayoutTest/GraphTest";
    GraphReader gr = new GraphReader(new File(fileName));
    HashMap<Node, ArrayList<Node>> map = gr.getGraph();
    Display display=new Display(map.keySet());
    
    public void beginSim(){
        for (Node n: map.keySet()) {
            if (n.isBaseStation()) {
                BlockingQueue<Message> queue = new LinkedBlockingQueue<>(1);
                long id = System.currentTimeMillis();
                MobileAgent mobileAgent = new MobileAgent(queue, id, n, true);
                threads.add(new Thread(mobileAgent));
                if(false);
            }
        }

        for (Node n: map.keySet()) {
            threads.add(new Thread(n));
        }

        for (Thread t: threads) {
            t.start();
        }
    }

    @Override
    public void start(Stage primaryStage){
     display.createGUI(primaryStage);
     beginSim();
    }
    
    public static void main(String[]args){
        launch(args);
    }

}
