package model.epanet.element;

public class Gama {
	private double cost;
	private double diameter;

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
		String text = "Diameter = " + this.diameter + " Cost = " + this.cost;
		return text;
	}

}
