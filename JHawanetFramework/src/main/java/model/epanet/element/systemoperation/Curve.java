package model.epanet.element.systemoperation;

import java.util.ArrayList;
import java.util.List;

public final class Curve {

//	public static enum Type {
//		PUMP("PUMP"), EFFICIENCY("EFFICIENCY"), VOLUME("VOLUME"), HEADLOSS("HEADLOSS");
//
//		private String name;
//
//		private Type(String name) {
//			this.name = name;
//		}
//
//		/**
//		 * @return the name
//		 */
//		public String getName() {
//			return name;
//		}
//
//	}
	
	private String id;
	private List<Double> x;
	private List<Double> y;
//	private Type type;
	
	public Curve() {
		this.x = new ArrayList<>();
		this.y = new ArrayList<>();
	}

	/**
	 * Copy constructor
	 * @param curve the object to copy
	 */
	public Curve(Curve curve) {
		this();
		this.id = curve.id;
		this.x.addAll(curve.x);
		this.y.addAll(curve.y);
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

//	/**
//	 * @return the type
//	 */
//	public Type getType() {
//		return type;
//	}
//
//	/**
//	 * @param type the type to set
//	 */
//	public void setType(Type type) {
//		this.type = type;
//	}

	/**
	 * Get the x value for curve. To add a new value to curve add to the list returned by this method.
	 * @return the x
	 */
	public List<Double> getX() {
		return x;
	}

	/**
	 * Get the x value for curve. To add a new value to curve add to the list returned by this method.
	 * @return the y
	 */
	public List<Double> getY() {
		return y;
	}
	
	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", this.id));
		for (int i = 0; i < this.x.size(); i++) {
			txt.append(String.format("%-10f \t %-10f", this.x.get(i), this.y.get(i)));
		}
		return txt.toString();
	}
	
	/**
	 * Copy this object.
	 * @return a copy of this object
	 */
	public Curve copy() {
		return new Curve(this);
	}
}
