package MapLayout;

import MobileAgents.Message;
import MobileAgents.MobileAgent;
import MobileAgents.Node;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
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
    private FileChooser fileChooser=new FileChooser();
    private ArrayList<Thread> threads = new ArrayList<>();
    private File file=null;
    private GraphReader gr;
    private HashMap<Node, ArrayList<Node>> map;
    private Button start=new Button("Start");
    private Display display;
    private boolean started=false;

    /**
     * Goes through Nodes in graph and sets the start of
     * of simulation, starts all of the Threads here.
     */
    private void beginSim(){
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
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files","*.txt"));
        file=fileChooser.showOpenDialog(null);
        if(file==null){
            System.out.println("Nothing Chosen");
            System.exit(0);
        }
        else {
            gr = new GraphReader(file);
            map = gr.getGraph();
            display = new Display(map.keySet());
            display.createGUI(primaryStage,start);
            display.start();
            start.setOnMousePressed(e -> {
                if (started == false) {
                    beginSim();
                    started = true;
                }
            });
        }
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
