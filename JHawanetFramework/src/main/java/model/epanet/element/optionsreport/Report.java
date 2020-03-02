package model.epanet.element.optionsreport;

/**
 * This class contains the value of [REPORT] section of a inp.
 *
 */
public final class Report {
	//code contains the code of all report section
	private String code;
	
	public Report() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Copy constructor.
	 * @param report the object to copy
	 */
	public Report(Report report) {
		this.code = report.code;
	}
	
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
	
	/**
	 * Copy this object.
	 * @return the copy
	 */
	public Report copy() {
		return new Report(this);
	}
}
