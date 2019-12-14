package model.epanet.element.optionsreport;

public final class Option {
	private String code;

	/**
	 * @return the codeOption
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param codeOption the codeOption to set
	 */
	public void setCode(String codeOption) {
		this.code = codeOption;
	}
	
	@Override
	public String toString() {
		return getCode();
	}
}
