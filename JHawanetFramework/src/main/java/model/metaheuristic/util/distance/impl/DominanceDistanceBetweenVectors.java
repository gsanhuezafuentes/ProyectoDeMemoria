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

import model.metaheuristic.util.distance.Distance;

import java.util.Objects;

/**
 * Class for calculating the dominance distance between two vectors
 *
 * @author <antonio@lcc.uma.es>
 */
public class DominanceDistanceBetweenVectors implements Distance<double[], double[]> {

  /**
   * Compute the dominance distance between vectors.
   * @param vector1 the vector1
   * @param vector2 the vector2
   * @return the distance.
   */
  @Override
  public double compute(double[] vector1, double[] vector2) {
    Objects.requireNonNull(vector1);
    Objects.requireNonNull(vector2);
    if (!(vector1.length == vector2.length)){
      throw new IllegalArgumentException("The vectors have different" +
              "dimension: " + vector1.length + " and " + vector2.length);
    }

    double distance = 0.0;

    for (int i = 0; i < vector1.length; i++) {
      double max = Math.max(vector2[i] - vector1[i], 0.0) ;
      distance += Math.pow(max, 2);
    }
    return Math.sqrt(distance);
  }
}
