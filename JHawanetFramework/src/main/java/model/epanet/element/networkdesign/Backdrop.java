package model.epanet.element.networkdesign;

public final class Backdrop {
	String code;

	public Backdrop() {
		// TODO Auto-generated constructor stub
	}

	public Backdrop(Backdrop backdrop) {
		this.code = backdrop.code;
	}

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

	/**
	 * Copy the object.
	 * 
	 * @return the copy
	 */
	public Backdrop copy() {
		return new Backdrop(this);
	}
}
