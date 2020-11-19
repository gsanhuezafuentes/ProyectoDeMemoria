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
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * This class implements the unary epsilon additive indicator as proposed in E.
 * Zitzler, E. Thiele, L. Laummanns, M., Fonseca, C., and Grunert da Fonseca. V
 * (2003): Performance Assessment of Multiobjective Optimizers: An Analysis and
 * Review. The code is the a Java version of the original metric implementation
 * by Eckart Zitzler.
 *
 * @author Antonio J. Nebro &lt;antonio@lcc.uma.es &gt;
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class Epsilon<S extends Solution<?>> extends GenericIndicator<S> {

    /**
     * Default constructor
     */
    public Epsilon() {
    }

    /**
     * Constructor
     *
     * @param referenceParetoFrontFile
     * @throws FileNotFoundException if the file isn't found.
     * @throws IOException           if there is a error reading the file.
     */
    public Epsilon(String referenceParetoFrontFile) throws IOException, FileNotFoundException {
        super(referenceParetoFrontFile);
    }

    /**
     * Constructor
     *
     * @param referenceParetoFront
     */
    public Epsilon(Front referenceParetoFront) {
        super(referenceParetoFront);
    }

    @Override
    public boolean isTheLowerTheIndicatorValueTheBetter() {
        return true;
    }

    /**
     * Evaluate() method
     *
     * @param solutionList the solution list.
     * @return the value of indicator
     * @throws NullPointerException if solutionList is null.
     */
    @Override
    public @NotNull Double evaluate(List<S> solutionList) {
        Objects.requireNonNull(solutionList);
        return epsilon(new ArrayFront(solutionList), referenceParetoFront);
    }

    /**
     * Returns the value of the epsilon indicator.
     *
     * @param front          Solution front
     * @param referenceFront Optimal Pareto front
     * @return the value of the epsilon indicator
     */
    private double epsilon(Front front, Front referenceFront) {
        double eps, epsJ = 0.0, epsK = 0.0, epsTemp;

        int numberOfObjectives = front.getPointDimensions();

        eps = Double.MIN_VALUE;

        for (int i = 0; i < referenceFront.getNumberOfPoints(); i++) {
            for (int j = 0; j < front.getNumberOfPoints(); j++) {
                for (int k = 0; k < numberOfObjectives; k++) {
                    epsTemp = front.getPoint(j).getValue(k)
                            - referenceFront.getPoint(i).getValue(k);
                    if (k == 0) {
                        epsK = epsTemp;
                    } else if (epsK < epsTemp) {
                        epsK = epsTemp;
                    }
                }
                if (j == 0) {
                    epsJ = epsK;
                } else if (epsJ > epsK) {
                    epsJ = epsK;
                }
            }
            if (i == 0) {
                eps = epsJ;
            } else if (eps < epsJ) {
                eps = epsJ;
            }
        }
        return eps;
    }

    @Override
    public @NotNull String getName() {
        return "EP";
    }
}
