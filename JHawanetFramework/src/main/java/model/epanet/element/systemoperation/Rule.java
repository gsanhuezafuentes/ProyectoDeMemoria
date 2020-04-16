package model.epanet.element.systemoperation;

import java.util.Objects;

/**
 * 
 *
 */
public final class Rule {
	String code;

	public Rule() {
		code = "";
	}

	/**
	 * Copy constructor.
	 * @param rule the object to copy
	 */
	public Rule(Rule rule) {
		this.code = rule.code;
	}

	/**
	 * @return the ruleCode
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param ruleCode the ruleCode to set or a empty string if this doesn't exist
	 * @throws NullPointerException if ruleCode is null
	 */
	public void setCode(String ruleCode) {
		Objects.requireNonNull(ruleCode);
		this.code = ruleCode;
	}

	@Override
	public String toString() {
		return getCode();
	}
	
	/**
	 * Create a copy of this object.
	 * @return the copy;
	 */
	public Rule copy() {
		return new Rule(this);
	}

}
