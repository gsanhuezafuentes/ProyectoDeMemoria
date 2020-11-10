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
package model.metaheuristic.util.front.util;

import model.metaheuristic.util.distance.Distance;
import model.metaheuristic.util.distance.impl.EuclideanDistanceBetweenVectors;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;
import model.metaheuristic.util.point.Point;
import model.metaheuristic.util.point.PointSolution;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Front is a  list of points. This class includes utilities to work with {@link Front} objects.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class FrontUtils {
    /**
     * Gets the maximum values for each objectives in a front.
     *
     * @param front A front of objective values.
     * @return double [] An array with the maximum values for each objective.
     * @throws NullPointerException     if the front is null.
     * @throws IllegalArgumentException if the number of points in front is 0.
     */
    public static double[] getMaximumValues(Front front) {
        Objects.requireNonNull(front, "The front is null.");
        if (front.getNumberOfPoints() == 0) {
            throw new IllegalArgumentException("The number of points in front is 0.");
        }

        int numberOfObjectives = front.getPoint(0).getDimension();

        double[] maximumValue = new double[numberOfObjectives];
        for (int i = 0; i < numberOfObjectives; i++) {
            maximumValue[i] = Double.NEGATIVE_INFINITY;
        }

        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            for (int j = 0; j < numberOfObjectives; j++) {
                if (front.getPoint(i).getValue(j) > maximumValue[j]) {
                    maximumValue[j] = front.getPoint(i).getValue(j);
                }
            }
        }

        return maximumValue;
    }

    /**
     * Gets the minimum values for each objectives in a given front
     *
     * @param front The front
     * @return double [] An array with the minimum value for each objective
     * @throws NullPointerException     if the front is null.
     * @throws IllegalArgumentException if the number of points in front is 0.
     */
    public static double[] getMinimumValues(Front front) {
        Objects.requireNonNull(front, "The front is null.");
        if (front.getNumberOfPoints() == 0) {
            throw new IllegalArgumentException("The number of points in front is 0.");
        }

        int numberOfObjectives = front.getPoint(0).getDimension();

        double[] minimumValue = new double[numberOfObjectives];
        for (int i = 0; i < numberOfObjectives; i++) {
            minimumValue[i] = Double.MAX_VALUE;
        }

        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            for (int j = 0; j < numberOfObjectives; j++) {
                if (front.getPoint(i).getValue(j) < minimumValue[j]) {
                    minimumValue[j] = front.getPoint(i).getValue(j);
                }
            }
        }

        return minimumValue;
    }

    /**
     * Gets the distance between a point and the nearest one in a front. If a distance equals to 0
     * is found, that means that the point is in the front, so it is excluded
     *
     * @param point The point
     * @param front The front that contains the other points to calculate the distances
     * @return The minimum distance between the point and the front
     */
    public static double distanceToNearestPoint(Point point, Front front) {
        return distanceToNearestPoint(point, front, new EuclideanDistanceBetweenVectors());
    }

    /**
     * Gets the distance between a point and the nearest one in a front. If a distance equals to 0
     * is found, that means that the point is in the front, so it is excluded
     *
     * @param point The point
     * @param front The front that contains the other points to calculate the distances
     * @return The minimum distance between the point and the front
     * @throws NullPointerException     if the front or point is null.
     * @throws IllegalArgumentException if the number of points in front is 0.
     */
    public static double distanceToNearestPoint(Point point, Front front, Distance<double[], double[]> distance) {
        Objects.requireNonNull(front, "Front is null.");
        Objects.requireNonNull(point, "Point is null.");
        if (front.getNumberOfPoints() == 0) {
            throw new IllegalArgumentException("The number of points in front is 0.");
        }

        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            double aux = distance.compute(point.getValues(), front.getPoint(i).getValues());
            if ((aux < minDistance) && (aux > 0.0)) {
                minDistance = aux;
            }
        }

        return minDistance;
    }

    /**
     * Gets the distance between a point and the nearest one in a given front. The Euclidean distance
     * is assumed
     *
     * @param point The point
     * @param front The front that contains the other points to calculate the
     *              distances
     * @return The minimum distance between the point and the front
     */
    public static double distanceToClosestPoint(Point point, Front front) {
        return distanceToClosestPoint(point, front, new EuclideanDistanceBetweenVectors());
    }

    /**
     * Gets the distance between a point and the nearest one in a given front
     *
     * @param point The point
     * @param front The front that contains the other points to calculate the
     *              distances
     * @return The minimum distance between the point and the front
     * @throws NullPointerException     if the front or point is null.
     * @throws IllegalArgumentException if the number of points in front is 0.
     */
    public static double distanceToClosestPoint(Point point, Front front, Distance<double[], double[]> distance) {
        Objects.requireNonNull(front, "Front is null.");
        Objects.requireNonNull(point, "Point is null.");
        if (front.getNumberOfPoints() == 0) {
            throw new IllegalArgumentException("The number of points in front is 0.");
        }

        double minDistance = distance.compute(point.getValues(), front.getPoint(0).getValues());

        for (int i = 1; i < front.getNumberOfPoints(); i++) {
            double aux = distance.compute(point.getValues(), front.getPoint(i).getValues());
            if (aux < minDistance) {
                minDistance = aux;
            }
        }

        return minDistance;
    }

    /**
     * This method receives a normalized pareto front and return the inverted one.
     * This method is for minimization problems
     *
     * @param front The pareto front to inverse
     * @return The inverted pareto front
     * @throws NullPointerException     if the front or point is null.
     * @throws IllegalArgumentException if the number of points in front is 0.
     */
    public static Front getInvertedFront(Front front) {
        Objects.requireNonNull(front, "Front is null.");
        if (front.getNumberOfPoints() == 0) {
            throw new IllegalArgumentException("The number of points in front is 0.");
        }

        int numberOfDimensions = front.getPoint(0).getDimension();
        Front invertedFront = new ArrayFront(front.getNumberOfPoints(), numberOfDimensions);

        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            for (int j = 0; j < numberOfDimensions; j++) {
                if (front.getPoint(i).getValue(j) <= 1.0
                        && front.getPoint(i).getValue(j) >= 0.0) {
                    invertedFront.getPoint(i).setValue(j, 1.0 - front.getPoint(i).getValue(j));
                } else if (front.getPoint(i).getValue(j) > 1.0) {
                    invertedFront.getPoint(i).setValue(j, 0.0);
                } else if (front.getPoint(i).getValue(j) < 0.0) {
                    invertedFront.getPoint(i).setValue(j, 1.0);
                }
            }
        }
        return invertedFront;
    }

    /**
     * Given a front, converts it to an array of double values
     *
     * @param front
     * @return A front as double[][] array
     * @throws NullPointerException if the front or point is null.
     */
    public static double[][] convertFrontToArray(Front front) {
        Objects.requireNonNull(front, "The front is null");

        double[][] arrayFront = new double[front.getNumberOfPoints()][];

        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            arrayFront[i] = new double[front.getPoint(i).getDimension()];
            for (int j = 0; j < front.getPoint(i).getDimension(); j++) {
                arrayFront[i][j] = front.getPoint(i).getValue(j);
            }
        }

        return arrayFront;
    }

    /**
     * Given a front, converts it to a Solution set of PointSolutions
     *
     * @param front
     * @return A front as a List<FrontSolution>
     */
  /*
  public static List<PointSolution> convertFrontToSolutionList(Front front) {
    if (front == null) {
      throw new JMetalException("The front is null");
    }

    int numberOfObjectives ;
    int solutionSetSize = front.getNumberOfPoints() ;
    if (front.getNumberOfPoints() == 0) {
      numberOfObjectives = 0 ;
    } else {
      numberOfObjectives = front.getPoint(0).getDimension();
    }
    List<PointSolution> solutionSet = new ArrayList<>(solutionSetSize) ;

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      PointSolution solution = new PointSolution(numberOfObjectives);
      for (int j = 0 ; j < numberOfObjectives; j++) {
        solution.setObjective(j, front.getPoint(i).getValue(j));
      }

      solutionSet.add(solution) ;
    }

    return solutionSet ;
  }
*/

    /**
     * Given a front, converts it to a Solution set of PointSolutions
     *
     * @param front
     * @return A front as a List<FrontSolution>
     * @throws NullPointerException     if the front or point is null.
     */

    public static List<PointSolution> convertFrontToSolutionList(Front front) {
        Objects.requireNonNull(front);

        int numberOfObjectives;
        int solutionSetSize = front.getNumberOfPoints();
        if (front.getNumberOfPoints() == 0) {
            numberOfObjectives = 0;
        } else {
            numberOfObjectives = front.getPoint(0).getDimension();
        }
        List<PointSolution> solutionSet = new ArrayList<>(solutionSetSize);

        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            PointSolution solution = new PointSolution(numberOfObjectives);
            for (int j = 0; j < numberOfObjectives; j++) {
                solution.setObjective(j, front.getPoint(i).getValue(j));
            }

            solutionSet.add(solution);
        }

        return solutionSet;
    }
}
