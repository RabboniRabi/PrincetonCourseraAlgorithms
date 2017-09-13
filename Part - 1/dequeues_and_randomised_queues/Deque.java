import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Rabboni Rabi
 */
public class Deque<Item> implements Iterable<Item> {

    private Node firstNode = null;
    private Node lastNode = null;
    private int size = 0;

    public Deque() {
    }

    public boolean isEmpty() {

        if (size == 0) {
            return true;
        }
        return false;

    }

    public int size() {
        return size;
    }

    public void addLast(Item item) {

        if (item == null) {
            throw new NullPointerException("Item was null");
        }

        Node newLast =  new Node();
        newLast.item = item;
        newLast.next = null;

        if (size != 0) {
            Node oldLast = lastNode;
            newLast.previous = oldLast;
            oldLast.next = newLast;
        }
        else { // This is the first node in the deque. So, first node and last node are the same.
            newLast.previous = null;
            firstNode = newLast;
        }

        // update the first node
        lastNode =  newLast;

        // Increment the number of nodes in the deque counter.
        size++;
    }

    public void addFirst(Item item) {

        if (item == null) {
            throw new NullPointerException("Item was null");
        }

        Node newFirst = new Node();
        newFirst.item = item;
        newFirst.previous = null;

        if (size != 0) {
            Node oldFirst = firstNode;
            newFirst.next = oldFirst;
            oldFirst.previous = newFirst;
        }
        else { // This is the first node in the deque. So, first node and last node are the same.
            newFirst.next = null;
            lastNode = newFirst;
        }

        // update the last node
        firstNode = newFirst;

        // Increment the number of nodes in the deque counter.
        size++;

    }

    public Item removeLast() {

        if (size == 0) {
            throw new NoSuchElementException("Cannot remove from an empty queue");
        }
        else if (size == 1) {
            Node oldLast = lastNode;
            lastNode = null;
            firstNode = null;
            // Update size after removing the last item in the deque.
            size = 0;

            return oldLast.item;
        }
        else {
            Node oldLast = lastNode;
            Node newLast = lastNode.previous;
            newLast.next = null;

            lastNode = newLast;

            // Decrement the number of nodes in the deque counter.
            size = size - 1;

            // If size of the deque has become 1, set the last node to be equal to the first
            if (size == 1) {
                firstNode = lastNode;
            }

            return oldLast.item;
        }

    }

    public Item removeFirst() {

        if (size == 0) {
            throw new NoSuchElementException("Cannot remove from an empty queue");
        }
        else if (size == 1) {
            Node oldFirst = firstNode;
            lastNode = null;
            firstNode = null;

            // Update size after removing the last item in the deque.
            size = 0;

            return oldFirst.item;
        }
        else {
            Node oldFirst = firstNode;
            Node newFirst = firstNode.next;
            newFirst.previous = null;

            // Decrement the number of nodes in the deque counter.
            size = size - 1;

            firstNode = newFirst;

            // If the size of the deque has become 1, set the first node to be equal to the last.
            if (size == 1) {
                lastNode = firstNode;
            }
            return oldFirst.item;

        }

    }

    @Override
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }


    /**
     * Inner class - Node
     */
    private class Node {
        private Item item;
        private Node next;
        private Node previous;
    }

    /**
     * Inner class - Deque iterator
     */
    private class DequeIterator implements Iterator<Item> {

        private Node current = firstNode;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {

            if (current == null) {
                throw new NoSuchElementException("No more elements in iteration");
            }

            Item item = current.item;
            current = current.next;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Calling remove method is not supported");
        }
    }

}
