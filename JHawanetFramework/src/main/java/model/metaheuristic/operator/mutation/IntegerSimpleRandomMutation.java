package model.metaheuristic.operator.mutation;

import java.util.Objects;

import annotations.operators.DefaultConstructor;
import exception.ApplicationException;
import model.metaheuristic.solution.IntegerSolution;
import model.metaheuristic.utils.random.BoundedRandomGenerator;
import model.metaheuristic.utils.random.JavaRandom;
import model.metaheuristic.utils.random.RandomGenerator;

/**
 * Class that perform a mutation setting up each element of the solution randomly between the defined bounds.
 *
 *
 */
public class IntegerSimpleRandomMutation implements MutationOperator<IntegerSolution> {

	private double mutationProbability;
	private RandomGenerator<Double> randomGenerator;
	private BoundedRandomGenerator<Integer> pointRandomGenerator;

	/** Constructor */
	@DefaultConstructor("Probability")
	public IntegerSimpleRandomMutation(double probability) {
		this(probability, () -> JavaRandom.getInstance().nextDouble(),
				(a, b) -> JavaRandom.getInstance().nextInt(a, b));
	}
	
	/** Constructor 
	 * @throws ApplicationException if probability is negative
	 */
	public IntegerSimpleRandomMutation(double probability, RandomGenerator<Double> randomGenerator,
			BoundedRandomGenerator<Integer> pointRandomGenerator) {
		if (probability < 0) {
			throw new ApplicationException("Mutation probability is negative: " + mutationProbability);
		}

		this.mutationProbability = probability;
		this.randomGenerator = randomGenerator;
		this.pointRandomGenerator = pointRandomGenerator;
	}

	/* Getters */
	/**
	 * Get the mutation probability
	 * @return the mutation probability
	 */
	public double getMutationProbability() {
		return mutationProbability;
	}

	/* Setters */
	/**
	 * Set the mutation probability
	 * @param mutationProbability the mutation probability
	 */
	public void setMutationProbability(double mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IntegerSolution execute(IntegerSolution solution) {
		Objects.requireNonNull(solution);

		doMutation(mutationProbability, solution);

		return solution;
	}

	/** Implements the mutation operation */
	private void doMutation(double probability, IntegerSolution solution) {

		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			if (randomGenerator.getRandomValue() <= probability) {
				Integer value = pointRandomGenerator.getRandomValue(solution.getLowerBound(i),
						solution.getUpperBound(i) + 1); // The last element is exclude for it is needed the +1
				solution.setVariable(i, value);
			}
		}

	}

}
