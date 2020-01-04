package model.epanet.element.systemoperation;

public final class Demand {
	String id;
	double demand;
	Pattern demandPatern;
	String demandCategory;

	public Demand() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create a demand with same values that the demand received. This is a shallow
	 * copy, i.e., If the field value is a reference to an object (e.g., a memory
	 * address) it copies the reference. If it is necessary for the object to be
	 * completely independent of the original you must ensure that you replace the
	 * reference to the contained objects.
	 * 
	 * @param demand the demand to copy
	 */
	public Demand(Demand demand) {
		this.id = demand.id;
		this.demand = demand.demand;
		this.demandPatern = demand.demandPatern;
		this.demandCategory = demand.demandCategory;
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
	 * @return the demandPatern
	 */
	public Pattern getDemandPatern() {
		return demandPatern;
	}

	/**
	 * @param demandPatern the demandPatern to set
	 */
	public void setDemandPatern(Pattern demandPatern) {
		this.demandPatern = demandPatern;
	}

	/**
	 * @return the demandCategory
	 */
	public String getDemandCategory() {
		return demandCategory;
	}

	/**
	 * @param demandCategory the demandCategory to set
	 */
	public void setDemandCategory(String demandCategory) {
		this.demandCategory = demandCategory;
	}

	@Override
	public String toString() {
		String txt = "";
		txt += getId() + "\t";
		txt += getDemand() + "\t";
		if (getDemandPatern() != null) {
			txt += getDemandPatern().getId() + "\t";
		}
		if (getDemandCategory() != null) {
			txt += getDemandCategory();
		}
		return txt;
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
