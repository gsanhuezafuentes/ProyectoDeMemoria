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
package model.metaheuristic.qualityindicator.impl.hypervolume;

import model.metaheuristic.qualityindicator.impl.GenericIndicator;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;
import model.metaheuristic.util.front.util.FrontUtils;
import model.metaheuristic.util.point.Point;
import model.metaheuristic.util.point.impl.ArrayPoint;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * This interface represents implementations of the Hypervolume quality indicator
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public abstract class Hypervolume<S extends Solution<?>> extends GenericIndicator<S> {

    public Hypervolume() {
    }

    /**
     * Constructor.
     *
     * @param referenceParetoFrontFile
     * @throws IOException           if there is a error reading file.
     * @throws FileNotFoundException the file can't be found.
     */
    public Hypervolume(String referenceParetoFrontFile) throws IOException, FileNotFoundException {
        super(referenceParetoFrontFile);
    }

    public Hypervolume(double[] referencePoint) {
        Front referenceFront = new ArrayFront(referencePoint.length, referencePoint.length);
        for (int i = 0; i < referencePoint.length; i++) {
            Point point = new ArrayPoint(referencePoint.length);
            for (int j = 0; j < referencePoint.length; j++) {
                if (j == i) {
                    double v = referencePoint[i];
                    point.setValue(j, v);
                } else {
                    point.setValue(j, 0.0);
                }
            }
            referenceFront.setPoint(i, point);
        }
        this.referenceParetoFront = referenceFront;
    }

    public Hypervolume(Front referenceParetoFront) {
        super(referenceParetoFront);
    }

    public abstract List<S> computeHypervolumeContribution(
            List<S> solutionList, List<S> referenceFrontList);

    public List<S> computeHypervolumeContribution(List<S> solutionList) {
        return this.computeHypervolumeContribution(
                solutionList, (List<S>) FrontUtils.convertFrontToSolutionList(referenceParetoFront));
    }

    public abstract double getOffset();

    public abstract void setOffset(double offset);

    @Override
    public boolean isTheLowerTheIndicatorValueTheBetter() {
        return false;
    }
}
