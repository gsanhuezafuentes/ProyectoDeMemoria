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

import epanet.core.EpanetException;
import model.metaheuristic.algorithm.AbstractEvolutionaryAlgorithm;
import model.metaheuristic.algorithm.multiobjective.spa2.utils.EnvironmentalSelection;
import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.evaluator.SolutionListEvaluator;
import model.metaheuristic.util.solutionattribute.StrengthRawFitness;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SPEA2<S extends Solution<?>> extends AbstractEvolutionaryAlgorithm<S> {
    protected final int maxIterations;
    protected final SolutionListEvaluator<S> evaluator;
    protected int iterations;
    protected List<S> archive;
    protected final StrengthRawFitness<S> strenghtRawFitness = new StrengthRawFitness<>();
    protected final EnvironmentalSelection<S> environmentalSelection;
    protected final int k ;
    private final int maxPopulationSize;

    private final CrossoverOperator<S> crossoverOperator;
    private final MutationOperator<S> mutationOperator;
    private final SelectionOperator<List<S>, S> selectionOperator;

    public SPEA2(Problem<S> problem, int maxIterations, int populationSize,
                 CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                 SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator,
                 int k) {
        setProblem(problem);
        this.maxIterations = maxIterations;
        this.maxPopulationSize = populationSize;

        this.k = k ;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = selectionOperator;
        this.environmentalSelection = new EnvironmentalSelection<>(populationSize, k);

        this.archive = new ArrayList<>(populationSize);

        this.evaluator = evaluator;
    }

    @Override
    protected void initProgress() {
        iterations = 1;
    }

    @Override
    protected void updateProgress() {
        iterations++;
    }

    @Override
    public boolean isStoppingConditionReached() {
        return iterations >= maxIterations;
    }

    @Override
    public @NotNull String getStatusOfExecution() {
        return "Number of evaluations: " + this.iterations + " / " + this.maxIterations;
    }

    @Override
    protected List<S> evaluatePopulation(List<S> population) throws EpanetException {
        population = evaluator.evaluate(population, getProblem());
        return population;
    }

    @Override
    protected List<S> selection(List<S> population) {
        List<S> union = new ArrayList<>(2*getMaxPopulationSize());
        union.addAll(archive);
        union.addAll(population);
        strenghtRawFitness.computeDensityEstimator(union);
        archive = environmentalSelection.execute(union);
        return archive;
    }

    @Override
    protected List<S> reproduction(List<S> population) {
        List<S> offSpringPopulation= new ArrayList<>(getMaxPopulationSize());

        while (offSpringPopulation.size() < getMaxPopulationSize()){
            List<S> parents = new ArrayList<>(2);
            S candidateFirstParent = selectionOperator.execute(population);
            parents.add(candidateFirstParent);
            S candidateSecondParent;
            candidateSecondParent = selectionOperator.execute(population);
            parents.add(candidateSecondParent);

            List<S> offspring = crossoverOperator.execute(parents);
            mutationOperator.execute(offspring.get(0));
            offSpringPopulation.add(offspring.get(0));
        }
        return offSpringPopulation;
    }

    @Override
    protected List<S> replacement(List<S> population,
                                  List<S> offspringPopulation) {
        return offspringPopulation;
    }

    @Override
    public @NotNull List<S> getResult() {
        return archive;
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

    private int getMaxPopulationSize() {
        return this.maxPopulationSize;
    }

    @Override public @NotNull String getName() {
        return "SPEA2" ;
    }
}

