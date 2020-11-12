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
package model.metaheuristic.algorithm.multiobjective.nsga;

import epanet.core.EpanetException;
import model.metaheuristic.algorithm.AbstractEvolutionaryAlgorithm;
import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.impl.RankingAndCrowdingSelection;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.SolutionListUtils;
import model.metaheuristic.util.evaluator.SolutionListEvaluator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class with the implementation of NSGA-II
 */
public class NSGAII<S extends Solution<?>> extends AbstractEvolutionaryAlgorithm<S> {

    protected final int maxEvaluations;

    protected int numberOfEvaluations;
    protected final Comparator<S> dominanceComparator;

    protected int maxPopulationSize;
    protected final int matingPoolSize;
    protected final int offspringPopulationSize;

    protected final SelectionOperator<List<S>, S> selectionOperator;
    protected final CrossoverOperator<S> crossoverOperator;
    protected final MutationOperator<S> mutationOperator;
    protected List<S> population;
    protected final SolutionListEvaluator<S> evaluator;

    /**
     * Constructor
     *
     * @param problem                 the problem to solve.
     * @param maxEvaluations          the max number of evaluation to realize.
     * @param populationSize          the population size (the number of solutions at the beginning of each generation).
     * @param matingPoolSize          the mating pool size (the number of solutions resulting of selection process).
     * @param offspringPopulationSize the offspring population size (the number of solutions resulting of crossover and mutation process).
     * @param crossoverOperator       the crossover operator.
     * @param mutationOperator        the mutation operator.
     * @param selectionOperator       the selection operator.
     * @param dominanceComparator     the dominance operator.
     * @param evaluator               the solution evaluator.
     */
    public NSGAII(Problem<S> problem, int maxEvaluations, int populationSize
            , int matingPoolSize, int offspringPopulationSize
            , SelectionOperator<List<S>, S> selectionOperator
            , CrossoverOperator<S> crossoverOperator
            , MutationOperator<S> mutationOperator
            , Comparator<S> dominanceComparator
            , SolutionListEvaluator<S> evaluator) {
        setProblem(problem);
        this.maxEvaluations = maxEvaluations;
        setMaxPopulationSize(populationSize);

        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = selectionOperator;

        this.dominanceComparator = dominanceComparator;
        this.evaluator = evaluator;

        this.matingPoolSize = matingPoolSize;
        this.offspringPopulationSize = offspringPopulationSize;
    }

    /**
     * Get the max population size.
     *
     * @return the max population size
     */
    public int getMaxPopulationSize() {
        return maxPopulationSize;
    }

    /**
     * Set the max population size
     *
     * @param maxPopulationSize the size of population
     */
    public void setMaxPopulationSize(int maxPopulationSize) {
        this.maxPopulationSize = maxPopulationSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initProgress() {
        numberOfEvaluations = getMaxPopulationSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<S> evaluatePopulation(List<S> population) throws EpanetException {
        return this.evaluator.evaluate(population, problem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateProgress() {
        numberOfEvaluations += offspringPopulationSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStoppingConditionReached() {
        return numberOfEvaluations >= maxEvaluations;
    }

    /**
     * This method iteratively applies a {@link SelectionOperator} to the population
     * to fill the mating pool population.
     *
     * @param population the population to operate the selection operator
     * @return The mating pool population
     */
    @Override
    protected List<S> selection(List<S> population) {
        List<S> matingPopulation = new ArrayList<>(population.size());
        for (int i = 0; i < matingPoolSize; i++) {
            S solution = selectionOperator.execute(population);
            matingPopulation.add(solution);
        }

        return matingPopulation;
    }

    /**
     * This methods iteratively applies a {@link CrossoverOperator} a
     * {@link MutationOperator} to the population to create the offspring
     * population. The population size must be divisible by the number of parents
     * required by the {@link CrossoverOperator}; this way, the needed parents are
     * taken sequentially from the population.
     * <p>
     * The number of solutions returned by the {@link CrossoverOperator} must be
     * equal to the offspringPopulationSize state variable
     *
     * @param matingPool the mating Pool
     * @return The new created offspring population
     */
    @Override
    protected List<S> reproduction(List<S> matingPool) {
        int numberOfParents = crossoverOperator.getNumberOfRequiredParents();

        checkNumberOfParents(matingPool, numberOfParents);

        List<S> offspringPopulation = new ArrayList<>(offspringPopulationSize);
        for (int i = 0; i < matingPool.size(); i += numberOfParents) {
            List<S> parents = new ArrayList<>(numberOfParents);
            for (int j = 0; j < numberOfParents; j++) {
                parents.add(matingPool.get(i + j));
            }

            List<S> offspring = crossoverOperator.execute(parents);

            for (S s : offspring) {
                mutationOperator.execute(s);
                offspringPopulation.add(s);
                if (offspringPopulation.size() >= offspringPopulationSize)
                    break;
            }
        }
        return offspringPopulation;
    }

    /**
     * This method implements a default scheme create the initial population of
     * genetic algorithm
     *
     * @return a list with the initial population
     */
    protected List<S> createInitialPopulation() {
        List<S> population = new ArrayList<>(getMaxPopulationSize());
        for (int i = 0; i < getMaxPopulationSize(); i++) {
            S newIndividual = getProblem().createSolution();
            population.add(newIndividual);
        }
        return population;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        List<S> jointPopulation = new ArrayList<>();
        jointPopulation.addAll(population);
        jointPopulation.addAll(offspringPopulation);

        RankingAndCrowdingSelection<S> rankingAndCrowdingSelection;
        rankingAndCrowdingSelection = new RankingAndCrowdingSelection<S>(getMaxPopulationSize(), dominanceComparator);

        return rankingAndCrowdingSelection.execute(jointPopulation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull List<S> getResult() {
        return SolutionListUtils.getNondominatedSolutions(getPopulation());
    }

    /**
     * A crossover operator is applied to a number of parents, and it assumed that
     * the population contains a valid number of solutions. This method checks that.
     *
     * @param population                  the population.
     * @param numberOfParentsForCrossover the number of parent to crossover.
     * @throws IllegalArgumentException if there is a wrong number of parent (population size % number of parent is not equals to 0).
     */
    protected void checkNumberOfParents(List<S> population, int numberOfParentsForCrossover) {
        if ((population.size() % numberOfParentsForCrossover) != 0) {
            throw new IllegalArgumentException("Wrong number of parents: the remainder if the " + "population size ("
                    + population.size() + ") is not divisible by " + numberOfParentsForCrossover);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getStatusOfExecution() {
        return "Number of evaluations: " + this.numberOfEvaluations + " / " + this.maxEvaluations;
    }

    @Override
    public @NotNull String getName() {
        return "NSGA-II";
    }

}
