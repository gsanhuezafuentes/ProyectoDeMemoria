package epanet.jna;

/**
 * This class denote the params code that can be modified or obtained of a node.
 * 
 * A node can be a junction, tank or reservoir.
 * 
 * Parameters {@link #EN_BASEDEMAND}, {@link #EN_HEAD}, {@link #EN_PRESSURE},
 * {@link #EN_QUALITY} and {@link #EN_SOURCEMASS} are computed values. The
 * others are input design parameters. <br>
 * <br>
 * The {@link #EN_SOURCETYPE} are identified by constant defined in
 * {@link EpanetSourceTypeConstant}. <br>
 * <br>
 * The codes for the various tank mixing model choices are defined in
 * {@link EpanetTankMixingModel}.
 * 
 * The elements {@link #EN_MIXMODEL}, {@link #EN_MIXZONEVOL},
 * {@link #EN_TANKDIAM }, {@link #EN_VOLCURVE }, {@link #EN_MINLEVEL },
 * {@link #EN_MAXLEVEL}, {@link #EN_MIXFRACTION } and {@link #EN_TANK_KBULK }
 * only can be used for tank.
 * 
 * 
 */
public enum EpanetNodeParameter {
	EN_ELEVATION(0, "Elevation"), //
	EN_BASEDEMAND(1, "Base demand"), //
	EN_PATTERN(2, "Demand pattern index"), //
	EN_EMITTER(3, "Emitter coeff."), //
	EN_INITQUAL(4, "Initial quality"), //
	EN_SOURCEQUAL(5, "Source quality"), //
	EN_SOURCEPAT(6, "Source pattern index"), //
	/**
	 * 
	 * The value that can be taken with this parameters are defined in
	 * {@link #EN_SOURCETYPE}.
	 * 
	 */
	EN_SOURCETYPE(7, "Source type"), //
	EN_TANKLEVEL(8, "Initial water level in tank "), //
	EN_DEMAND(9, "Actual demand "), //
	EN_HEAD(10, "Hydraulic head "), //
	EN_PRESSURE(11, "Pressure "), //
	EN_QUALITY(12, "Actual quality "), //
	EN_SOURCEMASS(13, "Mass flow rate per minute of a chemical source "), //
	EN_INITVOLUME_STORAGETANK(14, "Initial water volume "), //

	// Only for tanks

	/**
	 * The value that can be taken with this parameters are defined in
	 * {@link EpanetTankMixingModel}.
	 */
	EN_MIXMODEL(15, "Mixing model code"), //
	EN_MIXZONEVOL(16, "Inlet/Outlet zone volume in a 2-compartment tank "), //
	EN_TANKDIAM(17, "Tank diameter "), EN_MINVOLUME_STORAGETANK(18, "Minimum water volume "), //
	EN_VOLCURVE(19, "Index of volume versus depth curve (0 if none assigned) "), //
	EN_MINLEVEL(20, "Minimum water level "), //
	EN_MAXLEVEL(21, "Maximum water level "), //
	EN_MIXFRACTION(22, "Fraction of total volume occupied by the inlet/outlet zone in a 2-compartment tank "), //
	EN_TANK_KBULK(23, "Bulk reaction rate coefficient ");

	private int code;
	private String description;

	private EpanetNodeParameter(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetNodeParameter.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public EpanetNodeParameter convert(int code) {
		EpanetNodeParameter[] types = EpanetNodeParameter.values();
		for (EpanetNodeParameter type : types) {
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
