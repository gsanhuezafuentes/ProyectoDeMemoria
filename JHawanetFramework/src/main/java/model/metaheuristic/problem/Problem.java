package model.metaheuristic.problem;

import epanet.core.EpanetException;
import model.epanet.element.Network;
import model.metaheuristic.solution.Solution;

/**
 * Class that denote a problem. <br>
 * <br>
 * 
 * If the problem open a resource override the close method to close its.
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
 * 
 * @param <S> Type of solution
 * 
 * 
 */
public interface Problem<S extends Solution<?>> extends AutoCloseable {

	/**
	 * Get the number of variables associated to problem
	 * 
	 * @return number of variables
	 */
	int getNumberOfVariables();

	/**
	 * Get the number of objectives that contain this problem
	 * 
	 * @return number of objectives
	 */
	int getNumberOfObjectives();

	/**
	 * Get the number of constrains to this problem
	 * 
	 * @return number of constrains
	 */
	int getNumberOfConstraints();

	/**
	 * Evaluate the solution
	 * 
	 * @param solution The solution object to evaluate
	 * @throws EpanetException If there is a problem in EPANETToolkit to evaluate
	 *                         the solution.
	 */
	void evaluate(S solution) throws EpanetException;

	/**
	 * Make a solution to this problem. This can be created randomly and be used to
	 * fill the initial population needed in some algorithms
	 * 
	 * @return Solution
	 */
	S createSolution();

	/**
	 * Setting the lower bound for each decision variable
	 * 
	 * @param index the index of decision variable
	 * @return lower bound of the decision variable
	 */
	double getLowerBound(int index);

	/**
	 * Setting the upper bound for each decision variable
	 * 
	 * @param index the index of decision variable
	 * @return upper bound of the decision variable
	 */
	double getUpperBound(int index);
	
	/**
	 * Apply the solution to the network.
	 * 
	 * <br><br><strong>Notes:</strong> <br>
	 * This method is used to save the solution as a inp from the ResultWindow.
	 * 
	 * @param network a network instance configure with data of inp.
	 * @param solution the solution to be setting in the network
	 * @return a copy of the network configured with solution
	 */
	Network applySolutionToNetwork(Network network, Solution<S> solution);

	/**
	 * Override the close method of {@link AutoCloseable} interface. His default
	 * implementation is a empty body<br>
	 * <br>
	 * If you need close a resource override this method to close it.
	 * 
	 */
	@Override
	default void close() throws Exception {
	}
}