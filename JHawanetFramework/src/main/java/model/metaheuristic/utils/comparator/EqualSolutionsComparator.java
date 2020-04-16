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

import java.io.Serializable;
import java.util.Comparator;

import model.metaheuristic.solution.Solution;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based whether all the objective values are
 * equal or not. A dominance test is applied to decide about what solution is
 * the best.
 *
 */
@SuppressWarnings("serial")
public class EqualSolutionsComparator<S extends Solution<?>> implements Comparator<S>, Serializable {

	/**
	 * Compares two solutions.
	 *
	 * @param solution1 First <code>Solution</code>.
	 * @param solution2 Second <code>Solution</code>.
	 * @return -1, or 0, or 1, or 2 if solution1 is dominates solution2, solution1
	 *         and solution2 are equals, or solution1 is greater than solution2,
	 *         respectively.
	 */
	@Override
	public int compare(S solution1, S solution2) {
		if (solution1 == null) {
			return 1;
		} else if (solution2 == null) {
			return -1;
		}

		int dominate1; // dominate1 indicates if some objective of solution1
		// dominates the same objective in solution2. dominate2
		int dominate2; // is the complementary of dominate1.

		dominate1 = 0;
		dominate2 = 0;

		int flag;
		double value1, value2;
		for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
			value1 = solution1.getObjective(i);
			value2 = solution2.getObjective(i);

			if (value1 < value2) {
				flag = -1;
			} else if (value1 > value2) {
				flag = 1;
			} else {
				flag = 0;
			}

			if (flag == -1) {
				dominate1 = 1;
			}

			if (flag == 1) {
				dominate2 = 1;
			}
		}

		if (dominate1 == 0 && dominate2 == 0) {
			// No one dominates the other
			return 0;
		}

		if (dominate1 == 1) {
			// solution1 dominates
			return -1;
		} else if (dominate2 == 1) {
			// solution2 dominates
			return 1;
		}
		return 2;
	}
}
