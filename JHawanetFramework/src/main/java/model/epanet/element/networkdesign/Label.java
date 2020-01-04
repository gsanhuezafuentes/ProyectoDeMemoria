package model.epanet.element.networkdesign;

import model.epanet.element.networkcomponent.Node;
import model.epanet.element.networkcomponent.Point;

public final class Label {
	Point position;
	String label;
	Node anchorNode;

	public Label() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Copy constructor. This is a shallow copy, i.e., If the field value is a
	 * reference to an object (e.g., a memory address) it copies the reference. If
	 * it is necessary for the object to be completely independent of the original
	 * you must ensure that you replace the reference to the contained objects.
	 * 
	 * @param label the object to copy
	 */
	public Label(Label label) {
		this.position = label.position;
		this.label = label.label;
		this.anchorNode = label.anchorNode;
	}

	/**
	 * @return the point
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * @param point the point to set
	 */
	public void setPosition(Point point) {
		this.position = point;
	}

	/**
	 * @return the text
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param text the text to set
	 */
	public void setLabel(String text) {
		this.label = text;
	}

	/**
	 * @return the anchorNode
	 */
	public Node getAnchorNode() {
		return anchorNode;
	}

	/**
	 * @param anchorNode the anchorNode to set
	 */
	public void setAnchorNode(Node anchorNode) {
		this.anchorNode = anchorNode;
	}

	@Override
	public String toString() {
		String txt = "";
		txt += getPosition() + "\t";
		txt += getLabel() + "\t";
		if (getAnchorNode() != null) {
			txt += getAnchorNode().getId();
		}

		return txt;
	}

	/**
	 * Copy this object. This is a shallow copy.
	 * 
	 * @return the copy.
	 */
	public Label copy() {
		return new Label(this);
	}
}
