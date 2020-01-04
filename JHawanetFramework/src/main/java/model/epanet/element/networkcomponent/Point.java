package model.epanet.element.networkcomponent;

/**
 * Class that represent a Point 2D.
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
		String text = getX() + "\t" + getY();
		return text;
	}
	
}
