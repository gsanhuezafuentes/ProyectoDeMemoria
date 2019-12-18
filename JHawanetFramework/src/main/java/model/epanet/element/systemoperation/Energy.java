package model.epanet.element.systemoperation;

public final class Energy {
	String code;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param codeline the code to set
	 */
	public void setCode(String codeline) {
		this.code = codeline;
	}
	
	@Override
	public String toString() {
		return getCode();
	}
}
