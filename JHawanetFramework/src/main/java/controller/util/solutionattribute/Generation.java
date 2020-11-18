package controller.util.solutionattribute;

import controller.multiobjective.util.MultiObjectiveExperimentTask;
import controller.singleobjective.util.SingleObjectiveExperimentTask;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.solutionattribute.SolutionAttribute;

/**
 * This attribute is used to save the number of generation realized by the algorithm when it finish his execution.
 * <strong>Notes:</strong>
 * <p>
 * This attribute is used by the GUI. If it is used by an algorithm this will be overwritten when the GUI execute the algorithm.<br>
 * This attribute is used by {@link SingleObjectiveExperimentTask} and {@link MultiObjectiveExperimentTask} to save the generation
 * when the result was generated.
 *
 * @param <S> the type of solution.
 */
public class Generation<S extends Solution<?>> extends SolutionAttribute<S, Integer> {
}
