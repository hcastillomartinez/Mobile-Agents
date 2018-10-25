package MobileAgents;

import java.util.*;

/**
 * Implements Comparator so that List<List<Node>> can be sorted by the size
 * of the lists, will sort them from smallest to greatest.
 */
public class BestOption implements Comparator<Node> {
    @Override
    public int compare(Node a, Node b){
        if(b.getLevel()<a.getLevel())return 1;
        else if(a.getLevel()>a.getLevel())return -1;
        else return 0;
    }
}
