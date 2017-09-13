import java.util.ArrayList;
import java.util.List;

/**
 * Class used to represent the board of the 8 puzzle game and it's generic variations.
 * Each state of the board can be represented by an instance of this class.
 * Among the public methods, there are methods to get the manhattan/hamming distance
 * of blocks on the board from it's goal positions.
 *
 * @author Rabboni Rabi
 */

public class Board {

    private int [][] blocks;

    /**
     * Constructor to construct a board from an n-by-n array of blocks.
     * @param blocks
     */
    public Board(int[][] blocks) {

        if (blocks == null) {
            throw new IllegalArgumentException("Null value for blocks was passed.");
        }

        // Getting a new array of blocks and avoiding referencing private blocks field to the argument.
        this.blocks = getDeepCopyOfBlocks(blocks);
    }

    /* Returns the dimension of the board */
    public int dimension() {
        return blocks.length;
    }

    /* Returns the number of blocks out of place */
    public int hamming() {

        /* counter */
        int numberOfBlocksOutOfPlace = 0;
        /* sequence used to check if block is in right place */
        int sequenceValue = 1;

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {

                if (blocks[i][j] == 0) {
                    // no manhattan distance to be calculated for empty block
                }
                else if (blocks[i][j] != sequenceValue && blocks[i][j] != 0) {
                    numberOfBlocksOutOfPlace++;
                }
                sequenceValue++;
            }
        }

        return numberOfBlocksOutOfPlace;
    }

    /*
     * Returns sum of Manhattan distances between blocks and goal
     */
    public int manhattan() {

        int manhattanDistanceSum = 0;
        /* sequence used to check if block is in right place */
        int sequenceValue = 1;

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] == 0) {
                    // no manhattan distance to be calculated for empty block
                } else if (blocks[i][j] != sequenceValue) {
                    // int manhattanDistance = Math.abs(blocks[i][j] - sequenceValue);
                    int manhattanDistance = getManhattanX(i, blocks[i][j]) + getManhattanY(j, blocks[i][j]);
                    manhattanDistanceSum = manhattanDistanceSum + manhattanDistance;
                }
                sequenceValue++;
            }
        }
        return manhattanDistanceSum;
    }

    /*
     * Returns true if this board is the goal board
     */
    public boolean isGoal() {
        /* sequence used to check if blocks are in intended places */
        int sequenceValue = 1;



        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {

                if (i == dimension()-1 && j == dimension()-1) {
                    if (blocks[dimension()-1][dimension()-1] != 0) {
                        return false;
                    }
                }
                else if (blocks[i][j] != sequenceValue) {
                    return false;
                }
                sequenceValue++;
            }
        }
        return true;
    }

    /*
     * Twin board returned by exchanging blocks in the current board instance.
     */
    public Board twin() {

        int[][] newBlocks = getDeepCopyOfBlocks(blocks);

        // look for two blocks on the same row neither of which are 0 and swap their values.
        for (int i = 0; i < dimension() - 1; i++) {
            for (int j = 0; j < dimension() - 2; j++) {
                if (newBlocks[i][j] != 0 && newBlocks[i][j+1] != 0) {
                    int swapValue = newBlocks[i][j+1];
                    newBlocks[i][j+1] = newBlocks[i][j];
                    newBlocks[i][j] = swapValue;
                    break;
                }
            }
        }

        Board twinBoard = new Board(newBlocks);

        return twinBoard;

    }

    /*
     * checks if this board equals the passed board object y.
     */
    @Override
    public boolean equals(Object y) {

        if (y == this) {
            return true;
        }

        if (y == null || !(y instanceof Board) || ((Board) y).blocks.length != blocks.length) {
            return false;
        }
        Board boardToCompare = (Board) y;
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (blocks[i][j] != boardToCompare.blocks[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    // Returns list of all possible neighbors
    public Iterable<Board> neighbors() {

        int n = dimension(); // for convenience

        List<Board> neighbors = new ArrayList<>();

        // search the blocks for the blank block represented by 0.
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {

                if (this.blocks[i][j] == 0) {

                    /*
                     * Move the blank block to its neighbors by swapping blocks with neighbors.
                     * The set of available neighbors is determined by the position the blank
                     * block occupies in the grid. There are 9 distinct positions, each providing
                     * a set of neighbours for boards of any size.
                     */

                    if (i == 0 && j == 0) { // top left corner
                        neighbors.add(moveBlocksOfBoard(0, 0, 0, 1));
                        neighbors.add(moveBlocksOfBoard(0, 0, 1, 0));
                    }
                    else if (i == 0 && j == n - 1) { // top right corner
                        neighbors.add(moveBlocksOfBoard(0, n-1, 0, n-2));
                        neighbors.add(moveBlocksOfBoard(0, n-1, 1, n-1));
                    }
                    else if (i == n-1 && j == 0) { // bottom left corner
                        neighbors.add(moveBlocksOfBoard(n-1, 0, n-2, 0));
                        neighbors.add(moveBlocksOfBoard(n-1, 0, n-1, 1));
                    }
                    else if (i == n-1 && j == n-1) { // bottom right corner
                        neighbors.add(moveBlocksOfBoard(n-1, n-1, n-2, n-1));
                        neighbors.add(moveBlocksOfBoard(n-1, n-1, n-1, n-2));
                    }
                    else if (i == 0) { // top row
                        neighbors.add(moveBlocksOfBoard(0, j, 0, j-1));
                        neighbors.add(moveBlocksOfBoard(0, j, 0, j+1));
                        neighbors.add(moveBlocksOfBoard(0, j, 1, j));
                    }
                    else if (i == n-1) { // bottom row
                        neighbors.add(moveBlocksOfBoard(n-1, j, n-1, j-1));
                        neighbors.add(moveBlocksOfBoard(n-1, j, n-1, j+1));
                        neighbors.add(moveBlocksOfBoard(n-1, j, n-2, j));
                    }
                    else if (j == 0) { // leftmost column
                        neighbors.add(moveBlocksOfBoard(i, 0, i-1, 0));
                        neighbors.add(moveBlocksOfBoard(i, 0, i+1, 0));
                        neighbors.add(moveBlocksOfBoard(i, 0, i, 1));
                    }
                    else if (j == n-1) { // rightmost column
                        neighbors.add(moveBlocksOfBoard(i, n-1, i+1, n-1));
                        neighbors.add(moveBlocksOfBoard(i, n-1, i-1, n-1));
                        neighbors.add(moveBlocksOfBoard(i, n-1, i, n-2));
                    }
                    else { // all other places
                        neighbors.add(moveBlocksOfBoard(i, j, i-1, j));
                        neighbors.add(moveBlocksOfBoard(i, j, i+1, j));
                        neighbors.add(moveBlocksOfBoard(i, j, i, j-1));
                        neighbors.add(moveBlocksOfBoard(i, j, i, j+1));
                    }
                }
            }
        }


        return neighbors;

    }

    public String toString() {

        StringBuilder s = new StringBuilder();
        s.append(dimension()+"\n");

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                s.append(String.format("%2d ", blocks[i][j]));

            }
            s.append("\n");
        }
        return s.toString();
    }

    /*
     * Creates a new array of blocks(copy of the blocks of the board)
     * so that manipulations on blocks do no affect the blocks of the instance of the Board.
     */
    private int[][] getDeepCopyOfBlocks(int[][] blocksToCopy) {

        int[][] newBlocks = new int[blocksToCopy.length][blocksToCopy.length];

        for (int i = 0; i < blocksToCopy.length; i++) {
            for (int j = 0; j < blocksToCopy.length; j++) {
                newBlocks[i][j] = blocksToCopy[i][j];
            }
        }

        return newBlocks;
    }


    /*
     * Returns a new board that results from moving the blocks of the
     * board from given x, y to target x,y.
     */
    private Board moveBlocksOfBoard(int fromX, int fromY, int toX, int toY) {

        int[][] blocksCopy = getDeepCopyOfBlocks(this.blocks);
        int temporaryHolder = blocksCopy[toX][toY];
        blocksCopy[toX][toY] = blocksCopy[fromX][fromY];
        blocksCopy[fromX][fromY] = temporaryHolder;

        Board board = new Board(blocksCopy);

        return board;
    }

    /*
     * Private method that calculates the x component of the manhattan distance
     * of a block(value), given the horizontal component of the position(x) in the board.
     */
    private int getManhattanX(int x, int blockValue) {

        // calculate the row in the board (0 to dimension-1) to which this block value belongs.
        int row = (blockValue - 1) / dimension();

        // difference in the current x position with the target x position
        return Math.abs(x - row);
    }

    /*
     * Private method that calculates the y component of the manhattan distance
     * of a block(value), given the vertical component of the position(y) in the board.
     */
    private int getManhattanY(int y, int blockValue) {

        int column = (blockValue - 1) % dimension();

        // calculate the column in the board (0 to dimension-1) to which this block value belongs.
        return Math.abs(y - column);
    }

}
