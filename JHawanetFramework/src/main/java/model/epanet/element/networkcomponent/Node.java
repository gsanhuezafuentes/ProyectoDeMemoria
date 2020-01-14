package model.epanet.element.networkcomponent;

import model.epanet.element.Selectable;

public abstract class Node implements Selectable{
	private String id;
	private Point position;

	public Node() {
	}

	/**
	 * Copy constructor. This is a shallow copy, i.e., If the field value is a
	 * reference to an object (e.g., a memory address) it copies the reference. If
	 * it is necessary for the object to be completely independent of the original
	 * you must ensure that you replace the reference to the contained objects.
	 * 
	 * @param node
	 */
	public Node(Node node) {
		this.id = node.id;
		this.position = node.position;
	}

	public final Point getPosition() {
		return position;
	}

	public final void setPosition(Point position) {
		this.position = position;
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

	@Override
	public String toString() {
		String text = "id " + id + " " + position.toString();
		return text;
	}

	/**
	 * Copy this node. This is a shallow copy.
	 * 
	 * @return the copy
	 */
	public abstract Node copy();

}
