package model.epanet.element.systemoperation;

import java.util.Objects;

public final class Demand {
	private double demand;
	private String demandPattern; // ID to the Pattern
	private String demandCategory;

	public Demand() {
		this.demandPattern = "";
		this.demandCategory = "";
	}

	/**
	 * Create a demand with same values that the demand received. This is a deep
	 * copy.
	 * 
	 * @param demand the demand to copy
	 */
	public Demand(Demand demand) {
		this.demand = demand.demand;
		this.demandPattern = demand.demandPattern;
		this.demandCategory = demand.demandCategory;
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
	 * Get the ID to the {@link Pattern}
	 * @return the demandPatern
	 */
	public String getDemandPattern() {
		return demandPattern;
	}

	/**
	 * Set the ID to the {@link Pattern}
	 * @param demandPattern the demand pattern to set or empty string if it does not exist
	 */
	public void setDemandPattern(String demandPattern) {
		Objects.requireNonNull(demandPattern);
		this.demandPattern = demandPattern;
	}

	/**
	 * @return the demandCategory
	 */
	public String getDemandCategory() {
		return demandCategory;
	}

	/**
	 * @param demandCategory the demandCategory to set or null if it does not exist
	 */
	public void setDemandCategory(String demandCategory) {
		Objects.requireNonNull(demandCategory);
		this.demandCategory = demandCategory;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10f\t", getDemand()));
		if (getDemandPattern() != null) {
			txt.append(String.format("%-10s\t", getDemandPattern()));
		}
		if (getDemandCategory() != null) {
			txt.append(String.format("%-10s\t", getDemandCategory()));
		}
		return txt.toString();
	}

	/**
	 * Copy this instance. This is a shallow copy.
	 * 
	 * @return the copy
	 */
	public Demand copy() {
		return new Demand(this);
	}
}
