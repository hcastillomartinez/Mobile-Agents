package MapLayout;

import MobileAgents.Message;
import MobileAgents.MobileAgent;
import MobileAgents.Node;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Starts Threads and Launches GUI
 */

public class Coordinator extends Application {
    private FileChooser fileChooser=new FileChooser();
    private ArrayList<Thread> threads = new ArrayList<>();
    private File file=null;
    int choice;
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
                BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
                long id = 0;
                for (Node node: map.keySet()) {
                    id += node.getNodeID();
                }
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

    private boolean validGraph(Set<Node> nodes){
        boolean bs=false;
        boolean fire=false;
        for(Node n: nodes){
            if(n.isBaseStation())bs=true;
            if(n.getState().equals("red"))fire=true;
        }
        if(bs && fire){
            return true;
        }
        else return false;
    }


    /**
     * As program is launched Display sets everything
     * and simulation is started.
     * @param primaryStage the stage to draw the objects on
     */
    @Override
    public void start(Stage primaryStage){
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files","*.txt"));
        file=fileChooser.showOpenDialog(null);
        if(file==null){
            System.exit(0);
        }
        gr = new GraphReader(file);
        map = gr.getGraph();
        if(!validGraph(map.keySet())){
            System.out.println("adasd");
            System.out.println("not valid");
            System.exit(0);
        }
        display = new Display(map.keySet());
        display.createGUI(primaryStage,start,gr.greatestX(),gr.greatestY());
        display.start();
        start.setOnMousePressed(e -> {
            if (started == false) {
                beginSim();
                started = true;
            }
        });
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
