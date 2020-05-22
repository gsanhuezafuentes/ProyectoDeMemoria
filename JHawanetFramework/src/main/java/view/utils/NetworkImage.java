package view.utils;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import model.epanet.element.Network;
import model.epanet.element.networkcomponent.*;
import model.epanet.element.networkdesign.Label;
import model.epanet.element.utils.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * This class draw on a GraphicsContext the Network.
 */
public class NetworkImage {
    static private final Color TANKS_FILL_COLOR = Color.web("0xcccccc");
    static private final Color TANKS_STROKE_COLOR = Color.web("0x666666");
    @SuppressWarnings("FieldCanBeLocal")
    static private final int TANK_DIAM = 10;

    static private final Color RESERVOIRS_FILL_COLOR = Color.web("0x666666");
    static private final Color RESERVOIRS_STROKE_COLOR = Color.web("0xcccccc");
    @SuppressWarnings("FieldCanBeLocal")
    static private final int RESERVOIR_DIAM = 10;

    private static final Color PIPES_FILL_COLOR = Color.web("0x666666");

    private static final Color NODE_STROKE_COLOR = new Color(0, 0, 0, .5f);
    private static final Color NODE_FILL_COLOR = new Color(0xcc / 256f, 0xcc / 256f, 0xcc / 256f, .4f);
    private static final int NODE_DIAM = 2;

    private static final int SELECTION_RANGE = 10;

    static private final Color LABEL_COLOR = Color.rgb(0, 0, 0);

    /**
     * Get the nearest node or link to the mouse cursor position.
     *
     * @param w   Canvas width.
     * @param h   Canvas height.
     * @param x   Mouse x position.
     * @param y   Mouse y position.
     * @param net Epanet network.
     * @return reference to the nearest element or null if there isn't a selected.
     */
    public static Object peekNearest(double w, double h, double x, double y, Network net) {
        if (net == null)
            return null;

        Rectangle2D.Double bounds = getBounds(net);
        Function<Point, Point> epanetToCanvasCoordinates = epanetToCanvasCoordinatesFunction(w, h, bounds);

        Node nearest = null;
        for (Node n : net.getNodes()) {
            if (n.getPosition() == null) continue;
            Point point = epanetToCanvasCoordinates.apply(n.getPosition());
            double dist = Math.sqrt(Math.pow(point.getX() - x, 2) + Math.pow(point.getY() - y, 2));
            if ((dist < SELECTION_RANGE)) {
                nearest = n;
            }
        }

        // test
        Link nearestL = null;
        for (Link link : net.getLinks()) {
            List<Point> vertices = new ArrayList<>(link.getVertices());
            Node node1 = link.getNode1();
            Node node2 = link.getNode2();
            if (node1 == null || node1.getPosition() == null) continue;
            if (node2 == null || node2.getPosition() == null) continue;

            vertices.add(0, node1.getPosition());
            vertices.add(node2.getPosition());
            Point prev = null;
            for (Point position : vertices) {
                Point point = epanetToCanvasCoordinates.apply(position);
                // if the point isn't the first
                if (prev != null) {
                    /*
                     * Calculate the distance of a point to a line defined by two points
                     * https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line (Line defined by two points section)
                     */
                    double dist = Math.abs(
                            (point.getY() - prev.getY()) * x
                                    - (point.getX() - prev.getX()) * y
                                    + point.getX() * prev.getY()
                                    - point.getY() * prev.getX())
                            / Math.sqrt(
                            Math.pow(point.getY() - prev.getY(), 2) + Math.pow(point.getX() - prev.getX(), 2));
                    if (dist < SELECTION_RANGE && isProjectedPointOnLineSegment(new Point(x, y), prev, point)) {
                        nearestL = link;
                    }
                }
                prev = point;

            }
        }

        // if there is a node close return it
        if (nearest != null) {
            return nearest;
        }

        // if there is a link close return it
        if (nearestL != null) {
            return nearestL;
        }

        return null;
    }

    /**
     * Render the network in the canvas and draw a marker on selected element.<br>
     * <br>
     * <p>
     * The selected object can be a element that is not showed in the canvas, i.e.,
     * is not a node or link, so it method does not mark the selected item.
     *
     * @param g        Reference to the canvas graphics.
     * @param w        Canvas width.
     * @param h        Canvas height.
     * @param net      Epanet network.
     * @param selected Reference to the selected element of the network. null if
     *                 there isn't a selected item.
     */
    public static void drawNetwork(GraphicsContext g, double w, double h, Network net, Object selected) {
        Rectangle2D.Double bounds = getBounds(net);
        Function<Point, Point> epanetToCanvasCoordinates = epanetToCanvasCoordinatesFunction(w, h, bounds);

        // Draw tanks
        for (Tank tank : net.getTanks()) {
            // Tank
            Point position = tank.getPosition();
            if (position != null) {
                Point point = epanetToCanvasCoordinates.apply(position);
                g.setFill(TANKS_FILL_COLOR);
                g.fillRect((int) (point.getX() - RESERVOIR_DIAM / 2), (int) (point.getY() - RESERVOIR_DIAM / 2),
                        TANK_DIAM, TANK_DIAM);
                g.setStroke(TANKS_STROKE_COLOR);
                g.strokeRect((int) (point.getX() - RESERVOIR_DIAM / 2), (int) (point.getY() - RESERVOIR_DIAM / 2),
                        TANK_DIAM, TANK_DIAM);
            }
        }

        // Draw reservoir
        for (Reservoir reservoir : net.getReservoirs()) {
            Point position = reservoir.getPosition();
            if (position != null) {
                Point point = epanetToCanvasCoordinates.apply(position);
                g.setFill(RESERVOIRS_FILL_COLOR);
                g.fillRect((int) (point.getX() - RESERVOIR_DIAM / 2), (int) (point.getY() - RESERVOIR_DIAM / 2),
                        RESERVOIR_DIAM, RESERVOIR_DIAM);
                g.setStroke(RESERVOIRS_STROKE_COLOR);
                g.strokeRect((int) (point.getX() - RESERVOIR_DIAM / 2), (int) (point.getY() - RESERVOIR_DIAM / 2),
                        RESERVOIR_DIAM, RESERVOIR_DIAM);
            }
        }

        // Draw Links
        g.setStroke(PIPES_FILL_COLOR);
        for (Link link : net.getLinks()) {
            List<Point> vertices = new ArrayList<>(link.getVertices());
            Node node1 = link.getNode1();
            Node node2 = link.getNode2();
            if (node1 == null || node1.getPosition() == null) continue;
            if (node2 == null || node2.getPosition() == null) continue;

            vertices.add(0, node1.getPosition());
            vertices.add(node2.getPosition());
            Point prev = null;
            for (Point position : vertices) {
                Point point = epanetToCanvasCoordinates.apply(position);
                if (prev != null) {
                    g.strokeLine((int) prev.getX(), (int) prev.getY(), (int) point.getX(), (int) point.getY());
                }
                prev = point;
            }
            drawSymbol(link, vertices, g, epanetToCanvasCoordinates);
        }

        g.setLineWidth(5);

        // Draw junction
        Color nodefillColor = NODE_FILL_COLOR;
        Color nodeStrokeColor = NODE_STROKE_COLOR;
        g.setFill(nodefillColor);
        for (Junction junction : net.getJunctions()) {
            Point position = junction.getPosition();
            if (position != null) {
                Point point = epanetToCanvasCoordinates.apply(position);
                g.setFill(nodefillColor);
                g.fillOval((int) (point.getX() - NODE_DIAM / 2), (int) (point.getY() - NODE_DIAM / 2), NODE_DIAM,
                        NODE_DIAM);
                g.setStroke(nodeStrokeColor);
                g.strokeOval((int) (point.getX() - NODE_DIAM / 2), (int) (point.getY() - NODE_DIAM / 2), NODE_DIAM,
                        NODE_DIAM);
            }
        }
        g.setLineWidth(1);

        // Draw label
        for (Label l : net.getLabels()) {
            Point point = epanetToCanvasCoordinates.apply(l.getPosition());
            g.setFill(LABEL_COLOR);
            g.fillText(l.getLabel(), (int) point.getX(), (int) point.getY());
        }

        // Draw selected node
        if (selected != null) {
            if (selected instanceof Node) {
                Node selNode = (Node) selected;
                Point point = epanetToCanvasCoordinates.apply(selNode.getPosition());

                g.setStroke(Color.web("0xFF0000"));
                g.strokeOval((int) (point.getX() - 20 / 2), (int) (point.getY() - 20 / 2), 20, 20);

                g.setFill(LABEL_COLOR);
                g.fillText(selNode.getId(), (int) point.getX() + 20, (int) point.getY() + 20);
            } else if (selected instanceof Link) {// is a link
                Link selLink = (Link) selected;
                List<Point> vertices = new ArrayList<>(selLink.getVertices());
                Node node1 = selLink.getNode1();
                Node node2 = selLink.getNode2();
                if (node1 == null || node1.getPosition() == null) return;
                if (node2 == null || node2.getPosition() == null) return;

                vertices.add(0, node1.getPosition());
                vertices.add(node2.getPosition());
                // Get the midpoint link
                PointWithDegree wrapperPoint = calculateMidPointOfTheLinkPath(vertices, epanetToCanvasCoordinates);
                if (wrapperPoint != null) {
                    Point point = wrapperPoint.point;
                    g.setStroke(Color.web("0xFF0000"));
                    g.strokeOval((int) (point.getX() - 20 / 2), (int) (point.getY() - 20 / 2), 20, 20);

                    g.setFill(LABEL_COLOR);
                    g.fillText(selLink.getId(), (int) point.getX() + 20, (int) point.getY() + 20);
                }
            }
        }
    }

    /**
     * Draw the symbol for links in canvas
     * @param link the link
     * @param vertices the vertices
     * @param gc the graphics context of the canvas
     * @param epanetToCanvasCoordinates the function to map epanet coordinates to canvas coordinates
     * @throws NullPointerException if link or vertices is null
     */
    private static void drawSymbol(Link link, List<Point> vertices, GraphicsContext gc,Function<Point, Point> epanetToCanvasCoordinates) {
        Objects.requireNonNull(link);
        Objects.requireNonNull(vertices);
        PointWithDegree pointWithDegree = calculateMidPointOfTheLinkPath(vertices, epanetToCanvasCoordinates);
        Point point = pointWithDegree.point;
        double degree = pointWithDegree.angle;

        // apply the transform to the next draw
        if (link instanceof Pump) {
            degree = degree > 0 ? degree : 180-Math.abs(degree);
            Rotate r = new Rotate(degree, point.getX(), point.getY()-5);
            gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

            gc.fillOval(point.getX(),point.getY()-5,10, 10);
            gc.fillRect(point.getX()-3,point.getY()-15,10,15);

        }else if (link instanceof Valve){
            double[] x = {point.getX(), point.getX()-10, point.getX()+10, point.getX()-10, point.getX()+10, point.getX()};
            double[] y = {point.getY(), point.getY()-10, point.getY()-10, point.getY()+10, point.getY()+10, point.getY()};

            Rotate r = new Rotate(degree, point.getX(), point.getY());
            gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());

            gc.fillPolygon(x,y,x.length);
        }
        // remove the transform
        gc.setTransform(new Affine());
    }

    /**
     * Create a function that moves the point of space of epanet coordinates to canvas coordinates.
     *
     * @param w      Canvas width.
     * @param h      Canvas height.
     * @param bounds the boundaries containing epanet's coordinates.
     * @return a function that transforms points from one space to another
     */
    private static Function<Point, Point> epanetToCanvasCoordinatesFunction(double w, double h, Rectangle2D.Double bounds) {
        double factor = (bounds.width / bounds.height) < (w / h) ? h / bounds.height : w / bounds.width;

        double dx = bounds.getMinX();
        double dy = bounds.getMinY();
        double dwidth = Math.abs(bounds.getMaxX() - bounds.getMinX());
        double dheight = Math.abs(bounds.getMaxY() - bounds.getMinY());

        factor *= .9d;

        dx += dwidth * .5d - w * 0.5 / factor;
        dy += dheight * .5d - h * 0.5 / factor;

        // variables needed to create the function (effective final)
        double factorr = factor;
        double dxx = dx;
        double dyy = dy;
        Function<Point, Point> function = point -> new Point((int) ((point.getX() - dxx) * factorr), (int) ((-point.getY() - dyy) * factorr));
        return function;
    }


    /**
     * Calculate the mid point  of the line path of link and the angle between the point used to calculate the mid point.
     * This calculation include the initial point, the final point and the vertices of a link
     *
     * @param vertices the vertices of a link in epanet coordinates
     * @param epanetToCanvasCoordinates a function that map epanet to canvas coordinates
     * @return the middle point (in canvas coordinates) or null if there are not enough points to calculate the midpoint
     * @throws NullPointerException if link is null
     */
    private static @Nullable PointWithDegree calculateMidPointOfTheLinkPath(List<Point> vertices, Function<Point, Point> epanetToCanvasCoordinates) {
        Objects.requireNonNull(vertices);
        // To calculate the midpoint has to have two point
        if (vertices.size() < 2) return null;

        // the middle point between two vertices
        int midIndex = vertices.size() / 2;

        double x1 = vertices.get(midIndex).getX();
        double y1 = vertices.get(midIndex).getY();
        double x2 = vertices.get(midIndex - 1).getX();
        double y2 = vertices.get(midIndex - 1).getY();
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;
        Point midPoint = new Point(midX, midY);
        Point point = epanetToCanvasCoordinates.apply(midPoint);
        /*
           The angle is get of the form of the draw in this comment
            |    /
            | a /
            |  /
            | /
            |/
         */
        return new PointWithDegree(point, Math.toDegrees(Math.atan((x2-x1)/(y2-y1))));
    }

    /**
     * Calculate the bounds. The bounds are the lower and greatest point off all
     * component.
     *
     * @param net the network
     * @return the bounds
     */
    private static Rectangle2D.Double getBounds(Network net) {
        Rectangle2D.Double bounds = null;

        /*
         * Create a rectangle whose size is modified when a new point is added. The size
         * of rectangle is taked from the lower and greatest point added.
         */
        for (Node node : net.getNodes()) {
            if (node.getPosition() != null) {
                if (bounds == null)
                    // the - is because the coordinates in epanet are different of javafx canvas
                    bounds = new Rectangle2D.Double((int) node.getPosition().getX(), (int) -node.getPosition().getY(),
                            0, 0);
                else
                    bounds.add(new java.awt.Point((int) node.getPosition().getX(), (int) -node.getPosition().getY()));
            }
        }

        for (Link link : net.getLinks()) {
            for (Point position : link.getVertices()) {
                if (position != null) {
                    if (bounds == null)
                        bounds = new Rectangle2D.Double((int) position.getX(), (int) -position.getY(), 0, 0);
                    else
                        bounds.add(new java.awt.Point((int) position.getX(), (int) -position.getY()));
                }
            }
        }
        return bounds;
    }

    /**
     * Get if the point project on the line from x1 to x2.
     *
     * @param point the point to project on segment line
     * @param x1    the start point of segment line
     * @param x2    the last point of segment line
     * @return true if the point is proyected to segment formed by x1 to x2
     * @see <a href="http://www.sunshine2k.de/coding/java/PointOnLine/PointOnLine.html">Project point on a line</a>
     */
    private static boolean isProjectedPointOnLineSegment(Point point, Point x1, Point x2) {
        Point v1 = new Point(x2.getX() - x1.getX(), x2.getY() - x1.getY());
        /*
         * Get the max value of product point that is obtained when the proyected point
         * is the same as x2.
         */
        double dotProductV1 = dotProduct(v1, v1);

        Point projectedPoint = getProjectedPointOnLine(point, x1, x2);
        Point v2 = new Point(projectedPoint.getX() - x1.getX(), projectedPoint.getY() - x1.getY());
        double dotProductV1V2 = dotProduct(v1, v2);

        return (dotProductV1V2 > 0 && dotProductV1V2 < dotProductV1);
    }

    /**
     * Get the projected point on line.
     *
     * @param point the point to project on segment line
     * @param x1    the start point of segment line
     * @param x2    the last point of segment line
     * @return the projected point
     * @see <a href="http://www.sunshine2k.de/coding/java/PointOnLine/PointOnLine.html">Project point on a line</a>
     */
    private static Point getProjectedPointOnLine(Point point, Point x1, Point x2) {
        Point v1 = new Point(x2.getX() - x1.getX(), x2.getY() - x1.getY());
        Point v2 = new Point(point.getX() - x1.getX(), point.getY() - x1.getY());
        double dotValue = dotProduct(v1, v2);
        // square length of vector v1
        double length = v1.getX() * v1.getX() + v1.getY() * v1.getY();
        return new Point(x1.getX() + (dotValue * v1.getX()) / length, x1.getY() + (dotValue * v1.getY()) / length);
    }

    /**
     * Calculates the dot product using v1.x * v2.x + v1.y * v2.y
     *
     * @param v1 vector v1
     * @param v2 vector v2
     * @return the dot product
     * @throws NullPointerException if v1 or v2 is null
     */
    private static double dotProduct(Point v1, Point v2) {
        Objects.requireNonNull(v1);
        Objects.requireNonNull(v2);
        return v1.getX() * v2.getX() + v1.getY() * v2.getY();
    }

    /**
     * A class that wrapper the {@link Point} and add a angle paramater in degree
     */
    private static class PointWithDegree{
        public final Point point;
        public final double angle;

        /**
         * Constructor
         * @param point the point to wrap
         * @param angle angle
         * @throws NullPointerException if point is null
         */
        PointWithDegree(Point point, double angle){
            Objects.requireNonNull(point);
            this.point = point;
            this.angle = angle;
        }
    }
}
