/*
 * Code taken and modify from https://github.com/jMetal/jMetal
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
package model.metaheuristic.algorithm.singleobjective.differentialevolution;

import model.metaheuristic.operator.crossover.impl.IntegerDifferentialEvolutionCrossover;
import model.metaheuristic.operator.selection.impl.IntegerDifferentialEvolutionSelection;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.util.evaluator.SolutionListEvaluator;
import model.metaheuristic.util.evaluator.impl.SequentialSolutionEvaluator;

/**
 * DifferentialEvolutionBuilder class
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class IntegerDifferentialEvolutionBuilder {
  private Problem<IntegerSolution> problem;
  private int populationSize;
  private int maxEvaluations;
  private IntegerDifferentialEvolutionCrossover crossoverOperator;
  private IntegerDifferentialEvolutionSelection selectionOperator;
  private SolutionListEvaluator<IntegerSolution> evaluator;

  public IntegerDifferentialEvolutionBuilder(Problem<IntegerSolution>  problem) {
    this.problem = problem;
    this.populationSize = 100;
    this.maxEvaluations = 25000;
    this.crossoverOperator = new IntegerDifferentialEvolutionCrossover(0.5, 0.5, IntegerDifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);
    this.selectionOperator = new IntegerDifferentialEvolutionSelection();
    this.evaluator = new SequentialSolutionEvaluator<IntegerSolution>();
  }

  /**
   * Set the population size
   * @param populationSize the new population size
   * @return this instance
   * @throws IllegalArgumentException if populationSize is negative.
   */
  public IntegerDifferentialEvolutionBuilder setPopulationSize(int populationSize) {
    if (populationSize < 0) {
      throw new IllegalArgumentException("Population size is negative: " + populationSize);
    }

    this.populationSize = populationSize;

    return this;
  }

  /**
   * Set the max evaluations
   * @param maxEvaluations the number of evaluations
   * @return this instance
   * @throws IllegalArgumentException if maxEvaluations is negative.
   */
  public IntegerDifferentialEvolutionBuilder setMaxEvaluations(int maxEvaluations) {
    if (maxEvaluations < 0) {
      throw new IllegalArgumentException("MaxEvaluations is negative: " + maxEvaluations);
    }

    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public IntegerDifferentialEvolutionBuilder setCrossover(IntegerDifferentialEvolutionCrossover crossover) {
    this.crossoverOperator = crossover;

    return this;
  }

  public IntegerDifferentialEvolutionBuilder setSelection(IntegerDifferentialEvolutionSelection selection) {
    this.selectionOperator = selection;

    return this;
  }

  public IntegerDifferentialEvolutionBuilder setSolutionListEvaluator(SolutionListEvaluator<IntegerSolution> evaluator) {
    this.evaluator = evaluator;

    return this;
  }

  public IntegerDifferentialEvolution build() {
    return new IntegerDifferentialEvolution(problem, maxEvaluations, populationSize, crossoverOperator,
        selectionOperator, evaluator);
  }

  /* Getters */
  public Problem<IntegerSolution> getProblem() {
    return problem;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public IntegerDifferentialEvolutionCrossover getCrossoverOperator() {
    return crossoverOperator;
  }

  public IntegerDifferentialEvolutionSelection getSelectionOperator() {
    return selectionOperator;
  }

  public SolutionListEvaluator<IntegerSolution> getSolutionListEvaluator() {
    return evaluator;
  }
}

