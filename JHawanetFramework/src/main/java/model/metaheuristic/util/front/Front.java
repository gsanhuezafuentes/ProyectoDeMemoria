/*
 * Code took from https://github.com/jMetal/jMetal
 *
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. © 2019
 * GitHub, Inc.
 */
package model.metaheuristic.util.front;

import model.metaheuristic.util.point.Point;
import java.io.Serializable;
import java.util.Comparator;

/**
 * A front is a list of points
 *
 * @author Antonio J. Nebro
 */
public interface Front extends Serializable {
  /**
   * Get the number of points in front.
   * @return the number of points.
   */
  int getNumberOfPoints();

  /**
   * Get the number of dimensions in each point.
   * @return the number of dimensions.
   */
  int getPointDimensions();

  /**
   * Get a specific point in front.
   * @param index the index of the point.
   * @return the point.
   */
  Point getPoint(int index);

  /**
   * Set a point in the specific position.
   * @param index the position to add the point.
   * @param point the point
   */
  void setPoint(int index, Point point);

  /**
   * Sort the points in front.
   * @param comparator the comparator to use.
   */
  void sort(Comparator<Point> comparator);

  /**
   * The matrix of points.
   * @return the matrix.
   */
  double[][] getMatrix() ;
}
