package controller.problems;

import java.io.File;
import java.util.List;

import annotations.registrable.FileInput;
import annotations.registrable.NewProblem;
import annotations.registrable.NumberInput;
import annotations.registrable.OperatorInput;
import annotations.registrable.OperatorOption;
import annotations.registrable.Parameters;
import epanet.core.EpanetAPI;
import exception.ApplicationException;
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
import model.metaheuristic.problem.InversionCostProblem;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.IntegerSolution;

/**
 * Class that describe the problem to be showned in menu of problem in the
 * GUI.<br>
 * <br>
 * 
 * The sistem read this class using reflection to get the annotation and create
 * a GUI to configure the algorithm and inject the value to injectable method.
 */
public class InversionCostRegister implements Registrable {
	private SelectionOperator<List<IntegerSolution>, List<IntegerSolution>> selection;
	private CrossoverOperator<IntegerSolution> crossover;
	private MutationOperator<IntegerSolution> mutation;
	private int numberWithoutImprovement;
	private int maxEvaluations;
	private File gama;

	/**
	 * This constructor define the elements that will be needed to build the
	 * algorithm.
	 * 
	 * @see Registrable
	 * 
	 * @param selectionOperator        - the selection operator
	 * @param crossoverOperator        - the crossover operator
	 * @param mutationOperator         - the mutation operator
	 * @param numberWithoutImprovement - the number without improvement in the
	 *                                 result
	 * @param maxEvaluations           - the max number of evaluation
	 * @throws Exception A exception if there is some error in convert the
	 *                   parameters, or in the execution of algorithm.
	 */
	@NewProblem(displayName = "Cost problem")
	@Parameters(operators = {
			@OperatorInput(displayName = "Selection Operator", value = {
					@OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class) }),
			@OperatorInput(displayName = "Crossover Operator", value = {
					@OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
					@OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class) }), //
			@OperatorInput(displayName = "Mutation Operator", value = {
					@OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
					@OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
					@OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class) }) }, //
			files = { @FileInput(displayName = "Gama") }, //
			numbers = { @NumberInput(displayName = "Number of iteration without improvement"),
					@NumberInput(displayName = "Max number of evaluation") })
	@SuppressWarnings("unchecked")//The object injected are indicated in operators elements. It guarantee its types.
	public InversionCostRegister(Object selectionOperator, Object crossoverOperator, Object mutationOperator, File gama,
			int numberWithoutImprovement, int maxEvaluations) {
		this.selection = (SelectionOperator<List<IntegerSolution>, List<IntegerSolution>>) selectionOperator;
		this.crossover = (CrossoverOperator<IntegerSolution>) crossoverOperator;
		this.mutation = (MutationOperator<IntegerSolution>) mutationOperator;
		this.numberWithoutImprovement = numberWithoutImprovement;
		this.maxEvaluations = maxEvaluations;
		this.gama = gama;
	}
	

	/** {@inheritDoc} */
	@Override
	public Algorithm<IntegerSolution> build(String inpPath) throws Exception {
		if (inpPath == null || inpPath.isEmpty()) {
			throw new ApplicationException("There isn't a network opened");
		}
		EpanetAPI epanet;
		GeneticAlgorithm2<IntegerSolution> algorithm = null;
		epanet = new EpanetAPI();
		epanet.ENopen(inpPath, "ejecucion.rpt", "");

		if (this.gama == null) {
			throw new ApplicationException("There isn't gama file");
		}
		Problem<IntegerSolution> problem = new InversionCostProblem(epanet, this.gama.getAbsolutePath(), 30);
		algorithm = new GeneticAlgorithm2<IntegerSolution>(problem, 10, selection, crossover, mutation);
		algorithm.setMaxNumberOfIterationWithoutImprovement(numberWithoutImprovement);
		algorithm.setMaxEvaluations(maxEvaluations);
		
		return algorithm;
	}

}
