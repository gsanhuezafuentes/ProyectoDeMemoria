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

import model.metaheuristic.qualityindicator.QualityIndicator;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;
import model.metaheuristic.util.point.Point;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * The Error Ratio (ER) quality indicator reports the ratio of solutions in a front of points
 * that are not members of the true Pareto front.
 * <p>
 * NOTE: the indicator merely checks if the solutions in the front are not members of the
 * second front. No assumption is made about the second front is a true Pareto front, i.e,
 * the front could contain solutions that dominate some of those of the supposed Pareto front.
 * It is a responsibility of the caller to ensure that this does not happen.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * TODO: using an epsilon value
 */
@SuppressWarnings("serial")
public class ErrorRatio<Evaluate extends List<? extends Solution<?>>>
        implements QualityIndicator<Evaluate, Double> {
    private final Front referenceParetoFront;

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile
     * @throws FileNotFoundException if file can't be found.
     * @throws NullPointerException  if referenceParetoFrontFile is null.
     * @throws IOException           if there is a error reading the file.
     */
    public ErrorRatio(String referenceParetoFrontFile) throws IOException, FileNotFoundException {
        Objects.requireNonNull(referenceParetoFrontFile, "The pareto front object is null");

        Front front = new ArrayFront(referenceParetoFrontFile);
        referenceParetoFront = front;
    }

    /**
     * Constructor
     *
     * @param referenceParetoFront the reference pareto front.
     * @throws NullPointerException if referenceParetoFront is null.
     */
    public ErrorRatio(Front referenceParetoFront) {
        Objects.requireNonNull(referenceParetoFront);

        this.referenceParetoFront = referenceParetoFront;
    }

    /**
     * Evaluate() method
     *
     * @param solutionList evaluate the quality indicator.
     * @return the value.
     */
    @Override
    public @NotNull Double evaluate(Evaluate solutionList) {
        Objects.requireNonNull(solutionList);
        return er(new ArrayFront(solutionList), referenceParetoFront);
    }

    /**
     * Returns the value of the error ratio indicator.
     *
     * @param front          Solution front
     * @param referenceFront True Pareto front
     * @return the value of the error ratio indicator
     */
    private double er(Front front, Front referenceFront) {
        int numberOfObjectives = referenceFront.getPointDimensions();
        double sum = 0;

        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            Point currentPoint = front.getPoint(i);
            boolean thePointIsInTheParetoFront = false;
            for (int j = 0; j < referenceFront.getNumberOfPoints(); j++) {
                Point currentParetoFrontPoint = referenceFront.getPoint(j);
                boolean found = true;
                for (int k = 0; k < numberOfObjectives; k++) {
                    if (currentPoint.getValue(k) != currentParetoFrontPoint.getValue(k)) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    thePointIsInTheParetoFront = true;
                    break;
                }
            }
            if (!thePointIsInTheParetoFront) {
                sum++;
            }
        }

        return sum / front.getNumberOfPoints();
    }

    @Override
    public @NotNull String getName() {
        return "ER";
    }
}
