package model.epanet.element.networkcomponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import model.epanet.element.systemoperation.Curve;
import model.epanet.element.systemoperation.Pattern;

public final class Pump extends Link {

	/**
	 * Valid options to properties.
	 * 
	 */
	public static enum PumpProperty {
		/*
		 * This value has to be used passing with a Curve
		 */
		HEAD("HEAD"),
		/*
		 * This value has to be used passing a Double SPEED is saved in [PUMP] section
		 * and in [Status] section
		 */
		SPEED("SPEED"),
		/*
		 * This value has to be used passing a Double
		 */
		POWER("POWER"),
		/*
		 * This value has to be used passing a Pattern
		 */
		PATTERN("PATTERN");

		private String name;

		private PumpProperty(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
		public static PumpStatus parse(String name) {
			for (PumpStatus object : PumpStatus.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			throw new IllegalArgumentException("There are not a valid element with the name " + name);
		}

	}

	/**
	 * A enumerator that to define the status of Pump. This is saved in [Status]
	 * section of inp.
	 * 
	 */
	public static enum PumpStatus {
		OPEN("OPEN"), CLOSED("CLOSED");

		private String name;

		private PumpStatus(String name) {
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
		public static PumpStatus parse(String name) {
			for (PumpStatus object : PumpStatus.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			return null;
		}

	}

	public static final PumpStatus DEFAULT_STATUS = PumpStatus.OPEN;

	private Map<PumpProperty, Object> properties;
	private PumpStatus status;
	
	/*
	 * This field is saved in [Energy] section using the key Pump
	 */
	private String efficiencyCurve;
	/*
	 * This field is saved in [Energy] section using the key Pump
	 */
	private Double energyPrice;
	/*
	 * This field is saved in [Energy] section using the key Pump
	 */
	private String patternPrice;

	public Pump() {
		this.status = DEFAULT_STATUS;
		this.efficiencyCurve = "";
		this.patternPrice = "";
		this.properties = new HashMap<Pump.PumpProperty, Object>();
	}

	/**
	 * Create a pump with same values that the pump received. This is a shallow
	 * copy, i.e., If the field value is a reference to an object (e.g., a memory
	 * address) it copies the reference. If it is necessary for the object to be
	 * completely independent of the original you must ensure that you replace the
	 * reference to the contained objects.
	 * 
	 * 
	 * You must replace node1 and node2 to do the copy independent of the original
	 * 
	 * @param pump the pump to copy
	 */
	public Pump(Pump pump) {
		super(pump);
		this.properties = new HashMap<Pump.PumpProperty, Object>(pump.properties);
		this.energyPrice = pump.energyPrice;
		this.efficiencyCurve = pump.efficiencyCurve;
		this.patternPrice = pump.patternPrice;
	}

	/**
	 * Get the property by key. <br>
	 * <br>
	 * When key is {@link PumpProperty#HEAD} value has to be the id of the Curve has a String <br>
	 * <br>
	 * When key is {@link PumpProperty#PATTERN} value has to be the id of a Pattern has a String <br>
	 * <br>
	 * When key is {@link PumpProperty#SPEED} value has to be {@link Double} <br>
	 * <br>
	 * When key is {@link PumpProperty#POWER} value has to be {@link Double} <br>
	 * <br>
	 * When key is null so the property isn't setting up
	 * 
	 * @param key
	 * @return value the value (Double, Curve or Pattern or null)
	 */
	public Object getProperty(PumpProperty key) {
		return this.properties.get(key);
	}

	/**
	 * Set the property. <br>
	 * <br>
	 * When key is {@link PumpProperty#HEAD} value has to be the id of the Curve has a String <br>
	 * <br>
	 * When key is {@link PumpProperty#PATTERN} value has to be the id of a Pattern has a String <br>
	 * <br>
	 * When key is {@link PumpProperty#SPEED} value has to be {@link Double} <br>
	 * <br>
	 * When key is {@link PumpProperty#POWER} value has to be {@link Double}
	 * 
	 * {@link PumpProperty#POWER} and {@link PumpProperty#HEAD} cannot be configured
	 * at the same time. The last addition is the one that remains.
	 * 
	 * @param key   the key
	 * @param value the value (Double, Curve or Pattern)
	 * @throws IllegalArgumentException if value type is not valid to key used.
	 */
	public void setProperty(PumpProperty key, Object value) {
		Objects.requireNonNull(value);

		if (PumpProperty.HEAD == key) {
			if (!(value instanceof String)) {
				throw new IllegalArgumentException("The value of the key " + key.getName() + " is not of type String");
			}
			// if the property to set is HEAD so remove Power
			this.properties.remove(PumpProperty.POWER);
		} else if (PumpProperty.PATTERN == key) {
			if (!(value instanceof String)) {
				throw new IllegalArgumentException("The value of the key " + key.getName() + "is not of type String");
			}

		} else {
			if (!(value instanceof Double)) {
				throw new IllegalArgumentException("The value of the key " + key.getName() + "is not of type Double");
			}
			// if the property to set is Power so remove HEAD
			if (key == PumpProperty.POWER) {
				this.properties.remove(PumpProperty.HEAD);
			}
		}
		this.properties.put(key, value);
	}

	/**
	 * Get the keys of properties configured.
	 * 
	 * @return the keys
	 */
	public Set<PumpProperty> getPropertyKeys() {
		return this.properties.keySet();
	}

	/**
	 * Get the status of this pump
	 * @return the status
	 */
	public PumpStatus getStatus() {
		return status;
	}

	/**
	 * Set the status of this pipe
	 * @param status the status to set
	 * @throws NullPointerException if status is null
	 */
	public void setStatus(PumpStatus status) {
		Objects.requireNonNull(status);
		this.status = status;
	}

	/**
	 * Get the efficiency curve
	 * 
	 * @return the efficiencyCurve or a empty string if not exist
	 */
	public String getEfficiencyCurve() {
		return efficiencyCurve;
	}

	/**
	 * Set the efficiency curve. It receive the id of the curve
	 * 
	 * @param efficiencyCurve the efficiencyCurve to set or a empty string if it doesn't exist
	 * @throws NullPointerException if efficiencyCurve is null
	 */
	public void setEfficiencyCurve(String efficiencyCurve) {
		Objects.requireNonNull(efficiencyCurve);
		this.efficiencyCurve = efficiencyCurve;
	}

	/**
	 * Get the energy price
	 * 
	 * @return the energyPrice or null if not exist
	 */
	public Double getEnergyPrice() {
		return energyPrice;
	}

	/**
	 * Set the energy price
	 * 
	 * @param energyPrice the energyPrice to set or null if not exist
	 */
	public void setEnergyPrice(Double energyPrice) {
		this.energyPrice = energyPrice;
	}

	/**
	 * Get the pattern price
	 * 
	 * @return the patternPrice or null if not exist
	 */
	public String getPatternPrice() {
		return patternPrice;
	}

	/**
	 * Set the pattern price
	 * 
	 * @param patternPrice the patternPrice to set or a empty string if it doesn't exist
	 * @throws if patternPrice is null
	 */
	public void setPatternPrice(String patternPrice) {
		Objects.requireNonNull(patternPrice);
		this.patternPrice = patternPrice;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getId()));
		txt.append(String.format("%-10s\t", getNode1().getId()));
		txt.append(String.format("%-10s\t", getNode2().getId()));

		for (PumpProperty key : this.properties.keySet()) {
			if (PumpProperty.HEAD == key) {
				Curve curve = (Curve) this.getProperty(key);
				txt.append(String.format("HEAD %-10s\t", curve));

			} else if (PumpProperty.PATTERN == key) {
				Pattern pattern = (Pattern) this.getProperty(key);
				txt.append(String.format("PATTERN %-10s\t", pattern));

			} else if (PumpProperty.POWER == key) {
				double value = (Double) this.getProperty(key);
				txt.append(String.format("POWER %-10f\t", value));

			} else if (PumpProperty.SPEED == key) {
				double value = (Double) this.getProperty(key);
				txt.append(String.format("SPEED %-10f\t", value));

			}
		}
		String description = getDescription();
		if (description != null) {
			txt.append(String.format(";%s", description));
		}

		return txt.toString();
	}

	/**
	 * Copy this instance. This is a shallow copy. return the copy
	 */
	@Override
	public Pump copy() {
		return new Pump(this);
	}

}
