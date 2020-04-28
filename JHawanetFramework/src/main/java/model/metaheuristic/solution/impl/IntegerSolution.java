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
package model.metaheuristic.solution.impl;

import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.random.BoundedRandomGenerator;
import model.metaheuristic.utils.random.JavaRandom;

import java.util.HashMap;

/**
 * A solution whose variables are Integer
 *
 */
public class IntegerSolution extends AbstractGenericSolution<Integer, Problem<IntegerSolution>> {

	/**
	 * Constructor
	 * @param problem the problem
	 */
	public IntegerSolution(Problem<IntegerSolution> problem) {
		super(problem);
		
		initializeDecisionVariables();
	}

	/**
	 * Copy constructor
	 * @param solution the solution to copy
	 */
	public IntegerSolution(IntegerSolution solution) {
		this(solution.problem);
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			setVariable(i, solution.getVariable(i));
		}

		for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
			setObjective(i, solution.getObjective(i));
		}

		attributes = new HashMap<Object, Object>(solution.attributes);
	}
	
	/**
	 * The lower bound of the decision variable.
	 * 
	 * @param index the index of the decision variable
	 * @return the lower bound
	 */
	public int getLowerBound(int index) {
		return (int) problem.getLowerBound(index);
	}

	/**
	 * The upper bound of the decision variable.
	 * 
	 * @param index the index of the decision variable
	 * @return the upper bound
	 */
	public int getUpperBound(int index) {
		return (int) problem.getUpperBound(index);
	}

	/**
	 * Initialize the decision variables
	 */
	private void initializeDecisionVariables() {
		BoundedRandomGenerator<Integer> random = (lowerBound, upperBound) -> JavaRandom.getInstance().nextInt(lowerBound, upperBound);
		
		for (int i = 0; i < this.problem.getNumberOfVariables(); i++) {
			setVariable(i, random.getRandomValue(getLowerBound(i), getUpperBound(i) + 1));
		}
		
	
	}

	/** {@inheritDoc} */
	@Override
	public Solution<Integer> copy() {
		return new IntegerSolution(this);
	}

	@Override
	public String getVariableAsString(int index) {
		return Integer.toString(getVariable(index));
	}

}
