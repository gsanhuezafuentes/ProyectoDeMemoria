package model.epanet.element.networkcomponent;

public final class Emitter {
	private String junctionID;
	private double coefficient;

	public Emitter() {
	}

	/**
	 * Create a emitter with same values that the emitter passed.
	 * 
	 * @param emitter the emitter to copy
	 */
	public Emitter(Emitter emitter) {
		this.junctionID = emitter.junctionID;
		this.coefficient = emitter.coefficient;
	}

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

	/**
	 * Create a copy of this emitter.
	 * 
	 * @return the copy
	 */
	public Emitter copy() {
		return new Emitter(this);
	}
}
