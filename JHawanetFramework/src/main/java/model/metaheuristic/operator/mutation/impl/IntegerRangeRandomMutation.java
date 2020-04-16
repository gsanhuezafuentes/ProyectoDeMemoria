package model.metaheuristic.operator.mutation.impl;

import java.util.Objects;

import annotations.operators.DefaultConstructor;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.utils.random.BoundedRandomGenerator;
import model.metaheuristic.utils.random.JavaRandom;
import model.metaheuristic.utils.random.RandomGenerator;

/**
 * Class that perform a mutation setting up each element of the solution randomly between the defined bounds and between the defined range.
 */
public class IntegerRangeRandomMutation implements MutationOperator<IntegerSolution> {

    private double mutationProbability;
    private RandomGenerator<Double> randomGenerator;
    private BoundedRandomGenerator<Integer> pointRandomGenerator;
    private int range;

    /**
     * Constructor
     *
     * @param probability the probability of mutation
     * @param range       the range of mutation
     */
    @DefaultConstructor({"Probability", "Range"})
    public IntegerRangeRandomMutation(double probability, int range) {
        this(probability, range, () -> JavaRandom.getInstance().nextDouble(),
                (a, b) -> JavaRandom.getInstance().nextInt(a, b));
    }

    /**
     * Constructor
     *
     * @param probability     the probability of mutation
     * @param range           the range of mutation
     * @param randomGenerator the random function to use
     * @param pointRandomGenerator a random generator that generate numbers between a lower and a upper bound
     */
    public IntegerRangeRandomMutation(double probability, int range, RandomGenerator<Double> randomGenerator,
                                      BoundedRandomGenerator<Integer> pointRandomGenerator) {
        if (probability < 0) {
            throw new RuntimeException("Mutation probability is negative: " + mutationProbability);
        }
        if (range < 0) {
            throw new RuntimeException("Range is negative: " + mutationProbability);
        }

        this.mutationProbability = probability;
        this.range = range;
        this.randomGenerator = randomGenerator;
        this.pointRandomGenerator = pointRandomGenerator;
    }

    /* Getters */
    public double getMutationProbability() {
        return mutationProbability;
    }

    /* Setters */
    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }

    @Override
    public IntegerSolution execute(IntegerSolution solution) {
        Objects.requireNonNull(solution);

        doMutation(mutationProbability, range, solution);

        return solution;
    }

    /**
     * Implements the mutation operation
     */
    private void doMutation(double probability, int range, IntegerSolution solution) {
        for (int i = 0; i < solution.getNumberOfVariables(); i++) {
            if (randomGenerator.getRandomValue() <= probability) {
                Integer value = solution.getVariable(i);
                int minValue = solution.getLowerBound(i);
                int maxValue = solution.getUpperBound(i);

                int lowerBound = value - range;
                int upperBound = value + range;
                if (lowerBound < minValue) {
                    lowerBound = minValue;
                }
                if (upperBound > maxValue) {
                    upperBound = maxValue;
                }
                Integer newValue;
                do {
                    newValue = pointRandomGenerator.getRandomValue(lowerBound, upperBound + 1);
                } while (newValue == value);
                solution.setVariable(i, newValue);
            }
        }

    }
}
