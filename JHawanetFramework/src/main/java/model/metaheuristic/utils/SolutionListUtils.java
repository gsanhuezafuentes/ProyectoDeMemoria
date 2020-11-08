package model.metaheuristic.utils;

import exception.ApplicationException;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.random.BoundedRandomGenerator;
import model.metaheuristic.utils.random.JavaRandom;
import model.metaheuristic.utils.solutionattribute.DominanceRanking;

import java.util.*;

/**
 * Some functions to apply on a List of solution
 */
public class SolutionListUtils {

    /**
     * Select n random solution
     *
     * @param numberOfSolution number of diferent solution
     * @param solutionList     the solution list
     * @param <S>              the type of solution
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
     * @param <S>              the type of solution
     * @return selected solution
     * @throws IllegalArgumentException if numberOfSolution is greater than the size of solution list.
     */
    public static <S> List<S> selectNRandomDifferentSolutions(int numberOfSolution, List<S> solutionList,
                                                              BoundedRandomGenerator<Integer> random) {

        int sizeOfList = solutionList.size();
        if (numberOfSolution > sizeOfList) {
            throw new IllegalArgumentException("The number of solution is greater than the solution list");
        }

        Set<Integer> selectedIndex = new HashSet<Integer>();

        List<S> selectedSolution = new ArrayList<S>(numberOfSolution);
        for (int i = 0; i < numberOfSolution; ) {
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
     *
     * @param <S>          The type of solutions
     * @param solutionList the solution list
     * @return the nondominated solution
     */
    public static <S extends Solution<?>> List<S> getNondominatedSolutions(List<S> solutionList) {
        DominanceRanking<S> ranking = new DominanceRanking<S>();
        return ranking.computeRanking(solutionList).getSubfront(0);
    }

    /**
     * Find the worst solution
     *
     * @param solutionList the solution list
     * @param comparator the comparator to use
     * @param <S> the type of solution
     * @return the worst solution
     * @throws NullPointerException if solution list or comparator is null
     * @throws IllegalArgumentException if solution list is empty
     */
    public static <S> S findWorstSolution(Collection<S> solutionList, Comparator<S> comparator) {
        Objects.requireNonNull(solutionList, "No solution provided: " + solutionList);
        Objects.requireNonNull(comparator);
        if ((solutionList.isEmpty())) {
            throw new IllegalArgumentException("No solution provided: " + solutionList);
        }

        S worstKnown = solutionList.iterator().next();
        for (S candidateSolution : solutionList) {
            if (comparator.compare(worstKnown, candidateSolution) < 0) {
                worstKnown = candidateSolution;
            }
        }

        return worstKnown;
    }

    /**
     * Returns a matrix with the euclidean distance between each pair of solutions in the population.
     * Distances are measured in the objective space.
     *
     * @param solutionSet the list of solutions.
     * @return the distance matrix.
     * @param <S> the type of solution.
     */
    public static <S extends Solution<?>> double[][] distanceMatrix(List<S> solutionSet) {
        double[][] distance = new double[solutionSet.size()][solutionSet.size()];
        for (int i = 0; i < solutionSet.size(); i++) {
            distance[i][i] = 0.0;
            for (int j = i + 1; j < solutionSet.size(); j++) {
                distance[i][j] = SolutionUtils.distanceBetweenObjectives(solutionSet.get(i), solutionSet.get(j));
                distance[j][i] = distance[i][j];
            }
        }
        return distance;
    }

}
