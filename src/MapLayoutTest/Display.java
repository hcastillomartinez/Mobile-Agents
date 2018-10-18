package MapLayoutTest;

import java.io.File;
import java.util.*;

import MobileAgents.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import MobileAgents.Node;
import javafx.application.Application;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Display extends Application {

    private GraphReader graphReader=new GraphReader(new File("GraphText.txt"));
    private HashMap<Node, ArrayList<Node>> map=graphReader.getGraph();
    private AnchorPane root=new AnchorPane();
    private List<Node> drawn=new ArrayList<>();
    @Override
    public void start(Stage primaryStage){

        int circles=map.keySet().size();

        Set<Node> nodes=map.keySet();
        root.setPrefSize(circles*50,circles*50);


    }
    private void addNodes(AnchorPane root){
        int size=map.keySet().size();
        for(Iterator<Node> n=map.keySet().iterator();n.hasNext();){
            Node node=n.next();
            if(!drawn.contains(node)) {
                Circle circle = new Circle(20, Color.valueOf(node.getState()));
                drawn.add(node);
                drawChildren(node);
            }
        }
    }

    /**
     * BFS way to draw them
     * @param n
     */
    private void drawChildren(Node n){
        List<Node> neighbors=map.get(n);
        for(Node node: neighbors){
            addNodes(this.root);
        }
    }


}
