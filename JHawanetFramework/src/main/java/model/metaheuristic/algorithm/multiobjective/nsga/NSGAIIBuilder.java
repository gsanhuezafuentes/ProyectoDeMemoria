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

import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.impl.TournamentSelection;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.comparator.DominanceComparator;
import model.metaheuristic.util.comparator.RankingAndCrowdingDistanceComparator;
import model.metaheuristic.util.evaluator.impl.SequentialSolutionEvaluator;

import java.util.List;
import java.util.Objects;

public class NSGAIIBuilder<S extends Solution<?>> {

    private Problem<S> problem;
    private int maxEvaluations;
    private int populationSize;
    private int matingPoolSize;
    private int offspringPopulationSize;
    private MutationOperator<S> mutationOperator;
    private CrossoverOperator<S> crossoverOperator;
    private SelectionOperator<List<S>, S> selectionOperator;
    private SequentialSolutionEvaluator<S> evaluator;
    private DominanceComparator<S> dominanceComparator;

    /**
     * Constructor
     *
     * @param problem           the problem
     * @param crossoverOperator the crossover operator
     * @param mutationOperator  the mutation operator
     * @param populationSize    the size of population
     */
    public NSGAIIBuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator,
                         MutationOperator<S> mutationOperator, int populationSize) {
        this.problem = problem;
        this.maxEvaluations = 25000;
        this.populationSize = populationSize;
        this.matingPoolSize = populationSize;
        this.offspringPopulationSize = populationSize;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = new TournamentSelection<>(2, new RankingAndCrowdingDistanceComparator<>());

        this.dominanceComparator = new DominanceComparator<>();
        this.evaluator = new SequentialSolutionEvaluator<>();

    }

    /**
     * Get the problem.
     *
     * @return the problem.
     */
    public Problem<S> getProblem() {
        return problem;
    }

    /**
     * Set the problem.
     *
     * @param problem the problem.
     * @return the instance of builder.
     * @throws NullPointerException if problem is null.
     */
    public NSGAIIBuilder<S> setProblem(Problem<S> problem) {
        this.problem = Objects.requireNonNull(problem);
        return this;
    }

    /**
     * Get the max number of evaluation.
     *
     * @return the max number of evaluation.
     */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /**
     * Get the max number of iteration without a improvement of the result.
     *
     * @param maxEvaluations the max number of evaluation.
     * @return the max number of evaluation.
     * @throws IllegalArgumentException if maxEvaluations is negative.
     */
    public NSGAIIBuilder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new IllegalArgumentException("The maxEvaluations can't be less than 0");
        }
        this.maxEvaluations = maxEvaluations;
        return this;
    }

    /**
     * Get the population size.
     * <p>
     * This is the number of solutions at the beginning of each generation.
     *
     * @return the population size.
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Set the population size.
     * <p>
     * This is the number of solutions at the beginning of each generation.
     *
     * @param populationSize the new population size.
     * @return the instance of builder.
     * @throws IllegalArgumentException if population size is negative.
     */
    public NSGAIIBuilder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new IllegalArgumentException("The populationSize can't be less than 0");
        }
        this.populationSize = populationSize;
        return this;
    }

    /**
     * Get the mating pool size.
     * <p>
     * This is the number of solution resulting of selection process.
     *
     * @return the mating pool size
     */
    public int getMatingPoolSize() {
        return matingPoolSize;
    }

    /**
     * Set the mating pool size.
     * <p>
     * This is the number of solution resulting of selection process.
     *
     * @param matingPoolSize the mating pool size
     * @return the instance of builder.
     * @throws IllegalArgumentException if matingPoolSize is negative.
     */
    public NSGAIIBuilder<S> setMatingPoolSize(int matingPoolSize) {
        if (matingPoolSize < 0) {
            throw new IllegalArgumentException("The matingPoolSize can't be less than 0");
        }
        this.matingPoolSize = matingPoolSize;
        return this;
    }

    /**
     * Get the offspring population size.
     * <p>
     * This is the number of solution resulting of crossover and mutation process.
     *
     * @return the offspring population size
     */
    public int getOffspringPopulationSize() {
        return offspringPopulationSize;
    }

    /**
     * Set the offspring population size.
     * <p>
     * This is the number of solution resulting of crossover and mutation process.
     *
     * @param offspringPopulationSize the offspring population size
     * @return the instance of builder.
     * @throws IllegalArgumentException if offspringPopulationSize is negative.
     */
    public NSGAIIBuilder<S> setOffspringPopulationSize(int offspringPopulationSize) {
        if (offspringPopulationSize < 0) {
            throw new IllegalArgumentException("The offspringPopulationSize can't be less than 0");
        }
        this.offspringPopulationSize = offspringPopulationSize;
        return this;
    }

    /**
     * Get the mutation operator.
     *
     * @return the mutation operator.
     */
    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    /**
     * Get the mutation operator.
     *
     * @param mutationOperator the mutation operator.
     * @return the instance of builder.
     * @throws NullPointerException if mutationOperator is null.
     */
    public NSGAIIBuilder<S> setMutationOperator(MutationOperator<S> mutationOperator) {
        this.mutationOperator = Objects.requireNonNull(mutationOperator);
        return this;
    }

    /**
     * Get the crossover operator used.
     *
     * @return the crossover operator.
     */
    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    /**
     * Set the crossover operator.
     *
     * @param crossoverOperator the crossover operator.
     * @return the instance of builder.
     * @throws NullPointerException if crossoverOperator is null.
     */
    public NSGAIIBuilder<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        this.crossoverOperator = Objects.requireNonNull(crossoverOperator);
        return this;
    }

    /**
     * Get the selection operator.
     *
     * @return the selection operator.
     */
    public SelectionOperator<List<S>, S> getSelectionOperator() {
        return selectionOperator;
    }

    /**
     * Set the selection operator.
     *
     * @param selectionOperator the selection operator.
     * @return the instance of builder.
     * @throws NullPointerException if selectionOperator is null.
     */
    public NSGAIIBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        this.selectionOperator = Objects.requireNonNull(selectionOperator);
        return this;
    }

    /**
     * Get the evaluator.
     *
     * @return the evaluator.
     */
    public SequentialSolutionEvaluator<S> getEvaluator() {
        return evaluator;
    }

    /**
     * Set the evaluator to use.
     *
     * @param evaluator the evaluator.
     * @return the instance of builder.
     * @throws NullPointerException if evaluator is null.
     */
    public NSGAIIBuilder<S> setEvaluator(SequentialSolutionEvaluator<S> evaluator) {
        this.evaluator = Objects.requireNonNull(evaluator);
        return this;
    }

    /**
     * Get the dominance comparator.
     *
     * @return the dominance comparator.
     */
    public DominanceComparator<S> getDominanceComparator() {
        return dominanceComparator;
    }

    /**
     * Set the dominance comparator used by the algorithm.
     *
     * @param dominanceComparator the new dominance comparator.
     * @return the instance of builder.
     * @throws NullPointerException if dominanceComparator is null.
     */
    public NSGAIIBuilder<S> setDominanceComparator(DominanceComparator<S> dominanceComparator) {
        this.dominanceComparator = Objects.requireNonNull(dominanceComparator);
        return this;
    }

    /**
     * Build the algorithm.
     *
     * @return the algorithm.
     */
    public NSGAII<S> build() {

        return new NSGAII<>(this.problem, this.maxEvaluations, this.populationSize, this.matingPoolSize
                , this.offspringPopulationSize
                , this.selectionOperator
                , this.crossoverOperator
                , this.mutationOperator
                , this.dominanceComparator
                , this.evaluator);
    }


}
