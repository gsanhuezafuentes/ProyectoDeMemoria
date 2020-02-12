package model.epanet.element.networkcomponent;

public final class Pipe extends Link {

	public static enum PipeStatus {
		OPEN("OPEN"), CLOSED("CLOSED"), CV("CV");

		private String name;

		private PipeStatus(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

	}

	private double length;
	private double diameter;
	private double roughness;
	private double mloss;
	private PipeStatus status;

	public Pipe() {
	}

	/**
	 * Create a new pipe with the same values that the pipe received. This is a
	 * shallow copy, i.e., If the field value is a reference to an object (e.g., a
	 * memory address) it copies the reference. If it is necessary for the object to
	 * be completely independent of the original you must ensure that you replace
	 * the reference to the contained objects.
	 * 
	 * @param pipe the pipe to copy
	 */
	public Pipe(Pipe pipe) {
		super(pipe);
		this.length = pipe.length;
		this.diameter = pipe.diameter;
		this.roughness = pipe.roughness;
		this.mloss = pipe.mloss;
		this.status = pipe.status;
	}

	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(double length) {
		this.length = length;
	}

	/**
	 * @return the diam
	 */
	public double getDiameter() {
		return diameter;
	}

	/**
	 * @param diam the diam to set
	 */
	public void setDiameter(double diam) {
		this.diameter = diam;
	}

	/**
	 * @return the roughness
	 */
	public double getRoughness() {
		return roughness;
	}

	/**
	 * @param roughness the roughness to set
	 */
	public void setRoughness(double roughness) {
		this.roughness = roughness;
	}

	/**
	 * Get loss coeficient
	 * @return the mloss
	 */
	public double getMinorLoss() {
		return mloss;
	}

	/**
	 * @param mloss the mloss to set
	 */
	public void setMinorLoss(double mloss) {
		this.mloss = mloss;
	}

	/**
	 * @return the status
	 */
	public PipeStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(PipeStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getId()));
		txt.append(String.format("%-10s\t", getNode1().getId()));
		txt.append(String.format("%-10s\t", getNode2().getId()));
		txt.append(String.format("%-10f\t", getLength()));
		txt.append(String.format("%-10f\t", getDiameter()));
		txt.append(String.format("%-10f\t", getRoughness()));
		txt.append(String.format("%-10f\t", getMinorLoss()));
		txt.append(String.format("%-10s", getStatus().getName()));
		String description = getDescription();
		if (description != null) {
			txt.append(String.format(";%s", description));
		}
		return txt.toString();
	}

	/**
	 * Realize a shallow copy of the object.
	 */
	@Override
	public Pipe copy() {
		return new Pipe(this);
	}
}
