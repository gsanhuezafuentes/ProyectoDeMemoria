package model.epanet.element.networkcomponent;

import java.util.ArrayList;
import java.util.List;

public abstract class Link {
	private String id;
	private Node node1;
	private Node node2;
	private List<Point> vertices;

	public Link() {
		this.vertices = new ArrayList<Point>();
	}

	/**
	 * Get vertices that contains this links.
	 * 
	 * @return a list with the vertices of the link
	 */
	public final List<Point> getVertices() {
		return vertices;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the node1
	 */
	public Node getNode1() {
		return node1;
	}

	/**
	 * @param node1 the node1 to set
	 */
	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	/**
	 * @return the node2
	 */
	public Node getNode2() {
		return node2;
	}

	/**
	 * @param node2 the node2 to set
	 */
	public void setNode2(Node node2) {
		this.node2 = node2;
	}

	@Override
	public String toString() {
		String text = "id " + id + " from-node " + node1.getId() + " to-node " + this.node2.getId() + "\n";
		if (this.getVertices().size() != 0) {
			text += "Vertices\n";
			for (Point point : this.getVertices()) {
				text += point.toString();
			}
		}
		return text;
	}

}
