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

import model.metaheuristic.qualityindicator.impl.hypervolume.Hypervolume;
import model.metaheuristic.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;
import model.metaheuristic.util.front.util.FrontUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Class providing an implementation of the normalized hypervolume, which is calculated as follows:
 * relative hypervolume = 1 - (HV of the front / HV of the reference front)
 * <p>
 * Before computing this indicator it must be checked that the HV of the reference front is not zero.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NormalizedHypervolume<S extends Solution<?>> extends GenericIndicator<S> {
    private double referenceFrontHypervolume;
    private Hypervolume<S> hypervolume;

    public NormalizedHypervolume() {
    }

    /**
     * Contructor
     *
     * @param referenceParetoFrontFile reference pareto front.
     * @throws IOException           if there is a error reading the file.
     * @throws FileNotFoundException if the file can't be found.
     */
    public NormalizedHypervolume(String referenceParetoFrontFile)
            throws IOException, FileNotFoundException {
        super(referenceParetoFrontFile);
        Front referenceFront = new ArrayFront(referenceParetoFrontFile);
        hypervolume = new PISAHypervolume<>(referenceParetoFrontFile);

        referenceFrontHypervolume =
                this.hypervolume.evaluate(
                        (List<S>) FrontUtils.convertFrontToSolutionList(referenceFront));
    }

    /**
     * Constructor
     *
     * @param referencePoint the reference point.
     */
    public NormalizedHypervolume(double[] referencePoint) {
        Front referenceFront = new ArrayFront(referencePoint.length, referencePoint.length);
        hypervolume = new PISAHypervolume<>();
        hypervolume.setReferenceParetoFront(referenceFront);

        referenceFrontHypervolume =
                hypervolume.evaluate((List<S>) FrontUtils.convertFrontToSolutionList(referenceFront));
    }

    /**
     * Constructor.
     *
     * @param referenceParetoFront the reference front.
     */
    public NormalizedHypervolume(Front referenceParetoFront) {
        super(referenceParetoFront);
        hypervolume = new PISAHypervolume<>(referenceParetoFront);

        referenceFrontHypervolume =
                this.hypervolume.evaluate(
                        (List<S>) FrontUtils.convertFrontToSolutionList(referenceParetoFront));
    }

    @Override
    public void setReferenceParetoFront(Front referenceFront) {
        super.setReferenceParetoFront(referenceFront);

        hypervolume = new PISAHypervolume<>(referenceFront);
        referenceFrontHypervolume =
                hypervolume.evaluate((List<S>) FrontUtils.convertFrontToSolutionList(new ArrayFront(referenceFront)));
    }

    /**
     * Set the reference pareto front.
     *
     * @param referenceParetoFrontFile the reference front.
     * @throws IOException           if there is a error reading the file.
     * @throws FileNotFoundException if the file can't be found.
     */
    @Override
    public void setReferenceParetoFront(String referenceParetoFrontFile) throws IOException, FileNotFoundException {
        super.setReferenceParetoFront(referenceParetoFrontFile);

        hypervolume = new PISAHypervolume<>(referenceParetoFrontFile);
        referenceFrontHypervolume =
                hypervolume.evaluate((List<S>) FrontUtils.convertFrontToSolutionList(new ArrayFront(referenceParetoFrontFile)));
    }

    @Override
    public String getName() {
        return "Normalized Hypervolume";
    }

    @Override
    public boolean isTheLowerTheIndicatorValueTheBetter() {
        return true;
    }

    @Override
    public Double evaluate(List<S> solutionList) {
        double hypervolumeValue = hypervolume.evaluate(solutionList);

        return 1 - (hypervolumeValue / referenceFrontHypervolume);
    }
}
