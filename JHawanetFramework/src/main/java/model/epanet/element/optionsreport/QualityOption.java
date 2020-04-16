package model.epanet.element.optionsreport;

import java.util.Objects;

public class QualityOption {
	private static final String DEFAULT_PARAMETER = "NONE";
	private static final double DEFAULT_RELATIVE_DIFFUSIVITY = 1;
	private static final double DEFAULT_QUALITY_TOLERANCE = 0.1;

	/**
	 * The mass unit.
	 * 
	 * MGL : mg\L UGL : ug\L
	 *
	 */
	public static enum MassUnit {

		MGL("mg\\L"), UGL("ug\\L");

		private String name;

		private MassUnit(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		public static MassUnit parse(String name) {
			for (MassUnit object : MassUnit.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			throw new IllegalArgumentException("There are not a valid element with the name " + name);
		}
	}

	private String parameter; // default value
	private MassUnit massUnit = MassUnit.MGL;
	private double relativeDiffusivity;
	private String traceNodeID;
	private double qualityTolerance;

	public QualityOption() {
		this.parameter = QualityOption.DEFAULT_PARAMETER;
		this.relativeDiffusivity = QualityOption.DEFAULT_RELATIVE_DIFFUSIVITY;
		this.qualityTolerance = QualityOption.DEFAULT_QUALITY_TOLERANCE;
	}

	public QualityOption(QualityOption qualityOption) {
		this.parameter = qualityOption.parameter;
		this.massUnit = qualityOption.massUnit;
		this.relativeDiffusivity = qualityOption.relativeDiffusivity;
		this.traceNodeID = qualityOption.traceNodeID;
		this.qualityTolerance = qualityOption.qualityTolerance;
	}

	/**
	 * Get the parameter
	 * 
	 * @return the parameter
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * Set the parameter.
	 * 
	 * The parameter should be NONE/CHEMICAL/AGE/TRACE.<br>
	 * <br>
	 * 
	 * When you use TRACE you should assign a value to trace node.<br>
	 * <br>
	 * Instead of CHEMICAL you can write the actual name of the substance to be
	 * considered, followed by the concentration units to be used (e.g.
	 * CHLORINE)<br>
	 * <br>
	 * 
	 * The parameter is not validated.<br>
	 * <br>
	 * 
	 * <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * if parameter is null or a empty string so the default value (NONE) is
	 * established.
	 * 
	 * @param parameter the parameter to set
	 * 
	 */
	public void setParameter(String parameter) {
		if (parameter == null || parameter.isEmpty()) {
			this.parameter = QualityOption.DEFAULT_PARAMETER;
		}
		this.parameter = parameter;
	}

	/**
	 * Get the massUnit.<br>
	 * <br>
	 * The default value is {@link MassUnit#MGL}
	 * 
	 * @return the massUnit
	 */
	public MassUnit getMassUnit() {
		return massUnit;
	}

	/**
	 * Set the massUnit.<br>
	 * <br>
	 * The default value is {@link MassUnit#MGL}
	 * 
	 * @param massUnit the massUnit to set
	 * @throws NullPointerException if massUnit is null
	 */
	public void setMassUnit(MassUnit massUnit) {
		Objects.requireNonNull(massUnit);
		this.massUnit = massUnit;
	}

	/**
	 * Get the relativeDiffusivity
	 * 
	 * @return the relativeDiffusivity
	 */
	public double getRelativeDiffusivity() {
		return relativeDiffusivity;
	}

	/**
	 * Set the relativeDiffusivity
	 * 
	 * @param relativeDiffusivity the relativeDiffusivity to set
	 */
	public void setRelativeDiffusivity(double relativeDiffusivity) {
		this.relativeDiffusivity = relativeDiffusivity;
	}

	/**
	 * Get the traceNodeID
	 * 
	 * @return the traceNodeID if exist or null in other case
	 */
	public String getTraceNodeID() {
		return traceNodeID;
	}

	/**
	 * Set the NodeID. You should check if the node exist when set this value.
	 * 
	 * @param NodeID the Node ID to set
	 */
	public void setTraceNodeID(String NodeID) {
		this.traceNodeID = NodeID;
	}

	/**
	 * Get the qualityTolerance
	 * 
	 * @return the qualityTolerance
	 */
	public double getQualityTolerance() {
		return qualityTolerance;
	}

	/**
	 * Set the quality Tolerance
	 * 
	 * @param qualityTolerance the quality tolerance to set
	 */
	public void setQualityTolerance(double qualityTolerance) {
		this.qualityTolerance = qualityTolerance;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("PARAMETER\t%10s", this.parameter));
		txt.append(String.format("MASS UNIT\t%10s", this.massUnit.name()));
		txt.append(String.format("RELATIVE DIFFUSIVITY\t%10f", this.relativeDiffusivity));
		txt.append(String.format("Trace Node\t%10s", this.traceNodeID));
		txt.append(String.format("Quality TOLERANCE\t%10f", this.qualityTolerance));
		return txt.toString();
	}

	/**
	 * Copy this object
	 * 
	 * @return the copy of the called object
	 */
	public QualityOption copy() {
		return new QualityOption(this);
	}

}
