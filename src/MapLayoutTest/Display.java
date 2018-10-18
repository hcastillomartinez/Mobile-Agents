package MapLayoutTest;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import MobileAgents.Message;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import MobileAgents.Node;
import javafx.application.Application;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Display extends Application {

    @Override
    public void start(Stage primaryStage){
        BlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(1);
        String fileName = System.getProperty("user.dir") +
                "/src/MapLayoutTest/GraphTest";


        int circles=map.keySet().size();

        Set<Node> nodes=map.keySet();
        AnchorPane root=new AnchorPane();
        root.setPrefSize(circles*50,circles*50);


    }
    private void addNodes(AnchorPane root,Set nodes){
        for(Node node: nodes){

        }
    }


}
