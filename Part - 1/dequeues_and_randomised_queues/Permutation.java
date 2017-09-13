import edu.princeton.cs.algs4.StdIn;

/**
 * @author Rabboni Rabi
 */
public class Permutation {

    public static void main(String[] args) {

        RandomizedQueue<String> randomizedQueue = new RandomizedQueue<String>();

        // Number of items to print.
        int n = Integer.parseInt(args[0]);

        // Read the input and add the items to the randomized queue.
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            randomizedQueue.enqueue(item);
        }

        // Print out n items randomly.
        for (int i = 0; i < n; i++) {
            System.out.println(randomizedQueue.dequeue());
        }

    }
}
