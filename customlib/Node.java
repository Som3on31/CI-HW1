package customlib;

public class Node <V>{
    protected Node head;
    protected Node tail;
    protected V value;
    
    public Node(V value){
        head = null;
        tail = null;
        this.value = value;
    }
}
