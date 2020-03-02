package model.epanet.element.networkcomponent;

public final class Emitter {
	private double coefficient;

	public Emitter() {
	}

	/**
	 * Create a emitter with same values that the emitter passed.
	 * 
	 * @param emitter the emitter to copy
	 */
	public Emitter(Emitter emitter) {
		this.coefficient = emitter.coefficient;
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

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10f", getCoefficient()));
		return txt.toString();
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
