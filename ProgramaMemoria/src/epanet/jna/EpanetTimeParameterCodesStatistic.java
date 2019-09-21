package epanet.jna;

/**
 * Class to denote code statistic to use join with {@link EpanetTimeParameterCodes#EN_STATISTIC}.
 *
 */
public enum EpanetTimeParameterCodesStatistic {
	EN_NONE(0, "none"), //
	EN_AVERAGE(1, "averaged"), //
	EN_MINIMUM(2, "minimums"), //
	EN_MAXIMUM(3, "maximums"), //
	EN_RANGE(4, "ranges");

	private int code;
	private String description;

	private EpanetTimeParameterCodesStatistic(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetTimeParameterCodesStatistic.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public EpanetTimeParameterCodesStatistic convert(int code) {
		EpanetTimeParameterCodesStatistic[] types = EpanetTimeParameterCodesStatistic.values();
		for (EpanetTimeParameterCodesStatistic type : types) {
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
