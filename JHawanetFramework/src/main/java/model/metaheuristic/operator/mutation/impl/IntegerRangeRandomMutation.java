package model.metaheuristic.operator.mutation.impl;

import annotations.operator.DefaultConstructor;
import annotations.NumberInput;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.util.random.BoundedRandomGenerator;
import model.metaheuristic.util.random.JavaRandom;
import model.metaheuristic.util.random.RandomGenerator;

import java.util.Objects;

/**
 * Class that perform a mutation setting up each element of the solution randomly between the defined bounds and between the defined range.
 */
public class IntegerRangeRandomMutation implements MutationOperator<IntegerSolution> {

    private double mutationProbability;
    private final RandomGenerator<Double> randomGenerator;
    private final BoundedRandomGenerator<Integer> pointRandomGenerator;
    private final int range;

    /**
     * Constructor
     *
     * @param probability the probability of mutation
     * @param range       the range of mutation
     */
    @DefaultConstructor(numbers = {@NumberInput(displayName = "Probability", defaultValue = 0.1),
            @NumberInput(displayName = "Range", defaultValue = 1)})
    public IntegerRangeRandomMutation(double probability, int range) {
        this(probability, range, () -> JavaRandom.getInstance().nextDouble(),
                (a, b) -> JavaRandom.getInstance().nextInt(a, b));
    }

    /**
     * Constructor
     *
     * @param probability          the probability of mutation
     * @param range                the range of mutation
     * @param randomGenerator      the random function to use
     */
    public IntegerRangeRandomMutation(double probability, int range, RandomGenerator<Double> randomGenerator) {
        this(probability, range, randomGenerator, (a, b) -> JavaRandom.getInstance().nextInt(a, b));
    }

    /**
     * Constructor
     *
     * @param probability          the probability of mutation
     * @param range                the range of mutation
     * @param randomGenerator      the random function to use
     * @param pointRandomGenerator a random generator that generate numbers between a lower and a upper bound
     * @throws IllegalArgumentException if probability or range is less than 0.
     * @throws NullPointerException if randomGenerator or pointRandomGenerator is null.
     */
    public IntegerRangeRandomMutation(double probability, int range, RandomGenerator<Double> randomGenerator,
                                      BoundedRandomGenerator<Integer> pointRandomGenerator) {
        if (probability < 0) {
            throw new IllegalArgumentException("Mutation probability is negative: " + mutationProbability);
        }
        if (range < 0) {
            throw new IllegalArgumentException("Range is negative: " + mutationProbability);
        }
        Objects.requireNonNull(randomGenerator);
        Objects.requireNonNull(pointRandomGenerator);
        this.mutationProbability = probability;
        this.range = range;
        this.randomGenerator = randomGenerator;
        this.pointRandomGenerator = pointRandomGenerator;
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
     * Get the range of mutation
     *
     * @return the range of mutation
     */
    public int getRange() {
        return range;
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
                } while (newValue.equals(value) && range != 0); //when the range is 0 does not force the change of the variable.
                solution.setVariable(i, newValue);
            }
        }

    }
}
