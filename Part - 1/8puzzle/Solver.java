import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Solver class that initialises the 8puzzle game with an initial board and
 * attempts to arrive at a solution.
 *
 * @author Rabboni Rabi
 */
public class Solver {

    private boolean isSolvable;
    private int numberOfMoves = 0;
    private Iterable<Board> solution;


    public Solver(Board initial) {

        if (initial == null) {
            throw new IllegalArgumentException("Initial board passed is null.");
        }

        solve(initial);
    }

    public boolean isSolvable() {
        return isSolvable;
    }

    public int moves() {
        return numberOfMoves;
    }

    public Iterable<Board> solution() {
        return solution;
    }

    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }


    private void solve(Board initial) {

        MinPQ<SearchNode> priorityQueue = new MinPQ<SearchNode>();
        MinPQ<SearchNode> alternatePriorityQueue = new MinPQ<SearchNode>();

        SearchNode initialSearchNode = new SearchNode(0, initial, null);
        SearchNode alternateInitialSearchNode = new SearchNode(0, initial.twin(), null);

        priorityQueue.insert(initialSearchNode);
        alternatePriorityQueue.insert(alternateInitialSearchNode);

        // Keep going through the search nodes until the goal is obtained or no solution can be found is ascertained.
        while (true) {

            if ((priorityQueue.min()).board.isGoal()) {

                SearchNode goalSearchNode = priorityQueue.delMin();
                updateSuccess(goalSearchNode);

                break;

            }
            else if ((alternatePriorityQueue.min()).board.isGoal()) {
                // Since twin initial board has arrived at the goal, the original board has no solution.
                updateFailure();
                break;

            }
            else {

                // Delete the search node with minimum priority from the queue.
                SearchNode searchNode = priorityQueue.delMin();

                // Add neighbours of the deleted search node to priority queue.
                for (SearchNode node : searchNode.getNeighbours()) {
                    priorityQueue.insert(node);
                }

                SearchNode alternateSearchNode = alternatePriorityQueue.delMin();
                for (SearchNode node : alternateSearchNode.getNeighbours()) {
                    alternatePriorityQueue.insert(node);
                }

            }
        }
    }

    /*
     * Helper method that updates the public methods with the solution
     * upon reaching the goal board.
     */
    private void updateSuccess(SearchNode goalSearchNode) {

        this.isSolvable = true;
        this.numberOfMoves = goalSearchNode.numberOfMoves;

        // Build the solution board path.
        SearchNode searchNodeInPath = goalSearchNode;
        Stack<Board> boardMoves = new Stack<Board>();
        while (searchNodeInPath != null) {
            boardMoves.push(searchNodeInPath.board);
            searchNodeInPath = searchNodeInPath.previousSearchNode;
        }

        this.solution = boardMoves;
    }

    /*
     * Helper method that updates the public methods of the class on finding that
     * no solution exists for the given initial board.
     */
    private void updateFailure() {

        this.isSolvable = false;
        this.solution = null;
        this.numberOfMoves = -1;

    }

    /**
     * Private nested class that is used in the priority queue to find the goal board.
     * This private class has fields in addition to board, number of moves made to
     * get to the search node, the previous search node and the priority value (manhattan
     * value of the board + number of moves made to the to the search node.
     * These fields help in optimising the computation of the solution and determining
     * the path from the initial board to the goal board.
     */
    private class SearchNode implements Comparable<SearchNode> {

        private Board board;

        private int numberOfMoves; // Number of moves made from initial search node

        private SearchNode previousSearchNode;

        private int priority;

        // constructor
        public SearchNode(int numberOfMoves, Board board, SearchNode previousSearchNode) {
            this.board = board;
            this.numberOfMoves = numberOfMoves;
            this.previousSearchNode = previousSearchNode;
            this.priority = board.manhattan() + numberOfMoves;
        }

        @Override
        public int compareTo(SearchNode searchNode) {

            return this.priority - searchNode.priority;
        }

        /* Gets neighbouring search nodes that are not the previous node */
        public List<SearchNode> getNeighbours() {

            List<SearchNode> neighbouringSearchNodes = new ArrayList<SearchNode>();

            Iterator<Board> allNeighbours = board.neighbors().iterator();

            while (allNeighbours.hasNext()) {
                Board neighbourBoard = allNeighbours.next();
                // skip the neighbour who is the same as the previous search node.
                if (previousSearchNode == null || !neighbourBoard.equals(previousSearchNode.board)) {
                    SearchNode searchNode = new SearchNode(this.numberOfMoves + 1, neighbourBoard,
                            this);
                    neighbouringSearchNodes.add(searchNode);
                }

            }
            return neighbouringSearchNodes;
        }

    }

}
