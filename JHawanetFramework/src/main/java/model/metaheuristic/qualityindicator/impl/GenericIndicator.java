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
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Abstract class representing quality indicators that need a reference front to be computed
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class GenericIndicator<S extends Solution<?>> implements QualityIndicator<List<S>, Double> {

    protected Front referenceParetoFront = null;

    /**
     * Default constructor
     */
    public GenericIndicator() {
    }

    /**
     * Constructor.
     *
     * @param referenceParetoFrontFile the path to file.
     * @throws FileNotFoundException if the file can't be found.
     * @throws NullPointerException  if the referenceParetoFrontFile is null
     * @throws IOException           if there is a error reading the file.
     */
    public GenericIndicator(String referenceParetoFrontFile) throws IOException, FileNotFoundException {
        setReferenceParetoFront(referenceParetoFrontFile);
    }

    /**
     * Constructor.
     *
     * @param referenceParetoFront the reference pareto front to use.
     * @throws NullPointerException if the referenceParetoFront is null.
     */
    public GenericIndicator(Front referenceParetoFront) {
        Objects.requireNonNull(referenceParetoFront);

        this.referenceParetoFront = referenceParetoFront;
    }

    /**
     * Set the reference front from a file.
     *
     * @param referenceParetoFrontFile the reference front.
     * @throws FileNotFoundException if the file can't be found.
     * @throws NullPointerException  if referenceParetoFrontFile is null.
     * @throws IOException           if there is a error reading the file.
     */
    public void setReferenceParetoFront(String referenceParetoFrontFile) throws IOException, FileNotFoundException {
        Objects.requireNonNull(referenceParetoFrontFile);

        Front front = new ArrayFront(referenceParetoFrontFile);
        referenceParetoFront = front;
    }

    /**
     * Set the reference front.
     *
     * @param referenceFront the reference front.
     * @throws NullPointerException if referenceFront is null.
     */
    public void setReferenceParetoFront(Front referenceFront) {
        Objects.requireNonNull(referenceFront);
        referenceParetoFront = referenceFront;
    }

    /**
     * This method returns true if lower indicator values are preferred and false otherwise
     *
     * @return true if the lower indicator value is better, false in otherwise.
     */
    public abstract boolean isTheLowerTheIndicatorValueTheBetter();

    /**
     * Get the reference pareto front.
     *
     * @return the reference pareto front.
     */
    public Front getReferenceParetoFront() {
        return referenceParetoFront;
    }

    /**
     * The name of indicator.
     *
     * @return the name of indicator.
     */
    public abstract @NotNull String getName();
}
