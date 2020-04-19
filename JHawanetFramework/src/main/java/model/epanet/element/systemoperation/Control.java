package model.epanet.element.systemoperation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class Control {
	@NotNull private String code;

	public Control() {
		this.code = "";
	}

	/**
	 * Copy constructor.
	 * 
	 * @param control the object to copy
	 * @throws NullPointerException if control is null
	 */
	public Control(@NotNull Control control) {
		Objects.requireNonNull(control);
		this.code = control.code;
	}

	
	/**
	 * Get the code
	 * @return the code or empty string if there is does not code
	 */
	public @NotNull String getCode() {
		return this.code;
	}

	/**
	 * Set the code
	 * @param code the code to set or a empty string if there is no code control
	 * @throws NullPointerException if code is null
	 */
	public void setCode(@NotNull String code) {
		Objects.requireNonNull(code);
		this.code = code;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("code", code);
		return gson.toJson(map);
	}

	/**
	 * Copy the object.
	 * 
	 * @return a copy of this object
	 */
	public @NotNull Control copy() {
		return new Control(this);
	}

}
