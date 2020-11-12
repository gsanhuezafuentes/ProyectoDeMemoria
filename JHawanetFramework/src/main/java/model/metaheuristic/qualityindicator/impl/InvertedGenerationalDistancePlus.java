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
import model.metaheuristic.util.distance.impl.DominanceDistanceBetweenVectors;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;
import model.metaheuristic.util.front.util.FrontUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * This class implements the inverted generational distance metric plust (IGD+)
 * Reference: Ishibuchi et al 2015, "A Study on Performance Evaluation Ability of a Modified
 * Inverted Generational Distance Indicator", GECCO 2015
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class InvertedGenerationalDistancePlus<S extends Solution<?>> extends GenericIndicator<S> {

    /**
     * Default constructor
     */
    public InvertedGenerationalDistancePlus() {
    }

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile the reference pareto front.
     * @throws FileNotFoundException if can't find the file.
     * @throws IOException           if there is a error reading file.
     */
    public InvertedGenerationalDistancePlus(String referenceParetoFrontFile) throws IOException, FileNotFoundException {
        super(referenceParetoFrontFile);
    }

    /**
     * Constructor
     *
     * @param referenceParetoFront
     * @throws FileNotFoundException
     */
    public InvertedGenerationalDistancePlus(Front referenceParetoFront) {
        super(referenceParetoFront);
    }

    /**
     * Evaluate() method
     *
     * @param solutionList
     * @return the indicator value.
     * @throws NullPointerException of the solution list is null.
     */
    @Override
    public @NotNull Double evaluate(List<S> solutionList) {
        Objects.requireNonNull(solutionList);

        return invertedGenerationalDistancePlus(new ArrayFront(solutionList), referenceParetoFront);
    }

    /**
     * Returns the inverted generational distance plus value for a given front
     *
     * @param front          The front
     * @param referenceFront The reference pareto front
     */
    public double invertedGenerationalDistancePlus(Front front, Front referenceFront) {

        double sum = 0.0;
        for (int i = 0; i < referenceFront.getNumberOfPoints(); i++) {
            sum += FrontUtils.distanceToClosestPoint(referenceFront.getPoint(i),
                    front, new DominanceDistanceBetweenVectors());
        }

        // STEP 4. Divide the sum by the maximum number of points of the reference Pareto front
        return sum / referenceFront.getNumberOfPoints();
    }

    @Override
    public @NotNull String getName() {
        return "Inverted generational distance plus";
    }

    @Override
    public boolean isTheLowerTheIndicatorValueTheBetter() {
        return true;
    }
}
