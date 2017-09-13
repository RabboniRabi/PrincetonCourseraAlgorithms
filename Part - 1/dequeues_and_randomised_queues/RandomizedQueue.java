import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdRandom;

/**
 * @author Rabboni Rabi
 */
public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] itemArray;
    private int numberOfElements = 0;
    private int nextIndex = 0;

    public RandomizedQueue() {

    }

    public boolean isEmpty() {

        if (numberOfElements == 0) {
            return true;
        }
        return false;
    }

    public int size() {
        return numberOfElements;
    }

    public void enqueue(Item item) {

        if (item == null) {
            throw new NullPointerException("Item was null");
        }

        if (numberOfElements == 0) {
            itemArray = (Item[]) new Object[1];
            itemArray[0] = item;
            nextIndex = 1;
        }
        else if (nextIndex == itemArray.length) {
            resize(2 * itemArray.length);
            itemArray[numberOfElements] = item;
            nextIndex++;
        }
        else {
            itemArray[nextIndex] = item;
            nextIndex++;
        }
        numberOfElements++;
    }

    public Item dequeue() {

        if (numberOfElements == 0) {
            throw new NoSuchElementException("Cannot remove from empty queue");
        }

        // Pick a random index between 0 and nextIndex index
        int randomIndex = StdRandom.uniform(nextIndex);

        Item item = itemArray[randomIndex];

        /*
         * Move last non-null item in array, which will be at nextIndex - 1
         * to randomIndex and make value at nextIndex - 1 null.
         * In this way, all the non-null items in the array are bunched together
         * and non-null and null items are not peppered together and
         * we need not keep looping and looking at values in the array
         * until it is non-null.
         */
        itemArray[randomIndex] = itemArray[nextIndex - 1];
        itemArray[nextIndex - 1] = null;

        // Decrement the index in the array where the next item can be enqueued.
        nextIndex--;

        numberOfElements--;

        // If number of elements in the array has become less than or equal to
        // quarter the length of the array, halve the numberOfElements of the array.
        if (numberOfElements > 0 && numberOfElements == (itemArray.length/4)) {
            resize(itemArray.length/2);
        }

        return item;
    }

    public Item sample() {

        if (numberOfElements == 0) {
            throw new NoSuchElementException("Cannot sample from empty queue");
        }

        int randomIndex = StdRandom.uniform(nextIndex);
        Item item = itemArray[randomIndex];

        // Keep looking for an item that is not null.
        while (item == null) {
            randomIndex = StdRandom.uniform(nextIndex);
            item = itemArray[randomIndex];
        }

        return item;
    }


    @Override
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }


    /* Helper method to copy the contents of itemArray to new array
     * twice the length of the nextIndex itemArray. Changes reference
     * of itemArray to newly created array. In effect we are resizing.
     */

    private void resize(int capacity) {

        Item[] newItemArray = (Item[]) new Object[capacity];

        // Copy non-null items to new array
        int i = 0;
        while (i < itemArray.length && itemArray[i] != null) {
            newItemArray[i] = itemArray[i];
            i++;
        }

        itemArray = newItemArray;
    }

    /**
     * Inner class - Randomized queue iterator
     */
    private class RandomizedQueueIterator implements Iterator<Item> {

        private int i = numberOfElements;

        @Override
        public boolean hasNext() {
            if (i == 0) {
                return false;
            }
            return true;
        }

        @Override
        public Item next() {


            if (i == 0) {
                throw new NoSuchElementException("No items left in the queue");
            }

            Item item = dequeue();
            i--;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Calling remove method is not supported");
        }
    }
}
