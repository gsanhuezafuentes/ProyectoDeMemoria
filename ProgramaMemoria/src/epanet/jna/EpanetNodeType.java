package epanet.jna;

/**
 * This class denote the node type of the nodes.
 * 
 */
public enum EpanetNodeType {
	EN_JUNCTION(0, "Junction node"), EN_RESERVOIR(1, "Reservoir node "), EN_TANK(2, "Tank node");

	private int code;
	private String description;

	private EpanetNodeType(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetNodeType.
	 * 
	 * @param code The code of type.
	 * @return EpanetNodeType object
	 */
	public EpanetNodeType convert(int code) {
		EpanetNodeType[] types = EpanetNodeType.values();
		for (EpanetNodeType type : types) {
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
