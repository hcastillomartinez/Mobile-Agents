package Graph;

import MobileAgents.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
    private Map<Node,List<Node>> graph;

    public Graph(Node root){
        graph=new HashMap<>();
        graph.put(root,root.getNeighbors());
    }
    
    public void addNode(Node... node){
        for(Node n:node){
            graph.put(n,n.getNeighbors());
        }
    }
    
    public void addNeighbor(Node key,Node nb){
        List<Node> neighbor=graph.get(key);
        neighbor.add(nb);
    }
    
    public List<Node> neighbors(Node key){
        return graph.get(key);
    }
    
    public void printGraph(){
        Set<Node> keys=graph.keySet();
        for(Node n:keys){
            try{
                List<Node> neighbor=n.getNeighbors();
                System.out.println("root: "+n.getName()+" children: " +
                                       ""+neighborString(neighbor));
            }catch(NullPointerException np){
                np.printStackTrace();
            }

        }
    }
    
    public String neighborString(List<Node> temp){
        String names="";
        for(Node n: temp){
            names+=" "+n.getName();
        }
        return names;
    }
}
