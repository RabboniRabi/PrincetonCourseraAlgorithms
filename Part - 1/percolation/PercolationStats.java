import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

/**
 * @author Rabboni Rabi
 */
public class PercolationStats {

    private double[] openSitesProportionValues;
    private int n;
    private int trials;

    public PercolationStats(int n, int trails) {

        this.n = n;
        this.trials = trails;
        openSitesProportionValues = new double[trails];
        calculateNumberOfOpenSitesProportionValues();

    }

    public double mean() {
        return StdStats.mean(openSitesProportionValues);
    }

    public double stddev() {
        return StdStats.stddev(openSitesProportionValues);
    }

    public double confidenceLo() {

        double confidenceLo = mean() - ((1.9 * stddev())/Math.sqrt(trials));

        return confidenceLo;
    }

    public double confidenceHi() {

        double confidenceHi = mean() + ((1.9 * stddev())/Math.sqrt(trials));

        return confidenceHi;
    }

    public static void main(String[] args) {
        // Get the size n of nxn grid and the number of trials
        int n = 0, trials = 0;
        try {
            n = Integer.parseInt(args[0]);
            trials = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException nfe) {
            System.out.println("Enter valid grid size and trials number.");
        }

        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("Grid size or trials number is not valid");
        }

        // For testing
        PercolationStats percolationStats = new PercolationStats(n, trials);
        System.out.println("mean = " + percolationStats.mean());
        System.out.println("stddev = " + percolationStats.stddev());
        System.out.println("95% confidence interval = [" + percolationStats.confidenceLo() + ", ["
                + percolationStats.confidenceHi() + "]");


    }

    /**
     * Method that initialises a grid of size nxn
     * for given number of trials and populates
     * an array with ratio of open sites to total number of sites
     * for each trial.
     */
    private void calculateNumberOfOpenSitesProportionValues() {

        int totalNumberOfSites = n * n;

        // For each trial
        for (int i = 0; i < trials; i++) {

            // Initialise a nxn percolation grid
            Percolation percolationGrid = new Percolation(n);
            // Get number of open sites at point of percolation
            int numberOfOpenSites = openSitesUntilPercolation(percolationGrid, n);

            // Add this to the array containing number of open sites for each trial
            openSitesProportionValues[i] = (float) numberOfOpenSites / (float) totalNumberOfSites;
        }

    }


    /**
     * Helper method that keeps randomly opening sites
     * in a given fully blocked grid until percolation
     * happens from the top to bottom.
     * @param percolationGrid percolation grid of size nxn.
     * @param gridWidth Width of the nxn grid: n;
     * @return number of open sites at the point of percolation.
     */
    private int openSitesUntilPercolation(Percolation percolationGrid, int gridWidth) {

        while (!percolationGrid.percolates()) {
            // Generate a random number between 0 and number of sites - 1
            int randomSite = StdRandom.uniform(0, gridWidth*gridWidth);
            // Increment row and column by 1 since
            // in this program requirement the grid indices are from (1,1) to (n,n)
            int row = (randomSite / gridWidth) + 1;
            int column = (randomSite % gridWidth) + 1;


            percolationGrid.open(row, column);

        }

        return percolationGrid.numberOfOpenSites();
    }

}
