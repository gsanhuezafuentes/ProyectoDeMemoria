package model.epanet.element.optionsreport;

import java.util.Objects;

public final class Option {
	/**
	 * This are enum of flow unit. This are the next:<br>
	 * <br>
	 * 
	 * <pre>
	 * Flow CFS (cubic feet / sec)
	 * GPM (gallons / min)
	 * MGD (million gal / day)
	 * IMGD (Imperial MGD)
	 * AFD (acre-feet / day)
	 * LPS (liters / sec)
	 * LPM (liters / min)
	 * MLD (megaliters / day)
	 * CMH (cubic meters / hr)
	 * CMD (cubic meters / day)
	 * </pre>
	 * 
	 *
	 * 
	 */
	public static enum FlowUnit {
		CFS("CFS"), GPM("GPM"), MGD("MGD"), IMGD("MGD"), AFD("AFD"), LPS("LPS"), LPM("LPM"), MLD("MLD"), CMH("CMH"),
		CMD("CMD");

		private String name;

		private FlowUnit(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Parse the name to a object of the enum class if exist. if name no exist in enum class so return null;
		 * @param name the name of object
		 * @return the object of enum class or null if no exist
		 */
		public static FlowUnit parse(String name) {
			for (FlowUnit object : FlowUnit.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			return null;
		}
	}

	/**
	 * This are enum of headloss formule. This are the next:<br>
	 * <br>
	 * 
	 * <pre>
	 * Hazen-Williams (H-W)
	 * Darcy-Weisbach (D-W)
	 * Chezy-Manning (C-M)
	 * </pre>
	 * 
	 */
	public static enum HeadlossFormule {
		HW("H-W"), DW("D-W"), CM("C-M");

		private String name;

		private HeadlossFormule(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Parse the name to a object of the enum class if exist. if name no exist in enum class so return null;
		 * @param name the name of object
		 * @return the object of enum class or null if no exist
		 */
		public static HeadlossFormule parse(String name) {
			for (HeadlossFormule object : HeadlossFormule.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			return null;
		}
	}

	private final static FlowUnit DEFAULT_FLOWUNIT = FlowUnit.GPM; // CFS/GPM/MGD/IMGD/AFD/LPS/LPM/MLD/CMH/CMD
	private final static HeadlossFormule DEFAULT_HEADLOSS_FORMULE = HeadlossFormule.HW; // H-W/D-W/C-M
	private final static String DEFAULT_HYDRAULIC = ""; // USE/SAVE filename
	private final static double DEFAULT_VISCOSITY = 1;
	private final static double DEFAULT_SPECIFIC_GRAVITY = 1;
	private final static double DEFAULT_TRIALS = 40;
	private final static double DEFAULT_ACCURACY = 0.001;
	private final static String DEFAULT_UNBALANCED = "CONTINUE 10"; // STOP/CONTINUE/CONTINUE n
	private final static String DEFAULT_PATTERN = "1"; // ID
	private final static double DEFAULT_DEMAND_MULTIPLIER = 1.0;
	private final static double DEFAULT_EMITTER_EXPONENT = 0.5;
	private final static String DEFAULT_MAP = ""; // filename
	private final static double DEFAULT_CHECKFREQ = 2;
	private final static double DEFAULT_MAXCHECK = 10;
	private final static double DEFAULT_DAMPLIMIT = 0;

	private FlowUnit flowUnit; // CFS/GPM/MGD/IMGD/AFD/LPS/LPM/MLD/CMH/CMD
	private HeadlossFormule headlossFormule; // H-W/D-W/C-M
	private String hydraulic; // USE/SAVE filename
	private double viscosity;
	private double specificGravity;
	private double trials;
	private double accuracy;
	private String unbalanced; // STOP/CONTINUE/CONTINUE n
	private String pattern; // ID
	private double demandMultiplier;
	private double emitterExponent;
	private String map; // filename
	private double checkfreq;
	private double maxcheck;
	private double damplimit;

	public Option() {
		this.flowUnit = DEFAULT_FLOWUNIT; // CFS/GPM/MGD/IMGD/AFD/LPS/LPM/MLD/CMH/CMD
		this.headlossFormule = DEFAULT_HEADLOSS_FORMULE; // H-W/D-W/C-M
		this.hydraulic = DEFAULT_HYDRAULIC; // USE/SAVE filename
		this.viscosity = DEFAULT_VISCOSITY;
		this.specificGravity = DEFAULT_SPECIFIC_GRAVITY;
		this.trials = DEFAULT_TRIALS;
		this.accuracy = DEFAULT_ACCURACY;
		this.unbalanced = DEFAULT_UNBALANCED; // STOP/CONTINUE/CONTINUE n
		this.pattern = DEFAULT_PATTERN; // ID
		this.demandMultiplier = DEFAULT_DEMAND_MULTIPLIER;
		this.emitterExponent = DEFAULT_EMITTER_EXPONENT;
		this.map = DEFAULT_MAP; // filename
		this.checkfreq = DEFAULT_CHECKFREQ;
		this.maxcheck = DEFAULT_MAXCHECK;
		this.damplimit = DEFAULT_DAMPLIMIT;

	}

	/**
	 * Copy constructor.
	 * 
	 * @param option the option object
	 */
	public Option(Option option) {
		this.flowUnit = option.flowUnit;
		this.headlossFormule = option.headlossFormule;
		this.hydraulic = option.hydraulic;
		this.viscosity = option.viscosity;
		this.specificGravity = option.specificGravity;
		this.trials = option.trials;
		this.accuracy = option.accuracy;
		this.unbalanced = option.unbalanced;
		this.pattern = option.pattern;
		this.demandMultiplier = option.demandMultiplier;
		this.emitterExponent = option.emitterExponent;
		this.map = option.map;
		this.checkfreq = option.checkfreq;
		this.maxcheck = option.maxcheck;
		this.damplimit = option.damplimit;
	}

	/**
	 * Get the flowUnit
	 * 
	 * @return the flowUnit
	 */
	public FlowUnit getFlowUnit() {
		return flowUnit;
	}

	/**
	 * Set the flowUnit
	 * 
	 * @param flowUnit the flowUnit to set
	 * @throws NullPointerException if flowUnit is null
	 */
	public void setFlowUnit(FlowUnit flowUnit) {
		Objects.requireNonNull(flowUnit);
		this.flowUnit = flowUnit;
	}

	/**
	 * Get the headlossFormule
	 * 
	 * @return the headlossFormule
	 */
	public HeadlossFormule getHeadlossFormule() {
		return headlossFormule;
	}

	/**
	 * Set the headlossFormule
	 * 
	 * @param headlossFormule the headlossFormule to set
	 * @throws NullPointerException if headlossFormule is null
	 */
	public void setHeadlossFormule(HeadlossFormule headlossFormule) {
		Objects.requireNonNull(headlossFormule);
		this.headlossFormule = headlossFormule;
	}

	/**
	 * Get the hydraulic
	 * 
	 * @return the hydraulic
	 */
	public String getHydraulic() {
		return hydraulic;
	}

	/**
	 * Set the hydraulic. The format should be USE|SAVE filename
	 * 
	 * @param hydraulic the hydraulic to set or a empty string if it doesn't exist
	 * @throws NullPointerException if hydraulic is null
	 */
	public void setHydraulic(String hydraulic) {
		Objects.requireNonNull(hydraulic);
		this.hydraulic = hydraulic;
	}

	/**
	 * Get the viscosity
	 * 
	 * @return the viscosity
	 */
	public double getViscosity() {
		return viscosity;
	}

	/**
	 * Set the viscosity
	 * 
	 * @param viscosity the viscosity to set
	 */
	public void setViscosity(double viscosity) {
		this.viscosity = viscosity;
	}

	/**
	 * Get the specificGravity
	 * 
	 * @return the specificGravity
	 */
	public double getSpecificGravity() {
		return specificGravity;
	}

	/**
	 * Set the specificGravity
	 * 
	 * @param specificGravity the specificGravity to set
	 */
	public void setSpecificGravity(double specificGravity) {
		this.specificGravity = specificGravity;
	}

	/**
	 * Get the trials
	 * 
	 * @return the trials
	 */
	public double getTrials() {
		return trials;
	}

	/**
	 * Set the trials
	 * 
	 * @param trials the trials to set
	 */
	public void setTrials(double trials) {
		this.trials = trials;
	}

	/**
	 * Get the accuracy
	 * 
	 * @return the accuracy
	 */
	public double getAccuracy() {
		return accuracy;
	}

	/**
	 * Set the accuracy
	 * 
	 * @param accuracy the accuracy to set
	 */
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * Get the unbalanced
	 * 
	 * @return the unbalanced
	 */
	public String getUnbalanced() {
		return unbalanced;
	}

	/**
	 * Set the unbalanced. The format should be STOP|CONTINUE|CONTINUE n.
	 * 
	 * @param unbalanced the unbalanced to set or a empty string if it does not
	 *                   exist
	 * @throws NullPointerException if unbalanced is null
	 */
	public void setUnbalanced(String unbalanced) {
		Objects.requireNonNull(unbalanced);
		this.unbalanced = unbalanced;
	}

	/**
	 * Get the pattern
	 * 
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Set the pattern
	 * 
	 * @param patternID the pattern id to set
	 * @throws NullPointerException if patternID is null
	 */
	public void setPattern(String patternID) {
		Objects.requireNonNull(patternID);
		this.pattern = patternID;
	}

	/**
	 * Get the DEMAND MULTIPLIER
	 * 
	 * @return the DEMAND MULTIPLIER
	 */
	public double getDemandMultiplier() {
		return demandMultiplier;
	}

	/**
	 * Set the DEMAND MULTIPLIER
	 * 
	 * @param demandMultiplier the DEMAND MULTIPLIER to set
	 */
	public void setDemandMultiplier(double demandMultiplier) {
		this.demandMultiplier = demandMultiplier;
	}

	/**
	 * Get the EMITTER EXPONENT
	 * 
	 * @return the EMITTER EXPONENT
	 */
	public double getEmitterExponent() {
		return emitterExponent;
	}

	/**
	 * Set the EMITTER EXPONENT
	 * 
	 * @param emitterExponent the EMITTER EXPONENT to set
	 */
	public void setEmitterExponent(double emitterExponent) {
		this.emitterExponent = emitterExponent;
	}

	/**
	 * Get the map
	 * 
	 * @return the map
	 */
	public String getMap() {
		return map;
	}

	/**
	 * Set the map
	 * 
	 * @param filepath the map to set
	 * @throws NullPointerException if filepath is null
	 */
	public void setMap(String filepath) {
		Objects.requireNonNull(filepath);
		this.map = filepath;
	}

	/**
	 * Get the CHECKFREQ
	 * 
	 * @return the CHECKFREQ
	 */
	public double getCheckfreq() {
		return checkfreq;
	}

	/**
	 * Set the CHECKFREQ
	 * 
	 * @param checkfreq the CHECKFREQ to set
	 */
	public void setCheckfreq(double checkfreq) {
		this.checkfreq = checkfreq;
	}

	/**
	 * Get the MAXCHECK
	 * 
	 * @return the MAXCHECK
	 */
	public double getMaxcheck() {
		return maxcheck;
	}

	/**
	 * Set the MAXCHECK
	 * 
	 * @param maxcheck the MAXCHECK to set
	 */
	public void setMaxcheck(double maxcheck) {
		this.maxcheck = maxcheck;
	}

	/**
	 * Get the DAMPLIMIT
	 * 
	 * @return the DAMPLIMIT
	 */
	public double getDamplimit() {
		return damplimit;
	}

	/**
	 * Set the DAMPLIMIT
	 * 
	 * @param damplimit the DAMPLIMIT to set
	 */
	public void setDamplimit(double damplimit) {
		this.damplimit = damplimit;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("UNITS\t%10s\n", this.flowUnit.getName()));
		txt.append(String.format("HEADLOSS\t%10s\n", this.headlossFormule.getName()));
		txt.append(String.format("HYDRAULICS\t%10s\n", this.hydraulic));
		txt.append(String.format("VISCOSITY\t%10f\n", this.viscosity));
		txt.append(String.format("SPECIFIC GRAVITY\t%10f\n", this.specificGravity));
		txt.append(String.format("TRIALS\t%10f\n", this.trials));
		txt.append(String.format("ACCURACY\t%10f\n", this.accuracy));
		txt.append(String.format("UNBALANCED\t%10s\n", this.unbalanced));
		txt.append(String.format("PATTERN\t%10s\n", this.pattern));
		txt.append(String.format("DEMAND MULTIPLIER\t%10f\n", this.demandMultiplier));
		txt.append(String.format("EMITTER EXPONENT\t%10s\n", this.emitterExponent));
		txt.append(String.format("MAP\t%10s\n", this.map));
		txt.append(String.format("CHECKFREQ\t%10f\n", this.checkfreq));
		txt.append(String.format("MAXCHECK\t%10f\n", this.maxcheck));
		txt.append(String.format("DAMPLIMIT\t%10f\n", this.damplimit));
		return txt.toString();
	}

	/**
	 * Copy this object.
	 * 
	 * @return the copy.
	 */
	public Option copy() {
		return new Option(this);
	}
}
