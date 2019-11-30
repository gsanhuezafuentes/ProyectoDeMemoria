package model.epanet.element.networkcomponent;

import java.util.HashMap;
import java.util.Map;

import model.epanet.element.systemoperation.Curve;
import model.epanet.element.systemoperation.Pattern;

public class Pump extends Link {

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
	 * Get the property by key When key is {@link PumpProperty#HEAD} value has to be
	 * {@link Curve} <br>
	 * <br>
	 * When key is {@link PumpProperty#SPEED} value has to be {@link Double} <br>
	 * <br>
	 * When key is {@link PumpProperty#POWER} value has to be {@link Double}
	 * 
	 * @param key
	 * @return value the value (Double, Curve or Pattern)
	 */
	public Object getProperty(PumpProperty key) {
		return this.properties.get(key);
	}

	/**
	 * Set the property When key is {@link PumpProperty#HEAD} value has to be
	 * {@link Curve} <br>
	 * <br>
	 * When key is {@link PumpProperty#SPEED} value has to be {@link Double} <br>
	 * <br>
	 * When key is {@link PumpProperty#POWER} value has to be {@link Double}
	 * 
	 * @param key   the key
	 * @param value the value (Double, Curve or Pattern)
	 */
	public void setProperty(PumpProperty key, Object value) {
		if (value == null) {
			throw new RuntimeException("value can't be null");
		}
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

	@Override
	public String toString() {
		String txt = "";
		txt += getId() + "\t";
		txt += getNode1().getId() + "\t";
		txt += getNode2().getId() + "\t";

		for (PumpProperty key : this.properties.keySet()) {
			if (PumpProperty.HEAD == key) {
				Curve curve = (Curve) this.getProperty(key);
				txt += "HEAD" + " ";
				txt += curve.getId() + "\t";

			} else if (PumpProperty.PATTERN == key) {
				Pattern pattern = (Pattern) this.getProperty(key);
				txt += "PATTERN" + " ";
				txt += pattern.getId() + "\t";

			} else if (PumpProperty.POWER == key) {
				double value = (Double) this.getProperty(key);
				txt += "POWER" + " ";
				txt += value + "\t";
			} else if (PumpProperty.SPEED == key) {
				double value = (Double) this.getProperty(key);
				txt += "SPEED" + " ";
				txt += value + "\t";
			}
		}

		return txt;
	}

}
