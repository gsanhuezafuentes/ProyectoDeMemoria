package epanet.jna;

/**
 * This class denote the tank mixing model taken when {@link EpanetNodeParameter#EN_MIXMODEL} is used.
 *
 */
public enum EpanetTankMixingModel {
	EN_MIX1(0, "Single compartment, complete mix model"), //
	EN_MIX2(1, "Two-compartment, complete mix model"), //
	EN_FIFO(2, "Plug flow, first in, first out model"), //
	EN_LIFO(3, "Stacked plug flow, last in, first out model");
	
	private int code;
	private String description;
	
	private EpanetTankMixingModel(int code, String description) {
		this.code = code;
		this.description = description;
	}
	
	/**
	 * Convert a given code in a EpanetTankMixingModel.
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetTankMixingModel convert(int code) {
		EpanetTankMixingModel[] types = EpanetTankMixingModel.values();
		for (EpanetTankMixingModel type : types){
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException("Don't exist a type for code " + code + "in " + EpanetTankMixingModel.class.getSimpleName());
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
