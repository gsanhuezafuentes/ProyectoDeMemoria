package model.epanet.element.networkcomponent;

import java.util.HashMap;

import model.epanet.element.systemoperation.Curve;
import model.epanet.element.systemoperation.Pattern;

public class Pump extends Link {

	/**
	 * Valid options to properties.
	 * 
	 *
	 */
	static public enum Property {
		HEAD("HEAD"), SPEED("SPEED"), POWER("POWER"), PATTERN("PATTERN");

		private String name;

		private Property(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

	}

	private Node node1;
	private Node node2;
	private HashMap<Property, Object> properties;

	/**
	 * @return the node1
	 */
	public Node getNode1() {
		return node1;
	}

	/**
	 * @param node1 the node1 to set
	 */
	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	/**
	 * @return the node2
	 */
	public Node getNode2() {
		return node2;
	}

	/**
	 * @param node2 the node2 to set
	 */
	public void setNode2(Node node2) {
		this.node2 = node2;
	}

	/**
	 * Get the property by key When key is {@link Property#HEAD} value has to be
	 * {@link Curve} <br>
	 * <br>
	 * When key is {@link Property#SPEED} value has to be {@link Double} <br>
	 * <br>
	 * When key is {@link Property#POWER} value has to be {@link Double}
	 * 
	 * @param key
	 * @return value the value (Double, Curve or Pattern)
	 */
	public Object getProperty(Property key) {
		return this.properties.get(key);
	}

	/**
	 * Set the property When key is {@link Property#HEAD} value has to be
	 * {@link Curve} <br>
	 * <br>
	 * When key is {@link Property#SPEED} value has to be {@link Double} <br>
	 * <br>
	 * When key is {@link Property#POWER} value has to be {@link Double}
	 * 
	 * @param key   the key
	 * @param value the value (Double, Curve or Pattern)
	 */
	public void setProperty(Property key, Object value) {
		if (value == null) {
			throw new RuntimeException("value can't be null");
		}
		if (Property.HEAD == key) {
			if (!(value instanceof Curve)) {
				throw new RuntimeException("When key is " + key.getName() + " the value as to be a Curve ");
			}
		} else if (Property.PATTERN == key) {
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

}
