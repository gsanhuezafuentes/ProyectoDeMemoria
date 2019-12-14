package model.epanet.element.networkcomponent;

import model.epanet.element.systemoperation.Pattern;

public final class Junction extends Node {
	private double elev;
	private double demand;
	private Pattern pattern;

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
}
