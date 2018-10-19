package MapLayoutTest;

import java.io.File;
import java.util.*;

import MobileAgents.Node;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * draws the graph of a map.
 * Hector Castillo
 */
public class Display extends Application {
    private String fileName = System.getProperty("user.dir") +
            "/src/MapLayoutTest/GraphTest";
    private GraphReader graphReader=new GraphReader(new File(fileName));
    private HashMap<Node, ArrayList<Node>> map=graphReader.getGraph();
    private AnchorPane root=new AnchorPane();
    private Pane pane=new Pane();
    private List<Node> drawn=new ArrayList<>();
    private Set<Node> nodes=map.keySet();

    @Override
    public void start(Stage primaryStage){
        int circles=map.keySet().size();


        root.setPrefSize(circles*50,circles*50);
        pane.setPrefSize(circles*50,(circles*50)-200);
        pane.setLayoutX(25);
        pane.setLayoutY(100);
        root.getChildren().add(pane);
        addNodes();
        drawEdges();
        Scene scene=new Scene(root);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

    /**
     * Draws the circles on Pane, does not draw them in any particular
     * order just what the keySet is for the map and that varies with how
     * the nodes are ordered in the text file.
     */
    private void addNodes(){
        for(Iterator<Node> n=map.keySet().iterator();n.hasNext();){
            Node node=n.next();
            if(!drawn.contains(node)) {
                Circle circle = new Circle(15, Color.valueOf(node.getState()));
                circle.setId(node.getName());
                circle.setCenterX(node.getX()*50);
                circle.setCenterY(node.getY()*50);
                System.out.println("circle: "+node.getName());
                this.pane.getChildren().add(circle);
            }
        }
    }

    /**
     * Finds Node that has the ID that we are searching for, name of the node,
     * and when found typeCasts it before returning.
     * @param list, ObservableList that is children of Pane.
     * @param name, String that is ID of a circle(name of node).
     * @return
     */
    private Circle findChild(ObservableList<javafx.scene.Node> list,String name){
        for(javafx.scene.Node node: list){
            if(node.getId().equals(name))return (Circle)node;
        }
        return null;
    }

    /**
     * Goes from node to node drawing a line between nodes that is
     * its neighbors.
     */
    private void drawEdges() {
        ObservableList<javafx.scene.Node> circle = this.pane.getChildren();
        Circle circle1;
        Circle circle2;
        String nb="";
        for (Node node : this.nodes) {
            circle1 = findChild(circle, node.getName());
            List<Node> neighbors = node.getNeighbors();
            for(Node no:neighbors){
                nb+=" ("+no.getName()+")";
            }
            System.out.println("Circle: "+node.getName()+" Neighbors: "+nb);
            nb="";
            for (Node n : neighbors) {
                Line line = new Line();
                circle2 = findChild(circle, n.getName());
                line.startXProperty().bind(circle1.centerXProperty().add(circle1.translateXProperty()));
                line.startYProperty().bind(circle1.centerYProperty().add(circle1.translateYProperty()));
                line.endXProperty().bind(circle2.centerXProperty().add(circle2.translateXProperty()));
                line.endYProperty().bind(circle2.centerYProperty().add(circle2.translateYProperty()));
                this.pane.getChildren().add(line);
            }
        }
    }
}
