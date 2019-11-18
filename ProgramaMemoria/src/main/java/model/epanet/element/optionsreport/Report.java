package model.epanet.element.optionsreport;

public class Report {
	private String code;

	/**
	 * @return the reportCode
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param reportCode the reportCode to set
	 */
	public void setCode(String reportCode) {
		this.code = reportCode;
	}
	
	@Override
	public String toString() {
		return getCode();
	}
}
