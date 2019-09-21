package epanet.jna;

/**
 * Class that denote the option code.
 *
 */
public enum EpanetOptionCodes {
	EN_TRIALS(0, "The max number of trials use to solve network hydraulic at each hydraulic time step of a simulation",
			40),
	EN_ACCURACY(1, "The convergence criterion that determinates when a hydraulic solution has been reached", 0.001),
	EN_TOLERANCE(2,
			"The difference in water quality level below which we can say that a one parcel of water is essentially the same as another",
			0.01),
	EN_EMITEXPON(3,
			"The power to which the pressure at a junction is raised when computing the flow issuing from an emitter",
			0.5),
	EN_DEMANDMULT(4, "Adjust the value of baseline demands for all junctions and all demand categories", 1.0);

	private int code;
	private String description;
	private double defaultValue;

	private EpanetOptionCodes(int code, String description, double defaultValue) {
		this.code = code;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	/**
	 * Convert a given code in a EpanetOptionCodes.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public EpanetOptionCodes convert(int code) {
		EpanetOptionCodes[] types = EpanetOptionCodes.values();
		for (EpanetOptionCodes type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException("Don't exist a type for code " + code + "in " + getClass().getSimpleName());
	}

	/**
	 * @return the defaultValue
	 */
	public double getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
