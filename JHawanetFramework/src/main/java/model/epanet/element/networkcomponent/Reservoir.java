package model.epanet.element.networkcomponent;

import model.epanet.element.systemoperation.Pattern;

public final class Reservoir extends Node {
	private double head;
	private Pattern pattern;

	public Reservoir() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a new reservoir copy the reservoir received.This is a shallow copy,
	 * i.e., If the field value is a reference to an object (e.g., a memory address)
	 * it copies the reference. If it is necessary for the object to be completely
	 * independent of the original you must ensure that you replace the reference to
	 * the contained objects.
	 * 
	 * @param reservoir the reservoir to copy.
	 */
	public Reservoir(Reservoir reservoir) {
		super(reservoir);
		head = reservoir.head;
		pattern = reservoir.pattern;
	}

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

	/**
	 * Copy this object realizing a shallow copy.
	 */
	@Override
	public Reservoir copy() {
		return new Reservoir(this);
	}

}
