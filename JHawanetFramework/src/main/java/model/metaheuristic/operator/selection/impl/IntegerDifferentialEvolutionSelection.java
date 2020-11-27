/*
 * Code taken and modify from https://github.com/jMetal/jMetal
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
package model.metaheuristic.operator.selection.impl;

import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.util.random.BoundedRandomGenerator;
import model.metaheuristic.util.random.JavaRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class implementing the selection operator used in DE: a number of different solutions are
 * returned from a population. The number of solutions is requested in the class constructor (by
 * default, its value is 3), and they must be also different from the one indicated by an index
 * (typically, the current solution being processed by a DE algorithm). This current solution can
 * belong to the returned list if the {@link #selectCurrentSolution} variable is set to True when
 * invoking the {@link #execute(List)}} method; in this case, the current solution will
 * be the last one of the returned list.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class IntegerDifferentialEvolutionSelection
        implements SelectionOperator<List<IntegerSolution>, List<IntegerSolution>> {
    private int currentSolutionIndex = Integer.MIN_VALUE;
    private BoundedRandomGenerator<Integer> randomGenerator;
    private int numberOfSolutionsToSelect;
    private boolean selectCurrentSolution;

    /**
     * Constructor
     */
    public IntegerDifferentialEvolutionSelection() {
        this((a, b) -> JavaRandom.getInstance().nextInt(a, b), 3, false);
    }

    /**
     * Constructor
     */
    public IntegerDifferentialEvolutionSelection(
            int numberOfSolutionsToSelect, boolean selectCurrentSolution) {
        this(
                (a, b) -> JavaRandom.getInstance().nextInt(a, b),
                numberOfSolutionsToSelect,
                selectCurrentSolution);
    }

    /**
     * Constructor
     */
    public IntegerDifferentialEvolutionSelection(
            BoundedRandomGenerator<Integer> randomGenerator,
            int numberOfSolutionsToSelect,
            boolean selectCurrentSolution) {
        this.randomGenerator = randomGenerator;
        this.numberOfSolutionsToSelect = numberOfSolutionsToSelect;
        this.selectCurrentSolution = selectCurrentSolution;
    }

    public void setIndex(int index) {
        this.currentSolutionIndex = index;
    }

    /**
     * Execute() method
     */
    @Override
    public List<IntegerSolution> execute(List<IntegerSolution> solutionList) {
        Objects.requireNonNull(solutionList);
        if (!((currentSolutionIndex >= 0) && (currentSolutionIndex <= solutionList.size()))) {
            throw new IllegalArgumentException("Index value invalid: " + currentSolutionIndex);
        }
        if (!(solutionList.size() >= numberOfSolutionsToSelect)) {
            throw new IllegalArgumentException("The population has less than "
                    + numberOfSolutionsToSelect
                    + " solutions: "
                    + solutionList.size());
        }

        List<Integer> indexList = new ArrayList<>();

        int solutionsToSelect =
                selectCurrentSolution ? numberOfSolutionsToSelect - 1 : numberOfSolutionsToSelect;

        do {
            int index = randomGenerator.getRandomValue(0, solutionList.size());
            if (index != currentSolutionIndex && !indexList.contains(index)) {
                indexList.add(index);
            }
        } while (indexList.size() < solutionsToSelect);

        if (selectCurrentSolution) {
            indexList.add(currentSolutionIndex);
        }

        return indexList.stream().map(index -> solutionList.get(index)).collect(Collectors.toList());
    }
}
