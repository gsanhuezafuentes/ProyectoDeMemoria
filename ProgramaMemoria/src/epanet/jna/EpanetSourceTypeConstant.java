package epanet.jna;

/**
 * This class denote the tank mixing model taken when {@link EpanetNodeParameter#EN_SOURCETYPE} is used.
 *
 */
public enum EpanetSourceTypeConstant {
	EN_CONCEN(0, ""), //
	EN_MASS(1, ""), //
	EN_SETPOINT(2, ""), //
	EN_FLOWPACED(3, "");

	private int code;
	private String description;
	
	private EpanetSourceTypeConstant(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	/**
	 * Convert a given code in a EpanetSourceTypeConstant.
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetSourceTypeConstant convert(int code) {
		EpanetSourceTypeConstant[] types = EpanetSourceTypeConstant.values();
		for (EpanetSourceTypeConstant type : types){
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
