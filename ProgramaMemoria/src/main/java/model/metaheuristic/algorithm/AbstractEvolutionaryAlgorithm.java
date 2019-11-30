package model.metaheuristic.algorithm;

import java.util.List;

import epanet.core.EpanetException;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;

/**
 * 
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
 * SOFTWARE. © 2019 GitHub, Inc. *
 * 
 * @param <S>
 */
public abstract class AbstractEvolutionaryAlgorithm<S extends Solution<?>, R> implements Algorithm<R> {

	protected Problem<S> problem;
	protected List<S> population;
	private int step = 0;
	
	/**
	 * Method that update the progress. It is called by {@link #run()}.
	 */
	protected abstract void updateProgress();

	/**
	 * Method that initialize the count of progress of execution. It is called by
	 * {@link #run()}
	 */
	protected abstract void initProgress();

	/**
	 * Method to decide when the algorithm execution stop. It is called by
	 * {@link #run()}
	 * 
	 * @return a boolean that indicate if the algorithm execution can continue or not.
	 */
	@Override
	public abstract boolean isStoppingConditionReached();

	/**
	 * Get the result of execution of algorithm
	 */
	public abstract R getResult();

	/**
	 * Create the initial population to the problem
	 * 
	 * @return the initial population
	 */
	protected abstract List<S> createInitialPopulation();

	/**
	 * Execute the crossover and mutation for the evolutionary algorithm.
	 * 
	 * @param selectionPopulation the population returned by selection operator
	 * @return the offspring population
	 */
	protected abstract List<S> reproduction(List<S> selectionPopulation);

	/**
	 * Execute selection operator.
	 * 
	 * @param population the population
	 * @return the selected population
	 */
	protected abstract List<S> selection(List<S> population);

	/**
	 * Filter {@code population} and {@code offspringPopulation} to create the new
	 * population to next iteration of algorithm.
	 * 
	 * @param population          the preliminary population before execute some
	 *                            operator
	 * @param offspringPopulation the population after execute all the operator
	 * @return the new population
	 */
	protected abstract List<S> replacement(List<S> population, List<S> offspringPopulation);

	/* Setters and getters */
	/**
	 * Get the population of the algorithm
	 */
	public List<S> getPopulation() {
		return population;
	}

	/**
	 * Set the population of the algorithm
	 */
	public void setPopulation(List<S> population) {
		this.population = population;
	}

	/**
	 * Set the problem to execute with algorithm
	 * 
	 * @param problem the problem
	 */
	public void setProblem(Problem<S> problem) {
		this.problem = problem;
	}

	/**
	 * Get the problem assigned to algorithm
	 * 
	 * @return the problem
	 */
	public Problem<S> getProblem() {
		return problem;
	}

	/**
	 * {@inheritDoc}
	 * @throws Exception if are a problem in close of resources open in problem
	 * @throws EpanetException if there is a error in the simulation
	 */
	@Override
	public void run() throws Exception, EpanetException {
		List<S> offspringPopulation;
		List<S> selectionPopulation;

		population = createInitialPopulation();
		population = evaluatePopulation(population);
		initProgress();
		while (!isStoppingConditionReached()) {
			selectionPopulation = selection(population);
			offspringPopulation = reproduction(selectionPopulation);
			offspringPopulation = evaluatePopulation(offspringPopulation);
			population = replacement(population, offspringPopulation);
			updateProgress();
		}
		this.problem.close();
	}
	
	/**
	 * {@inheritDoc}
	 * @throws Exception if are a problem in close of resources open in problem
	 * @throws EpanetException if there is a error in the simulation
	 */	
	@Override
	public void runSingleStep() throws Exception, EpanetException {
		List<S> offspringPopulation;
		List<S> selectionPopulation;

		if (step == 0) {
			population = createInitialPopulation();
			population = evaluatePopulation(population);
			initProgress();			
		}

		if (!isStoppingConditionReached()) {
			selectionPopulation = selection(population);
			offspringPopulation = reproduction(selectionPopulation);
			offspringPopulation = evaluatePopulation(offspringPopulation);
			population = replacement(population, offspringPopulation);
			updateProgress();
		}
		else {
			close();			
		}
		this.step++;
	}

	/**
	 * Evaluate population
	 * 
	 * @param population
	 * @return A list of solution that already has been evaluated
	 * @throws EpanetException If there is a problem in the simulation of solution
	 *                         using EpanetToolkit
	 * 
	 */
	protected List<S> evaluatePopulation(List<S> population) throws EpanetException {
		for (S s : population) {
			problem.evaluate(s);
		}
		return population;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		this.problem.close();
	}

}