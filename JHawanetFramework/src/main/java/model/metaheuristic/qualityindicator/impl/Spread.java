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
import model.metaheuristic.util.point.util.comparator.LexicographicalPointComparator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the spread quality indicator. It must be only to two bi-objective problem.
 * Reference: Deb, K., Pratap, A., Agarwal, S., Meyarivan, T.: A fast and
 * elitist multiobjective genetic algorithm: NSGA-II. IEEE Trans. on Evol. Computation 6 (2002) 182-197
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class Spread<S extends Solution<?>> extends GenericIndicator<S> {

    /**
     * Default constructor
     */
    public Spread() {
    }

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile
     * @throws FileNotFoundException if file can't be found.
     * @throws IOException           if there is a error reading the file.
     */
    public Spread(String referenceParetoFrontFile) throws IOException, FileNotFoundException {
        super(referenceParetoFrontFile);
    }

    /**
     * Constructor
     *
     * @param referenceParetoFront
     * @throws FileNotFoundException
     */
    public Spread(Front referenceParetoFront) {
        super(referenceParetoFront);
    }

    /**
     * Evaluate() method
     *
     * @param solutionList
     * @return
     */
    @Override
    public Double evaluate(List<S> solutionList) {
        return spread(new ArrayFront(solutionList), referenceParetoFront);
    }

    /**
     * Calculates the Spread metric.
     *
     * @param front          The front.
     * @param referenceFront The true pareto front.
     */
    public double spread(Front front, Front referenceFront) {
        EuclideanDistanceBetweenVectors distance = new EuclideanDistanceBetweenVectors();

        // STEP 1. Sort normalizedFront and normalizedParetoFront;
        front.sort(new LexicographicalPointComparator());
        referenceFront.sort(new LexicographicalPointComparator());

        // STEP 2. Compute df and dl (See specifications in Deb's description of the metric)
        double df = distance.compute(front.getPoint(0).getValues(), referenceFront.getPoint(0).getValues());
        double dl = distance.compute(front.getPoint(front.getNumberOfPoints() - 1).getValues(),
                referenceFront.getPoint(referenceFront.getNumberOfPoints() - 1).getValues());

        double mean = 0.0;
        double diversitySum = df + dl;

        int numberOfPoints = front.getNumberOfPoints();

        // STEP 3. Calculate the mean of distances between points i and (i - 1).
        // (the points are in lexicografical order)
        for (int i = 0; i < (numberOfPoints - 1); i++) {
            mean += distance.compute(front.getPoint(i).getValues(), front.getPoint(i + 1).getValues());
        }

        mean = mean / (double) (numberOfPoints - 1);

        // STEP 4. If there are more than a single point, continue computing the
        // metric. In other case, return the worse value (1.0, see metric's description).
        if (numberOfPoints > 1) {
            for (int i = 0; i < (numberOfPoints - 1); i++) {
                diversitySum += Math.abs(distance.compute(front.getPoint(i).getValues(),
                        front.getPoint(i + 1).getValues()) - mean);
            }
            return diversitySum / (df + dl + (numberOfPoints - 1) * mean);
        } else {
            return 1.0;
        }
    }

    @Override
    public String getName() {
        return "SPREAD";
    }

    @Override
    public boolean isTheLowerTheIndicatorValueTheBetter() {
        return true;
    }
}
