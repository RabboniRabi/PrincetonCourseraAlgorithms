
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

/**
 * Class that uses sorting and slopes to find line segments in a given array of points.
 *
 * @author Rabboni Rabi
 */
public class FastCollinearPoints {

    // unmodifiable variable holding number of points that need to be in a horizontal
    // line to be considered a line segment.
    private static final int NUMBER_OF_POINTS_IN_LINE_SEGMENT = 4;

    // variable for number of line segments
    private int numberOfSegments = 0;

    private LineSegment[] lineSegments;

    private LineSegment[] possibleLineSegments;

    /**
     * Constructor that when initialised starts the process of
     * calculating and making available the {@link LineSegment}s and
     * the number of them.
     * @param points
     */
    public FastCollinearPoints(Point[] points) {

        // Check that the argument or no points in the argument are null
        if (points == null) {
            throw new NullPointerException("Argument to FastCollinearPoints constructor was null");
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

    public int numberOfSegments() {

        return numberOfSegments;
    }

    public LineSegment[] segments() {

        lineSegments = new LineSegment[numberOfSegments()];

        for (int i = 0; i < lineSegments.length; i++) {
            lineSegments[i] = possibleLineSegments[i];
        }

        return lineSegments;
    }

    /* Helper method */
    private void findLineSegments(Point[] points) {

        // deep copy items in points array to another array that will
        // be sorted with respect to each point in the iteration.
        Point[] relativeSortedPoints = new Point[points.length];
        for (int k = 0; k < points.length; k++) {
            relativeSortedPoints[k] = points[k];
        }

        // For each point in the points array
        for (int i = 0; i < relativeSortedPoints.length; i++) {

            // sort the array of points with respect the slope they make this point.
            Arrays.sort(relativeSortedPoints, points[i].slopeOrder());


            /*
             * Iterate through this relative sorted array to look for specified
             * minimum  number of consecutive points with the same same slope with
             * the current point.
             */
            double slopeValue = Double.NEGATIVE_INFINITY;
            int sameSlopeValueCount = 0;
            int firstCollinearPoint = 1;
            // first point in the relativeSortedPoints array will be points[i], so we skip that.
            for (int j = 1; j < relativeSortedPoints.length; j++) {


                if (points[i].slopeTo(relativeSortedPoints[j]) == Double.NEGATIVE_INFINITY) {

                    throw new IllegalArgumentException("Duplicate Point");
                }

                else if (slopeValue == points[i].slopeTo(relativeSortedPoints[j])) {
                    sameSlopeValueCount++;
                }

                else if (sameSlopeValueCount >= NUMBER_OF_POINTS_IN_LINE_SEGMENT - 1) {
                    // add line segment to list of line segments found.
                    addLineSegment(relativeSortedPoints, j-1, sameSlopeValueCount + 1);
                    slopeValue = points[i].slopeTo(relativeSortedPoints[j]);
                    sameSlopeValueCount = 1;
                    firstCollinearPoint = j;
                }

                else {

                    slopeValue = points[i].slopeTo(relativeSortedPoints[j]);
                    sameSlopeValueCount = 1;
                    firstCollinearPoint = j;
                }

            }

            // After iterating through all the elements in the relatively sorted array
            // check if the last same value count has enough collinear points.
            if (sameSlopeValueCount >= NUMBER_OF_POINTS_IN_LINE_SEGMENT - 1) {
                // add line segment to list of line segments found.
                addLineSegment(relativeSortedPoints, firstCollinearPoint + sameSlopeValueCount - 1, sameSlopeValueCount + 1);
            }
        }

    }

    /* Helper method that for a given sorted array, the last collinear point index
     * and the number of collinear points, populates a subarray of collinear points
     * and then gets the line segment and adds it to the array of line segments.
     */
    private void addLineSegment(Point[] sortedArray, int lastCollinearPointIndex, int numberOfCollinearPoints) {

        Point[] collinearPoints = new Point[numberOfCollinearPoints];

        // Populate the collinear points array from the given sorted array.
        collinearPoints[0] = sortedArray[0];


        int i = 1;
        for (int j = lastCollinearPointIndex; j > lastCollinearPointIndex - numberOfCollinearPoints + 1; j--) {
            collinearPoints[i] = sortedArray[j];
            i++;
        }

        /*
         * Since the same line segment can be found from each of the
         * group of n collinear points, and by simply adding a line segment
         * at each iteration we will end up with n of the same line segments.
         * So, a check is put so that a line segment is added only when the point
         * with whom the slopes of all other points are calculated (collinearPoints[0])
         * is the lowest among the array of collinear points.
         */
        if (isFirstPointLowestPoint(collinearPoints)) {

            LineSegment lineSegment = getLineSegment(collinearPoints);

            // Check that there is space in the array before inserting the line segment
            // and resize if necessary.
            if (numberOfSegments == possibleLineSegments.length) {
                resize(possibleLineSegments.length * 2);
            }

            possibleLineSegments[numberOfSegments] = lineSegment;
            numberOfSegments++;
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

    /*
     * Helper method that given a array of points, finds and returns
     * the lowest point in the array.
     */
    private boolean isFirstPointLowestPoint(Point[] points) {

        Point lowestPoint = points[0];
        boolean firstPointLowest = true;

        for (int i = 1; i < points.length; i++) {
            if (lowestPoint.compareTo(points[i]) > 0) {
                firstPointLowest = false;
                break;
            }
        }
        return firstPointLowest;

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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
