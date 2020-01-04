package model.epanet.element.systemoperation;

import java.util.ArrayList;
import java.util.List;

public final class Pattern {
	private String id;
	private List<Double> multipliers;
	
	public Pattern() {
		this.multipliers = new ArrayList<Double>();
	}
	
	/**
	 * Copy constructor.
	 * @param pattern the object to copy
	 */
	public Pattern(Pattern pattern) {
		this();
		this.id = pattern.id;
		this.multipliers.addAll(pattern.multipliers);
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the multipliers
	 */
	public List<Double> getMultipliers() {
		return multipliers;
	}

	/**
	 * @param multipliers the multipliers to set
	 */
	public void setMultipliers(List<Double> multipliers) {
		this.multipliers = multipliers;
	}
	
	/**
	 * Add a multiplier to multipliers list
	 * @param multiplier the multiplier to add
	 */
	public void addMultipliers(double multiplier) {
		this.multipliers.add(multiplier);
	}
	
	@Override
	public String toString() {
		String txt = getId();
		for (int i = 0; i < this.multipliers.size(); i++) {
			txt += String.format("\t%f", this.multipliers.get(i));
		}
		return txt;
	}
	
	/**
	 * Create a copy of this object.
	 * @return the copy
	 */
	public Pattern copy() {
		return new Pattern(this);
	}
}
