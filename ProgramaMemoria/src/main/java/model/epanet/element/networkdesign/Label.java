package model.epanet.element.networkdesign;

import model.epanet.element.networkcomponent.Node;
import model.epanet.element.networkcomponent.Point;

public class Label {
	Point point;
	String text;
	Node anchorNode;

	/**
	 * @return the point
	 */
	public Point getPoint() {
		return point;
	}

	/**
	 * @param point the point to set
	 */
	public void setPoint(Point point) {
		this.point = point;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
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

}
