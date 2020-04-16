package model.epanet.element.waterquality;

import java.util.Objects;

import model.epanet.element.systemoperation.Pattern;

public final class Source {

	public static enum SourceType {
		CONCEN("CONCEN"), MASS("MASS"), FLOWPACED("FLOWPACED"), SETPOINT("SETPOINT");

		private String name;

		private SourceType(String name) {
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
		public static SourceType parse(String name) {
			for (SourceType object : SourceType.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			return null;
		}
	}

	private SourceType sourceType;
	private double baselineStrenth;
	private String timePattern; // ID to Pattern

	public Source() {
		this.timePattern = "";
	}

	/**
	 * Create a new source with the same values that the source received. This is a
	 * deep copy.
	 * 
	 * If you want that the object will be totally independent you need set the
	 * timePattern ({@link #setTimePattern}).
	 * 
	 * @param source the object to copy
	 */
	public Source(Source source) {
		this.sourceType = source.sourceType;
		this.baselineStrenth = source.baselineStrenth;
		this.timePattern = source.timePattern;
	}

	/**
	 * @return the sourceType
	 */
	public SourceType getSourceType() {
		return sourceType;
	}

	/**
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * @return the baselineStrenth
	 */
	public double getBaselineStrenth() {
		return baselineStrenth;
	}

	/**
	 * @param baselineStrenth the baselineStrenth to set
	 */
	public void setBaselineStrenth(double baselineStrenth) {
		this.baselineStrenth = baselineStrenth;
	}

	/**
	 * Get the time pattern. This is an id of the {@link Pattern}
	 * 
	 * @return the timePattern or a empty string if it does not exist
	 */
	public String getTimePattern() {
		return timePattern;
	}

	/**
	 * Set the time pattern id.
	 * 
	 * @param timePattern the timePattern to set
	 * @throws NullPointerException if timePattern is null
	 */
	public void setTimePattern(String timePattern) {
		Objects.requireNonNull(timePattern);
		this.timePattern = timePattern;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getSourceType().getName()));
		txt.append(String.format("%-10f\t", getBaselineStrenth()));
		if (getTimePattern() != null) {
			txt.append(String.format("%-10s", getTimePattern()));
		}

		return txt.toString();
	}

	/**
	 * Create a copy of this object
	 * 
	 * @see Source#Source(Source)
	 * 
	 * @return the object to copy
	 */
	public Source copy() {
		return new Source(this);
	}

}
