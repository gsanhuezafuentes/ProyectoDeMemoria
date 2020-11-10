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
package model.metaheuristic.qualityindicator.impl;

import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.distance.impl.EuclideanDistanceBetweenVectors;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;
import model.metaheuristic.util.front.util.FrontUtils;
import model.metaheuristic.util.point.Point;
import model.metaheuristic.util.point.impl.ArrayPoint;
import model.metaheuristic.util.point.util.comparator.LexicographicalPointComparator;
import model.metaheuristic.util.point.util.comparator.PointDimensionComparator;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements the generalized spread metric for two or more dimensions.
 * Reference: A. Zhou, Y. Jin, Q. Zhang, B. Sendhoff, and E. Tsang
 * Combining model-based and genetics-based offspring generation for
 * multi-objective optimization using a convergence criterion,
 * 2006 IEEE Congress on Evolutionary Computation, 2006, pp. 3234-3241.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class GeneralizedSpread<S extends Solution<?>> extends GenericIndicator<S> {

  /**
   * Default constructor
   */
  public GeneralizedSpread() {
  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public GeneralizedSpread(String referenceParetoFrontFile) throws FileNotFoundException {
    super(referenceParetoFrontFile) ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public GeneralizedSpread(Front referenceParetoFront) {
    super(referenceParetoFront) ;
  }

  /**
   * Evaluate() method
   * @param solutionList
   * @return
   */
  @Override public Double evaluate(List<S> solutionList) {
    return generalizedSpread(new ArrayFront(solutionList), referenceParetoFront);
  }

  /**
   *  Calculates the generalized spread metric. Given the 
   *  pareto front, the true pareto front as <code>double []</code>
   *  and the number of objectives, the method return the value for the
   *  metric.
   *  @param front The front.
   *  @param referenceFront The reference pareto front.
   *  @return the value of the generalized spread metric
   **/
  public double generalizedSpread(Front front, Front referenceFront) {
    int numberOfObjectives = front.getPoint(0).getDimension() ;

    Point[] extremeValues = new Point[numberOfObjectives] ;
    for (int i = 0; i < numberOfObjectives; i++) {
      referenceFront.sort(new PointDimensionComparator(i));
      Point newPoint = new ArrayPoint(numberOfObjectives) ;
      for (int j = 0 ; j < numberOfObjectives; j++) {
        newPoint.setValue(j,
            referenceFront.getPoint(referenceFront.getNumberOfPoints()-1).getValue(j));
      }
      extremeValues[i] = newPoint ;
    }

    int numberOfPoints = front.getNumberOfPoints();

    front.sort(new LexicographicalPointComparator());

    if (new EuclideanDistanceBetweenVectors().compute(front.getPoint(0).getValues(),
        front.getPoint(front.getNumberOfPoints() - 1).getValues()) == 0.0) {
      return 1.0;
    } else {
      double dmean = 0.0;

      for (int i = 0 ; i < front.getNumberOfPoints(); i++) {
        dmean += FrontUtils.distanceToNearestPoint(front.getPoint(i), front);
      }

      dmean = dmean / (numberOfPoints);

      double dExtrems = 0.0;
      for (int i = 0 ; i < extremeValues.length; i++) {
        dExtrems += FrontUtils.distanceToClosestPoint(extremeValues[i], front);
      }

      double mean = 0.0;
      for (int i = 0; i < front.getNumberOfPoints(); i++) {
        mean += Math.abs(FrontUtils.distanceToNearestPoint(front.getPoint(i), front) -
            dmean);
      }

      return (dExtrems + mean) / (dExtrems + (numberOfPoints*dmean));
    }
  }

  @Override public String getName() {
    return "Generalized Spread" ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true ;
  }
}

