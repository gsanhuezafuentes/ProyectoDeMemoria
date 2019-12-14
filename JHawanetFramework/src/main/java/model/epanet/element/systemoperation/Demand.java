package model.epanet.element.systemoperation;

public final class Demand {
	String id;
	double demand;
	Pattern demandPatern;
	String demandCategory;

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
}
