package epanet.jna;

/**
 * This class denote the link type of the link.
 * 
 * 
 */
public enum EpanetLinkType {
	EN_CVPIPE(0, "Pipe with Check Valve"), 
	EN_PIPE(1, "Pipe"), 
	EN_PUMP(2, "Pump"), 
	EN_PRV(3, "Pressure Reducing Valve"),
	EN_PSV(4, "Pressure Sustaining Valve"),
	EN_PBV(5, "Pressure Breaker Valve"),
	EN_FCV(6, "Flow Control Valve"),
	EN_TCV(7, "Throttle Control Valve"),
	EN_GPV(8, "General Purpose Valve");
	
	private int code;
	private String description;
	
	private EpanetLinkType(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	/**
	 * Convert a given code in a EpanetLinkType.
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetLinkType convert(int code) {
		EpanetLinkType[] types = EpanetLinkType.values();
		for (EpanetLinkType type : types){
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException("Don't exist a type for code " + code + "in " + EpanetLinkType.class.getSimpleName());
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
