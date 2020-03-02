package model.epanet.element.networkcomponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.epanet.element.Selectable;
import model.epanet.element.utils.Point;

public abstract class Link extends Component implements Selectable {
	private String id;
	private Node node1;
	private Node node2;
	private List<Point> vertices;

	public Link() {
		this.id = "";
		this.vertices = new ArrayList<Point>();
	}

	/**
	 * Copy constructor. Realize a copy setting the same values that the link
	 * received. This is a shallow copy, i.e., If the field value is a reference to
	 * an object (e.g., a memory address) it copies the reference. If it is
	 * necessary for the object to be completely independent of the original you
	 * must ensure that you replace the reference to the contained objects.
	 * 
	 * You must replace node1 and node2 to do the copy independent of the original
	 * 
	 * @param link the object to copy
	 */
	public Link(Link link) {
		super(link);
		this.vertices = new ArrayList<Point>();
		this.id = link.id;
		this.node1 = link.node1; //shallow copy
		this.node2 = link.node2; //shallow copy
		this.vertices.addAll(link.vertices);
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
	 * Get the id
	 * @return the id or a empty string if it doesn't exist
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id
	 * @param id the id to set
	 * @throws NullPointerException if id is null
	 */
	public void setId(String id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	/**
	 * Get the start node
	 * 
	 * @return the node1
	 */
	public Node getNode1() {
		return node1;
	}

	/**
	 * Set the start node
	 * 
	 * @param node1 the node1 to set
	 * @throws NullPointerException if node1 is null
	 */
	public void setNode1(Node node1) {
		Objects.requireNonNull(node1);
		this.node1 = node1;
	}

	/**
	 * Get the end node
	 * 
	 * @return the node2
	 */
	public Node getNode2() {
		return node2;
	}

	/**
	 * Set the end node
	 * 
	 * @param node2 the node2 to set
	 * @throws NullPointerException if node2 is null
	 */
	public void setNode2(Node node2) {
		Objects.requireNonNull(node2);
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

	/**
	 * Copy this link realizing a shallow copy
	 * 
	 * @return the copy
	 */
	public abstract Link copy();

}
