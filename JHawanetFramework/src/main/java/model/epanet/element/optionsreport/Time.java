package model.epanet.element.optionsreport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.Network;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class defines various time step parameters used in the simulation.
 * 
 * The format for Duration, Hydraulic Timestep, Quality Timestep, Pattern
 * Timestep, Pattern Start, Report Timestep, Report Start is: <br>
 * <br>
 * 
 * value (units)
 * 
 * 1. Units can be SECONDS (SEC), MINUTES (MIN), HOURS, or DAYS. The default is
 * hours.<br>
 * <br>
 * 2. If no units are supplied, then time values can be expressed in either
 * decimal hours or in hours:minutes notation.<br>
 * <br>
 *
 * The Start ClockTime has the next format: value (AM/PM)<br>
 * <br>
 * 
 * For example:<br>
 * <br>
 * 
 * <pre>
 * DURATION 240 HOURS
 * QUALITY TIMESTEP 3 MIN
 * QUALITY TIMESTEP 0:03
 * REPORT START 120
 * STATISTIC AVERAGE
 * START CLOCKTIME 6:00 AM
 * </pre>
 * 
 * <br>
 * <br>
 * <strong>Notes:</strong> <br>
 * The format is not validated
 */
public final class Time {
	private static final Logger LOGGER = LoggerFactory.getLogger(Time.class);

	public enum Statistic {
		NONE("NONE"), AVERAGE("AVERAGE"), MINIMUM("MINIMUM"), MAXIMUM("MAXIMUM"), RANGE("RANGE");

		private final String name;

		Statistic(String name) {
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
		public static @NotNull Statistic parse(String name) {
			for (Statistic object : Statistic.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			throw new IllegalArgumentException("There are not a valid element with the name " + name);
		}
	}

	public static final String DEFAULT_DURATION = "0";
	private static final String DEFAULT_HYDRAULIC_TIMESTEP = "1:00";
	private static final String DEFAULT_QUALITY_TIMESTEP = "0:05";
	private static final String DEFAULT_PATTERN_TIMESTEP = "1:00";
	private static final String DEFAULT_PATTERN_START = "0:00";
	private static final String DEFAULT_REPORT_TIMESTEP = "1:00";
	private static final String DEFAULT_REPORT_START = "0:00";
	private static final String DEFAULT_START_CLOCKTIME = "12 AM";
	private static final Statistic DEFAULT_STATISTIC = Statistic.NONE; // the default value

	@NotNull private String duration;
	@NotNull private String hydraulicTimestep;
	@NotNull private String qualityTimestep;
	@NotNull private String patternTimestep;
	@NotNull private String patternStart;
	@NotNull private String reportTimestep;
	@NotNull private String reportStart;
	@NotNull private String startClockTime;
	@NotNull private Statistic statistic; // the default value

	public Time() {
		this.duration = DEFAULT_DURATION;
		this.hydraulicTimestep = DEFAULT_HYDRAULIC_TIMESTEP;
		this.qualityTimestep = DEFAULT_QUALITY_TIMESTEP;
		this.patternTimestep = DEFAULT_PATTERN_TIMESTEP;
		this.patternStart = DEFAULT_PATTERN_START;
		this.reportTimestep = DEFAULT_REPORT_TIMESTEP;
		this.reportStart = DEFAULT_REPORT_START;
		this.startClockTime = DEFAULT_START_CLOCKTIME;
		this.statistic = DEFAULT_STATISTIC;
	}

	/**
	 * Copy constructor
	 * @param time the time object to copy
	 * @throws NullPointerException if time is null
	 */
	public Time(@NotNull Time time) {
		Objects.requireNonNull(time);
		LOGGER.debug("Clonning Time.");

		this.duration = time.duration;
		this.hydraulicTimestep = time.hydraulicTimestep;
		this.qualityTimestep = time.qualityTimestep;
		this.patternTimestep = time.patternTimestep;
		this.patternStart = time.patternStart;
		this.reportTimestep = time.reportTimestep;
		this.reportStart = time.reportStart;
		this.startClockTime = time.startClockTime;
		this.statistic = time.statistic;

	}

	/**
	 * Get the duration
	 * 
	 * @return the duration
	 */
	public @NotNull String getDuration() {
		return duration;
	}

	/**
	 * Set the duration
	 * 
	 * @param duration the duration to set
	 * @throws NullPointerException if duration is null
	 */
	public void setDuration(@NotNull String duration) {
		Objects.requireNonNull(duration);
		this.duration = duration;
	}

	/**
	 * Get the hydraulic timestep
	 * 
	 * @return the hydraulicTimestep
	 */
	public @NotNull String getHydraulicTimestep() {
		return hydraulicTimestep;
	}

	/**
	 * Set the hydraulic timestep
	 * 
	 * @param hydraulicTimestep the hydraulic timestep to set
	 * @throws NullPointerException if hydraulicTimestep is null
	 */
	public void setHydraulicTimestep(@NotNull String hydraulicTimestep) {
		Objects.requireNonNull(hydraulicTimestep);
		this.hydraulicTimestep = hydraulicTimestep;
	}

	/**
	 * Get the quality timestep
	 * 
	 * @return the qualityTimestep
	 */
	public @NotNull String getQualityTimestep() {
		return qualityTimestep;
	}

	/**
	 * Set the quality timestep
	 * 
	 * @param qualityTimestep the quality timestep to set
	 * @throws NullPointerException if qualityTimestep is null
	 */
	public void setQualityTimestep(@NotNull String qualityTimestep) {
		Objects.requireNonNull(startClockTime);
		this.qualityTimestep = qualityTimestep;
	}

	/**
	 * Get the pattern timestep
	 * 
	 * @return the patternTimestep
	 */
	public @NotNull String getPatternTimestep() {
		return patternTimestep;
	}

	/**
	 * Set the pattern timestep
	 * 
	 * @param patternTimestep the pattern timestep to set
	 * @throws NullPointerException if patternTimestep is null
	 */
	public void setPatternTimestep(@NotNull String patternTimestep) {
		Objects.requireNonNull(startClockTime);

		this.patternTimestep = patternTimestep;
	}

	/**
	 * Get the pattern start
	 * 
	 * @return the patternStart
	 */
	public @NotNull String getPatternStart() {
		return patternStart;
	}

	/**
	 * Set the pattern start
	 * 
	 * @param patternStart the pattern start to set
	 * @throws NullPointerException if patternStart is null
	 */
	public void setPatternStart(@NotNull String patternStart) {
		Objects.requireNonNull(startClockTime);

		this.patternStart = patternStart;
	}

	/**
	 * Get the report timestep
	 * 
	 * @return the reportTimestep
	 */
	public @NotNull String getReportTimestep() {
		return reportTimestep;
	}

	/**
	 * Set the report timestep
	 * 
	 * @param reportTimestep the report timestep to set
	 * @throws NullPointerException if reportTimestep is null
	 */
	public void setReportTimestep(@NotNull String reportTimestep) {
		Objects.requireNonNull(startClockTime);

		this.reportTimestep = reportTimestep;
	}

	/**
	 * Get the report start
	 * 
	 * @return the reportStart
	 */
	public @NotNull String getReportStart() {
		return reportStart;
	}

	/**
	 * Set the report start
	 * 
	 * @param reportStart the report start to set
	 * @throws NullPointerException if reportStart is null
	 */
	public void setReportStart(@NotNull String reportStart) {
		Objects.requireNonNull(startClockTime);
		this.reportStart = reportStart;
	}

	/**
	 * Get the start clockTime
	 * 
	 * @return the startClockTime
	 */
	public @NotNull String getStartClockTime() {
		return startClockTime;
	}

	/**
	 * Set the start clockTime
	 * 
	 * @param startClockTime the start clockTime to set
	 * @throws NullPointerException if startClockTime is null
	 */
	public void setStartClockTime(@NotNull String startClockTime) {
		Objects.requireNonNull(startClockTime);
		this.startClockTime = startClockTime;
	}

	/**
	 * Get the statistic
	 * 
	 * @return the statistic
	 */
	public @NotNull Statistic getStatistic() {
		return statistic;
	}

	/**
	 * Set the statistic
	 * 
	 * @param statistic the statistic to set
	 * @throws NullPointerException if statistic is null
	 */
	public void setStatistic(@NotNull Statistic statistic) {
		Objects.requireNonNull(statistic);
		this.statistic = statistic;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("duration", duration);
		map.put("hydraulicTimestep", hydraulicTimestep);
		map.put("qualityTimestep", qualityTimestep);
		map.put("patternTimestep", patternTimestep);
		map.put("patternStart", patternStart);
		map.put("reportTimestep", reportTimestep);
		map.put("reportStart", reportStart);
		map.put("startClockTime", startClockTime);
		map.put("statistic", statistic);
		return gson.toJson(map);
	}

	/**
	 * Copy this object.
	 * 
	 * @return the copy
	 */
	public @NotNull Time copy() {
		return new Time(this);
	}
}
