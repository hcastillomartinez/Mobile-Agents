package MapLayout;

public class MapHolder {

    public MapHolder() {}

    public static void main(String[] args) {
        Node baseNode = new Node(0);
        Node second = new Node(1);
        Node third = new Node(2);
        Node fourth = new Node(2);
        Node fifth = new Node(3);
        Node sixth = new Node(1);

        System.out.println(baseNode.getLevel()
                                   + second.getLevel()
                                   + third.getLevel()
                                   + fourth.getLevel()
                                   + fifth.getLevel()
                                   + sixth.getLevel());
    }

}
