package model.metaheuristic.operator.crossover.impl;

import annotations.operator.DefaultConstructor;
import annotations.NumberInput;
import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.util.random.BoundedRandomGenerator;
import model.metaheuristic.util.random.JavaRandom;
import model.metaheuristic.util.random.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that pick randomly a point on both parents', and designated a 'crossover point'.
 */
public class IntegerSinglePointCrossover implements CrossoverOperator<IntegerSolution> {

    private double crossoverProbability;
    private final RandomGenerator<Double> crossoverRandomGenerator;
    private final BoundedRandomGenerator<Integer> pointRandomGenerator;

    /**
     * Constructor
     * @param crossoverProbability the crossover probability
     */
    @DefaultConstructor(numbers = @NumberInput(displayName = "CrossoverProbability", defaultValue = 0.1))
    public IntegerSinglePointCrossover(double crossoverProbability) {
        this(crossoverProbability, () -> JavaRandom.getInstance().nextDouble(),
                (a, b) -> JavaRandom.getInstance().nextInt(a, b));
    }

    /**
     * Constructor
     * @param crossoverProbability the crossover probability
     * @param randomGenerator the random generator
     */
    public IntegerSinglePointCrossover(double crossoverProbability, RandomGenerator<Double> randomGenerator) {
        this(crossoverProbability, randomGenerator, (a, b) -> JavaRandom.getInstance().nextInt(a, b));
    }

    /**
     * Constructor
     * @param crossoverProbability     the probability of mutation.
     * @param crossoverRandomGenerator the random function to use.
     * @param pointRandomGenerator a random generator that generate numbers between a lower and a upper bound.
     * @throws IllegalArgumentException if the crossoverProbability is negative.
     */
    public IntegerSinglePointCrossover(double crossoverProbability, RandomGenerator<Double> crossoverRandomGenerator,
                                       BoundedRandomGenerator<Integer> pointRandomGenerator) {
        if (crossoverProbability < 0) {
            throw new IllegalArgumentException("Crossover probability is negative: " + crossoverProbability);
        }
        this.crossoverProbability = crossoverProbability;
        this.crossoverRandomGenerator = crossoverRandomGenerator;
        this.pointRandomGenerator = pointRandomGenerator;
    }

    /* Getter */

    /**
     * Get the crossover probability
     *
     * @return the crossover probability
     */
    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    /* Setter */

    /**
     * Set the crossover probability
     *
     * @param crossoverProbability the crossover probability
     */
    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if solutions is null
     * @throws IllegalArgumentException if the size of solutions list is not 2.
     */
    @Override
    public List<IntegerSolution> execute(List<IntegerSolution> solutions) {
        Objects.requireNonNull(solutions);
        if (solutions.size() != 2) {
            throw new IllegalArgumentException("There must be two parents instead of " + solutions.size());
        }

        return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1));
    }

    /**
     * Do the crossover over {@code parent1} and {@code parent2}
     *
     * @param probability the crossover probability
     * @param parent1     the first parent
     * @param parent2     the second parent.
     * @return the children
     */
    private List<IntegerSolution> doCrossover(double probability, IntegerSolution parent1, IntegerSolution parent2) {
        List<IntegerSolution> offspring = new ArrayList<>(2);
        offspring.add((IntegerSolution) parent1.copy());
        offspring.add((IntegerSolution) parent2.copy());

        if (crossoverRandomGenerator.getRandomValue() < probability) {
            // 1. Get the total number of bits
            int totalNumberOfVariables = parent1.getNumberOfVariables();

            // 2. Calculate the point to make the crossover
            int crossoverPoint = pointRandomGenerator.getRandomValue(0, totalNumberOfVariables); // Random between 0 and
            // (totalNumberOfVariables
            // - 1)

            // crossover
            for (int i = crossoverPoint; i < totalNumberOfVariables; i++) {
                offspring.get(0).setVariable(i, parent2.getVariable(i));
                offspring.get(1).setVariable(i, parent1.getVariable(i));
            }
        }

        return offspring;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfRequiredParents() {
        return 2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfGeneratedChildren() {
        return 2;
    }

}
