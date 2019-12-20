package model.metaheuristic.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exception.ApplicationException;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.random.BoundedRandomGenerator;
import model.metaheuristic.utils.random.JavaRandom;
import model.metaheuristic.utils.solutionattribute.DominanceRanking;

/**
 * Some functions to apply on a List of solution
 * 
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
	 * @throws ApplicationException if numberOfSolution is greater than the size of solution list.
	 */
	public static <S> List<S> selectNRandomDifferentSolutions(int numberOfSolution, List<S> solutionList,
			BoundedRandomGenerator<Integer> random) {

		int sizeOfList = solutionList.size();
		if (numberOfSolution > sizeOfList) {
			throw new ApplicationException("The number of solution is greater than the solution list");
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
	
	/**
	 * Get the nondominated solutions
	 * @param <S> The type of solutions
	 * @param solutionList the solution list
	 * @return the nondominated solution
	 */
	public static <S extends Solution<?>> List<S> getNondominatedSolutions(List<S> solutionList) {
		DominanceRanking<S> ranking = new DominanceRanking<S>();
	    return ranking.computeRanking(solutionList).getSubfront(0);
	  }
}
