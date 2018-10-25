package MapLayoutTest;

import java.util.*;
import java.util.List;

import MobileAgents.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * draws the graph of a map.
 * Hector Castillo
 */
public class Display {
    private AnchorPane root=new AnchorPane();
    private Pane pane=new Pane();
    private ScrollPane child =new ScrollPane();
    private Set<Node> nodes;

    public Display(Set<Node> n){
        this.nodes=n;
    }

    public void createGUI(Stage primaryStage){
        int circles=nodes.size();
        root.setPrefSize(500,500);
        pane.setPrefSize(circles*50,circles*50);
        child.setPrefSize(500,350);
        child.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        child.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        child.setLayoutX(5);
        child.setLayoutY(50);
        root.getChildren().add(child);
        child.setContent(pane);
        List<Circle> circleList=new ArrayList<>();
        List<Line> lineList=new ArrayList<>();
        addNodes(circleList);
        drawEdges(circleList,lineList);
        pane.getChildren().addAll(lineList);
        pane.getChildren().addAll(circleList);
        for(Node n: this.nodes){
            System.out.println(n.getName()+ ": Level- "+n.getLevel());
        }
        Timer t=new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                update(circleList);

//                System.out.println("update");
            }
        },1,1);
        Scene scene=new Scene(root);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void update(List<Circle> circleList){
//        System.out.println("new update");
        for(Circle circle: circleList){
            Node n=circleToNode(this.nodes,circle.getId());
            circle.setFill(Paint.valueOf(n.getState()));
//            System.out.println(n.getState());
            
            if(n.getAgent()==null){
                circle.setStroke(Paint.valueOf(n.getState()));
            }
            else circle.setStroke(Color.GREEN);
        }

    }

    private Node circleToNode(Set<Node> keySet,String name){
        for(Node node:keySet){
            if(node.getName().equals(name))return node;
        }
        return null;
    }
    /**
     * Draws the circles on Pane, does not draw them in any particular
     * order just what the keySet is for the map and that varies with how
     * the nodes are ordered in the text file.
     */
    private void addNodes(List<Circle> circleList){
        for(Iterator<Node> n=nodes.iterator();n.hasNext();){
            Node node=n.next();
            Circle circle = new Circle(15, Color.valueOf(node.getState()));
            circle.setStrokeWidth(3.5);
            circle.setStroke(Color.BLACK);
            circle.setId(node.getName());
            circle.setCenterX(node.getX()*50+circle.getRadius()+20);
            circle.setCenterY(node.getY()*50+circle.getRadius()+20);
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
