package model.epanet.element.networkcomponent;

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

public final class Pipe extends Link {
	private static final Logger LOGGER = LoggerFactory.getLogger(Pipe.class);


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
	public enum PipeStatus {
		OPEN("OPEN"), CLOSED("CLOSED"), CV("CV");

		private final String name;

		PipeStatus(String name) {
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
		 * @throws IllegalArgumentException if name is not valid
		 */
		public static @NotNull PipeStatus parse(String name) {
			for (PipeStatus object : PipeStatus.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			throw new IllegalArgumentException("There are not a valid element with the name " + name);
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
	@NotNull private PipeStatus status; // the default value
	/*
	 * This parameter is save [Reaction] section with the label BULK
	 */
	@Nullable private Double bulkCoefficient;
	/*
	 * This parameter is save [Reaction] section with the label WALL
	 */
	@Nullable private Double wallCoefficient;

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
	 * @throws NullPointerException if pipe is null
	 */
	public Pipe(@NotNull Pipe pipe) {
		super(Objects.requireNonNull(pipe));
		LOGGER.debug("Clonning Pipe {}.", pipe.getId());

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
	public @NotNull PipeStatus getStatus() {
		return status;
	}

	/**
	 * Set the status of this pipe
	 * 
	 * @param status the status to set
	 * @throws NullPointerException if status is null
	 */
	public void setStatus(@NotNull PipeStatus status) {
		Objects.requireNonNull(status);
		this.status = status;
	}

	/**
	 * Get the bulk coefficient
	 * 
	 * @return the bulkCoefficient or null if not exist
	 */
	public @Nullable Double getBulkCoefficient() {
		return bulkCoefficient;
	}

	/**
	 * Set the bulk coefficient
	 * 
	 * @param bulkCoefficient the bulkCoefficient to set or null if there isn't a
	 *                        value
	 */
	public void setBulkCoefficient(@Nullable Double bulkCoefficient) {
		this.bulkCoefficient = bulkCoefficient;
	}

	/**
	 * Get the wall coefficient
	 * 
	 * @return the wallCoefficient or null if not exist
	 */
	public @Nullable Double getWallCoefficient() {
		return wallCoefficient;
	}

	/**
	 * Set the wall coefficient
	 * 
	 * @param wallCoefficient the wallCoefficient to set or null if there isn't a
	 *                        value
	 */
	public void setWallCoefficient(@Nullable Double wallCoefficient) {
		this.wallCoefficient = wallCoefficient;
	}

	@Override
	@SuppressWarnings("unchecked") // the superclass also use Gson to generate the string
	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Map<String, Object> map = new LinkedHashMap<String, Object>(gson.fromJson(super.toString(), LinkedHashMap.class)); //unchecked
		map.put("length", length);
		map.put("diameter", diameter);
		map.put("roughness", roughness);
		map.put("lossCoefficient", lossCoefficient);
		map.put("status", status);
		if (bulkCoefficient == null) {
			map.put("bulkCoefficient", "");
		} else {
			map.put("bulkCoefficient", bulkCoefficient);//
		}
		if (wallCoefficient == null) {
			map.put("wallCoefficient", "");
		} else {
			map.put("wallCoefficient", wallCoefficient);//
		}
		return gson.toJson(map);
	}

	/**
	 * Realize a shallow copy of the object.
	 */
	@Override
	public @NotNull Pipe copy() {
		return new Pipe(this);
	}
}
