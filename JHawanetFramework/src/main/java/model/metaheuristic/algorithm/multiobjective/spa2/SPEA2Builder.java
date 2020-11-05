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
package model.metaheuristic.algorithm.multiobjective.spa2;

import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.impl.TournamentSelection;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.evaluator.impl.SequentialSolutionEvaluator;

import java.util.List;

public class SPEA2Builder<S extends Solution<?>> {
    private Problem<S> problem;
    private int maxIterations;
    private int populationSize;
    private CrossoverOperator<S> crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private SelectionOperator<List<S>, S> selectionOperator;
    private SequentialSolutionEvaluator<S> evaluator;
    private int k;

    public SPEA2Builder(Problem<S> problem, CrossoverOperator<S> crossoverOperator,
                        MutationOperator<S> mutationOperator) {
        this.problem = problem;
        this.maxIterations = 250;
        this.populationSize = 100;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = new TournamentSelection<>(2);
        this.evaluator = new SequentialSolutionEvaluator<>();
        this.k = 1;
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
    public SPEA2Builder<S> setProblem(Problem<S> problem) {
        this.problem = problem;
        return this;
    }

    /**
     * Get the max number of iterations.
     *
     * @return the max number of evaluation.
     */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Get the max number of iteration.
     * <p>
     * When it property is set up the maxEvaluationWithoutImprovement is set up to 0.
     * <p>
     *
     * @param maxIterations the max number of iterations.
     * @return the max number of iterations.
     * @throws IllegalArgumentException if maxIterations is negative.
     */
    public SPEA2Builder<S> setMaxIterations(int maxIterations) {
        if (maxIterations < 0) {
            throw new IllegalArgumentException("The maxIterations can't be less than 0");
        }
        this.maxIterations = maxIterations;
        return this;
    }

    /**
     * Get the population size.
     *
     * @return the population size.
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Set the population size.
     *
     * @param populationSize the new population size.
     * @return the instance of builder.
     * @throws IllegalArgumentException if population size is negative.
     */
    public SPEA2Builder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new IllegalArgumentException("The populationSize can't be less than 0");
        }
        this.populationSize = populationSize;
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
    public SPEA2Builder<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
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
    public SPEA2Builder<S> setMutationOperator(MutationOperator<S> mutationOperator) {
        this.mutationOperator = mutationOperator;
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
    public SPEA2Builder<S> setSelectionOperator(TournamentSelection<S> selectionOperator) {
        this.selectionOperator = selectionOperator;
        return this;
    }

    /**
     * Get the evaluator.
     *
     * @return the evaluator used.
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
    public SPEA2Builder<S> setEvaluator(SequentialSolutionEvaluator<S> evaluator) {
        this.evaluator = evaluator;
        return this;
    }

    /**
     * Get k.
     *
     * @return the instance of builder.
     */
    public int getK() {
        return k;
    }

    /**
     * Set k.
     *
     * @param k k
     * @return the instance of builder.
     */
    public SPEA2Builder<S> setK(int k) {
        this.k = k;
        return this;
    }

    public SPEA2<S> build() {
        return new SPEA2<>(problem, maxIterations, populationSize
                , crossoverOperator, mutationOperator, selectionOperator, evaluator, k);
    }
}
