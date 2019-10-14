package model.metaheuristic.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Some functions to apply on a List of solution
 * 
 * @author gsanh
 *
 */
public class SolutionListUtils {

	/**
	 * Select n random solution
	 * 
	 * @param numberOfSolution number of diferent solution
	 * @param solutionList     the solution list
	 * @return selected solution
	 */
	public static <S> List<S> selectNRandomDifferentSolutions(int numberOfSolution, List<S> solutionList) {
		return selectNRandomDifferentSolutions(numberOfSolution, solutionList,
				(lowerBound, upperBound) -> JavaRandom.getInstance().nextInt(lowerBound, upperBound));
	}

	/**
	 * Select n random solution
	 * 
	 * @param numberOfSolution number of different solution
	 * @param solutionList     the solution list
	 * @param random           Random generator
	 * @return selected solution
	 */
	public static <S> List<S> selectNRandomDifferentSolutions(int numberOfSolution, List<S> solutionList,
			BoundedRandomGenerator<Integer> random) {

		int sizeOfList = solutionList.size();
		if (numberOfSolution > sizeOfList) {
			throw new RuntimeException("The number of solution is greater than the solution list");
		}

		Set<Integer> selectedIndex = new HashSet<Integer>();

		List<S> selectedSolution = new ArrayList<S>(numberOfSolution);
		for (int i = 0; i < numberOfSolution;) {
			int index = random.getRandomValue(0, sizeOfList);
			if (selectedIndex.contains(index)) {
				continue;
			}
			selectedSolution.add(solutionList.get(index));
			selectedIndex.add(index);
			i++;
		}
		return selectedSolution;
	}
}
