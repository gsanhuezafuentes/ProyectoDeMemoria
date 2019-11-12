package model.epanet.element.networkcomponent;

import model.epanet.element.systemoperation.Pattern;

public class Reservoir extends Node{
	private int head;
	private Pattern pattern;
	/**
	 * @return the head
	 */
	public int getHead() {
		return head;
	}
	/**
	 * @param head the head to set
	 */
	public void setHead(int head) {
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

	
	
}
