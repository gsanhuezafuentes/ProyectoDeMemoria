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
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;
import model.metaheuristic.util.front.util.FrontUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the inverted generational distance metric.
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary Algorithm Research:
 * A History and Analysis.
 * Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force
 * Inst. Technol. (1998)
 *
 * @author Antonio J. Nebro &lt;antonio@lcc.uma.es &gt;
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class InvertedGenerationalDistance<S extends Solution<?>> extends GenericIndicator<S> {

    private double pow = 2.0;

    /**
     * Default constructor
     */
    public InvertedGenerationalDistance() {
    }

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile the reference pareto front.
     * @param p the pow use in the equation
     * @throws FileNotFoundException if can't find the file.
     * @throws IOException           if there is a error reading file.
     */
    public InvertedGenerationalDistance(String referenceParetoFrontFile, double p) throws IOException, FileNotFoundException {
        super(referenceParetoFrontFile);
        pow = p;
    }

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile the reference pareto front.
     * @throws FileNotFoundException if can't find the file.
     * @throws IOException           if there is a error reading file.
     */
    public InvertedGenerationalDistance(String referenceParetoFrontFile) throws IOException, FileNotFoundException {
        this(referenceParetoFrontFile, 2.0);
    }

    /**
     * Constructor
     *
     * @param referenceParetoFront the reference pareto front
     */
    public InvertedGenerationalDistance(Front referenceParetoFront) {
        super(referenceParetoFront);
    }

    /**
     * Evaluate() method
     *
     * @param solutionList the solutions list to evaluate
     * @return
     */
    @Override
    public @NotNull Double evaluate(List<S> solutionList) {
        return invertedGenerationalDistance(new ArrayFront(solutionList), referenceParetoFront);
    }

    /**
     * Returns the inverted generational distance value for a given front
     *
     * @param front          The front
     * @param referenceFront The reference pareto front
     * @return the inverted generational distance value.
     */
    public double invertedGenerationalDistance(Front front, Front referenceFront) {
        double sum = 0.0;
        for (int i = 0; i < referenceFront.getNumberOfPoints(); i++) {
            sum += Math.pow(FrontUtils.distanceToClosestPoint(referenceFront.getPoint(i),
                    front), pow);
        }

        sum = Math.pow(sum, 1.0 / pow);

        return sum / referenceFront.getNumberOfPoints();
    }

    @Override
    public @NotNull String getName() {
        return "IGD";
    }

    @Override
    public boolean isTheLowerTheIndicatorValueTheBetter() {
        return true;
    }
}
