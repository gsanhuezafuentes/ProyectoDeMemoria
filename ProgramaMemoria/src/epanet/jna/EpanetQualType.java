package epanet.jna;

/**
 * Class that denote the type of water quality analysis.
 *
 */
public enum EpanetQualType {
	EN_NONE(0, "No quality analysis"), //
	EN_CHEM(1, "Chemical analysis"), //
	EN_AGE(2, "Water age analysis"), //
	EN_TRACE(3, "Source tracing");

	private int code;
	private String description;

	private EpanetQualType(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetQualType.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetQualType convert(int code) {
		EpanetQualType[] types = EpanetQualType.values();
		for (EpanetQualType type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException("Don't exist a type for code " + code + "in " + EpanetQualType.class.getSimpleName());
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
