package controller.problems;

import java.util.List;

import annotations.registrable.NewProblem;
import epanet.core.EpanetAPI;
import exception.ApplicationException;
import model.metaheuristic.algorithm.GeneticAlgorithm2;
import model.metaheuristic.operator.crossover.IntegerSinglePointCrossover;
import model.metaheuristic.operator.mutation.IntegerSimpleRandomMutation;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.UniformSelection;
import model.metaheuristic.problem.InversionCostProblem;
import model.metaheuristic.solution.IntegerSolution;

public class TestProblemRegister implements Registrable {
	private InversionCostProblem problem;

	@NewProblem(displayName = "Test")
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
			problem = new InversionCostProblem(epanet, "inp/hanoiHW.Gama", 30);
		}
	
		algorithm = new GeneticAlgorithm2<IntegerSolution>(problem, 10, selection, crossover, mutation);
		algorithm.setMaxEvaluations(10000);
		
		return algorithm;
	}

	/** {@inheritDoc}*/
	@Override
	public InversionCostProblem getProblem() {
		return this.problem;
	}
}
