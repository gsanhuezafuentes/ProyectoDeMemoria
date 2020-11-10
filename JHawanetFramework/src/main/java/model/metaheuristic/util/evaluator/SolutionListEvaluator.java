package model.metaheuristic.util.evaluator;

import epanet.core.EpanetException;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;

import java.util.List;

/**
 * Implement a solution evaluator.
 * <p>
 * <strong>Notes:</strong>
 * <p>
 * Epanet cannot run parallel simulations so the evaluator as to be sequentially. For this this class is added as a interface to
 * let you customize the evaluation or add print to debug
 * @param <S> the type of solution
 */
@FunctionalInterface
public interface SolutionListEvaluator<S extends Solution<?>> {
    /**
     * Evaluate a solution
     *
     * @param solutionList the solution list to evaluate
     * @param problem      the problem
     * @return the solution list evaluated
     * @throws EpanetException if there is a error in simulation
     */
    List<S> evaluate(List<S> solutionList, Problem<S> problem) throws EpanetException;
}
