package MapLayout;

public class MobileAgent {

    Node node;

    public MobileAgent(Node node) {
        this.node = node;
    }


    /**
     * Function to walk the map looking for signals indicating something is
     * wrong.
     */
    public void walkMap() {
        this.node.walk();
    }

}
