package model.metaheuristic.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exception.ApplicationException;
import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.comparator.DominanceComparator;

/**
 * 
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
 * SOFTWARE. © 2019 GitHub, Inc.
 */
public class GeneticAlgorithm2<S extends Solution<?>> extends AbstractEvolutionaryAlgorithm<S, S> {
	private int maxPopulationSize;
	private SelectionOperator<List<S>, List<S>> selectionOperator;
	private CrossoverOperator<S> crossoverOperator;
	private MutationOperator<S> mutationOperator;
	private DominanceComparator<S> comparator;
	/**
	 * Max number of evaluation
	 */
	private int maxEvaluations;
	/**
	 * Count with the number of the number of evaluation performed
	 */
	private int performedEvaluationsNumber;
	/**
	 * Max number of evaluation without a improvement of the result
	 */
	private int maxNumberOfIterationWithoutImprovement;
	/**
	 * Count of number of evaluation without a improvement of the result
	 */
	private int numberOfIterationWithoutImprovement;

	private S bestSolution;

	public GeneticAlgorithm2(Problem<S> problem, int populationSize,
			SelectionOperator<List<S>, List<S>> selectionOperator, CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator) {
		this.problem = problem;
		this.maxPopulationSize = populationSize;
		this.selectionOperator = selectionOperator;
		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
		this.maxEvaluations = 10000;

		this.maxNumberOfIterationWithoutImprovement = 0;
		this.numberOfIterationWithoutImprovement = 0;

		this.comparator = new DominanceComparator<S>();
	}

	/**
	 * @return the maxPopulationSize
	 */
	public int getMaxPopulationSize() {
		return maxPopulationSize;
	}

	/**
	 * Get the max number of evaluation. <br>
	 * <br>
	 * 
	 * When the result returned by this method is 0 the stop condition of the
	 * algorithm don't take into account this value and only use the
	 * MaxNumberOfIterationWithoutImprovement
	 * {@link GeneticAlgorithm2#getMaxNumberOfIterationWithoutImprovement()}. If the
	 * value is other than 0 so it condition is taked into account.<br>
	 * <br>
	 * 
	 * 
	 * The default is 10000
	 * 
	 * @return the maxEvaluations
	 */
	public int getMaxEvaluations() {
		return this.maxEvaluations;
	}

	/**
	 * Set max number of evaluation.<br>
	 * <br>
	 * 
	 * MaxEvaluations and MaxNumberOfIterationWithoutImprovement equals to 0 in same
	 * time is not valid.<br>
	 * <br>
	 * 
	 * The default is 10000 <br>
	 * <br>
	 * 
	 * If you want to set this to 0 to disable the stop condition using this value
	 * so change first {@link #setMaxNumberOfIterationWithoutImprovement} to a value
	 * other than 0
	 * 
	 * @param maxEvaluations the maxEvaluations to set
	 */
	public void setMaxEvaluations(int maxEvaluations) {
		validateMaxStoppingConditionCounters(maxEvaluations, this.maxNumberOfIterationWithoutImprovement);
		this.maxEvaluations = maxEvaluations;
	}

	/**
	 * Get the max number of iteration without a improvement of the result.
	 * 
	 * When the result returned by this method is 0 the stop condition of the
	 * algorithm don't take into account this value and only use the MaxEvaluation
	 * {@link GeneticAlgorithm2#getMaxEvaluations()}. If the value is other than 0
	 * so it condition is taked into account.
	 * 
	 * The default is 0.
	 * 
	 * @return MaxNumberOfIterationWithoutImprovement.
	 */
	public int getMaxNumberOfIterationWithoutImprovement() {
		return maxNumberOfIterationWithoutImprovement;
	}

	/**
	 * Set the max number of iteration without a improvement of the result.
	 * 
	 * When the result returned by this method is 0 so the stop condition of the
	 * algorithm don't take into account this condition. If the value is other than
	 * 0 so it condition is taked into account.
	 * 
	 * The default is 0.
	 * 
	 * MaxEvaluations and MaxNumberOfIterationWithoutImprovement equals to 0 in same
	 * time is not valid.
	 * 
	 * @param maxNumberOfIterationWithoutImprovement
	 */
	public void setMaxNumberOfIterationWithoutImprovement(int maxNumberOfIterationWithoutImprovement) {
		validateMaxStoppingConditionCounters(this.maxEvaluations, maxNumberOfIterationWithoutImprovement);
		this.maxNumberOfIterationWithoutImprovement = maxNumberOfIterationWithoutImprovement;
	}

	/**
	 * @return the problem
	 */
	@Override
	protected List<S> createInitialPopulation() {
		List<S> initialPopulation = new ArrayList<S>();
		for (int i = 0; i < getMaxPopulationSize(); i++) {
			initialPopulation.add(problem.createSolution());
		}
		return initialPopulation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<S> replacement(List<S> population, List<S> offspringPopulation) {

		Collections.sort(population, comparator);
		offspringPopulation.add(population.get(0));
		offspringPopulation.add(population.get(1));
		Collections.sort(offspringPopulation, comparator);
		offspringPopulation.remove(offspringPopulation.size() - 1);
		offspringPopulation.remove(offspringPopulation.size() - 1);

		return offspringPopulation;
	}

	/**
	 * Applies the SelectionOperation
	 * 
	 * @param population
	 * @return The list of selected population.
	 */
	@Override
	protected List<S> selection(List<S> population) {
		return selectionOperator.execute(population);
	}

	/**
	 * Applies crossover operator
	 * 
	 * @param selectionPopulation
	 * @return The offspring population
	 */
	@Override
	protected List<S> reproduction(List<S> selectionPopulation) {
		int numberOfParents = crossoverOperator.getNumberOfRequiredParents();

		checkNumberOfParents(population, numberOfParents);

		List<S> offspringPopulation = new ArrayList<S>(getMaxPopulationSize());

		for (int i = 0; i < getMaxPopulationSize(); i += numberOfParents) {
			List<S> parents = new ArrayList<S>();
			for (int j = 0; j < numberOfParents; j++) {
				parents.add(population.get(i + j));
			}

			List<S> offspring = crossoverOperator.execute(parents);
			for (S solution : offspring) {
				mutationOperator.execute(solution);
				offspringPopulation.add(solution);
			}
		}
		return offspringPopulation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public S getResult() {
		Collections.sort(getPopulation(), comparator);
		return population.get(0);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isStoppingConditionReached() {
		boolean result = false;
		if (maxEvaluations > 0) {
			result = performedEvaluationsNumber >= getMaxEvaluations();
		}
		if (maxNumberOfIterationWithoutImprovement > 0) {
			result = result || (numberOfIterationWithoutImprovement >= getMaxNumberOfIterationWithoutImprovement());
		}
		return result;
	}

	/**
	 * A crossover operator is applied to a number of parents, and it assumed that
	 * the population contains a valid number of solutions. This method checks that.
	 * 
	 * @param population
	 * @param numberOfParentsForCrossover
	 */
	protected void checkNumberOfParents(List<S> population, int numberOfParentsForCrossover) {
		if ((population.size() % numberOfParentsForCrossover) != 0) {
			throw new ApplicationException("Wrong number of parents: the remainder if the " + "population size ("
					+ population.size() + ") is not divisible by " + numberOfParentsForCrossover);
		}
	}

	/**
	 * Check if maxEvaluations and maxNumberOfIterationWithoutImprovement are
	 * valid.<br>
	 * <br>
	 * To be valid both can't be less than 0 and both can't be 0 at the same time.
	 */
	private void validateMaxStoppingConditionCounters(int maxEvaluations, int maxNumberOfIterationWithoutImprovement) {
		if (maxEvaluations < 0) {
			throw new ApplicationException("Wrong MaxEvaluations can't be less than 0");
		}
		if (maxNumberOfIterationWithoutImprovement < 0) {
			throw new ApplicationException("Wrong MaxNumberOfIterationWithoutImprovement can't be less than 0");
		}
		if (maxEvaluations == 0 && maxNumberOfIterationWithoutImprovement == 0) {
			throw new ApplicationException(
					"Wrong MaxEvaluations and MaxNumberOfIterationWithoutImprovement can't be zero at the same time");
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void initProgress() {
		if (getMaxEvaluations() > 0) {
			this.performedEvaluationsNumber = getMaxPopulationSize();
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void updateProgress() {
		if (getMaxEvaluations() > 0) {
			this.performedEvaluationsNumber += getMaxPopulationSize();
		}
		if (getMaxNumberOfIterationWithoutImprovement() > 0) {
			// Initialize best solution if it not exist
			if (bestSolution == null) {
				bestSolution = getResult();
			}
			S solution = getResult();
			// Check if there is a new best solution
			if (comparator.compare(solution, bestSolution) < 0) {
				this.bestSolution = solution;
				this.numberOfIterationWithoutImprovement = 0;
			}
			this.numberOfIterationWithoutImprovement++;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getStatusOfExecution() {
		String text = "Number of evaluations: " + this.performedEvaluationsNumber + " / " + this.maxEvaluations + "\n";
		text += "Number of interation without improvement: " + this.numberOfIterationWithoutImprovement + " / "
				+ this.maxNumberOfIterationWithoutImprovement;
		return text;
	}

}
