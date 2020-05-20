package model.epanet.element.waterquality;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.systemoperation.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class Source {

	public enum SourceType {
		CONCEN("CONCEN"), MASS("MASS"), FLOWPACED("FLOWPACED"), SETPOINT("SETPOINT");

		private final String name;

		SourceType(String name) {
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
		 * @return the object of enum class
		 * @throws IllegalArgumentException if name is not valid
		 */
		public static @NotNull SourceType parse(String name) {
			for (SourceType object : SourceType.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			throw new IllegalArgumentException();
		}
	}

	@NotNull private SourceType sourceType;
	private double sourceQuality;
	@NotNull private String timePattern; // ID to Pattern

	public Source() {
		this.timePattern = "";
		this.sourceType = SourceType.CONCEN; //default value

	}

	/**
	 * Create a new source with the same values that the source received. This is a
	 * deep copy.
	 * 
	 * If you want that the object will be totally independent you need set the
	 * timePattern ({@link #setTimePattern}).
	 * 
	 * @param source the object to copy
	 * @throws NullPointerException if source is null
	 */
	public Source(@NotNull Source source) {
		Objects.requireNonNull(source);
		this.sourceType = source.sourceType;
		this.sourceQuality = source.sourceQuality;
		this.timePattern = source.timePattern;
	}

	/**
	 * Get the source type. The default value is {@link SourceType#CONCEN}.
	 * @return the sourceType
	 */
	public @NotNull SourceType getSourceType() {
		return sourceType;
	}

	/**
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(@NotNull SourceType sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * Get the Baseline source strength
	 * @return the source quality
	 */
	public double getSourceQuality() {
		return sourceQuality;
	}

	/**
	 * @param sourceQuality the Baseline source strength to set
	 */
	public void setSourceQuality(double sourceQuality) {
		this.sourceQuality = sourceQuality;
	}

	/**
	 * Get the time pattern. This is an id of the {@link Pattern}
	 * 
	 * @return the timePattern or a empty string if it does not exist
	 */
	public @NotNull String getTimePattern() {
		return timePattern;
	}

	/**
	 * Set the time pattern id.
	 * 
	 * @param timePattern the timePattern to set
	 * @throws NullPointerException if timePattern is null
	 */
	public void setTimePattern(@NotNull String timePattern) {
		Objects.requireNonNull(timePattern);
		this.timePattern = timePattern;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("sourceType", sourceType);
		map.put("sourceQuality", sourceQuality);
		map.put("timePattern", timePattern);
		return gson.toJson(map);
	}

	/**
	 * Create a copy of this object
	 * 
	 * @see Source#Source(Source)
	 * 
	 * @return the object to copy
	 */
	public @NotNull Source copy() {
		return new Source(this);
	}

}
