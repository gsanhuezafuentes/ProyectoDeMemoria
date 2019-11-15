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
	private int maxEvaluations;
	private int numberEvaluations;

	public GeneticAlgorithm2(Problem<S> problem, int populationSize,
			SelectionOperator<List<S>, List<S>> selectionOperator, CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator) {
		this.problem = problem;
		this.maxPopulationSize = populationSize;
		this.selectionOperator = selectionOperator;
		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
		this.maxEvaluations = 10000;
		this.comparator = new DominanceComparator<S>();
	}

	/**
	 * @return the maxPopulationSize
	 */
	public int getMaxPopulationSize() {
		return maxPopulationSize;
	}

	/**
	 * @return the maxEvaluations
	 */
	public int getMaxEvaluations() {
		return this.maxEvaluations;
	}

	/**
	 * @param maxEvaluations the maxEvaluations to set
	 */
	public void setMaxEvaluations(int maxEvaluations) {
		this.maxEvaluations = maxEvaluations;
	}

	/**
	 * {@inheritDoc}
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
		System.out.println(getPopulation());
		return population.get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isStoppingConditionReached() {
		return numberEvaluations >= getMaxEvaluations();
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
	 * {@inheritDoc}
	 */
	@Override
	protected void initProgress() {
		this.numberEvaluations = getMaxPopulationSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateProgress() {
		this.numberEvaluations += getMaxPopulationSize();
	}
}
