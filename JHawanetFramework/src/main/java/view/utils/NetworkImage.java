package view.utils;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.epanet.element.Network;
import model.epanet.element.Selectable;
import model.epanet.element.networkcomponent.Link;
import model.epanet.element.networkcomponent.Node;
import model.epanet.element.networkcomponent.Point;
import model.epanet.element.networkcomponent.Tank;
import model.epanet.element.networkdesign.Label;

/**
 * This class draw on a GraphicsContext the Network.
 *
 */
public class NetworkImage {
	static private Color TANKS_FILL_COLOR = Color.web("0xcccccc");
	static private Color TANKS_STROKE_COLOR = Color.web("0x666666");
	static private int TANK_DIAM = 10;

	static private Color RESERVOIRS_FILL_COLOR = Color.web("0x666666");
	static private Color RESERVOIRS_STROKE_COLOR = Color.web("0xcccccc");
	static private int RESERVOIR_DIAM = 10;

	private static final Color PIPES_FILL_COLOR = Color.web("0x666666");

	private static final Color NODE_STROKE_COLOR = new Color(0, 0, 0, .5f);
	private static final Color NODE_FILL_COLOR = new Color(0xcc / 256f, 0xcc / 256f, 0xcc / 256f, .4f);
	private static final int NODE_DIAM = 2;

	static private Color LABEL_COLOR = Color.rgb(0, 0, 0);

	/**
	 * Get the nearest node to the mouse cursor position.
	 * 
	 * @param w   Canvas width.
	 * @param h   Canvas height.
	 * @param x   Mouse x position.
	 * @param y   Mouse y position.
	 * @param net Epanet network.
	 * @return Reference to the nearest node.
	 */
	public static Node peekNearest(double w, double h, double x, double y, Network net) {
		if (net == null)
			return null;

		Rectangle2D.Double bounds = null;
		for (Node node : net.getNodes()) {
			if (node.getPosition() != null) {
				if (bounds == null)
					bounds = new Rectangle2D.Double((int) node.getPosition().getX(), (int) -node.getPosition().getY(),
							0, 0);
				else
					bounds.add(new java.awt.Point((int) node.getPosition().getX(), (int) -node.getPosition().getY()));
			}
		}
		for (Link link : net.getLinks()) {
			// java.util.List<org.addition.epanetold.Types.Point> vertices =
			// link.getVertices();
			for (Point position : link.getVertices()) {
				if (position != null) {
					if (bounds == null)
						bounds = new Rectangle2D.Double((int) position.getX(), (int) -position.getY(), 0, 0);
					else
						bounds.add(new java.awt.Point((int) position.getX(), (int) -position.getY()));
				}
			}
		}

		double factor = (bounds.width / bounds.height) < (((double) w) / h) ? h / bounds.height : w / bounds.width;

		double dx = bounds.getMinX();
		double dy = bounds.getMinY();
		double dwidth = Math.abs(bounds.getMaxX() - bounds.getMinX());
		double dheight = Math.abs(bounds.getMaxY() - bounds.getMinY());

		factor *= .9d;

		dx += dwidth * .5d - w * 0.5 / factor;
		dy += dheight * .5d - h * 0.5 / factor;

		int range = 5;

		Node nearest = null;
		double distance = 0;
		for (Node n : net.getNodes()) {
			Point point = new Point((int) ((n.getPosition().getX() - dx) * factor),
					(int) ((-n.getPosition().getY() - dy) * factor));
			double dist = Math.sqrt(Math.pow(point.getX() - x, 2) + Math.pow(point.getY() - y, 2));
			if ((dist < range)) {

				nearest = n;
				distance = dist;
			}
		}

		// test
		Link nearestL = null;
		double distanceL = 0;
		for (Link link : net.getLinks()) {
			List<Point> vertices = new ArrayList<Point>(link.getVertices());
			Node node1 = link.getNode1();
			Node node2 = link.getNode2();
			vertices.add(0, node1.getPosition());
			vertices.add(node2.getPosition());
			Point prev = null;
			for (Point position : vertices) {
				Point point = new Point((int) ((position.getX() - dx) * factor),
						(int) ((-position.getY() - dy) * factor));
				if (prev != null) {
					double dist = Math
							.abs((point.getY() - prev.getY()) * x - (point.getX() - prev.getX()) * y
									+ point.getX() * prev.getY() - point.getY() * prev.getX())
							/ Math.sqrt(
									Math.pow(point.getY() - prev.getY(), 2) + Math.pow(point.getX() - prev.getX(), 2));
					if (dist < range && isProjectedPointOnLineSegment(new Point(x,y), prev, point)) {
						nearestL = link;
						distanceL = dist;
					}
				}
				prev = point;

			}
		}
		if (nearest != null) {
			System.out.println("node " + nearest.getId() + " " + distance);
		} else {
			System.out.println("node no selected");
		}
		if (nearestL != null) {
			System.out.println("link " + nearestL.getId() + " " + distanceL);
		} else {
			System.out.println("link no selected");
		}
		return nearest;
	}

	/**
	 * Render the network in the canvas.
	 * 
	 * @param g       Reference to the canvas graphics.
	 * @param w       Canvas width.
	 * @param h       Canvas height.
	 * @param net     Epanet network.
	 * @param selNode Reference to the selected node.
	 */
	public static void drawNetwork(GraphicsContext g, double w, double h, Network net, Selectable selected) {
		Rectangle2D.Double bounds = null;
		boolean drawTanks = true, drawPipes = true, drawNodes = true;

		// Create a rectangle whose size is modified when a new point is added. The size
		// of rectangle is taked from the lower and greatest point added.
		for (Node node : net.getNodes()) {
			if (node.getPosition() != null) {
				if (bounds == null)
					bounds = new Rectangle2D.Double((int) node.getPosition().getX(), (int) -node.getPosition().getY(),
							0, 0);
				else
					bounds.add(new java.awt.Point((int) node.getPosition().getX(), (int) -node.getPosition().getY()));
			}
		}
		for (Link link : net.getLinks()) {
			// java.util.List<org.addition.epanetold.Types.Point> vertices =
			// link.getVertices();
			for (Point position : link.getVertices()) {
				if (position != null) {
					if (bounds == null)
						bounds = new Rectangle2D.Double((int) position.getX(), (int) -position.getY(), 0, 0);
					else
						bounds.add(new java.awt.Point((int) position.getX(), (int) -position.getY()));
				}
			}
		}

//		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// g.setColor(new Color(0x99, 0x99, 0x99));
		// g.drawRect(0, 0, w - 1, h - 1);

		double factor = (bounds.width / bounds.height) < (((double) w) / h) ? h / bounds.height : w / bounds.width;

		double dx = bounds.getMinX();
		double dy = bounds.getMinY();
		double dwidth = Math.abs(bounds.getMaxX() - bounds.getMinX());
		double dheight = Math.abs(bounds.getMaxY() - bounds.getMinY());

		factor *= .9d;

		dx += dwidth * .5d - w * 0.5 / factor;
		dy += dheight * .5d - h * 0.5 / factor;

		// tanks
		if (drawTanks) {
			for (Tank tank : net.getTanks()) {
				if (tank.getDiameter() == 0) {
					// Reservoir
					Point position = tank.getPosition();
					if (position != null) {
						Point point = new Point((int) ((position.getX() - dx) * factor),
								(int) ((-position.getY() - dy) * factor));
						g.setFill(RESERVOIRS_FILL_COLOR);
						g.fillRect((int) (point.getX() - RESERVOIR_DIAM / 2), (int) (point.getY() - RESERVOIR_DIAM / 2),
								RESERVOIR_DIAM, RESERVOIR_DIAM);
						g.setStroke(RESERVOIRS_STROKE_COLOR);
						g.strokeRect((int) (point.getX() - RESERVOIR_DIAM / 2),
								(int) (point.getY() - RESERVOIR_DIAM / 2), RESERVOIR_DIAM, RESERVOIR_DIAM);
					}
				} else {
					// Tank
					Point position = tank.getPosition();
					if (position != null) {
						Point point = new Point((int) ((position.getX() - dx) * factor),
								(int) ((-position.getY() - dy) * factor));
						g.setFill(TANKS_FILL_COLOR);
						g.fillRect((int) (point.getX() - RESERVOIR_DIAM / 2), (int) (point.getY() - RESERVOIR_DIAM / 2),
								TANK_DIAM, TANK_DIAM);
						g.setStroke(TANKS_STROKE_COLOR);
						g.strokeRect((int) (point.getX() - RESERVOIR_DIAM / 2),
								(int) (point.getY() - RESERVOIR_DIAM / 2), TANK_DIAM, TANK_DIAM);
					}
				}
			}
		}

		if (drawPipes) {
			// links
			g.setStroke(PIPES_FILL_COLOR);
			for (Link link : net.getLinks()) {
				List<Point> vertices = new ArrayList<Point>(link.getVertices());
				Node node1 = link.getNode1();
				Node node2 = link.getNode2();
				vertices.add(0, node1.getPosition());
				vertices.add(node2.getPosition());
				Point prev = null;
				for (Point position : vertices) {
					Point point = new Point((int) ((position.getX() - dx) * factor),
							(int) ((-position.getY() - dy) * factor));
					if (prev != null) {
						g.strokeLine((int) prev.getX(), (int) prev.getY(), (int) point.getX(), (int) point.getY());
					}
					prev = point;

				}
			}
		}

		g.setLineWidth(5);
		if (drawNodes) {
			// nodes
			Color nodefillColor = NODE_FILL_COLOR;
			Color nodeStrokeColor = NODE_STROKE_COLOR;
			g.setFill(nodefillColor);
			for (Node node : net.getNodes()) {
				Point position = node.getPosition();
				if (position != null) {
					Point point = new Point((int) ((position.getX() - dx) * factor),
							(int) ((-position.getY() - dy) * factor));
					g.setFill(nodefillColor);
					g.fillOval((int) (point.getX() - NODE_DIAM / 2), (int) (point.getY() - NODE_DIAM / 2), NODE_DIAM,
							NODE_DIAM);
					g.setStroke(nodeStrokeColor);
					g.strokeOval((int) (point.getX() - NODE_DIAM / 2), (int) (point.getY() - NODE_DIAM / 2), NODE_DIAM,
							NODE_DIAM);
				}
			}
		}
		g.setLineWidth(1);

		for (Label l : net.getLabels()) {
			Point point = new Point((int) ((l.getPosition().getX() - dx) * factor),
					(int) ((-l.getPosition().getY() - dy) * factor));
			g.setFill(LABEL_COLOR);
			g.fillText(l.getLabel(), (int) point.getX(), (int) point.getY());
		}

		
		if (selected != null) {
			Node selNode = (Node) selected;
			Point point = new Point((int) ((selNode.getPosition().getX() - dx) * factor),
					(int) ((-selNode.getPosition().getY() - dy) * factor));

			g.setStroke(Color.web("0xFF0000"));
			g.strokeOval((int) (point.getX() - 20 / 2), (int) (point.getY() - 20 / 2), 20, 20);

			g.setFill(LABEL_COLOR);
			g.fillText(selNode.getId(), (int) point.getX() + 20, (int) point.getY() + 20);
		}
	}

	/**
	 * 
	 * @param point the point to project on segment line
	 * @param x1    the start point of segment line
	 * @param x2    the last point of segment line
	 * @return
	 */
	private static boolean isProjectedPointOnLineSegment(Point point, Point x1, Point x2) {
		Point v1 = new Point(x2.getX() - x1.getX(), x2.getY() - x1.getY());
		/**
		 * Get the max value of product point that is obtained when the proyected point is the same as x2.
		 */
		double dotProductV1 = dotProduct(v1, v1);
		
		Point projectedPoint = getProjectedPointOnLine(point, x1, x2);
		Point v2 = new Point(projectedPoint.getX() - x1.getX(), projectedPoint.getY() - x1.getY());
		double dotProductV1V2 = dotProduct(v1, v2);
	
		return (dotProductV1V2 > 0 && dotProductV1V2 < dotProductV1);
	}

	/**
	 * Get the projected point on line
	 * 
	 * @param point the point to project on segment line
	 * @param x1    the start point of segment line
	 * @param x2    the last point of segment line
	 * @return the projected point
	 */
	private static Point getProjectedPointOnLine(Point point, Point x1, Point x2) {
		Point v1 = new Point(x2.getX() - x1.getX(), x2.getY() - x1.getY());
		Point v2 = new Point(point.getX() - x1.getX(), point.getY() - x1.getY());
		double dotValue = dotProduct(v1, v2);
		//square length of vector v1
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
}
