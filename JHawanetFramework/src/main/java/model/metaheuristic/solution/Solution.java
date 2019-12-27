package model.metaheuristic.solution;

import java.util.List;
import java.util.Map;

/**
 * Representation of the solution. It contain the result of objetive function
 * associated to his decision variables.
 *
 * @param <Type> The type that contains the solution.
 * 
 *               <pre>
 * Base on code from https://github.com/jMetal/jMetal
 * 
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE. © 2019 GitHub, Inc.
 *               </pre>
 */
public interface Solution<Type> {
	/**
	 * Get the decision variables indicated by index.
	 * 
	 * @param index Index of decision variable
	 */
	public Type getVariable(int index);

	/**
	 * Let set or add new decision variables to solution
	 * 
	 * @param index the index associated to variable to be added or modified
	 * @param value the value associated to decision variable
	 */
	public void setVariable(int index, Type value);

	/**
	 * Get all variables
	 * 
	 * @return a list with the value of variables
	 */
	public List<Type> getVariables();

	/**
	 * Get the objetive value associated to index
	 * 
	 * @param index the index assigned to objetive value when was saved.
	 */
	public double getObjective(int index);

	/**
	 * Set or add a new objetive
	 * 
	 * @param index the index associated to objetive
	 * @param value the value of this objetive
	 */
	public void setObjective(int index, double value);

	/**
	 * Get all objetive values
	 * 
	 * @return A array with all values of objetive function
	 */
	public double[] getObjectives();

	/**
	 * Get attributed added to solution
	 * 
	 * @param id The element associated to attribute when was saved
	 * @return The attribute
	 */
	public Object getAttribute(Object id);

	/**
	 * Set or add attribute in this solution
	 * 
	 * @param id    The key associated to value
	 * @param value The value
	 */
	public void setAttribute(Object id, Object value);

	/**
	 * Get all attributes
	 * 
	 * @return A Map with the keys and his respectives values.
	 */
	public Map<Object, Object> getAttributes();

	/**
	 * Get the number of decision variables
	 * 
	 * @return the number of variables
	 */
	int getNumberOfDecisionVariables();

	/**
	 * Get the number of objectives
	 * 
	 * @return the number of objectives
	 */
	int getNumberOfObjectives();

	/**
	 * Copy the solution and make a new without any reference to original solution.
	 * 
	 * @return the copy solution
	 */
	Solution<Type> copy();

}