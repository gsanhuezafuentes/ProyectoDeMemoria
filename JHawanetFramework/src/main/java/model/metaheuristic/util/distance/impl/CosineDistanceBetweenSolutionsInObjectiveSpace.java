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
package model.metaheuristic.util.distance.impl;

import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.distance.Distance;

/**
 * Class for calculating the cosine distance between two {@link Solution} objects in objective space.
 *
 * @author &lt;antonio@lcc.uma.es&gt;
 */
public class CosineDistanceBetweenSolutionsInObjectiveSpace<S extends Solution<?>>
    implements Distance<S, S> {

  private final S referencePoint;

  public CosineDistanceBetweenSolutionsInObjectiveSpace(S referencePoint) {
    this.referencePoint = referencePoint ;
  }

  /**
   * Compute the cosine distance
   * @param solution1 the solution1
   * @param solution2 the solution2
   * @return the result value
   */
  @Override
  public double compute(S solution1, S solution2) {
    double sum = 0.0 ;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      sum += (solution1.getObjective(i) - referencePoint.getObjective(i)) *
          (solution2.getObjective(i) - referencePoint.getObjective(i));
    }

    double result = sum / (sumOfDistancesToIdealPoint(solution1) * sumOfDistancesToIdealPoint(solution2)) ;

    return result ;
  }

  /**
   * Compute the euclidean distance between the solution and the reference point.
   * @param solution the solution
   * @return the distance.
   */
  private double sumOfDistancesToIdealPoint(S solution) {
    double sum = 0.0 ;

    for (int i = 0 ; i < solution.getNumberOfObjectives(); i++) {
      sum += Math.pow(solution.getObjective(i) - referencePoint.getObjective(i), 2.0) ;
    }

    return Math.sqrt(sum) ;
  }
}
