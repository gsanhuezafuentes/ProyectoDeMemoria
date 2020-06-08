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
package model.metaheuristic.algorithm.multiobjective.smpso;

import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.mutation.impl.IntegerPolynomialMutation;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.utils.archive.BoundedArchive;
import model.metaheuristic.utils.evaluator.impl.SequentialSolutionEvaluator;

import java.util.Objects;

public class SMPSOIntegerBuilder {

    private Problem<IntegerSolution> problem;
    private BoundedArchive<IntegerSolution> leaders;
    private int swarmSize;
    private int maxIterations;
    private double c1Max;
    private double c1Min;
    private double c2Max;
    private double c2Min;
    private double r1Max;
    private int changeVelocity2;
    private int changeVelocity1;
    private double weightMin;
    private double r1Min;
    private double r2Max;
    private double r2Min;
    private double weightMax;
    private MutationOperator<IntegerSolution> mutationOperator;
    private SequentialSolutionEvaluator<IntegerSolution> evaluator;

    public SMPSOIntegerBuilder(Problem<IntegerSolution> problem, BoundedArchive<IntegerSolution> leaders) {
        this.problem = problem;
        this.leaders = leaders;

        this.swarmSize = 100;
        this.maxIterations = 250;

        this.c1Max = 2.5;
        this.c1Min = 1.5;
        this.c2Max = 2.5;
        this.c2Min = 1.5;
        this.r1Max = 1.0;
        this.r1Min = 0.0;
        this.r2Max = 1.0;
        this.r2Min = 0.0;
        this.weightMax = 0.1;
        this.weightMin = 0.1;
        this.changeVelocity1 = -1;
        this.changeVelocity2 = -1;

        this.mutationOperator = new IntegerPolynomialMutation(1.0 / this.problem.getNumberOfVariables(), 20.0);
        this.evaluator = new SequentialSolutionEvaluator<>();
    }

    public Problem<IntegerSolution> getProblem() {
        return problem;
    }

    public SMPSOIntegerBuilder setProblem(Problem<IntegerSolution> problem) {
        this.problem = problem;
        return this;
    }

    public BoundedArchive<IntegerSolution> getLeaders() {
        return leaders;
    }

    public SMPSOIntegerBuilder setLeaders(BoundedArchive<IntegerSolution> leaders) {
        this.leaders = Objects.requireNonNull(leaders);
        return this;
    }

    public int getSwarmSize() {
        return swarmSize;
    }

    public SMPSOIntegerBuilder setSwarmSize(int swarmSize) {
        this.swarmSize = swarmSize;
        return this;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public SMPSOIntegerBuilder setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
        return this;
    }

    public double getC1Max() {
        return c1Max;
    }

    public SMPSOIntegerBuilder setC1Max(double c1Max) {
        this.c1Max = c1Max;
        return this;
    }

    public double getC1Min() {
        return c1Min;
    }

    public SMPSOIntegerBuilder setC1Min(double c1Min) {
        this.c1Min = c1Min;
        return this;
    }

    public double getC2Max() {
        return c2Max;
    }

    public SMPSOIntegerBuilder setC2Max(double c2Max) {
        this.c2Max = c2Max;
        return this;
    }

    public double getC2Min() {
        return c2Min;
    }

    public SMPSOIntegerBuilder setC2Min(double c2Min) {
        this.c2Min = c2Min;
        return this;
    }

    public double getR1Max() {
        return r1Max;
    }

    public SMPSOIntegerBuilder setR1Max(double r1Max) {
        this.r1Max = r1Max;
        return this;
    }

    public int getChangeVelocity2() {
        return changeVelocity2;
    }

    public SMPSOIntegerBuilder setChangeVelocity2(int changeVelocity2) {
        this.changeVelocity2 = changeVelocity2;
        return this;
    }

    public int getChangeVelocity1() {
        return changeVelocity1;
    }

    public SMPSOIntegerBuilder setChangeVelocity1(int changeVelocity1) {
        this.changeVelocity1 = changeVelocity1;
        return this;
    }

    public double getWeightMin() {
        return weightMin;
    }

    public SMPSOIntegerBuilder setWeightMin(double weightMin) {
        this.weightMin = weightMin;
        return this;
    }

    public double getR1Min() {
        return r1Min;
    }

    public SMPSOIntegerBuilder setR1Min(double r1Min) {
        this.r1Min = r1Min;
        return this;
    }

    public double getR2Max() {
        return r2Max;
    }

    public SMPSOIntegerBuilder setR2Max(double r2Max) {
        this.r2Max = r2Max;
        return this;
    }

    public double getR2Min() {
        return r2Min;
    }

    public SMPSOIntegerBuilder setR2Min(double r2Min) {
        this.r2Min = r2Min;
        return this;
    }

    public double getWeightMax() {
        return weightMax;
    }

    public SMPSOIntegerBuilder setWeightMax(double weightMax) {
        this.weightMax = weightMax;
        return this;
    }

    /**
     * Get the mutation operator.
     *
     * @return the mutation operator.
     */
    public MutationOperator<IntegerSolution> getMutationOperator() {
        return mutationOperator;
    }

    /**
     * Get the mutation operator.
     *
     * @param mutationOperator the mutation operator.
     * @return the instance of builder.
     * @throws NullPointerException if mutationOperator is null.
     */
    public SMPSOIntegerBuilder setMutationOperator(MutationOperator<IntegerSolution> mutationOperator) {
        this.mutationOperator = Objects.requireNonNull(mutationOperator);
        return this;
    }

    /**
     * Get the evaluator.
     *
     * @return the evaluator.
     */
    public SequentialSolutionEvaluator<IntegerSolution> getEvaluator() {
        return evaluator;
    }

    /**
     * Set the evaluator to use.
     *
     * @param evaluator the evaluator.
     * @return the instance of builder.
     * @throws NullPointerException if evaluator is null.
     */
    public SMPSOIntegerBuilder setEvaluator(SequentialSolutionEvaluator<IntegerSolution> evaluator) {
        this.evaluator = Objects.requireNonNull(evaluator);
        return this;
    }

    /**
     * Build the algorithm.
     *
     * @return the algorithm.
     */
    public SMPSOInteger build() {
        return new SMPSOInteger(problem, swarmSize, leaders, mutationOperator, maxIterations
                , r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max
                , weightMin, weightMax, changeVelocity1, changeVelocity2, evaluator);

    }

}
