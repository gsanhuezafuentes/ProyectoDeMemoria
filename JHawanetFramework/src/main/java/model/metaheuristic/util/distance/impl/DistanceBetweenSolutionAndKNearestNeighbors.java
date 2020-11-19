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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class for calculating the Euclidean distance between two Double {@link Solution} objects in solution space.
 *
 * @author &lt;antonio@lcc.uma.es&gt;
 */
public class DistanceBetweenSolutionAndKNearestNeighbors<S extends Solution<?>>
        implements Distance<S, List<S>> {

  private final int k ;
  private final Distance<S, S> distance ;

  public DistanceBetweenSolutionAndKNearestNeighbors(int k, Distance<S, S> distance) {
    this.k = k ;
    this.distance = distance ;
  }

  public DistanceBetweenSolutionAndKNearestNeighbors(int k) {
    this(k, new EuclideanDistanceBetweenSolutionsInObjectiveSpace<>()) ;
  }

  /**
   * Computes the knn distance. If the solution list size is lower than k, then k = size in the computation
   * @param solution
   * @param solutionList
   * @return
   */
  @Override
  public double compute(S solution, List<S> solutionList) {
    List<Double> listOfDistances = knnDistances(solution, solutionList) ;
    listOfDistances.sort(Comparator.naturalOrder());

    int limit = Math.min(k, listOfDistances.size()) ;

    double result ;
    if (limit == 0) {
      result = 0.0 ;
    } else {
      //double sum = 0.0;
      //for (int i = 0; i < limit; i++) {
      //  sum += listOfDistances.get(i);
      //}
      //result = sum/limit ;
      result = listOfDistances.get(limit-1) ;
    }
    return result;
  }

  /**
   * Computes the distance between a solution and the solutions of a list. Distances equal to 0 are ignored.
   * @param solution
   * @param solutionList
   * @return A list with the distances
   */
  private List<Double> knnDistances(S solution, List<S> solutionList) {
    List<Double> listOfDistances = new ArrayList<>() ;
    for (S s : solutionList) {
      double distanceBetweenSolutions = distance.compute(solution, s);
      if (distanceBetweenSolutions != 0) {
        listOfDistances.add(distanceBetweenSolutions);
      }
    }

    return listOfDistances ;
  }
}
