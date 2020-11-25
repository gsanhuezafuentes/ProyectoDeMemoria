/*
 * Code taken from https://github.com/jMetal/jMetal
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

import annotations.operator.DefaultConstructor;
import annotations.NumberInput;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.SolutionListUtils;
import model.metaheuristic.util.SolutionUtils;
import model.metaheuristic.util.comparator.DominanceComparator;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Applies a n-ary tournament selection to return a solution from a
 * list.
 */
public class TournamentSelection<S extends Solution<?>> implements SelectionOperator<List<S>, S> {
    private final Comparator<S> comparator;

    private final int n_arity;

    /**
     * Constructor
     *
     * @param n_arity the number of solutions to realize the tournament
     */
    @DefaultConstructor(@NumberInput(displayName = "Arity", defaultValue = 2))
    public TournamentSelection(int n_arity) {
        this( n_arity, new DominanceComparator<S>());
    }

    /**
     * Constructor
     * @param n_arity the number of solutions to realize the tournament
     * @param comparator the comparator to use
     */
    public TournamentSelection(int n_arity, Comparator<S> comparator) {
        this.n_arity = n_arity;
        this.comparator = comparator;
    }

    /**
     * Get the arity of tournament selection.
     * If the arity is two so it is a BinaryTournament.
     * @return the arity.
     */
    public int getN_arity() {
        return n_arity;
    }

    /**
     * Execute() method.
     *
     * @throws NullPointerException if solutionList is null.
     * @throws IllegalArgumentException if solutionList is empty.
     */
    @Override
    public S execute(List<S> solutionList) {
        Objects.requireNonNull(solutionList);
        if (solutionList.isEmpty()) {
            throw new IllegalArgumentException("The solution list is empty");
        }

        S result;
        if (solutionList.size() == 1) {
            result = solutionList.get(0);
        } else {
            result = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList).get(0);
            int count = 1; // at least 2 solutions are compared
            do {
                S candidate = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList).get(0);
                result = SolutionUtils.getBestSolution(result, candidate, comparator);
            } while (++count < this.n_arity);
        }

        return result;
    }
}
