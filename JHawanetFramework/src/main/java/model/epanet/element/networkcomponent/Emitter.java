package model.epanet.element.networkcomponent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class Emitter {
	private double coefficient;

	public Emitter() {
	}

	/**
	 * Create a emitter with same values that the emitter passed.
	 * 
	 * @param emitter the emitter to copy
	 * @throws NullPointerException if emitter is null
	 */
	public Emitter(@NotNull Emitter emitter) {
		Objects.requireNonNull(emitter);
		this.coefficient = emitter.coefficient;
	}


	/**
	 * @return the coefficient
	 */
	public double getCoefficient() {
		return coefficient;
	}

	/**
	 * @param coefficient the coefficient to set
	 */
	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("coefficient", coefficient);
		return gson.toJson(map);
	}

	/**
	 * Create a copy of this emitter.
	 * 
	 * @return the copy
	 */
	public @NotNull Emitter copy() {
		return new Emitter(this);
	}

}
