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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * This class implements the generational distance indicator.
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary
 * Algorithm Research: A History and Analysis.
 * Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force
 * Inst. Technol. (1998)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class GenerationalDistance<S extends Solution<?>> extends GenericIndicator<S> {
    private double pow = 2.0;

    /**
     * Default constructor
     */
    public GenerationalDistance() {
    }

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile
     * @param p
     * @throws FileNotFoundException if the file isn't found.
     * @throws IOException if there is a error reading the file.
     */
    public GenerationalDistance(String referenceParetoFrontFile, double p) throws IOException, FileNotFoundException {
        super(referenceParetoFrontFile);
        pow = p;
    }

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile
     * @throws FileNotFoundException if the file isn't found.
     * @throws IOException if there is a error reading the file.
     */
    public GenerationalDistance(String referenceParetoFrontFile) throws IOException, FileNotFoundException {
        this(referenceParetoFrontFile, 2.0);
    }

    /**
     * Constructor
     *
     * @param referenceParetoFront
     */
    public GenerationalDistance(Front referenceParetoFront) {
        super(referenceParetoFront);
    }

    /**
     * Evaluate() method
     *
     * @param solutionList the pareto front approximation.
     * @return the indicator value.
     */
    @Override
    public Double evaluate(List<S> solutionList) {
        Objects.requireNonNull(solutionList, "The pareto front approximation is null");
        return generationalDistance(new ArrayFront(solutionList), referenceParetoFront);
    }

    /**
     * Returns the generational distance value for a given front
     *
     * @param front          The front
     * @param referenceFront The reference pareto front
     */
    public double generationalDistance(Front front, Front referenceFront) {
        double sum = 0.0;
        for (int i = 0; i < front.getNumberOfPoints(); i++) {
            sum += Math.pow(FrontUtils.distanceToClosestPoint(front.getPoint(i),
                    referenceFront), pow);
        }

        sum = Math.pow(sum, 1.0 / pow);

        return sum / front.getNumberOfPoints();
    }

    @Override
    public String getName() {
        return "Generational distance";
    }

    @Override
    public boolean isTheLowerTheIndicatorValueTheBetter() {
        return true;
    }
}
