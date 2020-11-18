package model.metaheuristic.util;

import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.random.JavaRandom;

import java.util.Comparator;

/**
 * Class with function to apply on solution
 *
 */
public class SolutionUtils {

	/**
	 * Compare {@code solution1} and {@code solution2} using {@code comparator} and choose the better
	 * @param <S> The type of solution
	 * @param solution1 the first solution
	 * @param solution2 the second solution
	 * @param comparator the comparator to use
	 * @return the best solution
	 */
	public static <S> S getBestSolution(S solution1, S solution2, Comparator<S> comparator) {
		int result = comparator.compare(solution1, solution2);
		if (result > 0) {
			return solution1;
		}
		else if (result < 0) {
			return solution2;
		}
		
		//If are equal choose one randomly
		JavaRandom random = JavaRandom.getInstance();
		if (random.nextDouble() > 0.5) {
			return solution1;
		}
		else {
			return solution2;
		}
	}

	/**
	 * Returns the euclidean distance between a pair of solutions in the objective space.
	 * @param firstSolution the first solution.
	 * @param secondSolution the second solution.
	 * @return the euclidean distance between objective.
	 * @param <S> the type of solution.
	 */
	public static <S extends Solution<?>> double distanceBetweenObjectives(S firstSolution, S secondSolution) {

		double diff;
		double distance = 0.0;

		//euclidean distance
		for (int nObj = 0; nObj < firstSolution.getNumberOfObjectives(); nObj++) {
			diff = firstSolution.getObjective(nObj) - secondSolution.getObjective(nObj);
			distance += Math.pow(diff, 2.0);
		}

		return Math.sqrt(distance);
	}

}
