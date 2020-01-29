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
	 *
	 */
	static public enum PumpProperty {
		HEAD("HEAD"), SPEED("SPEED"), POWER("POWER"), PATTERN("PATTERN");

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

	}

	private Map<PumpProperty, Object> properties;

	public Pump() {
		this.properties = new HashMap<Pump.PumpProperty, Object>();
	}

	/**
	 * Create a pump with same values that the pump received. This is a shallow
	 * copy, i.e., If the field value is a reference to an object (e.g., a memory
	 * address) it copies the reference. If it is necessary for the object to be
	 * completely independent of the original you must ensure that you replace the
	 * reference to the contained objects.
	 * 
	 * @param pump the pump to copy
	 */
	public Pump(Pump pump) {
		super(pump);
		this.properties = new HashMap<Pump.PumpProperty, Object>(pump.properties);
	}

	/**
	 * Get the property by key. <br>
	 * <br>
	 * When key is {@link PumpProperty#HEAD} value has to be {@link Curve} <br>
	 * <br>
	 * When key is {@link PumpProperty#PATTERN} value has to be {@link Pattern} <br>
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
	 * When key is {@link PumpProperty#HEAD} value has to be {@link Curve} <br>
	 * <br>
	 * When key is {@link PumpProperty#PATTERN} value has to be {@link Pattern} <br>
	 * <br>
	 * When key is {@link PumpProperty#SPEED} value has to be {@link Double} <br>
	 * <br>
	 * When key is {@link PumpProperty#POWER} value has to be {@link Double}
	 * 
	 * 
	 * @param key   the key
	 * @param value the value (Double, Curve or Pattern)
	 */
	public void setProperty(PumpProperty key, Object value) {
		Objects.requireNonNull(value);

		if (PumpProperty.HEAD == key) {
			if (!(value instanceof Curve)) {
				throw new RuntimeException("When key is " + key.getName() + " the value as to be a Curve ");
			}
		} else if (PumpProperty.PATTERN == key) {
			if (!(value instanceof Pattern)) {
				throw new RuntimeException("When key is " + key.getName() + " the value as to be a Pattern ");
			}
		} else {
			if (!(value instanceof Double)) {
				throw new RuntimeException("When key is " + key.getName() + " the value as to be a Double ");
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

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getId()));
		txt.append(String.format("%-10s\t", getNode1().getId()));
		txt.append(String.format("%-10s\t", getNode2().getId()));

		for (PumpProperty key : this.properties.keySet()) {
			if (PumpProperty.HEAD == key) {
				Curve curve = (Curve) this.getProperty(key);
				txt.append(String.format("HEAD %-10s\t", curve.getId()));

			} else if (PumpProperty.PATTERN == key) {
				Pattern pattern = (Pattern) this.getProperty(key);
				txt.append(String.format("PATTERN %-10s\t", pattern.getId()));

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
