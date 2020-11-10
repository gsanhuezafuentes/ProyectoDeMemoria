/*
 * Code taken from from https://github.com/jMetal/jMetal
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
package model.metaheuristic.util.comparator;

import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.solutionattribute.OverallConstraintViolation;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on the overall constraint violation of
 * the solutions, as done in NSGA-II.
 *
 */
public class OverallConstraintViolationComparator<S extends Solution<?>> implements ConstraintViolationComparator<S> {
	private final OverallConstraintViolation<S> overallConstraintViolation;

	/**
	 * Constructor
	 */
	public OverallConstraintViolationComparator() {
		overallConstraintViolation = new OverallConstraintViolation<S>();
	}

	/**
	 * Compares two solutions. If the solutions has no constraints the method return
	 * 0
	 * 
	 * If both constrains are greater than 0 then this method return 0.
	 * 
	 * @param solution1 Object representing the first <code>Solution</code>.
	 * @param solution2 Object representing the second <code>Solution</code>.
	 * @return -1, or 0, or 1 if o1 is greater than, equal, or less than o2,
	 *         respectively.
	 */
	public int compare(S solution1, S solution2) {
		double violationDegreeSolution1;
		double violationDegreeSolution2;
		if (overallConstraintViolation.getAttribute(solution1) == null) {
			return 0;
		}
		violationDegreeSolution1 = overallConstraintViolation.getAttribute(solution1);
		violationDegreeSolution2 = overallConstraintViolation.getAttribute(solution2);

		if ((violationDegreeSolution1 < 0) && (violationDegreeSolution2 < 0)) {
			if (violationDegreeSolution1 > violationDegreeSolution2) {
				return -1;
			} else if (violationDegreeSolution2 > violationDegreeSolution1) {
				return 1;
			} else {
				return 0;
			}
		} else if ((violationDegreeSolution1 == 0) && (violationDegreeSolution2 < 0)) {
			return -1;
		} else if ((violationDegreeSolution1 < 0) && (violationDegreeSolution2 == 0)) {
			return 1;
		} else {
			return 0;
		}
	}
}
