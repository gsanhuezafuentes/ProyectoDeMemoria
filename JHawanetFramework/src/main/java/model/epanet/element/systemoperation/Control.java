package model.epanet.element.systemoperation;

import java.util.Objects;

public final class Control {
	private String code;

	public Control() {
		this.code = "";
	}

	/**
	 * Copy constructor.
	 * 
	 * @param control the object to copy
	 */
	public Control(Control control) {
		this.code = control.code;
	}

	
	/**
	 * Get the code
	 * @return the code or empty string if there is does not code
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * Set the code
	 * @param code the code to set or a empty string if there is no code control
	 * @throws if code is null
	 */
	public void setCode(String code) {
		Objects.requireNonNull(code);
		this.code = code;
	}

	/**
	 * Copy the object.
	 * 
	 * @return a copy of this object
	 */
	public Control copy() {
		return new Control(this);
	}

}
