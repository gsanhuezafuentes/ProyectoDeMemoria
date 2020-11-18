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

/**
 * Class for calculating the cosine similarity between two vectors.
 *
 * @author <antonio@lcc.uma.es>
 */
public class CosineSimilarityBetweenVectors implements Distance<double[], double[]> {

  private final double[] referencePoint;

  /**
   * Constructor.
   * @param referencePoint the reference point.
   */
  public CosineSimilarityBetweenVectors(double[] referencePoint) {
    this.referencePoint = referencePoint;
  }

  /**
   * Compute the distance between the elements.
   * @param vector1 the first element
   * @param vector2 the second element
   * @return the resulting value.
   */
  @Override
  public double compute(double[] vector1, double[] vector2) {
    double sum = 0.0;

    for (int i = 0; i < vector1.length; i++) {
      sum += (vector1[i] - referencePoint[i]) * (vector2[i] - referencePoint[i]);
    }

    return sum / (sumOfDistancesToIdealPoint(vector1) * sumOfDistancesToIdealPoint(vector2));
  }

  /**
   * The euclidean distance between the vector and the reference point.
   * @param vector
   * @return
   */
  private double sumOfDistancesToIdealPoint(double[] vector) {
    double sum = 0.0;

    for (int i = 0; i < vector.length; i++) {
      sum += Math.pow(vector[i] - referencePoint[i], 2.0);
    }

    return Math.sqrt(sum);
  }
}
