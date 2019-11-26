package view.problems;

import java.util.List;

import annotations.Injectable;
import annotations.NewProblem;
import annotations.NumberInput;
import annotations.OperatorInput;
import annotations.OperatorOption;
import annotations.Parameters;
import epanet.core.EpanetAPI;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.algorithm.GeneticAlgorithm2;
import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.operator.crossover.IntegerSBXCrossover;
import model.metaheuristic.operator.crossover.IntegerSinglePointCrossover;
import model.metaheuristic.operator.mutation.IntegerPolynomialMutation;
import model.metaheuristic.operator.mutation.IntegerRangeRandomMutation;
import model.metaheuristic.operator.mutation.IntegerSimpleRandomMutation;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.UniformSelection;
import model.metaheuristic.problem.CostConstructionProblem;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.IntegerSolution;

public class CostProblem implements Registrable {

	@NewProblem(displayName = "Cost problem")
	public CostProblem() {
	}

	@Injectable
	@Parameters(operators = {
			@OperatorInput(displayName = "Selection Operator", value = {
					@OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class) }),
			@OperatorInput(displayName = "Crossover Operator", value = {
					@OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
					@OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class) }),//
			@OperatorInput(displayName = "Mutation Operator", value = {
					@OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
					@OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
					@OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class) })},
			numbers = { @NumberInput(displayName = "Number of iteration without improvement"),
					@NumberInput(displayName = "Max number of evaluation") })
	
	@SuppressWarnings("unchecked")
	public Algorithm<IntegerSolution> create(Object selectionOperator, Object crossoverOperator,
			Object mutationOperator, int numberWithoutImprovement, int maxEvaluations) throws Exception {
		SelectionOperator<List<IntegerSolution>, List<IntegerSolution>> selection = (SelectionOperator<List<IntegerSolution>, List<IntegerSolution>>) selectionOperator;
		CrossoverOperator<IntegerSolution> crossover = (CrossoverOperator<IntegerSolution>) crossoverOperator;
		MutationOperator<IntegerSolution> mutation = (MutationOperator<IntegerSolution>) mutationOperator;

		EpanetAPI epanet;
		GeneticAlgorithm2<IntegerSolution> algorithm = null;
		epanet = new EpanetAPI();
		epanet.ENopen("inp/hanoi-Frankenstein.INP", "inp/hanoi.rpt", "");

		Problem<IntegerSolution> problem = new CostConstructionProblem(epanet, "inp/hanoiHW.Gama", 30);
		algorithm = new GeneticAlgorithm2<IntegerSolution>(problem, 10, selection, crossover, mutation);
		algorithm.setMaxNumberOfIterationWithoutImprovement(numberWithoutImprovement);
		algorithm.setMaxEvaluations(maxEvaluations);

		return algorithm;
	}

}
