package model.epanet.element.networkcomponent;

import java.util.Objects;

public final class Pipe extends Link {

	/**
	 * A enumerator that to define the status of Pipe.
	 * 
	 * <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * The section [Status] only can setting OPEN and CLOSED. CV have to be
	 * configured directly in [PUMP] section.
	 * 
	 */
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
		
		/**
		 * Parse the name to a object of the enum class if exist. if name no exist in enum class so return null;
		 * @param name the name of object
		 * @return the object of enum class or null if no exist
		 */
		public static PipeStatus parse(String name) {
			for (PipeStatus object : PipeStatus.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			return null;
		}
	}

	public static final double DEFAULT_LENGTH = 1000;
	public static final double DEFAULT_DIAMETER = 12;
	public static final double DEFAULT_ROUGHNESS = 100;
	public static final double DEFAULT_LOSS_COEFFICIENT = 0;
	public static final PipeStatus DEFAULT_STATUS = PipeStatus.OPEN;

	private double length;
	private double diameter;
	private double roughness;
	private double lossCoefficient;
	private PipeStatus status; // the default value
	/*
	 * This parameter is save [Reaction] section with the label BULK
	 */
	private Double bulkCoefficient;
	/*
	 * This parameter is save [Reaction] section with the label WALL
	 */
	private Double wallCoefficient;

	public Pipe() {
		this.length = DEFAULT_LENGTH;
		this.diameter = DEFAULT_DIAMETER;
		this.roughness = DEFAULT_ROUGHNESS;
		this.lossCoefficient = DEFAULT_LOSS_COEFFICIENT;
		this.status = DEFAULT_STATUS;
	}

	/**
	 * Create a new pipe with the same values that the pipe received. This is a
	 * shallow copy because inherits of LINK.
	 * 
	 * You must replace node1 and node2 to do the copy independent of the original
	 * 
	 * @param pipe the pipe to copy
	 */
	public Pipe(Pipe pipe) {
		super(pipe);
		this.length = pipe.length;
		this.diameter = pipe.diameter;
		this.roughness = pipe.roughness;
		this.lossCoefficient = pipe.lossCoefficient;
		this.status = pipe.status;
		this.bulkCoefficient = pipe.bulkCoefficient;
		this.wallCoefficient = pipe.wallCoefficient;
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
	 * Get loss coefficient
	 * 
	 * @return the loss coefficient
	 */
	public double getLossCoefficient() {
		return lossCoefficient;
	}

	/**
	 * @param mloss the loss coefficient to set
	 */
	public void setLossCoefficient(double mloss) {
		this.lossCoefficient = mloss;
	}

	/**
	 * Get the status of this pipe
	 * 
	 * @return the status
	 */
	public PipeStatus getStatus() {
		return status;
	}

	/**
	 * Set the status of this pipe
	 * 
	 * @param status the status to set
	 * @throws NullPointerException if status is null
	 */
	public void setStatus(PipeStatus status) {
		Objects.requireNonNull(status);
		this.status = status;
	}

	/**
	 * Get the bulk coefficient
	 * 
	 * @return the bulkCoefficient or null if not exist
	 */
	public Double getBulkCoefficient() {
		return bulkCoefficient;
	}

	/**
	 * Set the bulk coefficient
	 * 
	 * @param bulkCoefficient the bulkCoefficient to set or null if there isn't a
	 *                        value
	 */
	public void setBulkCoefficient(Double bulkCoefficient) {
		this.bulkCoefficient = bulkCoefficient;
	}

	/**
	 * Get the wall coefficient
	 * 
	 * @return the wallCoefficient or null if not exist
	 */
	public Double getWallCoefficient() {
		return wallCoefficient;
	}

	/**
	 * Set the wall coefficient
	 * 
	 * @param wallCoefficient the wallCoefficient to set or null if there isn't a
	 *                        value
	 */
	public void setWallCoefficient(Double wallCoefficient) {
		this.wallCoefficient = wallCoefficient;
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
		txt.append(String.format("%-10f\t", getLossCoefficient()));
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
