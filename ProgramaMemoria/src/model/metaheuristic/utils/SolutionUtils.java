package model.metaheuristic.utils;

import java.util.Comparator;
import java.util.Random;

/**
 * Class with function to apply on solution
 * @author gsanh
 *
 */
public class SolutionUtils {

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
