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
package model.metaheuristic.algorithm.singleobjective.geneticalgorithm;

import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.impl.UniformSelection;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.evaluator.impl.SequentialSolutionEvaluator;

import java.util.List;
import java.util.Objects;

public class GeneticAlgorithmBuilder<S extends Solution<?>> {


    private Problem<S> problem;
    private int maxEvaluations;
    private int maxNumberOfEvaluationWithoutImprovement;
    private int populationSize;
    private MutationOperator<S> mutationOperator;
    private CrossoverOperator<S> crossoverOperator;
    private SequentialSolutionEvaluator<S> evaluator;
    private SelectionOperator<List<S>, List<S>> selectionOperator;

    /**
     * Constructor
     * @param problem the problem
     * @param crossoverOperator the crossover operator
     * @param mutationOperator the mutation operator
     */
    public GeneticAlgorithmBuilder(Problem<S> problem,
                                   CrossoverOperator<S> crossoverOperator,
                                   MutationOperator<S> mutationOperator) {
        this.problem = problem;
        this.maxEvaluations = 25000;
        this.maxNumberOfEvaluationWithoutImprovement = 0;
        this.populationSize = 100;
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = crossoverOperator;
        this.selectionOperator = new UniformSelection<>(1.6);
        this.evaluator = new SequentialSolutionEvaluator<>();
    }

    /**
     * Get the problem.
     * @return the problem.
     */
    public Problem<S> getProblem() {
        return problem;
    }

    /**
     * Set the problem.
     * @param problem the problem.
     * @return the instance of builder.
     * @throws NullPointerException if problem is null.
     */
    public GeneticAlgorithmBuilder<S> setProblem(Problem<S> problem) {
        this.problem = Objects.requireNonNull(problem);
        return this;
    }

    /**
     * Get the max number of evaluation.
     * @return the max number of evaluation.
     */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /**
     * Get the max number of evaluations.
     * <p>
     * When it property is set up the maxEvaluationWithoutImprovement is set up to 0.
     * <p>
     * @param maxEvaluations the max number of evaluation.
     * @return the instance of builder.
     * @throws IllegalArgumentException if maxEvaluations is negative.
     */
    public GeneticAlgorithmBuilder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new IllegalArgumentException("The maxEvaluations can't be less than 0");
        }
        this.maxEvaluations = maxEvaluations;
        this.maxNumberOfEvaluationWithoutImprovement = 0;
        return this;
    }

    /**
     * Get the max number of evaluation without improvement.
     * @return the max number of evaluation without improvement.
     */
    public int getMaxNumberOfEvaluationWithoutImprovement() {
        return maxNumberOfEvaluationWithoutImprovement;
    }

    /**
     * Get the max number of iteration without a improvement of the result.
     * <p>
     * When it property is set up the maxEvaluation property is set up to 0.
     * <p>
     * @param maxNumberOfEvaluationWithoutImprovement the max number of evaluation without improvement.
     * @return the instance of builder.
     * @throws IllegalArgumentException if maxNumberOfEvaluationWithoutImprovement is negative.
     */
    public GeneticAlgorithmBuilder<S> setMaxNumberOfEvaluationWithoutImprovement(int maxNumberOfEvaluationWithoutImprovement) {
        if (maxNumberOfEvaluationWithoutImprovement < 0) {
            throw new IllegalArgumentException("The maxNumberOfEvaluationWithoutImprovement can't be less than 0");
        }
        this.maxNumberOfEvaluationWithoutImprovement = maxNumberOfEvaluationWithoutImprovement;
        this.maxEvaluations = 0;
        return this;
    }

    /**
     * Get the population size.
     * @return the population size.
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Set the population size.
     * @param populationSize the new population size.
     * @return the instance of builder.
     * @throws IllegalArgumentException if population size is negative.
     */
    public GeneticAlgorithmBuilder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new IllegalArgumentException("The populationSize can't be less than 0");
        }
        this.populationSize = populationSize;
        return this;
    }

    /**
     * Get the mutation operator.
     * @return the mutation operator.
     */
    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    /**
     * Get the mutation operator.
     * @param mutationOperator the mutation operator.
     * @return the instance of builder.
     * @throws NullPointerException if mutationOperator is null.
     */
    public GeneticAlgorithmBuilder<S> setMutationOperator(MutationOperator<S> mutationOperator) {
        Objects.requireNonNull(mutationOperator);
        this.mutationOperator = mutationOperator;
        return this;
    }

    /**
     * Get the crossover operator used.
     * @return the crossover operator.
     */
    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    /**
     * Set the crossover operator.
     * @param crossoverOperator the crossover operator.
     * @return the instance of builder.
     * @throws NullPointerException if crossoverOperator is null.
     */
    public GeneticAlgorithmBuilder<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
        Objects.requireNonNull(crossoverOperator);
        this.crossoverOperator = crossoverOperator;
        return this;
    }

    /**
     * Get the evaluator.
     * @return the evaluator used.
     */
    public SequentialSolutionEvaluator<S> getEvaluator() {
        return evaluator;
    }

    /**
     * Set the evaluator to use.
     * @param evaluator the evaluator.
     * @return the instance of builder.
     * @throws NullPointerException if evaluator is null.
     */
    public GeneticAlgorithmBuilder<S> setEvaluator(SequentialSolutionEvaluator<S> evaluator) {
        Objects.requireNonNull(evaluator);
        this.evaluator = evaluator;
        return this;
    }

    /**
     * Get the selection operator.
     * @return the selection operator.
     */
    public SelectionOperator<List<S>, List<S>> getSelectionOperator() {
        return selectionOperator;
    }

    /**
     * Set the selection operator.
     * @param selectionOperator the selection operator.
     * @return the instance of builder.
     * @throws NullPointerException if selectionOperator is null.
     */
    public GeneticAlgorithmBuilder<S> setSelectionOperator(SelectionOperator<List<S>, List<S>> selectionOperator) {
        Objects.requireNonNull(selectionOperator);
        this.selectionOperator = selectionOperator;
        return this;
    }

    /**
     * Build the algorithm.
     * @return the algorithm.
     */
    public GeneticAlgorithm2<S> build() {
        GeneticAlgorithm2<S> ga = new GeneticAlgorithm2<>(problem, populationSize, selectionOperator, crossoverOperator, mutationOperator, evaluator);
        if (this.maxNumberOfEvaluationWithoutImprovement != 0) {
            ga.setMaxNumberOfIterationWithoutImprovement(this.maxNumberOfEvaluationWithoutImprovement);
        } else {
            ga.setMaxEvaluations(maxEvaluations);
        }
        return ga;
    }
}
