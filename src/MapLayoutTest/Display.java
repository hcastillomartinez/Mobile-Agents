package MapLayoutTest;

import java.io.File;
import java.util.*;

import MobileAgents.Node;
import javafx.application.Application;
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
        List<Circle> circleList=new ArrayList<>();
        List<Line> lineList=new ArrayList<>();
        addNodes(circleList);
        drawEdges(circleList,lineList);
        pane.getChildren().addAll(lineList);
        pane.getChildren().addAll(circleList);
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
    private void addNodes(List<Circle> circleList){
        for(Iterator<Node> n=map.keySet().iterator();n.hasNext();){
            Node node=n.next();
                Circle circle = new Circle(15, Color.valueOf(node.getState()));
                circle.setId(node.getName());
                circle.setCenterX(node.getX()*50);
                circle.setCenterY(node.getY()*50);
                circleList.add(circle);
        }
    }

    /**
     * Finds the Circle that represents a node in the graph.
     * @param circleList, List that contains Circle representations on nodes.
     * @param name, ID of node we are looking for.
     * @return Returns a Circle.
     */
    private Circle nodeToCircle(List<Circle> circleList, String name){
        for(Circle circle:circleList){
            if(circle.getId().equals(name))return circle;
        }
        return null;
    }

    /**
     * Goes from node to node drawing a line between nodes that is
     * its neighbors.
     */
    private void drawEdges(List<Circle> circleList, List<Line> lineList) {
        Circle circle1;
        Circle circle2;
        for (Node node : this.nodes) { //keySet
            circle1 = nodeToCircle(circleList, node.getName());
            List<Node> neighbors = node.getNeighbors();
            for (Node n : neighbors) {
                Line line = new Line();
                circle2 = nodeToCircle(circleList, n.getName());
                line.startXProperty().bind(circle1.centerXProperty());
                line.startYProperty().bind(circle1.centerYProperty());
                line.endXProperty().bind(circle2.centerXProperty());
                line.endYProperty().bind(circle2.centerYProperty());
                lineList.add(line);
            }
        }
    }
}
