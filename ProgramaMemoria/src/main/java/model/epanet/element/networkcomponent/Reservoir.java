package model.epanet.element.networkcomponent;

import model.epanet.element.systemoperation.Pattern;

public class Reservoir extends Node {
	private double head;
	private Pattern pattern;

	/**
	 * @return the head
	 */
	public double getHead() {
		return head;
	}

	/**
	 * @param head the head to set
	 */
	public void setHead(double head) {
		this.head = head;
	}

	/**
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public String toString() {
		String txt = "";
		txt += getId() + "\t";
		txt += getHead() + "\t";
		if (getPattern() != null) {
			txt += getPattern().getId() + "\t";
		}
		return txt;
	}

}
