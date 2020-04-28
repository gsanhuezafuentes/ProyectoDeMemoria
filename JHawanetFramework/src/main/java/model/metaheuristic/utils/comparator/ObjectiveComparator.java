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
package model.metaheuristic.utils.comparator;

import exception.ApplicationException;
import model.metaheuristic.solution.Solution;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This class implements a comparator based on a given objective
 *
 */
@SuppressWarnings("serial")
public class ObjectiveComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
	public enum Ordering {
		ASCENDING, DESCENDING
	}

	private final int objectiveId;

	private final Ordering order;

	/**
	 * Constructor.
	 *
	 * @param objectiveId The index of the objective to compare
	 */
	public ObjectiveComparator(int objectiveId) {
		this.objectiveId = objectiveId;
		order = Ordering.ASCENDING;
	}

	/**
	 * Comparator.
	 * 
	 * @param objectiveId The index of the objective to compare
	 * @param order       Ascending or descending order
	 */
	public ObjectiveComparator(int objectiveId, Ordering order) {
		this.objectiveId = objectiveId;
		this.order = order;
	}

	/**
	 * Compares two solutions according to a given objective.
	 *
	 * @param solution1 The first solution
	 * @param solution2 The second solution
	 * @return -1, or 0, or 1 if solution1 is less than, equal, or greater than
	 *         solution2, respectively, according to the established order
	 * @throws ApplicationException if the number of objective in solution1 or solution2 is less than the objectiveId received in constructor.
	 */
	@Override
	public int compare(S solution1, S solution2) {
		int result;
		if (solution1 == null) {
			if (solution2 == null) {
				result = 0;
			} else {
				result = 1;
			}
		} else if (solution2 == null) {
			result = -1;
		} else if (solution1.getNumberOfObjectives() <= objectiveId) {
			throw new ApplicationException("The solution1 has " + solution1.getNumberOfObjectives() + " objectives "
					+ "and the objective to sort is " + objectiveId);
		} else if (solution2.getNumberOfObjectives() <= objectiveId) {
			throw new ApplicationException("The solution2 has " + solution2.getNumberOfObjectives() + " objectives "
					+ "and the objective to sort is " + objectiveId);
		} else {
			double objective1 = solution1.getObjective(this.objectiveId);
			double objective2 = solution2.getObjective(this.objectiveId);
			if (order == Ordering.ASCENDING) {
				result = Double.compare(objective1, objective2);
			} else {
				result = Double.compare(objective2, objective1);
			}
		}
		return result;
	}
}
