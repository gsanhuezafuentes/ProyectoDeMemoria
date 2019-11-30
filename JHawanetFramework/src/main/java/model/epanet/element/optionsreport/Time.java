package model.epanet.element.optionsreport;

public class Time {
	String code;

	/**
	 * @return the timeCode
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param timeCode the timeCode to set
	 */
	public void setCode(String timeCode) {
		this.code = timeCode;
	}
	
	@Override
	public String toString() {
		return getCode();
	}
}
