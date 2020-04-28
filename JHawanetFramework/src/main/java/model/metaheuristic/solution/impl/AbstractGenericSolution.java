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

import java.util.*;

/**
 * Is a abstract class with some method defined by default to new solutions
 *
 * @param <T> Type of solution
 * @param <P> Type of problem
 */
public abstract class AbstractGenericSolution<T, P extends Problem<?>> implements Solution<T> {
	private final List<T> decisionVariables;
	private final double[] objectives;
	protected Map<Object, Object> attributes;
	protected P problem;

	public AbstractGenericSolution(P problem) {
		this.problem = problem;
		this.attributes = new HashMap<>();
		this.decisionVariables = new ArrayList<>(problem.getNumberOfVariables());
		this.objectives = new double[problem.getNumberOfObjectives()];

		initializeObjectiveValues();
		for (int i = 0; i < problem.getNumberOfVariables(); i++) {
			decisionVariables.add(i, null);
		}
	}

	/** {@inheritDoc} */
	@Override
	public T getVariable(int index) {
		return decisionVariables.get(index);

	}

	/** {@inheritDoc} */
	@Override
	public void setVariable(int index, T value) {
		decisionVariables.set(index, value);
	}

	/** {@inheritDoc} */
	@Override
	public List<T> getVariables() {
		return decisionVariables;
	}

	/** {@inheritDoc} */
	@Override
	public double getObjective(int index) {
		return this.objectives[index];
	}

	/** {@inheritDoc} */
	@Override
	public void setObjective(int index, double value) {
		this.objectives[index] = value;
	}

	/** {@inheritDoc} */
	@Override
	public double[] getObjectives() {
		return objectives;
	}

	/** {@inheritDoc} */
	@Override
	public Object getAttribute(Object id) {
		return attributes.get(id);
	}

	/** {@inheritDoc} */
	@Override
	public void setAttribute(Object id, Object value) {
		attributes.put(id, value);
	}

	/** {@inheritDoc} */
	@Override
	public Map<Object, Object> getAttributes() {
		return attributes;
	}

	/** {@inheritDoc} */
	@Override
	public int getNumberOfVariables() {
		return this.decisionVariables.size();
	}

	/** {@inheritDoc} */
	@Override
	public int getNumberOfObjectives() {
		return this.objectives.length;
	}

	/**
	 * Initialize the objectives variables
	 */
	private void initializeObjectiveValues() {
		for (int i = 0; i < this.problem.getNumberOfObjectives(); i++) {
			this.objectives[i] = 0.0;
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("Variables: ");
		for (T var : decisionVariables) {
			result.append(var).append(" ");
		}
		result.append("Objectives: ");
		for (Double obj : objectives) {
			result.append(obj).append(" ");
		}
		result.append("\t");
		result.append("AlgorithmAttributes: ").append(attributes).append("\n");

		return result.toString();
	}

	private boolean equalsIgnoringAttributes(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		AbstractGenericSolution<?, ?> that = (AbstractGenericSolution<?, ?>) o;

		if (!Arrays.equals(objectives, that.objectives))
			return false;

		if (!decisionVariables.equals(that.decisionVariables))
			return false;

		return true;
	}

	@Override
	public boolean equals(Object o) {

		if (!this.equalsIgnoringAttributes(o)) {
			return false;
		}

		AbstractGenericSolution<?, ?> that = (AbstractGenericSolution<?, ?>) o;
		// avoid recursive infinite comparisons when solution as attribute

		// examples when problems would arise with a simple comparison
		// attributes.equals(that.attributes):
		// if A contains itself as Attribute
		// If A contains B as attribute, B contains A as attribute
		//
		// the following implementation takes care of this by considering solutions as
		// attributes as a special case

		if (attributes.size() != that.attributes.size()) {
			return false;
		}

		for (Object key : attributes.keySet()) {
			Object value = attributes.get(key);
			Object valueThat = that.attributes.get(key);

			if (value != valueThat) { // it only makes sense comparing when having different references

				if (value == null) {
					return false;
				} else if (valueThat == null) {
					return false;
				} else { // both not null

					boolean areAttributeValuesEqual;
					if (value instanceof AbstractGenericSolution) {
						areAttributeValuesEqual = ((AbstractGenericSolution<?, ?>) value)
								.equalsIgnoringAttributes(valueThat);
					} else {
						areAttributeValuesEqual = !value.equals(valueThat);
					}
					if (!areAttributeValuesEqual) {
						return false;
					} // if equal the next attributeValue will be checked
				}
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(objectives);
		result = 31 * result + decisionVariables.hashCode();
		result = 31 * result + attributes.hashCode();
		return result;
	}

}
