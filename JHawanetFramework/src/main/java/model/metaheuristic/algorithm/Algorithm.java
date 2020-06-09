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
package model.metaheuristic.algorithm;

import epanet.core.EpanetException;
import model.metaheuristic.solution.Solution;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Interface that contains method to define Algorithm
 *
 * @param <Result> The Type of result <br>
 *                 <br>
 *
 */
public interface Algorithm<Result extends Solution<?>>{

//	/**
//	 * Start algorithm execution
//	 *
//	 * @throws EpanetException If there is a problem in the simulation of solution
//	 *                         using EpanetToolkit
//	 * @throws Exception       If there is a problem in the close method of problem
//	 */
//	void run() throws Exception, EpanetException;

	/**
	 * Execute a only step (iteration) of algorithm for call. It method is used by
	 * GUI because let cancel the execution.
	 * 
	 * @throws EpanetException If there is a problem in the simulation of solution
	 *                         using EpanetToolkit
	 * @throws Exception       If there is a problem in the close method of problem
	 */
	void runSingleStep() throws Exception, EpanetException;

	/**
	 * Method to decide when the algorithm execution must stop. It would be called
	 * by run() and {@link #runSingleStep()} to verify if the algorithm can
	 * continue his execution.
	 * 
	 * @return a boolean that indicate if the algorithm execution can continue or
	 *         not.
	 */
	boolean isStoppingConditionReached();

	/**
	 * Get a string with the status of execution.<br>
	 * <br>
	 * A example of a message can be:<br>
	 * <br>
	 * 
	 * Number of evaluation: 100/1000 <br>
	 * 
	 * <br>
	 * <br>
	 * <strong>Notes:</strong> <br>
	 * The string returned for this method will be showed when the algorithm running
	 * in the GUI.
	 * 
	 * @return a string with the status or empty string.
	 */
	@NotNull String getStatusOfExecution();

	/**
	 * Get the result of the algorithm execution.
	 * 
	 * A singleobjective metaheuristics should return a list with only one solution. In
	 * case of multiobjective metaheuristics it should return a list with more than
	 * one solution. In the rare case that there is no solution a empty list has to
	 * be returned.
	 * 
	 * @return the solution or solutions given as result of execution of algorithm or empty list.
	 */
	@NotNull List<Result> getResult();

	/**
	 * Get the name of algorithm.
	 * @return the name of algorithm or empty string.
	 */
	@NotNull String getName();

}