package model.epanet.element.systemoperation;

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
 * 
 *
 */
public final class Rule {
	private static final Logger LOGGER = LoggerFactory.getLogger(Rule.class);

	@NotNull String code;

	public Rule() {
		code = "";
	}

	/**
	 * Copy constructor.
	 * @param rule the object to copy
	 * @throws NullPointerException if rule is null
	 */
	public Rule(@NotNull Rule rule) {
		Objects.requireNonNull(rule);
		LOGGER.debug("Clonning Rule.");

		this.code = rule.code;
	}

	/**
	 * Get the rule code
	 * @return the ruleCode
	 */
	public @NotNull String getCode() {
		return code;
	}

	/**
	 * @param ruleCode the ruleCode to set or a empty string if this doesn't exist
	 * @throws NullPointerException if ruleCode is null
	 */
	public void setCode(@NotNull String ruleCode) {
		Objects.requireNonNull(ruleCode);
		this.code = ruleCode;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("code", code);
		return gson.toJson(map);
	}

	/**
	 * Create a copy of this object.
	 * @return the copy;
	 */
	public @NotNull Rule copy() {
		return new Rule(this);
	}

}
