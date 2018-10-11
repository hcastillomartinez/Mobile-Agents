import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Node extends Thread implements Runnable{
    private String name;
    private Point coordinate;
    private BlockingQueue<String> queue;
    private String state;
    private List<Node> neighbors;

    public Node(BlockingQueue<String> temp,Point coords,String color,String name){
        neighbors=new ArrayList<>();
        this.name=name;
        queue=temp;
        state=color;
        coordinate=coords;
    }
    public String getNodeName(){
        return name;
    }
    @Override
    public void run(){

    }
    public List<Node> getNeighbors(){
        return neighbors;
    }

    public String getStatus(){
        return state;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    /**
     * Puts message onto queue, next Nodes to get this message should be its
     * neighbors.
     */
    public void sendMessage(){
        try{

            queue.put(state);
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }

    /**
     * Takes message from queue that is expected to be from a neighbor and decides
     * what action to take( right now just based on color and not accounting for
     * agents.
     */
    public void interpretMessage(){
        try{
            String message=queue.take();
            if(message.equals("red")){
                state="yellow";
            }
        }catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }
}
