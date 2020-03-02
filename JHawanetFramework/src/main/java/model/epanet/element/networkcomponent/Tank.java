package model.epanet.element.networkcomponent;

import java.util.Objects;

import model.epanet.element.systemoperation.Curve;
import model.epanet.element.waterquality.Mixing;

/**
 * This class represent a tank.
 *
 */
public final class Tank extends Node {
	public static final double DEFAULT_ELEVATION = 0;
	public static final double DEFAULT_INITIAL_LEVEL = 10;
	public static final double DEFAULT_MINIMUM_LEVEL = 0;
	public static final double DEFAULT_MAXIMUM_LEVEL = 20;
	public static final double DEFAULT_DIAMETER = 50;
	public static final double DEFAULT_MINIMUM_VOLUME = 0;

	private double elevation;
	private double initialLevel;
	private double minimumLevel;
	private double maximumLevel;
	private double diameter;
	private double minimumVolume;
	private String volumeCurve; // A id to the Curve
	private Mixing mixing;
	/*
	 * This parameter is save [Reaction] section with the label TANK
	 */
	private Double reactionCoefficient;

	public Tank() {
		this.elevation = DEFAULT_ELEVATION;
		this.initialLevel = DEFAULT_INITIAL_LEVEL;
		this.minimumLevel = DEFAULT_MINIMUM_LEVEL;
		this.maximumLevel = DEFAULT_MAXIMUM_LEVEL;
		this.diameter = DEFAULT_DIAMETER;
		this.minimumVolume = DEFAULT_MINIMUM_VOLUME;
		this.volumeCurve = "";
		this.mixing = new Mixing(); // This by default set the model has mixing
	}

	/**
	 * Create a new tank copy the values of tank received. This is a deep copy.
	 * 
	 * @param tank the tank to copy
	 */
	public Tank(Tank tank) {
		super(tank);
		this.elevation = tank.elevation;
		this.initialLevel = tank.initialLevel;
		this.minimumLevel = tank.minimumLevel;
		this.maximumLevel = tank.maximumLevel;
		this.diameter = tank.diameter;
		this.minimumVolume = tank.minimumVolume;
		this.volumeCurve = tank.volumeCurve;
		this.mixing = tank.mixing.copy();
		this.reactionCoefficient = tank.reactionCoefficient;

	}

	/**
	 * Get the elevation
	 * 
	 * @return the elevation
	 */
	public double getElevation() {
		return elevation;
	}

	/**
	 * Set the elevation
	 * 
	 * @param elev the elevation to set
	 */
	public void setElevation(double elev) {
		this.elevation = elev;
	}

	/**
	 * Get the initial level
	 * 
	 * @return the initLevel
	 */
	public double getInitialLevel() {
		return initialLevel;
	}

	/**
	 * Set the initial level
	 * 
	 * @param initLvl the initial level to set
	 */
	public void setInitialLevel(double initLvl) {
		this.initialLevel = initLvl;
	}

	/**
	 * Get the minimum level
	 * 
	 * @return the minLevel
	 */
	public double getMinimumLevel() {
		return this.minimumLevel;
	}

	/**
	 * Set the minimum level
	 * 
	 * @param minLvl the minLevel to set
	 */
	public void setMinimumLevel(double minLvl) {
		this.minimumLevel = minLvl;
	}

	/**
	 * Get the maximum level
	 * 
	 * @return the maxLevel
	 */
	public double getMaximumLevel() {
		return maximumLevel;
	}

	/**
	 * Set the maximum level
	 * 
	 * @param maxLvl the maxLevel to set
	 */
	public void setMaximumLevel(double maxLvl) {
		this.maximumLevel = maxLvl;
	}

	/**
	 * Set the diameter
	 * 
	 * @return the diameter
	 */
	public double getDiameter() {
		return diameter;
	}

	/**
	 * Get the diameter
	 * 
	 * @param diam the diam to set
	 */
	public void setDiameter(double diam) {
		this.diameter = diam;
	}

	/**
	 * Get the minimum volume
	 * 
	 * @return the minVol
	 */
	public double getMinimumVolume() {
		return minimumVolume;
	}

	/**
	 * Set the minimum volume
	 * 
	 * @param minVol the minVol to set
	 */
	public void setMinimumVolume(double minVol) {
		this.minimumVolume = minVol;
	}

	/**
	 * Get the curve id. The id of {@link Curve} or a empty string if it doesn't exist.
	 * 
	 * @return the volCurve or a empty string if it doesn't exist
	 */
	public String getVolumeCurve() {
		return volumeCurve;
	}

	/**
	 * Set the curve id. The id of {@link Curve}.
	 * 
	 * @param volCurve the volCurve to set
	 * @throws if volCurve is null
	 */
	public void setVolumeCurve(String volCurve) {
		Objects.requireNonNull(volCurve);
		this.volumeCurve = volCurve;
	}

	/**
	 * Get the mixing
	 * 
	 * @return the mixing
	 */
	public Mixing getMixing() {
		return mixing;
	}

	/**
	 * Get the reaction coefficient
	 * 
	 * @return the reactionCoefficient or null if does not exist
	 */
	public Double getReactionCoefficient() {
		return reactionCoefficient;
	}

	/**
	 * Set the reaction coefficient
	 * 
	 * @param reactionCoefficient the reactionCoefficient to set or null if there
	 *                            isn't a reaction value
	 */
	public void setReactionCoefficient(Double reactionCoefficient) {
		this.reactionCoefficient = reactionCoefficient;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getId()));
		txt.append(String.format("%-10f\t", getElevation()));
		txt.append(String.format("%-10f\t", getInitialLevel()));
		txt.append(String.format("%-10f\t", getMinimumLevel()));
		txt.append(String.format("%-10f\t", getMaximumLevel()));
		txt.append(String.format("%-10f\t", getDiameter()));
		txt.append(String.format("%-10f\t", getMinimumVolume()));
		if (getVolumeCurve() != null) {
			txt.append(String.format("%-10s", getVolumeCurve()));
		}
		String description = getDescription();
		if (description != null) {
			txt.append(String.format(";%s", description));
		}
		return txt.toString();
	}

	/**
	 * Copy this object realizing a shallow copy.
	 */
	@Override
	public Tank copy() {
		return new Tank(this);
	}
}
