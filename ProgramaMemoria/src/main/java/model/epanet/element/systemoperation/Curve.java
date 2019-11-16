package model.epanet.element.systemoperation;

import java.util.ArrayList;
import java.util.List;

public class Curve {

	public static enum Type {
		PUMP("PUMP"), EFFICIENCY("EFFICIENCY"), VOLUME("VOLUME"), HEADLOSS("HEADLOSS");

		private String name;

		private Type(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

	}
	
	private String id;
	private List<Integer> x;
	private List<Integer> y;
	private Type type;
	
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

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the x
	 */
	public List<Integer> getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public List<Integer> getY() {
		return y;
	}
	
}
