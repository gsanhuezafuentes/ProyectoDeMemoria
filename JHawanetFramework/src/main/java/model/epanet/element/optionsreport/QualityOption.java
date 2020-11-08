package model.epanet.element.optionsreport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.Network;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class QualityOption {
	private static final Logger LOGGER = LoggerFactory.getLogger(QualityOption.class);

	private static final String DEFAULT_PARAMETER = "NONE";
	private static final double DEFAULT_RELATIVE_DIFFUSIVITY = 1;
	private static final double DEFAULT_QUALITY_TOLERANCE = 0.1;

	/**
	 * The mass unit.
	 * 
	 * MGL : mg\L UGL : ug\L
	 *
	 */
	public enum MassUnit {

		MGL("mg/L"), UGL("ug/L");

		private final String name;

		MassUnit(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Parse the string to the enum
		 * @param name the name
		 * @return the associated enum
		 * @throws IllegalArgumentException if name is not valid
		 */
		public static @NotNull MassUnit parse(@NotNull String name) {
			for (MassUnit object : MassUnit.values()) {
				if (object.getName().equalsIgnoreCase(name.replace("\\", "/"))) {
					return object;
				}
			}
			throw new IllegalArgumentException("There are not a valid element with the name " + name);
		}
	}

	@NotNull private String parameter; // default value
	@NotNull private MassUnit massUnit;
	private double relativeDiffusivity;
	@NotNull private String traceNodeID;
	private double qualityTolerance;

	public QualityOption() {
		this.parameter = QualityOption.DEFAULT_PARAMETER;
		this.massUnit = MassUnit.MGL;
		this.relativeDiffusivity = QualityOption.DEFAULT_RELATIVE_DIFFUSIVITY;
		this.qualityTolerance = QualityOption.DEFAULT_QUALITY_TOLERANCE;
		this.traceNodeID = "";
	}

	/**
	 * Copy constructor
	 * @param qualityOption the quality option
	 * @throws NullPointerException if qualityOption is null
	 */
	public QualityOption(@NotNull QualityOption qualityOption) {
		Objects.requireNonNull(qualityOption);
		LOGGER.debug("Clonning QualityOption.");

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
	public @NotNull String getParameter() {
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
	 * if parameter is a empty string so the default value (NONE) is
	 * established.
	 * 
	 * @param parameter the parameter to set.
	 * @throws NullPointerException if parameter is null
	 * 
	 */
	public void setParameter(@Nullable String parameter) {
		Objects.requireNonNull(parameter);
		if (parameter.isEmpty()) {
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
	public @NotNull MassUnit getMassUnit() {
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
	public void setMassUnit(@NotNull MassUnit massUnit) {
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
	public @NotNull String getTraceNodeID() {
		return traceNodeID;
	}

	/**
	 * Set the NodeID. You should check if the node exist when set this value.
	 * 
	 * @param NodeID the Node ID to set
	 */
	public void setTraceNodeID(@NotNull String NodeID) {
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
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("parameter", parameter);
		map.put("massUnit", massUnit.getName());
		map.put("relativeDiffusivity", relativeDiffusivity);
		map.put("traceNodeID", traceNodeID);
		map.put("qualityTolerance", qualityTolerance);
		return gson.toJson(map);
	}

	/**
	 * Copy this object
	 * 
	 * @return the copy of the called object
	 */
	public @NotNull QualityOption copy() {
		return new QualityOption(this);
	}

}
