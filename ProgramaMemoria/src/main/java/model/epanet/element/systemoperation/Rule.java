package model.epanet.element.systemoperation;

/**
 * 
 *
 */
public class Rule {
	String ruleId;
	String code;

	/**
	 * @return the ruleId
	 */
	public String getRuleId() {
		return ruleId;
	}

	/**
	 * @param ruleId the ruleId to set
	 */
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	/**
	 * @return the ruleCode
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param ruleCode the ruleCode to set
	 */
	public void setCode(String ruleCode) {
		this.code = ruleCode;
	}

	@Override
	public String toString() {
		String txt = "RULE"+ "\t" + getRuleId() + "\t";
		txt = getCode();
		return txt;
	}

}
