package model.metaheuristic.operator.selection.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import exception.ApplicationException;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.SolutionListUtils;
import model.metaheuristic.utils.SolutionUtils;
import model.metaheuristic.utils.comparator.DominanceComparator;

/**
 * @author Juanjo
 * @version 1.0
 * 
 *          <pre>
 * Applies a n-ary tournament selection to return a solution from a
 * list.
 * 
 * Base on code from https://github.com/jMetal/jMetal
 * 
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. © 2019 GitHub, Inc.
 *          </pre>
 * 
 */
public class TournamentSelection<S extends Solution<?>> implements SelectionOperator<List<S>, S> {
	private Comparator<S> comparator;

	private final int n_arity;

	/** Constructor */
	public TournamentSelection(int n_arity) {
		this(new DominanceComparator<S>(), n_arity);
	}

	/** Constructor */
	public TournamentSelection(Comparator<S> comparator, int n_arity) {
		this.n_arity = n_arity;
		this.comparator = comparator;
	}

	/**
	 * Execute() method.
	 * @throws NullPointerException if solutionList is null
	 * @throws ApplicationException if solutionList is empty
	 */
	@Override
	public S execute(List<S> solutionList) {
		Objects.requireNonNull(solutionList);
		if (solutionList.isEmpty()) {
			throw new ApplicationException("The solution list is empty");
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
