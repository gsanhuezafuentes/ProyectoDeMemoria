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

import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;

import java.util.List;
import java.util.Objects;

/**
 * Class for normalizing {@link Front} objects
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class FrontNormalizer {
  private final double[] maximumValues;
  private final double[] minimumValues;

  /**
   * Constructor.
   * @param referenceFront the reference front.
   * @throws NullPointerException if referenceFront is null.
   */
  public FrontNormalizer(List<? extends Solution<?>> referenceFront) {
    Objects.requireNonNull(referenceFront);
    maximumValues = FrontUtils.getMaximumValues(new ArrayFront(referenceFront));
    minimumValues = FrontUtils.getMinimumValues(new ArrayFront(referenceFront));
  }

  /**
   * Constructor.
   * @param referenceFront the reference front.
   * @throws NullPointerException if referenceFront is null.
   */
  public FrontNormalizer(Front referenceFront) {
    Objects.requireNonNull(referenceFront);
    maximumValues = FrontUtils.getMaximumValues(referenceFront);
    minimumValues = FrontUtils.getMinimumValues(referenceFront);
  }

  /**
   * Constructor
   * @param minimumValues A array with the minimum values.
   * @param maximumValues A array with the maximum values.
   * @throws NullPointerException if minimumValues or maximumValues is null.
   * @throws IllegalArgumentException if the length of minimumValues is different from the length of maximumValues.
   */
  public FrontNormalizer(double[] minimumValues, double[] maximumValues) {
    Objects.requireNonNull(minimumValues, "The array of minimum values is null");
    Objects.requireNonNull(maximumValues, "The array of maximum values is null");
     if (maximumValues.length != minimumValues.length) {
      throw new IllegalArgumentException("The length of the maximum array (" + maximumValues.length + ") " +
          "is different from the length of the minimum array (" + minimumValues.length + ")");
    }

    this.maximumValues = maximumValues ;
    this.minimumValues = minimumValues ;
  }

  /**
   * Returns a normalized front
   * @param solutionList
   * @return the solution normalized.
   * @throws NullPointerException if solutionList is null.
   */
  public List<? extends Solution<?>> normalize(List<? extends Solution<?>> solutionList) {
    Front normalizedFront ;
    Objects.requireNonNull(solutionList, "The solution list is null.");

    normalizedFront = getNormalizedFront(new ArrayFront(solutionList), maximumValues, minimumValues);

    return FrontUtils.convertFrontToSolutionList(normalizedFront) ;
  }

  /**
   * Returns a normalized front
   * @param front the front to normalize.
   * @return the normalize front.
   * @throws NullPointerException if front is null.
   */
  public Front normalize(Front front) {
    Objects.requireNonNull(front, "The front is null");

    return getNormalizedFront(front, maximumValues, minimumValues);
  }

  /**
   * Normalize the front.
   * @param front the front
   * @param maximumValues the maximum values of the elements in front.
   * @param minimumValues the minimum values of the elements in front.
   * @return the normalized front.
   * @throws IllegalArgumentException if the front is empty, the lenght of point in front is different from the length of maximum array or .
   */
  private Front getNormalizedFront(Front front, double[] maximumValues, double[] minimumValues) {
   if (front.getNumberOfPoints() == 0) {
      throw new IllegalArgumentException("The front is empty") ;
    } else if (front.getPoint(0).getDimension() != maximumValues.length) {
      throw new IllegalArgumentException("The length of the point dimensions ("
          + front.getPoint(0).getDimension() + ") "
          + "is different from the length of the maximum array (" + maximumValues.length+")") ;
    }

    Front normalizedFront = new ArrayFront(front) ;
    int numberOfPointDimensions = front.getPoint(0).getDimension() ;

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfPointDimensions; j++) {
        if ((maximumValues[j] - minimumValues[j]) == 0) {
          throw new IllegalArgumentException("Maximum and minimum values of index " + j + " "
              + "are the same: " + maximumValues[j]);
        }

        normalizedFront.getPoint(i).setValue(j, (front.getPoint(i).getValue(j)
            - minimumValues[j]) / (maximumValues[j] - minimumValues[j]));
      }
    }
    return normalizedFront;
  }
}
