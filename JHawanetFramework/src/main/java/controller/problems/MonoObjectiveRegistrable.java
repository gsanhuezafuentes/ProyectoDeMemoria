package controller.problems;

import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.problem.Problem;

public abstract class MonoObjectiveRegistrable implements Registrable<Algorithm<?>> {

	@Override
	public abstract Algorithm<?> build(String inpPath) throws Exception;

	@Override
	public abstract Problem<?> getProblem();
	


}
