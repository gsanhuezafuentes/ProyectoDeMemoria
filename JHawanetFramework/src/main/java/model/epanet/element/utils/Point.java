package model.epanet.element.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that represent a Point 2D.
 * 
 * @author gsanh
 *
 */
public final class Point {
	private final double x;
	private final double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return Get the position x.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return Get the position y.
	 */
	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new HashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("x", x);
		map.put("y", y);
		return gson.toJson(map);
	}
}
