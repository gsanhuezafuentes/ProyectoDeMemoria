package model.epanet.element.optionsreport;

public final class Option {
	private String code;
	
	public Option() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Copy constructor. 
	 * @param option
	 */
	public Option(Option option) {
		this.code = option.code;
	}

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
	
	/**
	 * Copy this object.
	 * @return the copy.
	 */
	public Option copy() {
		return new Option(this);
	}
}
