package epanet.jna;

/**
 * Class that contain the status report
 *
 */
public enum EpanetStatusReport {
	NO_REPORTING(0, "no status reporting"), //
	NORMAL_REPORTING(1, "normal reporting"), //
	FULL_REPORTING(2, "full status reporting");

	private int code;
	private String description;

	private EpanetStatusReport(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetQualType.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public EpanetStatusReport convert(int code) {
		EpanetStatusReport[] types = EpanetStatusReport.values();
		for (EpanetStatusReport type : types) {
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
