package model.metaheuristic.util.solutionattribute;

import model.metaheuristic.solution.Solution;

/**
 * Used to add a constrain in a solution that can be evaluated with {@link model.metaheuristic.util.comparator.DominanceComparator} together
 * {@link model.metaheuristic.util.comparator.OverallConstraintViolationComparator}.
 * <p>
 * <strong>Notes:</strong>
 * <p>
 * If this attribute is present in the solution it will be shown in the GUI results window.
 *
 * @param <S> Type of solution
 */
public class OverallConstraintViolation<S extends Solution<?>> extends SolutionAttribute<S, Double> {

}
