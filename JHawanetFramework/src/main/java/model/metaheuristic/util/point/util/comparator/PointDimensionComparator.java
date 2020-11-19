package model.metaheuristic.util.point.util.comparator;

import model.metaheuristic.util.point.Point;

import java.util.Comparator;
import java.util.Objects;

/**
 * This class implements the {@link Comparator} interface. It is used
 * to compare two points according the value of a particular dimension.
 *
 * @author Antonio J. Nebro &lt;antonio@lcc.uma.es&gt;
 * @author Juan J. Durillo
 */
public class PointDimensionComparator implements Comparator<Point> {

  /**
   * Stores the value of the index to compare
   */
  private final int index;

  /**
   * Constructor
   * @throws IllegalArgumentException if index is less than 0.
   */
  public PointDimensionComparator(int index) {
    if (index < 0) {
      throw new IllegalArgumentException("The index value is negative");
    }
    this.index = index;
  }

  /**
   * Compares the objects o1 and o2.
   *
   * @param pointOne An object that reference a double[]
   * @param pointTwo An object that reference a double[]
   * @return -1 if o1 &lt; o1, 1 if o1 &gt; o2 or 0 in other case.
   */
  @Override
  public int compare(Point pointOne, Point pointTwo) {
    Objects.requireNonNull(pointOne, "PointOne is null");
    Objects.requireNonNull(pointTwo, "PointTwo is null");
    if (index >= pointOne.getDimension()) {
      throw new IndexOutOfBoundsException("The index value " + index
          + " is out of range (0,  " + (pointOne.getDimension()-1) + ")") ;
    } else if (index >= pointTwo.getDimension()) {
      throw new IndexOutOfBoundsException("The index value " + index
          + " is out of range (0,  " + (pointTwo.getDimension()-1) + ")") ;
    }

    return Double.compare(pointOne.getValue(index), pointTwo.getValue(index));
  }
}
