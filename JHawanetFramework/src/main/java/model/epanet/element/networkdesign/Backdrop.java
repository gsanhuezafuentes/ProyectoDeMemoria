package model.epanet.element.networkdesign;

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

public final class Backdrop {
	private static final Logger LOGGER = LoggerFactory.getLogger(Backdrop.class);

	public enum Unit {
		FEET("FEET"), METERS("METERS"), DEGREES("DEGREES"), NONE("NONE");

		private final String name;

		Unit(String name) {
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
		public static @Nullable Unit parse(String name) {
			for (Unit object : Unit.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			return null;
		}
	}

	private double xBottomLeft;
	private double yBottomLeft;
	private double xUpperRight;
	private double yUpperRight;
	@NotNull private Unit unit;
	@NotNull private String file;
	private double xOffset;
	private double yOffset;

	public Backdrop() {
		this.file = "";
		this.unit = Unit.NONE;
	}

	/**
	 * Copy constructor
	 * @param backdrop the object to copy
	 * @throws NullPointerException if backdrop is null
	 */
	public Backdrop(@NotNull Backdrop backdrop) {
		Objects.requireNonNull(backdrop);
		LOGGER.debug("Clonning Backdrop.");

		this.xBottomLeft = backdrop.xBottomLeft;
		this.yBottomLeft = backdrop.yBottomLeft;
		this.xUpperRight = backdrop.xUpperRight;
		this.yUpperRight = backdrop.yUpperRight;
		this.unit = backdrop.unit;
		this.file = "";
		this.xOffset = backdrop.xOffset;
		this.yOffset = backdrop.yOffset;
	}

	/**
	 * Get the x-value in the bottom left corner
	 * 
	 * @return the xBottomLeft
	 */
	public double getXBottomLeft() {
		return xBottomLeft;
	}

	/**
	 * Get the y-value in the bottom left corner
	 * 
	 * @return the yBottomLeft
	 */
	public double getYBottomLeft() {
		return yBottomLeft;
	}

	/**
	 * Get the x-value in the upper right corner
	 * 
	 * @return the xUpperRight
	 */
	public double getXUpperRight() {
		return xUpperRight;
	}

	/**
	 * Get the y-value in the upper right corner
	 * 
	 * @return the yUpperRight
	 */
	public double getYUpperRight() {
		return yUpperRight;
	}

	/**
	 * Set the dimension
	 * 
	 * @param xBottomLeft the x-value of bottom left corner
	 * @param yBottomLeft the y-value of bottom left corner
	 * @param xUpperRight the x-value of upper right corner
	 * @param yUpperRight the y-value of upper right corner
	 */
	public void setDimension(double xBottomLeft, double yBottomLeft, double xUpperRight, double yUpperRight) {
		this.xBottomLeft = xBottomLeft;
		this.yBottomLeft = yBottomLeft;
		this.xUpperRight = xUpperRight;
		this.yUpperRight = yUpperRight;
	}

	/**
	 * Get the unit
	 * 
	 * @return the unit
	 */
	public @NotNull Unit getUnit() {
		return unit;
	}

	/**
	 * Set the unit
	 * 
	 * @param unit the unit to set
	 * @throws NullPointerException if unit is null
	 */
	public void setUnit(@NotNull Unit unit) {
		Objects.requireNonNull(unit);
		this.unit = unit;
	}

	/**
	 * Get the file path
	 * 
	 * @return the file or a empty string if it doesn't exist
	 */
	public @NotNull String getFile() {
		return file;
	}

	/**
	 * Set the file path
	 * 
	 * @param file the file to set
	 * @throws NullPointerException if file is null
	 */
	public void setFile(@NotNull String file) {
		Objects.requireNonNull(file);
		this.file = file;
	}

	/**
	 * Get the x-value of the offset
	 * 
	 * @return the xOffset
	 */
	public double getXOffset() {
		return xOffset;
	}

	/**
	 * Get the y-value of the offset
	 * 
	 * @return the yOffset
	 */
	public double getYOffset() {
		return yOffset;
	}

	/**
	 * Set the offset
	 * 
	 * @param x the x-value
	 * @param y the y-value
	 */
	public void setOffset(double x, double y) {
		this.xOffset = x;
		this.yOffset = y;
	}


	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("xBottomLeft", xBottomLeft);
		map.put("yBottomLeft", yBottomLeft);
		map.put("xUpperRight", xUpperRight);
		map.put("yUpperRight", yUpperRight);
		map.put("unit", unit);
		map.put("file", file);
		map.put("xOffset", xOffset);
		map.put("yOffset", yOffset);
		return gson.toJson(map);
	}

	/**
	 * Copy the object.
	 * 
	 * @return the copy
	 */
	public @NotNull Backdrop copy() {
		return new Backdrop(this);
	}
}
