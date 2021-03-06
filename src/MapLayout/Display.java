package MapLayout;

import java.util.*;
import java.util.List;

import MobileAgents.MobileAgent;
import MobileAgents.Node;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Draw a graph that is represented by set of Nodes
 * it takes in.
 */
public class Display {
    private AnchorPane root=new AnchorPane();
    private Pane pane=new Pane();
    private ListView<String> agentList=new ListView<>();
    private ScrollPane child =new ScrollPane();
    private Set<Node> nodes;
    private Node bs=null;
    private boolean flag=false;
    private Timeline timeLine;
    /**
     * Sets nodes to be Set passed in.
     * @param n, Set of Nodes
     */
    public Display(Set<Node> n){
        nodes=n;
    }

    /**
     * Sets the Nodes(JavaFx.Scene) on the stage passed in. Where
     * update(TimerTask where update done) happens.
     * @param start the start button for the program
     * @param primaryStage, Stage
     */
    public void createGUI(Stage primaryStage,Button start,int x,int y){
        root.setPrefSize(Screen.getPrimary().getBounds().getWidth() * 0.5 + 20,
                         Screen.getPrimary().getBounds().getHeight() * 0.5 +
                             20);
        pane.setPrefSize(x*55,y*100);
        child.setPrefSize(Screen.getPrimary().getBounds().getWidth() * 0.5,
                          Screen.getPrimary().getBounds().getHeight() * 0.5);
        agentList.setPrefSize(300,400);
        agentList.setLayoutX(Screen.getPrimary().getBounds().getWidth() * 0.5+30);
        agentList.setLayoutY(5);
        start.setLayoutY(Screen.getPrimary().getBounds().getHeight() * 0.5+10);
        start.setLayoutX((Screen.getPrimary().getBounds().getWidth() * 0.5 + 20)/2);
        child.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        child.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        child.setLayoutX(5);
        child.setLayoutY(5);
        root.getChildren().addAll(child,agentList,start);
        child.setContent(pane);
        Scene scene=new Scene(root);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Adds the circles to the list and starts timers
     * that will be updating the nodes on display.
     */
    public void start(){
        List<Shape> circleList=new ArrayList<>();
        List<Line> lineList=new ArrayList<>();
        addNodes(circleList);
        drawEdges(circleList,lineList);
        pane.getChildren().addAll(lineList);
        pane.getChildren().addAll(circleList);
        Timer t=new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                update(circleList);
            }
        },1,1);
        updateTable(timeLine);
    }
    /**
     * Goes through circle list and gets the node corresponding to
     * circle and checks its state, based off of what state and agent
     * status GUI Nodes are updated.
     * @param circleList, List<Shapes>
     */
    private void update(List<Shape> circleList){
        for(Shape circle: circleList) {
            if (circle.getId().equals("bs")) {
                Node n=circleToNode(nodes,bs.retrieveName());
                circle.setFill(Paint.valueOf(n.getState()));
                if (n.getAgent() == null) {
                    circle.setStroke(Color.BLACK);
                } else circle.setStroke(Color.GREEN);
            } else{
                Node n = circleToNode(nodes, circle.getId());
                circle.setFill(Paint.valueOf(n.getState()));
                if (n.getAgent() == null) {
                    circle.setStroke(Color.BLACK);
                } else circle.setStroke(Color.GREEN);
            }
        }
    }

    /**
     * Updates the table on the screen that shows the live agents
     * if the base station is dead the ones on the table will never be updated again
     * as the base station can no longer communicate. Also new agents that are created
     * that cannot be reached will also not show up on table as they shouldn't be able
     * to.
     */
    private synchronized void updateTable(Timeline timeline) {
        timeline=new Timeline(new KeyFrame(Duration.millis(1),e->{
            if(flag==false && bs.getState().equals("red")){
                agentList.getItems().add("Base Station is on fire.");
                flag=true;
            }
            else if(bs.mobileAgents().size()!=0 && flag==false){
                agentList.getItems().clear();
                for(MobileAgent m:bs.mobileAgents()){
                    agentList.getItems().add(m.toString());
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Gets the node from its corresponding GUI representation.
     * @param keySet, Set<Node> that is nodes in the graph.
     * @param name, Circle ID
     * @return Returns a Node.
     */
    private Node circleToNode(Set<Node> keySet,String name){
        for(Node node:keySet){
            if(node.retrieveName().equals(name))return node;
        }
        return null;
    }

    /**
     * Draws the circles on Pane, does not draw them in any particular
     * order just what the from the keySet that is for the map and
     *that varies with how the nodes are ordered in the text file.
     */
    private void addNodes(List<Shape> circleList){
        for(Iterator<Node> n=nodes.iterator();n.hasNext();){
            Node node=n.next();
            if(node.isBaseStation()){
                bs=node;
                Rectangle rect=new Rectangle(30,30,Color.valueOf(node.getState()));
                rect.setStrokeWidth(3.5);
                rect.setStroke(Color.BLACK);
                rect.setId("bs");
                Circle circle = new Circle(15, Color.valueOf(node.getState()));
                circle.setStrokeWidth(3.5);
                circle.setStroke(Color.BLACK);
                circle.setId(node.retrieveName());
                circle.setCenterX(node.getX() * 50 + circle.getRadius() + 20);
                circle.setCenterY(node.getY() * 50 + circle.getRadius() + 20);
                rect.setX(node.getX()*50+circle.getRadius()+5);
                rect.setY(node.getY()*50+circle.getRadius()+5);
                circleList.add(circle);
                circleList.add(rect);
            }
            else {
                Circle circle = new Circle(15, Color.valueOf(node.getState()));
                circle.setStrokeWidth(3.5);
                circle.setStroke(Color.BLACK);
                circle.setId(node.retrieveName());
                circle.setCenterX(node.getX() * 50 + circle.getRadius() + 20);
                circle.setCenterY(node.getY() * 50 + circle.getRadius() + 20);
                circleList.add(circle);
            }
        }
    }

    /**
     * Finds the Circle that represents a node in the graph.
     * @param circleList, List<Circle> that contains Circle representations on nodes.
     * @param name, Name of node that is also ID of circle.
     * @return Returns a Circle.
     */
    private Shape nodeToCircle(List<Shape> circleList, String name){
        for(Shape circle:circleList){
            if(circle.getId().equals(name))return circle;
        }
        return null;
    }

    /**
     * Goes from node to node drawing a line between nodes that is
     * its neighbors.
     */
    private void drawEdges(List<Shape> circleList, List<Line> lineList) {
        Circle circle1;
        Circle circle2;
        for (Node node : nodes) { //keySet
            circle1 = (Circle)nodeToCircle(circleList, node.retrieveName());
            List<Node> neighbors = node.getNeighbors();
            for (Node n : neighbors) {
                Line line = new Line();
                circle2 = (Circle)nodeToCircle(circleList, n.retrieveName());
                line.startXProperty().bind(circle1.centerXProperty());
                line.startYProperty().bind(circle1.centerYProperty());
                line.endXProperty().bind(circle2.centerXProperty());
                line.endYProperty().bind(circle2.centerYProperty());
                lineList.add(line);
            }
        }
    }
}
