package model.epanet.element.networkcomponent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class Reservoir extends Node {
	public static final double DEFAULT_TOTAL_HEAD = 0;

	private double totalHead;
	@NotNull private String headPattern; // Pattern ID

	public Reservoir() {
		totalHead = DEFAULT_TOTAL_HEAD;
		headPattern = "";
	}

	/**
	 * Create a new reservoir copy the reservoir received.This is a deep copy.
	 * 
	 * @param reservoir the reservoir to copy.
	 * @throws NullPointerException if reservoir is null
	 */
	public Reservoir(@NotNull Reservoir reservoir) {
		super(Objects.requireNonNull(reservoir));
		totalHead = reservoir.totalHead;
		headPattern = reservoir.headPattern;
	}

	/**
	 * Get the head value
	 * @return the head
	 */
	public double getTotalHead() {
		return totalHead;
	}

	/**
	 * Set the head value
	 * @param head the head to set
	 */
	public void setTotalHead(double head) {
		this.totalHead = head;
	}

	/**
	 * Get the pattern id or a empty string if it doesn't exist
	 * 
	 * @return the pattern id or a empty string if it doesn't exist
	 */
	public @NotNull String getHeadPattern() {
		return headPattern;
	}

	/**
	 * Set the pattern id
	 * 
	 * @param pattern the pattern to set
	 * @throws NullPointerException if pattern is null
	 */
	public void setHeadPattern(@NotNull String pattern) {
		Objects.requireNonNull(pattern);
		this.headPattern = pattern;
	}

	@Override
	@SuppressWarnings("unchecked") // the superclass also use Gson to generate the string
	public String toString() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		Map<String, Object> map = new LinkedHashMap<String, Object>(gson.fromJson(super.toString(), LinkedHashMap.class)); //unchecked
		map.put("totalHead", totalHead);
		map.put("headPattern", headPattern);
		return gson.toJson(map);
	}

	/**
	 * Copy this object realizing a shallow copy.
	 */
	@Override
	public @NotNull Reservoir copy() {
		return new Reservoir(this);
	}

}
