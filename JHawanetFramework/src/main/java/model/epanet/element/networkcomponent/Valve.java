package model.epanet.element.networkcomponent;

public final class Valve extends Link {

	private Double diameter;
	private String type;
	private String setting;
	private Double minorLoss;

	public Valve() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Copy constructor. This is a shallow copy, i.e., If the field value is a
	 * reference to an object (e.g., a memory address) it copies the reference. If
	 * it is necessary for the object to be completely independent of the original
	 * you must ensure that you replace the reference to the contained objects.
	 * 
	 * @param valve
	 */
	public Valve(Valve valve) {
		super(valve);
		this.diameter = valve.diameter;
		this.type = valve.type;
		this.setting = valve.setting;
		this.minorLoss = valve.minorLoss;
	}

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
		txt += String.format("%-10s\t", getId());
		txt += String.format("%-10s\t", getNode1().getId());
		txt += String.format("%-10s\t", getNode2().getId());
		txt += String.format("%-10f\t", getDiameter());
		txt += String.format("%-10s\t", getType());
		txt += String.format("%-10s\t", getSetting());
		txt += String.format("%-10f", getMinorLoss());
		return txt;
	}

	/**
	 * Copy the object realizing a shallow copy of this.
	 */
	@Override
	public Valve copy() {
		// TODO Auto-generated method stub
		return new Valve(this);
	}

}
