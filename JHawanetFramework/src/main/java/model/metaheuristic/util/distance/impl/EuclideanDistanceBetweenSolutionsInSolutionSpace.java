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
 * Class for calculating the Euclidean distance between two Double {@link Solution} objects in solution space.
 *
 * @author &lt;antonio@lcc.uma.es&gt;
 */
public class EuclideanDistanceBetweenSolutionsInSolutionSpace<S extends Solution<Double>>
    implements Distance<S, S> {

  private final EuclideanDistanceBetweenVectors distance = new EuclideanDistanceBetweenVectors() ;

  /**
   * Calcule the euclidean distance between two points.
   * @param solution1
   * @param solution2
   * @return
   */
  @Override
  public double compute(S solution1, S solution2) {
    double[] vector1 = new double[solution1.getNumberOfVariables()] ;
    double[] vector2 = new double[solution1.getNumberOfVariables()] ;
    for (int i = 0 ; i < solution1.getNumberOfVariables(); i++) {
      vector1[i] = solution1.getVariable(i) ;
      vector2[i] = solution2.getVariable(i) ;
    }

    return distance.compute(vector1, vector2) ;
  }
}
