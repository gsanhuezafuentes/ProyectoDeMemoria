package model.metaheuristic.utils.evaluator;

import epanet.core.EpanetException;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;

import java.util.List;

public class SequentialSolutionEvaluator<S extends Solution<?>> implements SolutionListEvaluator<S> {

    @Override
    public List evaluate(List<S> solutionList, Problem<S> problem) throws EpanetException {
        for (S s : solutionList) {
            problem.evaluate(s);
        }
        return solutionList;
    }
}
