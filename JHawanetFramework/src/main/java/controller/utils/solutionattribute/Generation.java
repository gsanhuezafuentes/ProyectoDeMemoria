package controller.utils.solutionattribute;

import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.solutionattribute.SolutionAttribute;

/**
 * This attribute is used to save the number of generation realized by the algorithm when it finish his execution.
 * <strong>Notes:</strong>
 * <p>
 * This attribute is used by the GUI. If it is used by an algorithm this will be overwritten when the GUI execute the algorithm.<br>
 * This attribute is used by {@link controller.utils.SingleObjectiveExperimentTask} and {@link controller.utils.MultiObjectiveExperimentTask} to save the generation
 * when the result was generated.
 *
 * @param <S> the type of solution.
 */
public class Generation<S extends Solution<?>> extends SolutionAttribute<S, Integer> {
}
