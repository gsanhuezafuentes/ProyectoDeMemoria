package model.metaheuristic.utils;

import java.util.Comparator;

import model.metaheuristic.utils.random.JavaRandom;

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

}
