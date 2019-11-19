package model.epanet.element.networkdesign;

import model.epanet.element.networkcomponent.Node;
import model.epanet.element.networkcomponent.Point;

public class Label {
	Point position;
	String label;
	Node anchorNode;

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
}
