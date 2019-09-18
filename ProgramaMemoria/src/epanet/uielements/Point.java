package epanet.uielements;

/**
 * Class that represent a Point 2D.
 * @author gsanh
 *
 */
public class Point {
	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * @return Get the position x.
	 */
	public int getX() {
		return x;
	}
	/**
	 * @return Get the position y.
	 */
	public int getY() {
		return y;
	}
	
}
