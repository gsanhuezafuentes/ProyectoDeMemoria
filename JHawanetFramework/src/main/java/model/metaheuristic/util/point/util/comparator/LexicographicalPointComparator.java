package model.metaheuristic.util.point.util.comparator;


import model.metaheuristic.util.point.Point;

import java.util.Comparator;
import java.util.Objects;

/**
 * This class implements the Comparator interface for comparing two points.
 * The order used is lexicographical order.
 *
 * @author Antonio J. Nebro &lt;antonio@lcc.uma.es&gt;
 * @author Juan J. Durillo

 */
public class LexicographicalPointComparator implements Comparator<Point> {

  /**
   * The compare method compare the objects o1 and o2.
   *
   * @param pointOne An object that reference a double[].
   * @param pointTwo An object that reference a double[].
   * @return The following value: -1 if point1 &lt; point2, 1 if point1 &gt; point2 or 0 in other case.
   * @throws NullPointerException if pointOne or pointTwo is null.
   */
  @Override
  public int compare(Point pointOne, Point pointTwo) {
    Objects.requireNonNull(pointOne, "PointOne is null");
    Objects.requireNonNull(pointTwo, "PointTwo is null");

    // Determine the first i such as pointOne[i] != pointTwo[i];
    int index = 0;
    while ((index < pointOne.getDimension())
        && (index < pointTwo.getDimension())
        && pointOne.getValue(index) == pointTwo.getValue(index)) {
      index++;
    }

    int result = 0 ;
    if ((index >= pointOne.getDimension()) || (index >= pointTwo.getDimension())) {
      result = 0;
    } else if (pointOne.getValue(index) < pointTwo.getValue(index)) {
      result = -1;
    } else if (pointOne.getValue(index) > pointTwo.getValue(index)) {
      result = 1;
    }
    return result ;
  }
}
