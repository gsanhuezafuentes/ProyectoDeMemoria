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
package model.metaheuristic.utils.comparator;

import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.solutionattribute.CrowdingDistance;

import java.util.Comparator;

/**
 * Compares two solutions according to the crowding distance attribute. The
 * higher the distance the better
 *
 */
public class CrowdingDistanceComparator<S extends Solution<?>> implements Comparator<S> {
	private final CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();

	/**
	 * Compare two solutions.
	 *
	 * @param solution1 Object representing the first <code>Solution</code>.
	 * @param solution2 Object representing the second <code>Solution</code>.
	 * @return -1, or 0, or 1 if solution1 is has greater, equal, or less distance
	 *         value than solution2, respectively.
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
		} else {
			double distance1 = Double.MIN_VALUE;
			double distance2 = Double.MIN_VALUE;

			if (crowdingDistance.getAttribute(solution1) != null) {
				distance1 = crowdingDistance.getAttribute(solution1);
			}

			if (crowdingDistance.getAttribute(solution2) != null) {
				distance2 = crowdingDistance.getAttribute(solution2);
			}

			if (distance1 > distance2) {
				result = -1;
			} else if (distance1 < distance2) {
				result = 1;
			} else {
				result = 0;
			}
		}

		return result;
	}
}
