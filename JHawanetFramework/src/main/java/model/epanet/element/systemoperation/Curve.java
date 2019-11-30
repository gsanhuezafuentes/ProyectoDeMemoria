package model.epanet.element.systemoperation;

import java.util.ArrayList;
import java.util.List;

public class Curve {

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
		String txt = this.id;
		for (int i = 0; i < this.x.size(); i++) {
			txt += String.format("\t %f \t %f", this.x.get(i), this.y.get(i));
		}
		return txt;
	}
}
