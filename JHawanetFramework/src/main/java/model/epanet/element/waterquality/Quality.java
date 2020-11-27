package model.epanet.element.waterquality;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Quality {
	private static final Logger LOGGER = LoggerFactory.getLogger(Quality.class);

	private double initialQuality;
	
	public Quality() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Copy constructor
	 * @param quality the object to copy
	 * @throws NullPointerException if quality is null
	 */
	public Quality(@NotNull Quality quality) {
		Objects.requireNonNull(quality);
		LOGGER.debug("Clonning Quality.");

		this.initialQuality = quality.initialQuality;
	}
	
	/**
	 * @return the initialQuality
	 */
	public double getInitialQuality() {
		return initialQuality;
	}
	
	/**
	 * @param initialQuality the initialQuality to set
	 */
	public void setInitialQuality(double initialQuality) {
		this.initialQuality = initialQuality;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new HashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("initialQuality", initialQuality);
		return gson.toJson(map);
	}

	/**
	 * Create a copy of this object.
	 * @return the copy
	 */
	public @NotNull Quality copy() {
		return new Quality(this);
	}
}
