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

import epanet.core.EpanetException;
import model.metaheuristic.algorithm.AbstractEvolutionaryAlgorithm;
import model.metaheuristic.operator.crossover.impl.IntegerDifferentialEvolutionCrossover;
import model.metaheuristic.operator.selection.impl.IntegerDifferentialEvolutionSelection;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.util.comparator.ObjectiveComparator;
import model.metaheuristic.util.evaluator.SolutionListEvaluator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements a differential evolution algorithm on integer variables.
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class IntegerDifferentialEvolution extends AbstractEvolutionaryAlgorithm<IntegerSolution> {
  private int populationSize;
  private int maxEvaluations;
  private IntegerDifferentialEvolutionCrossover crossoverOperator ;
  private IntegerDifferentialEvolutionSelection selectionOperator ;
  private SolutionListEvaluator<IntegerSolution> evaluator;
  private Comparator<IntegerSolution> comparator;

  private int evaluations;

  /**
   * Constructor
   *
   * @param problem Problem to solve
   * @param maxEvaluations Maximum number of evaluations to perform
   * @param populationSize
   * @param crossoverOperator
   * @param selectionOperator
   * @param evaluator
   */
  public IntegerDifferentialEvolution(Problem<IntegerSolution> problem, int maxEvaluations, int populationSize,
                                      IntegerDifferentialEvolutionCrossover crossoverOperator,
                                      IntegerDifferentialEvolutionSelection selectionOperator, SolutionListEvaluator<IntegerSolution> evaluator) {
    setProblem(problem); ;
    this.maxEvaluations = maxEvaluations;
    this.populationSize = populationSize;
    this.crossoverOperator = crossoverOperator;
    this.selectionOperator = selectionOperator;
    this.evaluator = evaluator;

    comparator = new ObjectiveComparator<IntegerSolution>(0);
  }
  
  public int getEvaluations() {
    return evaluations;
  }

  public void setEvaluations(int evaluations) {
    this.evaluations = evaluations;
  }

  @Override protected void initProgress() {
    evaluations = populationSize;
  }

  @Override protected void updateProgress() {
    evaluations += populationSize;
  }

  @Override
  public boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  /**
   * Get a string with the status of execution.<br>
   * <br>
   * A example of a message can be:<br>
   * <br>
   * <p>
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
  @Override
  public @NotNull String getStatusOfExecution() {
    return "Evaluations: " + this.evaluations + "/" + this.maxEvaluations;
  }

  @Override protected List<IntegerSolution> createInitialPopulation() {
    List<IntegerSolution> population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      IntegerSolution newIndividual = getProblem().createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override protected List<IntegerSolution> evaluatePopulation(List<IntegerSolution> population) throws EpanetException {
    return evaluator.evaluate(population, getProblem());
  }

  @Override protected List<IntegerSolution> selection(List<IntegerSolution> population) {
    return population;
  }

  @Override protected List<IntegerSolution> reproduction(List<IntegerSolution> matingPopulation) {
    List<IntegerSolution> offspringPopulation = new ArrayList<>();

    for (int i = 0; i < populationSize; i++) {
      selectionOperator.setIndex(i);
      List<IntegerSolution> parents = selectionOperator.execute(matingPopulation);

      crossoverOperator.setCurrentSolution(matingPopulation.get(i));
      List<IntegerSolution> children = crossoverOperator.execute(parents);

      offspringPopulation.add(children.get(0));
    }

    return offspringPopulation;
  }

  @Override protected List<IntegerSolution> replacement(List<IntegerSolution> population,
      List<IntegerSolution> offspringPopulation) {
    List<IntegerSolution> pop = new ArrayList<>();

    for (int i = 0; i < populationSize; i++) {
      if (comparator.compare(population.get(i), offspringPopulation.get(i)) < 0) {
        pop.add(population.get(i));
      } else {
        pop.add(offspringPopulation.get(i));
      }
    }

    pop.sort(comparator);
    return pop;
  }

  /**
   * Returns the best individual.
   * @return a list with only one individual.
   */
  @Override public List<IntegerSolution> getResult() {
    getPopulation().sort(comparator);

    return Collections.singletonList(getPopulation().get(0));
  }

  @Override public String getName() {
    return "DE" ;
  }
}
