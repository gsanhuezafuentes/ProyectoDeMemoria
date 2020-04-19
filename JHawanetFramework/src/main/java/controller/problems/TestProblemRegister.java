package controller.problems;

import java.util.List;

import annotations.registrable.NewProblem;
import epanet.core.EpanetAPI;
import exception.ApplicationException;
import model.metaheuristic.algorithm.monoobjective.GeneticAlgorithm2;
import model.metaheuristic.operator.crossover.impl.IntegerSinglePointCrossover;
import model.metaheuristic.operator.mutation.impl.IntegerSimpleRandomMutation;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.impl.UniformSelection;
import model.metaheuristic.problem.impl.PipeOptimizing;
import model.metaheuristic.solution.impl.IntegerSolution;

public final class TestProblemRegister extends MonoObjectiveRegistrable {
	private PipeOptimizing problem;

	@NewProblem(displayName = "Pipe Optimizing Hanoi", algorithmName = "Genetic Algorithm")
	public TestProblemRegister() {
	}
	
	/** {@inheritDoc}
	 * 	@throws ApplicationException if inpPath is empty or null or if gama file is null
	 */
	@Override
	public GeneticAlgorithm2<IntegerSolution> build(String inpPath) throws Exception {
		if (inpPath == null || inpPath.isEmpty()) {
			throw new ApplicationException("There isn't a network opened");
		}
		EpanetAPI epanet;
		GeneticAlgorithm2<IntegerSolution> algorithm = null;
		epanet = new EpanetAPI();
		epanet.ENopen(inpPath, "ejecucion.rpt", "");
		SelectionOperator<List<IntegerSolution>, List<IntegerSolution>> selection = new UniformSelection<IntegerSolution>(1.6);
		IntegerSinglePointCrossover crossover = new IntegerSinglePointCrossover(0.1); //0.1
		IntegerSimpleRandomMutation mutation = new IntegerSimpleRandomMutation(0.03); //0.03

		if (this.problem == null) {
			problem = new PipeOptimizing(epanet, "inp/hanoiHW.Gama", 30);
		}
	
		algorithm = new GeneticAlgorithm2<>(problem, 10, selection, crossover, mutation);
		algorithm.setMaxEvaluations(10000);
		
		return algorithm;
	}

	/** {@inheritDoc}*/
	@Override
	public PipeOptimizing getProblem() {
		return this.problem;
	}
}
