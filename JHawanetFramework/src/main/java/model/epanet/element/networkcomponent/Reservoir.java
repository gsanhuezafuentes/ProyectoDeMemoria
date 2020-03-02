package model.epanet.element.networkcomponent;

import java.util.Objects;

public final class Reservoir extends Node {
	public static final double DEFAULT_TOTAL_HEAD = 0;

	private double totalHead;
	private String headPattern; // Pattern ID

	public Reservoir() {
		totalHead = DEFAULT_TOTAL_HEAD;
		headPattern = "";
	}

	/**
	 * Create a new reservoir copy the reservoir received.This is a deep copy.
	 * 
	 * @param reservoir the reservoir to copy.
	 */
	public Reservoir(Reservoir reservoir) {
		super(reservoir);
		totalHead = reservoir.totalHead;
		headPattern = reservoir.headPattern;
	}

	/**
	 * Get the head value
	 * @return the head
	 */
	public double getTotalHead() {
		return totalHead;
	}

	/**
	 * Set the head value
	 * @param head the head to set
	 */
	public void setTotalHead(double head) {
		this.totalHead = head;
	}

	/**
	 * Get the pattern id or a empty string if it doesn't exist
	 * 
	 * @return the pattern id or a empty string if it doesn't exist
	 */
	public String getHeadPattern() {
		return headPattern;
	}

	/**
	 * Set the pattern id
	 * 
	 * @param pattern the pattern to set
	 * @throws NullPointerException if pattern is null
	 */
	public void setHeadPattern(String pattern) {
		Objects.requireNonNull(pattern);
		this.headPattern = pattern;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getId()));
		txt.append(String.format("%-10f\t", getTotalHead()));
		if (getHeadPattern() != null) {
			txt.append(String.format("%-10s\t", getHeadPattern()));
		}
		String description = getDescription();
		if (description != null) {
			txt.append(String.format(";%s", description));
		}
		return txt.toString();
	}

	/**
	 * Copy this object realizing a shallow copy.
	 */
	@Override
	public Reservoir copy() {
		return new Reservoir(this);
	}

}
