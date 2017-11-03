
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;


import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * A two dimensional implementation of a k-dimension tree.
 * The two dimensions are x-axis and y-axis.
 * </p>
 *
 * @author Rabboni Rabi
 */
public class KdTree {

    private static final String ILLEGAL_ARG_EXCEPTION_MSG = "Given parameter is null";

    // root node of the 2-d tree
    private Node rootNode;

    private int noOfNodes = 0;

    public KdTree() {
    }

    /**
     * Method to check if the tree is empty.
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return rootNode == null;
    }

    /**
     * Size of the tree.
     *
     * @return number of points in the tree.
     */
    public int size() {
        return noOfNodes;
    }

    /**
     * <p>
     * Add the point to the tree if it is not already in the tree.
     * </p>
     *
     * @param p the point to be inserted
     */
    public void insert(Point2D p) {

        if (p == null) {
            throw new IllegalArgumentException(ILLEGAL_ARG_EXCEPTION_MSG);
        }

        if (!contains(p)) { // If point is not already contained in the tree

            // Initialise a new node with point p.
            Node newNode = new Node(p);

            // Initialise a node inserted flag
            boolean nodeInserted = false;

            // If tree is empty, insert at the root node and return.
            if (rootNode == null) {
                rootNode = newNode;
                rootNode.setxCheck(true);
                rootNode.setHasChildren(true);
                noOfNodes++;
                return;
            }

            Node currentNode = rootNode;
            while (!nodeInserted) {

                // compare along the x or y coordinate depending on the current node
                newNode.setxCheck(currentNode.xCheck);
                int compareVal = currentNode.compareTo(newNode);

                if (compareVal == -1) { // Look left
                    if (currentNode.leftNode ==  null) { // Insert here
                        newNode.setxCheck(!currentNode.xCheck); // opposite of parent's x check flag
                        currentNode.setLeftNode(newNode);
                        currentNode.setHasChildren(true);
                        nodeInserted = true;
                    }
                    else {
                        currentNode = currentNode.leftNode;
                    }
                }
                else { // Look right

                    if (currentNode.rightNode == null) { // Insert here
                        newNode.setxCheck(!currentNode.xCheck);
                        currentNode.setRightNode(newNode);
                        currentNode.setHasChildren(true);
                        nodeInserted = true;
                    }
                    else {
                        currentNode = currentNode.rightNode;
                    }
                }
            }

            noOfNodes++;
        }
    }

    /**
     * <p>
     * Method to check if the set contains the point p.
     * </p>
     *
     * @param p the point to check if it is in the tree
     * @return true if it contains the point p
     */
    public boolean contains(Point2D p) {

        if (p == null) {
            throw new IllegalArgumentException(ILLEGAL_ARG_EXCEPTION_MSG);
        }

        if (rootNode == null) {
            // tree is empty
            return false;
        }

        // Temporary node containing the given point.
        Node tempNode = new Node(p);

        // A flag to indicate if the tree has been traversed in search of the node.
        boolean treeTraversed = false;

        // Start the search at the root node.
        Node currentNode = rootNode;

        while (!treeTraversed) {

            // Check if point in current node equals given point.
            if (currentNode.getPoint().equals(p)) {
                return true;
            }

            // Check if current node has children to look at.
            if (!currentNode.hasChildren) {
                return false;
            }

            // Use compare to go to left child or right child.
            // compare along the x or y coordinate depending on the current node
            tempNode.setxCheck(currentNode.xCheck);
            int compareVal = currentNode.compareTo(tempNode);

            if (compareVal == -1) { // Look at left child

                if (currentNode.leftNode == null) {
                    treeTraversed = true;
                }
                else {
                    currentNode = currentNode.leftNode;
                }
            }
            else { // Look at right child
                if (currentNode.rightNode == null) {
                    treeTraversed = true;
                }
                else {
                    currentNode = currentNode.rightNode;
                }
            }
        }

        return false;
    }

    /**
     * <p>
     * Method to draw all the points in the PointSET to standard draw.
     * </p>
     */
    public void draw() {

        // draw the unit square
        StdDraw.square(0.5, 0.5, 0.5);

        // start drawing nodes from the root node
        drawNode(rootNode);
    }

    /**
     * Method to return all points inside and on the boundary of the rectangle.
     *
     * @param rect the rectamgle
     *
     * @return iterator over the set of points
     */
    public Iterable<Point2D> range(RectHV rect) {

        if (rect == null) {
            throw new IllegalArgumentException(ILLEGAL_ARG_EXCEPTION_MSG);
        }

        return getListOfPointsInRange(rootNode, rect);
    }

    /**
     * <p>
     * Method to return the nearest neighbour in the set to the point p.
     * Return null if the set is empty.
     * </p>
     * @param p the query point
     * @return nearest neighbouring point
     */
    public Point2D nearest(Point2D p) {

        if (p == null) {
            throw new IllegalArgumentException(ILLEGAL_ARG_EXCEPTION_MSG);
        }

        if (rootNode == null) {
            return null;
        }

        return nearestNode(p, rootNode, null).getPoint();
    }

    /**
     * <p>
     * Helper method to recursively call child nodes and collect points in range of the given
     * rectangle.
     * Ignores children that are outside that range of the rectangle to make the search
     * efficient.
     * </p>
     *
     * @param currentNode The current node
     * @param rectangle The rectangle within whose bounds, points are to be searched for
     *
     * @return List of nodes in range of the rectangle
     */
    private List<Point2D> getListOfPointsInRange(Node currentNode, RectHV rectangle) {

        if (currentNode == null) {
            return null;
        }

        List<Point2D> nodesInRange = new ArrayList<>();

        if (rectangle.contains(currentNode.getPoint())) {
            nodesInRange.add(currentNode.point);
        }

        // If node has no children, return
        if (!currentNode.hasChildren) {
            return nodesInRange;
        }

        if (currentNode.xCheck) { // Check along the x dimension

            /*
             * If current node has left children and at least
             * part of the rectangle is on the left of the current node.
             */
            if (currentNode.leftNode != null
                    && currentNode.getPoint().x() >= rectangle.xmin()) {

                /*
                 * Look at the left children. Else the left children of the current node
                 * can be ignored.
                 */
                List<Point2D> pointsInLeftSubtree = getListOfPointsInRange(currentNode.leftNode, rectangle);
                nodesInRange.addAll(pointsInLeftSubtree);
            }

            /*
             * If current node has left children and at least
             * part of the rectangle is on the left of the current node.
             */
            if (currentNode.rightNode != null
                    && currentNode.getPoint().x() < rectangle.xmax()) {

                /*
                 * Look at the right children. Else the left children of the current node
                 * can be ignored.
                 */
                List<Point2D> pointsInRightSubtree = getListOfPointsInRange(currentNode.rightNode, rectangle);
                nodesInRange.addAll(pointsInRightSubtree);
            }


        }
        else { // Check along the y dimension

            /*
             * If current node has left children and at least
             * part of the rectangle is on the left of the current node.
             */
            if (currentNode.leftNode != null
                    && currentNode.getPoint().y() >= rectangle.ymin()) {

                /*
                 * Look at the left children. Else the left children of the current node
                 * can be ignored.
                 */
                List<Point2D> pointsInLeftSubtree = getListOfPointsInRange(currentNode.leftNode, rectangle);
                nodesInRange.addAll(pointsInLeftSubtree);
            }

            /*
             * If current node has left children and at least
             * part of the rectangle is on the left of the current node.
             */
            if (currentNode.rightNode != null
                    && currentNode.getPoint().y() < rectangle.ymax()) {

                /*
                 * Look at the right children. Else the left children of the current node
                 * can be ignored.
                 */
                List<Point2D> pointsInRightSubtree = getListOfPointsInRange(currentNode.rightNode, rectangle);
                nodesInRange.addAll(pointsInRightSubtree);
            }

        }

        return nodesInRange;
    }


    /**
     * <p>
     * Helper method that recursively calls the left and right children (avoiding such calls if possible)
     * to find the nearest node in the 2-d tree to the given point.
     * </p>
     *
     * @param p the given point
     * @param node the current node being looked at
     * @return the nearest node from this current node and it's subtree
     */
    private Node nearestNode(Point2D p, Node node, Node currentClosestNode) {

        double currentClosestDistance;

        if (currentClosestNode == null) {
            currentClosestNode = node;
            currentClosestDistance = Math.sqrt(node.getPoint().distanceSquaredTo(p));
        }
        else {
            currentClosestDistance = Math.sqrt(currentClosestNode.getPoint().distanceSquaredTo(p));
        }

        double distance = Math.sqrt(node.getPoint().distanceSquaredTo(p));

        // If a node is found with a closer distance to the query point p, update the closest node and distance
        if (distance < currentClosestDistance) {
            currentClosestNode = node;
        }

        if (!node.hasChildren) {
            return currentClosestNode;
        }

        // Initialise flags that indicate if search can be continued in left and right children of the current node.
        boolean goToLeftChild = false;
        boolean goToRightChild = false;

        if (node.leftNode != null) {
            goToLeftChild = true;
        }

        if (node.rightNode != null) {
            goToRightChild = true;
        }


        // check done to determine the order in which the child nodes are recursively called
        boolean inLowerRectangle = isPointInLowerRectangle(p, node);

        // If point is in the lower rectangle of the splitting node, look at the left subtree first
        if (inLowerRectangle) {

            if (goToLeftChild) {
                // Before traversing left subtree, check and see if it can be avoided and update flag
                goToLeftChild = checkAndPruneLeftSubtree(p, node, currentClosestNode, goToLeftChild);
            }

            if (goToLeftChild) {
                // Recursively call the left child node and update the current closest node
                currentClosestNode = nearestNode(p, node.leftNode, currentClosestNode);
            }

            if (goToRightChild) {
                // Before traversing right subtree, check and see if it can be avoided and update flag
                goToRightChild = checkAndPruneRightSubtree(p, node, currentClosestNode, goToRightChild);
            }
            if (goToRightChild) {
                // Recursively call the right child node and update the current closest node
                currentClosestNode = nearestNode(p, node.rightNode, currentClosestNode);
            }
        }
        // Point is in the higher rectangle of the splitting node, look at the right subtree first
        else {
            if (goToRightChild) {
                goToRightChild = checkAndPruneRightSubtree(p, node, currentClosestNode, goToRightChild);
            }

            if (goToRightChild) {
                // Recursively call the right child node and update the current closest node
                currentClosestNode = nearestNode(p, node.rightNode, currentClosestNode);
            }

            if (goToLeftChild) {
                goToLeftChild = checkAndPruneLeftSubtree(p, node, currentClosestNode, goToLeftChild);
            }

            if (goToLeftChild) {

                // Recursively call the left child node and update the current closest node
                currentClosestNode = nearestNode(p, node.leftNode, currentClosestNode);

            }

        }
        return currentClosestNode;
    }



    /**
     * <p>
     * Helper method to check if the x value of given point is less
     * than or equal to the x value of the point contained in the given node.
     * </p>
     *
     * @param point the given query point
     * @param node the current node
     * @return true if point is left of current node
     */
    private boolean isPointLeftOfNode(Point2D point, Node node) {

        if (point.x() < node.getPoint().x()) {
            return true;
        }
        return false;
    }


    /**
     * <p>
     * Helper method to check if the y value of given point is less
     * than or equal to the y value of the point contained in the given node.
     * </p>
     *
     * @param point the given query point
     * @param node the current node
     * @return true if point is below current node
     */
    private boolean isPointBelowNode(Point2D point, Node node) {

        if (point.y() < node.getPoint().y()) {
            return true;
        }
        return false;
    }


    /**
     * <p>
     * Helper method to determine if the point is in the lower rectangle
     * of the two rectangles divided by the node.
     * </p>
     *
     * @param point the reference point
     * @param node the given node
     * @return true if point is in the left sub-rectangle
     */
    private boolean isPointInLowerRectangle(Point2D point, Node node) {

        if (node.xCheck) {
            return isPointLeftOfNode(point, node);
        }
        else {
            return isPointBelowNode(point, node);
        }
    }

    /**
     * <p>
     * Helper method to check and flag if traversing the left subtree can be avoided.
     * If the point is to the left of the node and the straight line distance between
     * the point and the node is greater than the current closest distance, then the left
     * subtree need not be visited.
     * </p>
     *
     * @param p the query point
     * @param node the current node being evaluated
     * @param currentClosestNode the current closest node found to the query point
     * @param goToLeftChild the boolean flag to be updated
     * @return false if going to left sub-tree can be avoided. true otherwise.
     */
    private boolean checkAndPruneLeftSubtree(Point2D p, Node node, Node currentClosestNode, boolean goToLeftChild) {

        double currentClosestDist = Math.sqrt(currentClosestNode.getPoint().distanceSquaredTo(p));

        if (node.xCheck) {

            if (p.x() - node.getPoint().x() > currentClosestDist) {
                goToLeftChild = false;
                return goToLeftChild;
            }
        }
        else {
            if ((p.y() - node.getPoint().y()) > currentClosestDist) {
                goToLeftChild = false;
                return goToLeftChild;
            }
        }
        return true;
    }

    /**
     * <p>
     * Helper method to check and flag if traversing the right subtree can be avoided.
     * If the point is to the right of the node and the straight line distance between
     * the point and the node is greater than the current closest distance, then the right
     * subtree need not be visited.
     * </p>
     *
     * @param p the query point
     * @param node the current node being evaluated
     * @param currentClosestNode the current closest node found to the query point
     * @param gotToRightChild the boolean flag to be updated
     * @return false if going to right sub-tree can be avoided. true otherwise.
     */
    private boolean checkAndPruneRightSubtree(Point2D p, Node node, Node currentClosestNode, boolean gotToRightChild) {

        double currentClosestDist = Math.sqrt(currentClosestNode.getPoint().distanceSquaredTo(p));

        if (node.xCheck) {

            if ((node.getPoint().x() - p.x()) > currentClosestDist) {
                gotToRightChild = false;
                return gotToRightChild;

            }
        }
        else {
            if ((node.getPoint().y() - p.y()) > currentClosestDist) {
                gotToRightChild = false;
                return gotToRightChild;
            }
        }
        return true;
    }

    /**
     * <p>
     * Helper method to draw the nodes in the 2-d tree. Recursively calls itself to draw child nodes of each node
     * if they exist.
     * </p>
     *
     * @param currentNode the current node to draw
     */
    private void drawNode(Node currentNode) {

        // draw the point
        Point2D currentPoint  = currentNode.getPoint();
        StdDraw.setPenColor();
        StdDraw.setPenRadius(0.0125);
        StdDraw.point(currentPoint.x(), currentPoint.y());
        StdDraw.setPenRadius();

        // draw the horizontal or vertical line depending on the x or y co-ordinate based splitting of this node
        if (currentNode.xCheck) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(currentPoint.x(), 0, currentPoint.x(), 1);
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(0, currentPoint.y(), 1, currentPoint.y());
        }

        // recursively call this method to draw the left child node if it exists
        if (currentNode.leftNode != null) {
            drawNode(currentNode.leftNode);
        }

        // recursively call this method to draw the right child node if it exists
        if (currentNode.rightNode != null) {
            drawNode(currentNode.rightNode);
        }

    }


    /**
     * <p>
     * Private class to represent each node in a 2-d tree.
     *
     * Each node contains the following information:
     * </p>
     * <ul>
     *     <li>{@link Point2D}</li>
     *     <li>A left {@link Node} child if it exists</li>
     *     <li>A right {@link Node} child if it exists</li>
     *     <li>A xCheck flag - to indicate if comparison at this
     *     node should be based on x-coordinate or y-coordinate</li>
     *     <li>A hasChildren flag to indicate if the node has atleast one child</li>
     * </ul>
     */
    private class Node implements Comparable<Node> {

        private Point2D point;

        private Node leftNode;

        private Node rightNode;

        private boolean xCheck;

        private boolean hasChildren;

        Node(Point2D point) {

            this.point = point;
            hasChildren = false;
            leftNode = null;
            rightNode = null;

        }

        @Override
        public int compareTo(Node node) {

            double difference;

            // if true, compare by x-coordinate, else compare by y-coordinate
            if (this.xCheck) {
                difference = node.getPoint().x() - this.point.x();
            }
            else {
                difference = (node.getPoint().y() - this.point.y());
            }

            // If difference between the coordinates compared is less than or equal to 0, return -1
            // else return 1.
            if (difference < 0) {
                return -1;
            }
            return 1;
        }

        public Point2D getPoint() {
            return point;
        }

        public void setPoint(Point2D point) {
            this.point = point;
        }

        public Node getLeftNode() {
            return leftNode;
        }

        public void setLeftNode(Node leftNode) {
            this.leftNode = leftNode;
        }

        public Node getRightNode() {
            return rightNode;
        }

        public void setRightNode(Node rightNode) {
            this.rightNode = rightNode;
        }

        public boolean getxCheck() {
            return xCheck;
        }

        public void setxCheck(boolean xCheck) {
            this.xCheck = xCheck;
        }

        public boolean getHasChildren() {
            return hasChildren;
        }

        public void setHasChildren(boolean hasChildren) {
            this.hasChildren = hasChildren;
        }
    }


}


