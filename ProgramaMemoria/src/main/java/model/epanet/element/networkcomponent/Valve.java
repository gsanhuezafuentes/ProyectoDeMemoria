package model.epanet.element.networkcomponent;

public class Valve extends Link {

	private Double diameter;
	private String type;
	private String setting;
	private Double minorLoss;

	/**
	 * @return the diameter
	 */
	public Double getDiameter() {
		return diameter;
	}

	/**
	 * @param diameter the diameter to set
	 */
	public void setDiameter(Double diameter) {
		this.diameter = diameter;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the setting
	 */
	public String getSetting() {
		return setting;
	}

	/**
	 * @param setting the setting to set
	 */
	public void setSetting(String setting) {
		this.setting = setting;
	}

	/**
	 * @return the minorLoss
	 */
	public Double getMinorLoss() {
		return minorLoss;
	}

	/**
	 * @param minorLoss the minorLoss to set
	 */
	public void setMinorLoss(Double minorLoss) {
		this.minorLoss = minorLoss;
	}
	
	@Override
	public String toString() {
		String txt = "";
		txt += getId() + "\t";
		txt += getNode1().getId() + "\t";
		txt += getNode2().getId() + "\t";
		txt += getDiameter() +"\t";
		txt += getType() + "\t";
		txt += getSetting() +"\t";
		txt += getMinorLoss();
		return txt;
	}

}
