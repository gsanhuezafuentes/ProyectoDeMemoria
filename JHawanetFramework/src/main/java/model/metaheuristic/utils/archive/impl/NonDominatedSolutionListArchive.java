/*
 * Base on code from https://github.com/jMetal/jMetal
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
package model.metaheuristic.utils.archive.impl;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.archive.Archive;
import model.metaheuristic.utils.comparator.DominanceComparator;
import model.metaheuristic.utils.comparator.EqualSolutionsComparator;

/**
 * This class implements an archive containing non-dominated solutions
 */
@SuppressWarnings("serial")
public class NonDominatedSolutionListArchive<S extends Solution<?>> implements Archive<S> {
	private List<S> solutionList;
	private Comparator<S> dominanceComparator;
	private Comparator<S> equalSolutions = new EqualSolutionsComparator<S>();

	/**
	 * Constructor
	 */
	public NonDominatedSolutionListArchive() {
		this(new DominanceComparator<S>());
	}

	/**
	 * Constructor
	 * @param comparator the dominance comparator to use
	 */
	public NonDominatedSolutionListArchive(DominanceComparator<S> comparator) {
		dominanceComparator = comparator;

		solutionList = new ArrayList<>();
	}

	/**
	 * Inserts a solution in the list
	 *
	 * @param solution The solution to be inserted.
	 * @return true if the operation success, and false if the solution is dominated
	 *         or if an identical individual exists. The decision variables can be
	 *         null if the solution is read from a file; in that case, the
	 *         domination tests are omitted
	 */
	@Override
	public boolean add(S solution) {
		boolean solutionInserted = false;
		if (solutionList.size() == 0) {
			solutionList.add(solution);
			solutionInserted = true;
		} else {
			Iterator<S> iterator = solutionList.iterator();
			boolean isDominated = false;

			boolean isContained = false;
			while (((!isDominated) && (!isContained)) && (iterator.hasNext())) {
				S listIndividual = iterator.next();
				int flag = dominanceComparator.compare(solution, listIndividual);
				if (flag == -1) {
					iterator.remove();
				} else if (flag == 1) {
					isDominated = true; // dominated by one in the list
				} else if (flag == 0) {
					int equalflag = equalSolutions.compare(solution, listIndividual);
					if (equalflag == 0) // solutions are equals
						isContained = true;
				}
			}

			if (!isDominated && !isContained) {
				solutionList.add(solution);
				solutionInserted = true;
			}

			return solutionInserted;
		}

		return solutionInserted;
	}

	public Archive<S> join(Archive<S> archive) {
		return this.addAll(archive.getSolutionList());
	}

	public Archive<S> addAll(List<S> list) {
		for (S solution : list) {
			this.add(solution);
		}

		return this;
	}

	@Override
	public List<S> getSolutionList() {
		return solutionList;
	}

	@Override
	public int size() {
		return solutionList.size();
	}

	@Override
	public S get(int index) {
		return solutionList.get(index);
	}
}
