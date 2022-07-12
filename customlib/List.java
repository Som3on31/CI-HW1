package customlib;

public class List<V> {

    Node<V> head;
    Node<V> tail;

    public List() {
        head = null;
        tail = null;
        System.out.println("Sucessfulled created list ");
    }

    public void add(Node<V> newValue) {
        if (tail == null && head == null) {
            Node<V> current = newValue;
            while (current.head != null) {
                current = current.head;
            }
            head = current;

            current = newValue;
            while (current.tail != null) {
                current = current.tail;
            }
            tail = current;
        }

    }

    public boolean remove() {
        if (tail != null || head != null) {
            tail = tail.head;
            System.out.println("a" + " is sucessfully removed from the list.");
            tail.tail = null;
            
            return true;
        }
        System.out.println("Cannot remove the latest node from the list, it is already empty");
        return false;
    }
}
