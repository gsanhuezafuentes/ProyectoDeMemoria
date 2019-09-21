package epanet.jna;

/**
 * Class with Flow units using in epanet.
 *
 */
public enum EpanetFlowUnits {
	EN_CFS(0, "cubic feet per second"), //
	EN_GPM(1, "gallons per minute"), //
	EN_MGD(2, "million gallons per day"), //
	EN_IMGD(3, "Imperial mgd"), //
	EN_AFD(4, "acre-feet per day"), //
	EN_LPS(5, "liters per second"), //
	EN_LPM(6, "liters per minute"), //
	EN_MLD(7, "million liters per day"), //
	EN_CMH(8, "cubic meters per hour"), //
	EN_CMD(9, "cubic meters per day");

	private int code;
	private String description;

	private EpanetFlowUnits(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetQualType.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public EpanetFlowUnits convert(int code) {
		EpanetFlowUnits[] types = EpanetFlowUnits.values();
		for (EpanetFlowUnits type : types) {
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
