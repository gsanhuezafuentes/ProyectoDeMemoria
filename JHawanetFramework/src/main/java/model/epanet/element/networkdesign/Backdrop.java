package model.epanet.element.networkdesign;

public class Backdrop {
	String code;

	/**
	 * @return the backdropCode
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param backdropCode the backdropCode to set
	 */
	public void setCode(String backdropCode) {
		this.code = backdropCode;
	}
	
	@Override
	public String toString() {
		return getCode();
	}
}
