package model.epanet.element.systemoperation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.utils.Point;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Curve {

	public enum Type {
		PUMP("PUMP"), EFFICIENCY("EFFICIENCY"), VOLUME("VOLUME"), HEADLOSS("HEADLOSS");

		private final String name;

		Type(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Parse the string to the enum
		 * @param name the name
		 * @return the associated enum
		 * @throws IllegalArgumentException if name is not valid
		 */
		public static @NotNull Type parse(String name) {
			for (Type object : Type.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			throw new IllegalArgumentException("There are not a valid element with the name " + name);
		}

	}

	@NotNull private String id;
	@NotNull private final List<Point> points;
	@NotNull private String description;
	@NotNull private Type type;

	public Curve() {
		this.id = "";
		this.description = "";
		this.points = new ArrayList<>();
		this.type = Type.PUMP; // default value
	}

	/**
	 * Copy constructor
	 * 
	 * @param curve the object to copy
	 * @throws NullPointerException if curve is null
	 */
	public Curve(@NotNull Curve curve) {
		this();
		Objects.requireNonNull(curve);
		this.id = curve.id;
		this.points.addAll(curve.points);
		this.description = curve.description;
		this.type = curve.type;
	}

	/**
	 * @return the id
	 */
	public @NotNull String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 * @throws NullPointerException if id is null
	 */
	public void setId(@NotNull String id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	/**
	 * Get the type of curve
	 * 
	 * @return the type
	 */
	public @NotNull Type getType() {
		return this.type;
	}

	/**
	 * Set the type of curve
	 * 
	 * @param type the type to set
	 * @throws NullPointerException if type is null
	 */
	public void setType(@NotNull Type type) {
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
	public @NotNull String getDescription() {
		return description;
	}

	/**
	 * Set the description
	 * 
	 * @param description the description to set or a empty string if it doesn't
	 *                    exist
	 * @throws NullPointerException if description is null
	 */
	public void setDescription(@NotNull String description) {
		Objects.requireNonNull(description);
		this.description = description;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("id", id);
		if (points.isEmpty()) {
			map.put("points", "");
		} else {
			map.put("points", points);
		}
		map.put("description", description);
		map.put("type", type);
		return gson.toJson(map);
	}

	/**
	 * Copy this object.
	 * 
	 * @return a copy of this object
	 */
	public @NotNull Curve copy() {
		return new Curve(this);
	}
}
