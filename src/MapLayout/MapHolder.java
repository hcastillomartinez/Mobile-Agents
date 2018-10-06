package MapLayout;

import java.util.LinkedList;


public class MapHolder {

    public MapHolder() {}

    public static void main(String[] args) {
        LayoutMaker layoutMaker = new LayoutMaker(15);
        LinkedList<Node> list = layoutMaker.getLayout();
        for (Node n: list) {
            System.out.println(n + " = " + n.getEdges());
        }
    }

}
