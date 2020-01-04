package model.epanet.element.optionsreport;

public final class Time {
	String code;
	
	public Time() {
		// TODO Auto-generated constructor stub
	}

	public Time(Time time) {
		this.code = time.code;
	}
	
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
	
	/**
	 * Copy this object. 
	 * @return the copy
	 */
	public Time copy() {
		return new Time(this);
	}
}
