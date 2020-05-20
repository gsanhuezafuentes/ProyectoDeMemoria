package model.epanet.element;

public final class Gama {
	private final double cost;
	private final double diameter;

	public Gama(double diameter, double cost) {
		this.cost = cost;
		this.diameter = diameter;
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * @return the diameter
	 */
	public double getDiameter() {
		return diameter;
	}
	
	@Override
	public String toString() {
		return "Diameter = " + this.diameter + " Cost = " + this.cost;
	}

}
