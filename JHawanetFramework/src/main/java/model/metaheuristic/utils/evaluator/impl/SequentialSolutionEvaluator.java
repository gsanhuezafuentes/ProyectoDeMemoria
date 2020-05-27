package model.metaheuristic.utils.evaluator.impl;

import epanet.core.EpanetException;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.evaluator.SolutionListEvaluator;

import java.util.List;

public class SequentialSolutionEvaluator<S extends Solution<?>> implements SolutionListEvaluator<S> {

    @Override
    public List evaluate(List<S> solutionList, Problem<S> problem) throws EpanetException {
        System.out.println(problem);
        for (S s : solutionList) {
            problem.evaluate(s);
        }
        return solutionList;
    }
}
