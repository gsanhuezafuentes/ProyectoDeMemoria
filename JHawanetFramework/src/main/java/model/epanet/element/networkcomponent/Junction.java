package model.epanet.element.networkcomponent;

import java.util.ArrayList;
import java.util.List;

import model.epanet.element.systemoperation.Demand;
import model.epanet.element.systemoperation.Pattern;

/**
 * This class define a junction.
 * 
 * The elevation default value is set to 0 <br>
 * The base demand default value is set to 0 <br>
 * <br>
 * 
 * <br>
 * <br>
 * <strong>Notes:</strong> <br>
 * baseDemand and demandPattern has the same value that the first element in the
 * list of demandCategory. If you change base demand or demandPattern you change
 * the first element in demandCategory list, and vice versa. <br>
 * 
 * demandPattern is a optional<br>
 * 
 * emitter is optional<br>
 * 
 *
 */
public final class Junction extends Node {
	/**
	 * The default value for elevation
	 */
	public final static double DEFAULT_ELEVATION = 0;
	/**
	 * The default value for base demand
	 */
	public final static double DEFAULT_BASE_DEMAND = 0;

	private double elevation;
//	private double baseDemand;
	private List<Demand> demandCategories;
//	private Pattern demandPattern; 
	private Emitter emitter;

	public Junction() {
		this.elevation = DEFAULT_ELEVATION;
//		this.baseDemand = BASE_DEMAND;
		this.demandCategories = new ArrayList<Demand>();
		Demand defaultDemand = new Demand();
		defaultDemand.setDemand(DEFAULT_BASE_DEMAND);
		this.demandCategories.add(defaultDemand);

	}

	/**
	 * Create a junction with the same values that the junction received. This copy
	 * is a deep copy of original.
	 * 
	 * @param junction the junction to copy.
	 */
	public Junction(Junction junction) {
		super(junction);
		this.elevation = junction.elevation;
//		this.baseDemand = junction.baseDemand;
//		this.demandPattern = junction.demandPattern;
		this.demandCategories = new ArrayList<Demand>();

		for (Demand demand : junction.demandCategories) {
			this.demandCategories.add(demand.copy());
		}

	}

	/**
	 * Get the elevation value
	 * 
	 * @return the elevation
	 */
	public double getElevation() {
		return elevation;
	}

	/**
	 * Set the elevation value
	 * 
	 * @param elev the elevation to set
	 */
	public void setElevation(double elev) {
		this.elevation = elev;
	}

	/**
	 * Get the base demand. It has the same value that the first element in category
	 * demand {@link #getDemandCategories()}.
	 * 
	 * @return the demand
	 */
	public double getBaseDemand() {
		// if the demand list hasn't element so add the default element
		if (this.demandCategories.isEmpty()) {
			Demand demand = new Demand();
			demand.setDemand(DEFAULT_BASE_DEMAND);
			this.demandCategories.add(demand);
		}
		return this.demandCategories.get(0).getDemand();
	}

	/**
	 * Set the base demand. i.e., set the value of the first element in category
	 * demand list {@link #getDemandCategories()}.
	 * 
	 * @param demand the demand to set
	 */
	public void setBaseDemand(double demand) {
		// if the demand list hasn't element so add the default element
		if (this.demandCategories.isEmpty()) {
			Demand demandObj = new Demand();
			demandObj.setDemand(demand);
			this.demandCategories.add(demandObj);
			return;
		}
		this.demandCategories.get(0).setDemand(demand);
	}

	/**
	 * Get the id to the {@link Pattern}. This has the same pattern id that the
	 * first element in category demand list {@link #getDemandCategories()}.
	 * 
	 * @return the pattern
	 */
	public String getDemandPattern() {
		// if the demand list hasn't element so add the default element
		if (this.demandCategories.isEmpty()) {
			Demand demand = new Demand();
			demand.setDemand(DEFAULT_BASE_DEMAND);
			this.demandCategories.add(demand);
		}
		return this.demandCategories.get(0).getDemandPattern();
	}

	/**
	 * Set the id to the {@link Pattern}. <br>
	 * 
	 * The value assigned to this also is the same value that the first element in
	 * DemandCategory ({@link #getDemandCategories()}) list.
	 * 
	 * @param pattern the pattern to set
	 */
	public void setDemandPattern(String pattern) {
		// if the demand list hasn't element so add the default element
		if (this.demandCategories.isEmpty()) {
			Demand demand = new Demand();
			demand.setDemand(DEFAULT_BASE_DEMAND);
			this.demandCategories.add(demand);
		}
		this.demandCategories.get(0).setDemandPattern(pattern);
	}

	/**
	 * Get the demands.<br>
	 * <br>
	 * You can get this array and modified. By default this array contains a default
	 * demand.
	 * 
	 * @return the demandCategories
	 */
	public List<Demand> getDemandCategories() {
		return this.demandCategories;
	}

	/**
	 * Get the emitter
	 * 
	 * @return the emitter
	 */
	public Emitter getEmitter() {
		return this.emitter;
	}

	/**
	 * Set the emitter
	 * 
	 * @param emitter the emitter to set
	 */
	public void setEmitter(Emitter emitter) {
		this.emitter = emitter;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getId()));
		txt.append(String.format("%-10f\t", getElevation()));
		txt.append(String.format("%-10f\t", getBaseDemand()));
		if (getDemandPattern() != null) {
			txt.append(String.format("%-10s", getDemandPattern()));
		}
		String description = getDescription();
		if (description != null) {
			txt.append(String.format(";%s", description));
		}
		return txt.toString();
	}

	/**
	 * Realize a shallow copy of this junction.
	 * 
	 * @return the shallow copy
	 */
	@Override
	public Junction copy() {
		return new Junction(this);

	}

}
