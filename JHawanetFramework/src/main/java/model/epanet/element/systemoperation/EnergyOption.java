package model.epanet.element.systemoperation;

import java.util.Objects;

public final class EnergyOption {
	public static final double DEFAULT_GLOBAL_EFFICIENCY = 75;
	public static final double DEFAULT_GLOBAL_PRICE = 0;
	public static final String DEFAULT_GLOBAL_PATTERN = "";
	public static final double DEFAULT_DEMAND_CHARGE = 0;

	private double globalEfficiency;
	private double globalPrice;
	private String globalPattern;
	private double demandCharge;

	public EnergyOption() {
		this.globalEfficiency = DEFAULT_GLOBAL_EFFICIENCY;
		this.globalPrice = DEFAULT_GLOBAL_PRICE;
		this.globalPattern = DEFAULT_GLOBAL_PATTERN;
		this.demandCharge = DEFAULT_DEMAND_CHARGE;
	}

	/**
	 * Copy constructor. Realize a copy of the received energy.
	 * 
	 * @param energy the object to copy.
	 */
	public EnergyOption(EnergyOption energy) {
		this.globalEfficiency = energy.globalEfficiency;
		this.globalPrice = energy.globalPrice;
		this.globalPattern = energy.globalPattern;
		this.demandCharge = energy.demandCharge;
	}

	/**
	 * Get the global efficiency
	 * 
	 * @return the global efficiency
	 */
	public double getGlobalEfficiency() {
		return globalEfficiency;
	}

	/**
	 * Set the global efficiency
	 * 
	 * @param globalEfficiency the global efficiency to set
	 */
	public void setGlobalEfficiency(double globalEfficiency) {
		this.globalEfficiency = globalEfficiency;
	}

	/**
	 * Get the global price
	 * 
	 * @return the global price
	 */
	public double getGlobalPrice() {
		return globalPrice;
	}

	/**
	 * Set the global price
	 * 
	 * @param globalPrice the global price to set
	 */
	public void setGlobalPrice(double globalPrice) {
		this.globalPrice = globalPrice;
	}

	/**
	 * Get the global pattern
	 * 
	 * @return the global pattern
	 */
	public String getGlobalPattern() {
		return globalPattern;
	}

	/**
	 * Set the global pattern
	 * 
	 * @param globalPattern the global pattern to set
	 * @throws NullPointerException if globalPattern is null
	 */
	public void setGlobalPattern(String globalPattern) {
		Objects.requireNonNull(globalPattern);
		this.globalPattern = globalPattern;
	}

	/**
	 * Get the demand charge
	 * 
	 * @return the demand charge
	 */
	public double getDemandCharge() {
		return demandCharge;
	}

	/**
	 * Set the demand charge
	 * 
	 * @param demandCharge the demand charge to set
	 */
	public void setDemandCharge(double demandCharge) {
		this.demandCharge = demandCharge;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("Global Efficiency\t%10f\n", this.globalEfficiency));
		txt.append(String.format("Global Price\t%10f\n", this.globalPrice));
		txt.append(String.format("Global Pattern\t%10s\n", this.globalPattern));
		txt.append(String.format("Demand Charge\t%10f\n", this.demandCharge));
		return txt.toString();
	}

	/**
	 * Create a copy of the object.
	 * 
	 * @return the copy.
	 */
	public EnergyOption copy() {
		return new EnergyOption(this);
	}
}
