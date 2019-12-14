package model.epanet.element.networkcomponent;

public final class Emitter {
	private String junctionID;
	private double coefficient;

	/**
	 * @return the junctionID
	 */
	public String getJunctionID() {
		return junctionID;
	}

	/**
	 * @param junctionID the junctionID to set
	 */
	public void setJunctionID(String junctionID) {
		this.junctionID = junctionID;
	}

	/**
	 * @return the coefficient
	 */
	public double getCoefficient() {
		return coefficient;
	}

	/**
	 * @param coefficient the coefficient to set
	 */
	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}

}
