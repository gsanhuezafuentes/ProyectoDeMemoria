package model.epanet.element.networkcomponent;

import model.epanet.element.systemoperation.Pattern;

public final class Junction extends Node {
	private double elev;
	private double demand;
	private Pattern pattern;

	public Junction() {
	}

	/**
	 * Create a junction with the same values that the junction received. This copy
	 * is a shallow copy of original, i.e., If the field value is a reference to an
	 * object (e.g., a memory address) it copies the reference. If it is necessary
	 * for the object to be completely independent of the original you must ensure
	 * that you replace the reference to the contained objects.
	 * 
	 * @param junction the junction to copy.
	 */
	public Junction(Junction junction) {
		super(junction);
		this.elev = junction.elev;
		this.demand = junction.demand;
		this.pattern = junction.pattern;
	}

	/**
	 * @return the elev
	 */
	public double getElev() {
		return elev;
	}

	/**
	 * @param elev the elev to set
	 */
	public void setElev(double elev) {
		this.elev = elev;
	}

	/**
	 * @return the demand
	 */
	public double getDemand() {
		return demand;
	}

	/**
	 * @param demand the demand to set
	 */
	public void setDemand(double demand) {
		this.demand = demand;
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
		txt += getElev() + "\t";
		txt += getDemand() + "\t";
		if (getPattern() != null) {
			txt += getPattern().getId();

		}
		return txt;
	}

	/**
	 * Realize a shallow copy of this junction.
	 * 
	 * @return the shallow copy
	 */
	@Override
	public Junction copy() {
		return new Junction(this);

	}

}
