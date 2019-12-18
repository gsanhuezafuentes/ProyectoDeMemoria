package model.metaheuristic.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.metaheuristic.problem.Problem;

/**
 * 
 *
 * <pre>
 * Base on code from https://github.com/jMetal/jMetal
 * 
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. © 2019 GitHub, Inc.
 * </pre>
 */
public class IntegerSolution implements Solution<Integer> {
	private Map<Object, Object> attributes;
	private List<Integer> desicionVariables;
	private double[] objectives;
	private Problem<IntegerSolution> problem;

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

	/** Constructor */
	public IntegerSolution(Problem<IntegerSolution> problem) {

		this.problem = problem;
		this.attributes = new HashMap<Object, Object>();
		this.desicionVariables = new ArrayList<Integer>(problem.getNumberOfVariables());
		this.objectives = new double[problem.getNumberOfObjectives()];

		initializeObjectives();
		initializeDecisionVariables();
	}

	/** Constructor */

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
	 * Initialize the decision variables
	 */
	private void initializeDecisionVariables() {
		for (int i = 0; i < this.problem.getNumberOfVariables(); i++) {
			this.desicionVariables.add(i, null);
		}
	}

	/**
	 * Initialize the objetive variables
	 */
	private void initializeObjectives() {
		for (int i = 0; i < this.problem.getNumberOfObjectives(); i++) {
			this.objectives[i] = 0.0;
		}
	}

	/** {@inheritDoc} */
	@Override
	public Integer getVariable(int index) {
		return desicionVariables.get(index);

	}

	/** {@inheritDoc} */
	@Override
	public void setVariable(int index, Integer value) {
		desicionVariables.set(index, value);
	}

	/** {@inheritDoc} */
	@Override
	public List<Integer> getVariables() {
		return desicionVariables;
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
		return this.desicionVariables.size();
	}

	/** {@inheritDoc} */
	@Override
	public int getNumberOfObjectives() {
		return this.objectives.length;
	}

	/** {@inheritDoc} */
	@Override
	public Solution<Integer> copy() {
		return new IntegerSolution(this);
	}

	@Override
	public String toString() {
		String result = "Variables: ";
		for (int var : this.desicionVariables) {
			result += "" + var + " ";
		}
		result += "Objectives: ";
		for (Double obj : this.objectives) {
			result += "" + String.format("%f", obj) + " ";
		}
		result += "AlgorithmAttributes: " + attributes + "\n";

		return result;
	}

	/**
	 * Compare objectives and decision variables
	 * 
	 * @param o
	 * @return
	 */
	private boolean equalsIgnoringAttributes(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		IntegerSolution that = (IntegerSolution) o;

		if (!Arrays.equals(this.objectives, that.objectives))
			return false;

		if (!this.desicionVariables.equals(that.desicionVariables))
			return false;

		return true;
	}

	/**
	 * Compare solution.
	 */
	@Override
	public boolean equals(Object o) {

		if (!this.equalsIgnoringAttributes(o)) {
			return false;
		}

		IntegerSolution that = (IntegerSolution) o;
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

					areAttributeValuesEqual = !value.equals(valueThat);

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
		result = 31 * result + desicionVariables.hashCode();
		result = 31 * result + attributes.hashCode();
		return result;
	}
}
