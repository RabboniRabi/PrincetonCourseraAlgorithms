
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

/**
 * Class that uses brute force method to find line segments
 * containing four points.
 *
 * @author Rabboni Rabi
 */
public class BruteCollinearPoints {

    // variable for number of line segments
    private int numberOfSegments = 0;

    private LineSegment[] lineSegments;

    private LineSegment[] possibleLineSegments;

    /**
     * Constructor for the class. Will find all the line segments
     * and make number of segments and array of line segments
     * available upon initialisation.
     * @param points
     */
    public BruteCollinearPoints(Point[] points) {

        // Check that the argument or no points in the argument are null
        if (points == null) {
            throw new NullPointerException("Argument to BruteCollinearPoints constructor was null");
        }

        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new NullPointerException("A point in the points array argument was null");
            }
        }

        // check for duplicates
        for (int j = 0; j < points.length-1; j++) {
            for (int k = j+1; k < points.length; k++) {
                if (points[j].slopeTo(points[k]) == Double.NEGATIVE_INFINITY) {
                    throw new IllegalArgumentException("Duplicate point found in points array argument");
                }
            }
        }

        /* Although the number of possible line segments which include m
         * points from a given set of n points is nCm, we will initialise
         * the array containing all line segments to be of length n.
         * If we find more line segments, we can resize this array.
         */
        possibleLineSegments = new LineSegment[points.length];

        findLineSegments(points);

    }

    /**
     * Returns the number of line segments in the given
     * array of points
     * @return
     */
    public int numberOfSegments() {
        return numberOfSegments;

    }

    /**
     * Returns an array of line segments.
     * @return
     */
    public LineSegment[] segments()  {

        lineSegments = new LineSegment[numberOfSegments()];

        for (int i = 0; i < lineSegments.length; i++) {
            lineSegments[i] = possibleLineSegments[i];
        }

        return lineSegments;
    }


    /* Helper method */
    private void findLineSegments(Point[] points) {

        for (int i = 0; i <= points.length - 4; i++) {

            for (int j = i+1; j <= points.length - 3; j++) {

                double slopeValue = points[i].slopeTo(points[j]);

                for (int k = j+1; k <= points.length - 2; k++) {
                    if (slopeValue ==  points[i].slopeTo(points[k])) {
                        for (int m = k+1; m <= points.length - 1; m++) {
                            if (slopeValue == points[i].slopeTo(points[m])) {
                                Point[] collinearPoints = new Point[4];
                                collinearPoints[0] = points[i];
                                collinearPoints[1] = points[j];
                                collinearPoints[2] = points[k];
                                collinearPoints[3] = points[m];

                                LineSegment lineSegment = getLineSegment(collinearPoints);
                                addLineSegment(lineSegment);
                            }
                        }
                    }

                }

            }
        }
    }


    /*
     * Helper method that constructs a line segment
     * from a given set of collinear points.
     */
    private LineSegment getLineSegment(Point[] collinearPoints) {

        Point lowestPoint = collinearPoints[0];
        Point highestPoint = collinearPoints[0];

        for (int i = 1; i < collinearPoints.length; i++) {

            if (lowestPoint.compareTo(collinearPoints[i]) > 0) {
                lowestPoint = collinearPoints[i];
            }

            if (highestPoint.compareTo(collinearPoints[i]) < 0) {
                highestPoint = collinearPoints[i];
            }

        }


        // The lowest and lowest point in the set of collinear points form the
        LineSegment lineSegment = new LineSegment(lowestPoint, highestPoint);

        return lineSegment;
    }

    /* Helper method to add line segment to array of line segments */
    private void addLineSegment(LineSegment lineSegment) {

        numberOfSegments++;

        if (numberOfSegments == possibleLineSegments.length) {
            resize(possibleLineSegments.length * 2);
        }


        possibleLineSegments[numberOfSegments - 1] = lineSegment;

    }

    /*
     * Private method to resize the allPossibleLineSegments array
     * in case more line segments are found than the initial capacity.
     */
    private void resize(int capacity) {

        LineSegment[] expandedArray = new LineSegment[capacity];

        for (int i = 0; i < possibleLineSegments.length; i++) {
            expandedArray[i] = possibleLineSegments[i];
        }
        possibleLineSegments = expandedArray;
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }


}
