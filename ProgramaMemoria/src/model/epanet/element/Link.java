package model.epanet.element;

import java.util.ArrayList;
import java.util.List;

public abstract class Link {
	private String id;
	private Node toNode;
	private Node fromNode;
	private List<Point> vertices;

	public Link() {
		this.vertices = new ArrayList<Point>();
	}

	/**
	 * Get vertices that contains this links.
	 * 
	 * @return
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
	 * @return the to
	 */
	public Node getToNode() {
		return toNode;
	}

	/**
	 * @param to the to to set
	 */
	public void setToNode(Node to) {
		this.toNode = to;
	}

	/**
	 * @return the from
	 */
	public Node getFromNode() {
		return fromNode;
	}

	/**
	 * @param from the from to set
	 */
	public void setFromNode(Node from) {
		this.fromNode = from;
	}

	@Override
	public String toString() {
		String text = "id " + id + " from-node " + fromNode.getId() + " to-node " + this.toNode.getId() + "\n";
		if (this.getVertices().size() != 0) {
			text += "Vertices\n";
			for (Point point : this.getVertices()) {
				text += point.toString();
			}
		}
		return text;
	}

}
