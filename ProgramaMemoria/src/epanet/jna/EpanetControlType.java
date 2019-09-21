package epanet.jna;

/**
 * Class that contain control type code.
 *
 */
public enum EpanetControlType {
	LOW_LEVEL(0, "Applies when tank level or node pressure drops below specified level"), //
	HIGH_LEVEL(1, "Applies when tank level or node pressure rises above specified level"), //
	TIMER(2, "Applies at specific time into simulation"), //
	TIME_OF_DAY(3, "Applies at specific time of day");
	
	private int code;
	private String description;
	
	private EpanetControlType(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	/**
	 * Convert a given code in a EpanetControlType.
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public EpanetControlType convert(int code) {
		EpanetControlType[] types = EpanetControlType.values();
		for (EpanetControlType type : types){
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
