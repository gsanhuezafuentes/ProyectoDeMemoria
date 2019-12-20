package controller.problems;

import annotations.registrable.NewProblem;
import epanet.core.EpanetAPI;
import exception.ApplicationException;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.algorithm.GeneticAlgorithm2;
import model.metaheuristic.operator.crossover.IntegerSinglePointCrossover;
import model.metaheuristic.operator.mutation.IntegerSimpleRandomMutation;
import model.metaheuristic.operator.selection.UniformSelection;
import model.metaheuristic.problem.InversionCostProblem;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.IntegerSolution;

public class TestProblemRegister implements Registrable {
	@NewProblem(displayName = "Test")
	public TestProblemRegister() {
	}
	
	/** {@inheritDoc}
	 * 	@throws ApplicationException if inpPath is empty or null or if gama file is null
	 */
	@Override
	public Algorithm<IntegerSolution> build(String inpPath) throws Exception {
		if (inpPath == null || inpPath.isEmpty()) {
			throw new ApplicationException("There isn't a network opened");
		}
		EpanetAPI epanet;
		GeneticAlgorithm2<IntegerSolution> algorithm = null;
		epanet = new EpanetAPI();
		epanet.ENopen(inpPath, "ejecucion.rpt", "");

		IntegerSinglePointCrossover crossover = new IntegerSinglePointCrossover(0.1); //0.1
		IntegerSimpleRandomMutation mutation = new IntegerSimpleRandomMutation(0.03); //0.03
		UniformSelection<IntegerSolution> selection = new UniformSelection<IntegerSolution>(1.6);//1.6

		Problem<IntegerSolution> problem = new InversionCostProblem(epanet, "inp/hanoiHW.Gama", 30);
		algorithm = new GeneticAlgorithm2<IntegerSolution>(problem, 10, selection, crossover, mutation);
		algorithm.setMaxEvaluations(10000);
		
		return algorithm;
	}
}
