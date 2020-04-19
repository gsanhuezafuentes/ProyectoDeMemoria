package model.epanet.element.waterquality;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class ReactionOption {
	public static final double DEFAULT_ORDER_BULK = 1;
	public static final double DEFAULT_ORDER_TANK = 1;
	public static final int DEFAULT_ORDER_WALL = 1;
	public static final double DEFAULT_GLOBAL_BULK = 0;
	public static final double DEFAULT_GLOBAL_WALL = 0;
	public static final double DEFAULT_LIMITING_POTENTIAL = 0;
	public static final double DEFAULT_ROUGHNESS_CORRELATION = 0;

	// orderBulk always have the same value that orderTank in inp file
	private double orderBulk; 
	private double orderTank;
	private int orderWall;
	private double globalBulk;
	private double globalWall;
	private double limitingPotential;
	private double roughnessCorrelation;

	public ReactionOption() {
		this.orderBulk = DEFAULT_ORDER_BULK;
		this.orderTank = DEFAULT_ORDER_TANK;
		this.orderWall = DEFAULT_ORDER_WALL;
		this.globalBulk = DEFAULT_GLOBAL_BULK;
		this.globalWall = DEFAULT_GLOBAL_WALL;
		this.limitingPotential = DEFAULT_LIMITING_POTENTIAL;
		this.roughnessCorrelation = DEFAULT_ROUGHNESS_CORRELATION;

	}

	/**
	 * Copy constructor
	 * 
	 * @param reaction the object to copy
	 * @throws NullPointerException if reaction is null
	 */
	public ReactionOption(@NotNull ReactionOption reaction) {
		Objects.requireNonNull(reaction);
		this.orderBulk = reaction.orderBulk;
		this.orderTank = reaction.orderTank;
		this.orderWall = reaction.orderWall;
		this.globalBulk = reaction.globalBulk;
		this.globalWall = reaction.globalWall;
		this.limitingPotential = reaction.limitingPotential;
		this.roughnessCorrelation = reaction.roughnessCorrelation;
	}

	/**
	 * Get the order bulk
	 * 
	 * @return the order bulk
	 */
	public double getOrderBulk() {
		return orderBulk;
	}

	/**
	 * Set the order bulk
	 * 
	 * @param orderBulk the order bulk to set
	 */
	public void setOrderBulk(double orderBulk) {
		this.orderBulk = orderBulk;
	}

	/**
	 * Get the order tank
	 * 
	 * @return the order tank
	 */
	public double getOrderTank() {
		return orderTank;
	}

	/**
	 * Set the order tank
	 * 
	 * @param orderTank the order tank to set
	 */
	public void setOrderTank(double orderTank) {
		this.orderTank = orderTank;
	}

	/**
	 * Get the order wall
	 * 
	 * @return the order wall
	 */
	public int getOrderWall() {
		return orderWall;
	}

	/**
	 * Set the order wall. This method only accept 1 or 0.
	 * 
	 * @param orderWall the orderWall to set
	 * @throws IllegalArgumentException if orderWall is other than 0 or 1.
	 */
	public void setOrderWall(int orderWall) {
		if (!(orderWall == 0 || orderWall == 1)) {
			throw new IllegalArgumentException("The orderWall parameter has to be 0 or 1 and was " + orderWall);
		}
		this.orderWall = orderWall;
	}

	/**
	 * Get the global bulk
	 * 
	 * @return the global bulk
	 */
	public double getGlobalBulk() {
		return globalBulk;
	}

	/**
	 * Set the global bulk
	 * 
	 * @param globalBulk the global bulk to set
	 */
	public void setGlobalBulk(double globalBulk) {
		this.globalBulk = globalBulk;
	}

	/**
	 * Get the global wall
	 * 
	 * @return the global wall
	 */
	public double getGlobalWall() {
		return globalWall;
	}

	/**
	 * Set the globalWall
	 * 
	 * @param globalWall the global wall to set
	 */
	public void setGlobalWall(double globalWall) {
		this.globalWall = globalWall;
	}

	/**
	 * Get the limiting potential
	 * 
	 * @return the limiting potential
	 */
	public double getLimitingPotential() {
		return limitingPotential;
	}

	/**
	 * Set the limiting potential
	 * 
	 * @param limitingPotential the limiting potential to set
	 */
	public void setLimitingPotential(double limitingPotential) {
		this.limitingPotential = limitingPotential;
	}

	/**
	 * Get the roughness correlation
	 * 
	 * @return the roughness correlation
	 */
	public double getRoughnessCorrelation() {
		return roughnessCorrelation;
	}

	/**
	 * Set the roughness correlation
	 * 
	 * @param roughnessCorrelation the roughness correlation to set
	 */
	public void setRoughnessCorrelation(double roughnessCorrelation) {
		this.roughnessCorrelation = roughnessCorrelation;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("orderBulk", orderBulk);
		map.put("orderTank", orderTank);
		map.put("orderWall", orderWall);
		map.put("globalBulk", globalBulk);
		map.put("globalWall", globalWall);
		map.put("limitingPotential", limitingPotential);
		map.put("roughnessCorrelation", roughnessCorrelation);
		return gson.toJson(map);
	}

	/**
	 * Create a copy of this object
	 * 
	 * @return the copy
	 */
	public @NotNull ReactionOption copy() {
		return new ReactionOption(this);
	}
}
