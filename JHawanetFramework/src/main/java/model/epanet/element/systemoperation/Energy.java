package model.epanet.element.systemoperation;

public final class Energy {
	String code;

	public Energy() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Copy constructor. Realize a copy of the received energy.
	 * 
	 * @param energy the object to copy.
	 */
	public Energy(Energy energy) {
		this.code = energy.code;
	}

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

	/**
	 * Create a copy of the object.
	 * 
	 * @return the copy.
	 */
	public Energy copy() {
		return new Energy(this);
	}
}
