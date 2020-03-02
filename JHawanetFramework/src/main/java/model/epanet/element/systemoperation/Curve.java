package model.epanet.element.systemoperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import model.epanet.element.utils.Point;

public final class Curve {

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

		public static Type parse(String name) {
			for (Type object : Type.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			throw new IllegalArgumentException("There are not a valid element with the name " + name);
		}

	}

	private String id;
	private List<Point> points;
//	private Type type;
	private String description;
	private Type type = Type.PUMP; // default value

	public Curve() {
		this.points = new ArrayList<>();
	}

	/**
	 * Copy constructor
	 * 
	 * @param curve the object to copy
	 */
	public Curve(Curve curve) {
		this();
		this.id = curve.id;
		this.points.addAll(curve.points);
		this.description = curve.description;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 * @throws NullPointerException if id is null
	 */
	public void setId(String id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	/**
	 * Get the type of curve
	 * 
	 * @return the type
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Set the type of curve
	 * 
	 * @param type the type to set
	 * @throws NullPointerException if type is null
	 */
	public void setType(Type type) {
		Objects.requireNonNull(type);
		this.type = type;
	}

	/**
	 * Add point to the curve
	 * 
	 * @param x the x-value
	 * @param y the y-value
	 */
	public void addPointToCurve(double x, double y) {
		this.points.add(new Point(x, y));
	}

	/**
	 * Get the point in the index
	 * @param index the index of point
	 * @return the point
	 */
	public Point getPoint(int index) {
		return this.points.get(index);
	}
	
	/**
	 * Get the number of point in curve
	 * @return the number of point
	 */
	public int getNumberOfPoint() {
		return this.points.size();
	}

	/**
	 * Get the description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description
	 * 
	 * @param description the description to set or a empty string if it doesn't
	 *                    exist
	 * @throws NullPointerException if description is null
	 */
	public void setDescription(String description) {
		Objects.requireNonNull(description);
		this.description = description;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", this.id));
		for (int i = 0; i < this.points.size(); i++) {
			Point point = this.points.get(0);
			txt.append(String.format("%-10f \t %-10f", point.getX(), point.getY()));
		}
		return txt.toString();
	}

	/**
	 * Copy this object.
	 * 
	 * @return a copy of this object
	 */
	public Curve copy() {
		return new Curve(this);
	}
}
