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

package model.metaheuristic.operator.mutation.impl;

import java.util.Objects;

import annotations.operators.DefaultConstructor;
import exception.ApplicationException;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.utils.random.JavaRandom;
import model.metaheuristic.utils.random.RandomGenerator;

/**
 * This class implements a polynomial mutation operator to be applied to Integer
 * solutions
 * <p>
 * If the lower and upper bounds of a variable are the same, no mutation is
 * carried out and the bound value is returned.
 */
public class IntegerPolynomialMutation implements MutationOperator<IntegerSolution> {
    private static final double DEFAULT_PROBABILITY = 0.01;
    private static final double DEFAULT_DISTRIBUTION_INDEX = 20.0;

    private double distributionIndex;
    private double mutationProbability;

    private final RandomGenerator<Double> random;

    /**
     * Constructor
     */
    public IntegerPolynomialMutation() {
        this(DEFAULT_PROBABILITY, DEFAULT_DISTRIBUTION_INDEX);
    }

    /**
     * Constructor
     *
     * @param problem           the problem
     * @param distributionIndex the distribution index
     */
    public IntegerPolynomialMutation(Problem<IntegerSolution> problem, double distributionIndex) {
        this(1.0 / problem.getNumberOfVariables(), distributionIndex);
    }

    /**
     * Constructor
     *
     * @param mutationProbability the mutation probability
     * @param distributionIndex   the distribution index
     */
    @DefaultConstructor({"MutationProbability", "DistributionIndex"})
    public IntegerPolynomialMutation(double mutationProbability, double distributionIndex) {
        this(mutationProbability, distributionIndex, () -> JavaRandom.getInstance().nextDouble());
    }

    /**
     * Constructor
     *
     * @param mutationProbability the mutation probability
     * @param distributionIndex   the distribution index
     * @param random              the random function to use
     * @throws ApplicationException if mutationProbability or distributionIndex is negative
     */
    public IntegerPolynomialMutation(double mutationProbability, double distributionIndex,
                                     RandomGenerator<Double> random) {
        if (mutationProbability < 0) {
            throw new ApplicationException("Mutation probability is negative: " + mutationProbability);
        } else if (distributionIndex < 0) {
            throw new ApplicationException("Distribution index is negative: " + distributionIndex);
        }
        this.mutationProbability = mutationProbability;
        this.distributionIndex = distributionIndex;

        this.random = random;
    }

    /* Getters */

    /**
     * Get the mutation probability
     *
     * @return the mutation probability
     */
    public double getMutationProbability() {
        return mutationProbability;
    }

    /**
     * Set the mutation probability
     *
     * @param mutationProbability the mutation probability
     */
    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    /* Setters */

    /**
     * Get the distribution index
     *
     * @return the distribution index
     */
    public double getDistributionIndex() {
        return distributionIndex;
    }

    /**
     * Set the distribution index
     *
     * @param distributionIndex the distribution index
     */
    public void setDistributionIndex(double distributionIndex) {
        this.distributionIndex = distributionIndex;
    }

    /**
     * Execute() method
     */
    public IntegerSolution execute(IntegerSolution solution) {
        Objects.requireNonNull(solution);

        doMutation(mutationProbability, solution);
        return solution;
    }

    /**
     * Perform the mutation operation
     */
    private void doMutation(double probability, IntegerSolution solution) {
        double rnd, delta1, delta2, mutPow, deltaq;
        double y, yl, yu, val, xy;

        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            if (random.getRandomValue() <= probability) {
                y = (double) solution.getVariable(i);
                yl = solution.getLowerBound(i);
                yu = solution.getUpperBound(i);
                if (yl == yu) {
                    y = yl;
                } else {
                    delta1 = (y - yl) / (yu - yl);
                    delta2 = (yu - y) / (yu - yl);
                    rnd = random.getRandomValue();
                    mutPow = 1.0 / (distributionIndex + 1.0);
                    if (rnd <= 0.5) {
                        xy = 1.0 - delta1;
                        val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, distributionIndex + 1.0));
                        deltaq = Math.pow(val, mutPow) - 1.0;
                    } else {
                        xy = 1.0 - delta2;
                        val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, distributionIndex + 1.0));
                        deltaq = 1.0 - Math.pow(val, mutPow);
                    }
                    y = y + deltaq * (yu - yl);
                    y = repairSolutionVariableValue(y, yl, yu);
                }
                solution.setVariable(i, (int) y);
            }
        }
    }

    /**
     * Fix the y value.
     *
     * @param y          value to repair
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @return if y is less than lowerBound return lowerBound, if y is greater than
     * upperBound return upperBound, otherwise return y.
     */
    private double repairSolutionVariableValue(double y, double lowerBound, double upperBound) {
        double result = y;
        if (y < lowerBound) {
            result = lowerBound;
        } else if (y > upperBound) {
            result = upperBound;
        }

        return result;
    }

}
