package model.epanet.element.networkdesign;

import java.util.Objects;

import model.epanet.element.utils.Point;

public final class Label {
	private Point position;
	private String label;
	private String anchorNode; // This is the ID of a Node (Junction or Reservoir or Tank)

	public Label() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Copy constructor. This is a deep copy.
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
	 * @throws NullPointerException if text is null
	 */
	public void setLabel(String text) {
		Objects.requireNonNull(text);
		this.label = text;
	}

	/**
	 * Get the anchor Node id
	 * 
	 * @return the anchorNode
	 */
	public String getAnchorNode() {
		return anchorNode;
	}

	/**
	 * Set the anchor node id
	 * 
	 * @param anchorNode the anchorNode to set
	 * @throws NullPointerException if anchorNode is null
	 */
	public void setAnchorNode(String anchorNode) {
		Objects.requireNonNull(anchorNode);
		this.anchorNode = anchorNode;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%s\t", getPosition()));
		txt.append(String.format("%-10s\t", getLabel()));
		if (getAnchorNode() != null) {
			txt.append(String.format("%-10s", getAnchorNode()));
		}

		return txt.toString();
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
