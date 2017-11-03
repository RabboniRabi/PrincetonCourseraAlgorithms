import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Brute force implementation of a data type to represent
 * a set of points in a unit square.
 *
 * @author Rabboni Rabi
 */
public class PointSET {

    // A red-black BST to store points in the PointSET
    private TreeSet<Point2D> pointsTreeSet;

    /**
     * Construct an empty set of points.
     */
    public PointSET() {

        // Initialise the tree
        pointsTreeSet = new TreeSet<Point2D>();

    }

    /**
     * <p>
     * Method to check if set is empty.
     * </p>
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return pointsTreeSet.isEmpty();
    }

    /**
     * <p>
     * Size of the set.
     *</p>
     *
     * @return number of points in the set.
     */
    public int size() {

        return pointsTreeSet.size();

    }

    /**
     * <p>
     * Add the point to the set if it is not already in the set.
     * </p>
     *
     * @param p the query point
     */
    public void insert(Point2D p) {

        if (!contains(p)) {
            pointsTreeSet.add(p);
        }

    }

    /**
     * <p>
     * Method to check if the set contains the point p.
     *</p>
     *
     * @param p the query point
     * @return true if it contains the point p
     */
    public boolean contains(Point2D p) {

        return pointsTreeSet.contains(p);
    }

    /**
     * <p>
     * Method to draw all the points in the PointSET to standard draw.
     * </p>
     */
    public void draw() {

        Iterator<Point2D> iterator = pointsTreeSet.iterator();

        while (iterator.hasNext()) {
            Point2D point = iterator.next();
            StdDraw.setPenRadius(0.0125);
            StdDraw.point(point.x(), point.y());
        }

    }

    /**
     * <p>
     * Method to return all points in inside and on the boundary of the rectangle.
     *</p>
     *
     * @param rect the given rectangle in whose range points are to be found.
     *
     * @return iterator over the set of points
     */
    public Iterable<Point2D> range(RectHV rect) {

        List<Point2D> pointsInRange = new ArrayList<Point2D>();

        Iterator<Point2D> iterator = pointsTreeSet.iterator();

        while (iterator.hasNext()) {
            Point2D point = iterator.next();
            if (rect.contains(point)) {
                pointsInRange.add(point);
            }
        }

        return pointsInRange;
    }

    /**
     * <p>
     * Method to return the nearest neighbour in the set to the query point p.
     * Return null if the set is empty.
     * </p>
     *
     * @param p the query point
     * @return nearest neighbouring point
     */
    public Point2D nearest(Point2D p) {

        // Iterate over the points in a brute force manner and find the closest point
        Iterator<Point2D> iterator = pointsTreeSet.iterator();

        double closestDistance = Double.POSITIVE_INFINITY;
        Point2D closestPoint = null;
        while (iterator.hasNext()) {

            Point2D point = iterator.next();

            double distance = point.distanceSquaredTo(p);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPoint = point;
            }

        }

        return closestPoint;
    }

}
