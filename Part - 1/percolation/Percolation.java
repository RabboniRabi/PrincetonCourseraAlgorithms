

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * @author Rabboni Rabi
 */

public class Percolation {

    /* A boolean representation of a percolation grid where a false means blocked
       and a true means open.*/
    private boolean[][] sitesGrid;
    private int gridWidth;
    private int numberOfSites = 0;
    private int numberOfOpenSites = 0;
    private int topVirtualSiteLocation;
    private int bottomVirtualSiteLocation;
    private WeightedQuickUnionUF weightedQuickUnionUF;

    public Percolation(int n) {

        if (n <= 0) {
            throw new IllegalArgumentException("Invalid size of grid passed");
        }

        gridWidth = n;
        numberOfSites = n * n;
        // Two extra sites - virtual site at the top and virtual site at the bottom.
        weightedQuickUnionUF = new WeightedQuickUnionUF(numberOfSites + 2);

        /*
        * Since the weighted quick union initialises the grid
        * from 0 to n-1 where n is the number of sites(cells),
        * the nth element is seen as the top virtual site
        * and the n + 1th element is seen as the bottom virtual site
        * in my implementation.
        *
        * An instance of WeightedQuickUnionFind is initialised
        * in the class constructor
        * with number of sites to be n + 2
        */
        topVirtualSiteLocation = numberOfSites;
        bottomVirtualSiteLocation = numberOfSites + 1;

        // Connect the top row of the grid to the top virtual site
        connectTopRowOfGridToTopVirtualSite();
        // connect the bottom row of the grid to the bottom virutal site
        connectBottomRowOfGridToBottomVirtualSite();
        // Initialise the grid with all sites blocked
        initialiseAllSitesBlockedGrid();



    }

    public void open(int row, int col) {

        // If the site is not already open, open it.
        if (!isOpen(row, col)) {

            // Use local variables to switch to standard array convention
            int arrayRow = row - 1;
            int arrayColumn = col - 1;

            if ((arrayRow < 0) || (arrayRow >= sitesGrid[0].length) || (arrayColumn < 0)
                    || (arrayColumn >= sitesGrid[0].length)) {
                throw new IndexOutOfBoundsException();
            }

            sitesGrid[arrayRow][arrayColumn] = true;

            /* Check the four neighbours and create a union with them if they are open too.
             * Four neighbours are present for each cell-except those at the edges.
             * Four neighbours are up, down, left and right.
             */
            if (arrayRow - 1 >= 0) {
                if (isOpen(row - 1, col)) {
                    // connect these two
                    weightedQuickUnionUF.union((arrayRow * gridWidth) + arrayColumn,
                            ((arrayRow - 1) * gridWidth + arrayColumn));
                }
            }
            if (arrayRow + 1 < gridWidth) {
                if (isOpen(row + 1, col)) {
                    // connect these two
                    weightedQuickUnionUF.union((arrayRow * gridWidth) + arrayColumn,
                            (arrayRow + 1) * gridWidth + arrayColumn);
                }
            }
            if (arrayColumn - 1 >= 0) {
                if (isOpen(row, col-1)) {
                    // connect these two
                    weightedQuickUnionUF.union((arrayRow * gridWidth) + arrayColumn,
                            (arrayRow * gridWidth) + (arrayColumn - 1));
                }
            }
            if (arrayColumn + 1 < gridWidth) {
                if (isOpen(row, col+1)) {
                    // connect these two
                    weightedQuickUnionUF.union((arrayRow * gridWidth) + arrayColumn,
                            (arrayRow * gridWidth) + (arrayColumn + 1));
                }
            }

            // Increment the number of open sites counter.
            numberOfOpenSites++;

            // System.out.println("numberOfOpenSites: " + numberOfOpenSites);

        }

    }

    public boolean isOpen(int row, int col) {

        if ((row < 1) || (row > sitesGrid[0].length) || (col < 1) || (col > sitesGrid[0].length)) {
            throw new IndexOutOfBoundsException();
        }

        return sitesGrid[row-1][col-1];
    }

    public boolean isFull(int row, int col) {

        if ((row < 1) || (row > sitesGrid[0].length) || (col < 1) || (col > sitesGrid[0].length)) {
            throw new IndexOutOfBoundsException();
        }

        /*
         * Check if the site corresponding to this row and column
         * is connected to the top virtual row.
         */
        int siteNumber = (row-1) * gridWidth + (col -1);

        if (siteNumber < gridWidth || siteNumber >= (numberOfSites - gridWidth)) {
            /*
             * For the top row  and bottom row that is connected to the top virtual site,
             * we also have to check if it is open to be full.
             */
            return weightedQuickUnionUF.connected(siteNumber, topVirtualSiteLocation) && isOpen(row, col);
        }
        else {
            return weightedQuickUnionUF.connected(siteNumber, topVirtualSiteLocation);
        }

    }

    public int numberOfOpenSites() {
        return numberOfOpenSites;
    }

    public boolean percolates() {

        /**
         * For special case n = 1 when the grid is just a single cell,
         * the system percolates if the one cell is open.
         */
        if (numberOfSites == 1) {
            return isOpen(1, 1);
        }
        else {

            return weightedQuickUnionUF.connected(topVirtualSiteLocation, bottomVirtualSiteLocation);
        }

    }

    public static void main(String[] args) {

        // local variable
        int gridWidth = 5; // could equally use grid height
        Percolation percolationGrid = new Percolation(gridWidth);
        while (!percolationGrid.percolates()) {
            int randomSite = StdRandom.uniform(1, percolationGrid.numberOfSites);
           // System.out.println("random site: " + randomSite);
            int row = randomSite / gridWidth;
            int column = randomSite % gridWidth;

            // If not already open
            if (!percolationGrid.isOpen(row, column)) {
                // open it
                percolationGrid.open(row, column);
            }
        }


    }

    /* Helper methods */
    private void connectTopRowOfGridToTopVirtualSite() {

        for (int i = 0; i < gridWidth; i++) {
            weightedQuickUnionUF.union(i, topVirtualSiteLocation);
        }
    }
    private void connectBottomRowOfGridToBottomVirtualSite() {

        /* Start from the last element in the bottom row
         * connect all elements in this row upto the first element
         */
        int lastElement = numberOfSites - 1;
        int firstElement = numberOfSites - gridWidth;

        for (int i = lastElement; i >= firstElement; i--) {
            weightedQuickUnionUF.union(i, bottomVirtualSiteLocation);
        }
    }

    /*
     *
     */
    private void initialiseAllSitesBlockedGrid() {

        sitesGrid = new boolean[gridWidth][gridWidth];
        // Create the sites grid with all sites blocked
        // The nested for loop will have a time complexity of O(n^2)
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridWidth; j++) {
                sitesGrid[i][j] = false;
            }
        }

    }

}
