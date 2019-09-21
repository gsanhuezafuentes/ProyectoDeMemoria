package epanet.jna;

/**
 * Class that represent component codes
 *
 */
public enum EpanetCountComponentCode {
	EN_NODECOUNT(0, "Nodes"), //
	EN_TANKCOUNT(1, "Reservoirs and tank nodes"), //
	EN_LINKCOUNT(2, "Links"), //
	EN_PATCOUNT(3, "Time patterns"), //
	EN_CURVECOUNT(4, "Curves"), //
	EN_CONTROLCOUNT(5, "Simple controls");

	private int code;
	private String description;

	private EpanetCountComponentCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetCountComponentCode.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public EpanetCountComponentCode convert(int code) {
		EpanetCountComponentCode[] types = EpanetCountComponentCode.values();
		for (EpanetCountComponentCode type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException("Don't exist a type for code " + code + "in " + getClass().getSimpleName());
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
