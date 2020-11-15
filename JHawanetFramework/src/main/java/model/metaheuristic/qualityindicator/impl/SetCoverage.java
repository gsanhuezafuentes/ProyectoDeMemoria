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
import model.metaheuristic.util.SolutionListUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * Set coverage metric
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
@SuppressWarnings("serial")
public class SetCoverage
        implements QualityIndicator<Pair<? extends List<? extends Solution<?>>, ? extends List<? extends Solution<?>>>, Pair<Double, Double>> {

    /**
     * Constructor
     */
    public SetCoverage() {
    }

    @Override
    public @NotNull Pair<Double, Double> evaluate(
            Pair<? extends List<? extends Solution<?>>, ? extends List<? extends Solution<?>>> pairOfSolutionLists) {
        List<? extends Solution<?>> front1 = pairOfSolutionLists.getLeft();
        List<? extends Solution<?>> front2 = pairOfSolutionLists.getRight();

        Objects.requireNonNull(front1, "The first front is null");
        Objects.requireNonNull(front2, "The second front is null");

        return new ImmutablePair<>(evaluate(front1, front2), evaluate(front2, front1));
    }

    /**
     * Calculates the set coverage of set1 over set2
     *
     * @param set1
     * @param set2
     * @return The value of the set coverage
     */
    public double evaluate(List<? extends Solution<?>> set1, List<? extends Solution<?>> set2) {
        double result;
        int sum = 0;

        if (set2.size() == 0) {
            if (set1.size() == 0) {
                result = 0.0;
            } else {
                result = 1.0;
            }
        } else {
            for (Solution<?> solution : set2) {
                if (SolutionListUtils.isSolutionDominatedBySolutionList(solution, set1)) {
                    sum++;
                }
            }
            result = (double) sum / set2.size();
        }
        return result;
    }

    @Override
    public @NotNull String getName() {
        return "SC";
    }
}
