package model.epanet.element.waterquality;

public final class Quality {
	private double initialQuality;
	
	public Quality() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Copy constructor
	 * @param quality the object to copy
	 */
	public Quality(Quality quality) {
		this.initialQuality = quality.initialQuality;
	}
	
	/**
	 * @return the initialQuality
	 */
	public double getInitialQuality() {
		return initialQuality;
	}
	
	/**
	 * @param initialQuality the initialQuality to set
	 */
	public void setInitialQuality(double initialQuality) {
		this.initialQuality = initialQuality;
	}
	
	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("Initial Quality %-10f", getInitialQuality()));

		return txt.toString();
	}
	
	/**
	 * Create a copy of this object.
	 * @return the copy
	 */
	public Quality copy() {
		return new Quality(this);
	}
}
