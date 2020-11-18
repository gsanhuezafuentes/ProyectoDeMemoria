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
package model.metaheuristic.util.comparator;

import model.metaheuristic.solution.Solution;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This class implements a comparator based on the rank of the solutions; if the
 * rank is the same then the crowding distance is used.
 *
 */
@SuppressWarnings("serial")
public class RankingAndCrowdingDistanceComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
	private final Comparator<S> rankComparator = new RankingComparator<S>();
	private final Comparator<S> crowdingDistanceComparator = new CrowdingDistanceComparator<S>();

	/**
	 * Compares two solutions.
	 *
	 * @param solution1 Object representing the first solution
	 * @param solution2 Object representing the second solution.
	 * @return -1, or 0, or 1 if solution1 is less than, equal, or greater than
	 *         solution2, respectively.
	 */
	@Override
	public int compare(S solution1, S solution2) {
		int result = rankComparator.compare(solution1, solution2);
		if (result == 0) {
			result = crowdingDistanceComparator.compare(solution1, solution2);
		}

		return result;
	}
}
