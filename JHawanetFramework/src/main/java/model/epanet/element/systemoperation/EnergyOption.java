package model.epanet.element.systemoperation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class EnergyOption {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnergyOption.class);

	public static final double DEFAULT_GLOBAL_EFFICIENCY = 75;
	public static final double DEFAULT_GLOBAL_PRICE = 0;
	public static final String DEFAULT_GLOBAL_PATTERN = "";
	public static final double DEFAULT_DEMAND_CHARGE = 0;

	private double globalEfficiency;
	private double globalPrice;
	@NotNull private String globalPattern;
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
	 * @throws NullPointerException if energy is null
	 */
	public EnergyOption(@NotNull EnergyOption energy) {
		Objects.requireNonNull(energy);
		LOGGER.debug("Clonning EnergyOption.");

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
	public @NotNull String getGlobalPattern() {
		return globalPattern;
	}

	/**
	 * Set the global pattern
	 * 
	 * @param globalPattern the global pattern to set
	 * @throws NullPointerException if globalPattern is null
	 */
	public void setGlobalPattern(@NotNull String globalPattern) {
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
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("globalEfficiency", globalEfficiency);
		map.put("globalPrice", globalPrice);
		map.put("globalPattern", globalPattern);
		map.put("demandCharge", demandCharge);
		return gson.toJson(map);
	}

	/**
	 * Create a copy of the object.
	 * 
	 * @return the copy.
	 */
	public @NotNull EnergyOption copy() {
		return new EnergyOption(this);
	}
}
