package controller.problems;

import java.io.File;
import java.util.List;

import annotations.registrable.FileInput;
import annotations.registrable.NewProblem;
import annotations.registrable.NumberInput;
import annotations.registrable.NumberToggleInput;
import annotations.registrable.OperatorInput;
import annotations.registrable.OperatorOption;
import annotations.registrable.Parameters;
import epanet.core.EpanetAPI;
import exception.ApplicationException;
import model.metaheuristic.algorithm.monoobjective.GeneticAlgorithm2;
import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.operator.crossover.impl.IntegerSBXCrossover;
import model.metaheuristic.operator.crossover.impl.IntegerSinglePointCrossover;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.mutation.impl.IntegerPolynomialMutation;
import model.metaheuristic.operator.mutation.impl.IntegerRangeRandomMutation;
import model.metaheuristic.operator.mutation.impl.IntegerSimpleRandomMutation;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.impl.UniformSelection;
import model.metaheuristic.problem.impl.PipeOptimizing;
import model.metaheuristic.solution.impl.IntegerSolution;

/**
 * Class that describe the problem to be showned in menu of problem in the
 * GUI.<br>
 * <br>
 * 
 * The sistem read this class using reflection to get the annotation and create
 * a GUI to configure the algorithm and inject the value to injectable method.
 */
public final class PipeOptimizingRegister extends MonoObjectiveRegistrable {
	private SelectionOperator<List<IntegerSolution>, List<IntegerSolution>> selection;
	private CrossoverOperator<IntegerSolution> crossover;
	private MutationOperator<IntegerSolution> mutation;
	private int numberWithoutImprovement;
	private int maxEvaluations;
	private File gama;
	private PipeOptimizing problem;

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
	@NewProblem(displayName = "Pipe optimizing", algorithmName = "Genetic Algorithm")
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
			files = { @FileInput(displayName = "Gama")}, //
			numbers = { @NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size") }, //
			numbersToggle = {
					@NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
					@NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation") })
	@SuppressWarnings("unchecked") // The object injected are indicated in operators elements. It guarantee its
									// types.
	public PipeOptimizingRegister(Object selectionOperator, Object crossoverOperator, Object mutationOperator, File gama,
			int minPressure, int populationSize, int numberWithoutImprovement, int maxEvaluations) {
		System.out.println(selectionOperator);
		System.out.println(crossoverOperator);
		System.out.println(mutationOperator);
		System.out.println(gama);
		System.out.println(minPressure);
		System.out.println(populationSize);
		System.out.println(numberWithoutImprovement);
		System.out.println(maxEvaluations);
		this.selection = (SelectionOperator<List<IntegerSolution>, List<IntegerSolution>>) selectionOperator; // unchecked cast
		this.crossover = (CrossoverOperator<IntegerSolution>) crossoverOperator; // unchecked cast
		this.mutation = (MutationOperator<IntegerSolution>) mutationOperator; // unchecked cast
		this.numberWithoutImprovement = numberWithoutImprovement;
		this.maxEvaluations = maxEvaluations;
		this.gama = gama;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ApplicationException if inpPath is empty or null or if gama file is
	 *                              null
	 */
	@Override
	public GeneticAlgorithm2<IntegerSolution> build(String inpPath) throws Exception {
		if (inpPath == null || inpPath.isEmpty()) {
			throw new ApplicationException("There isn't a network opened");
		}
		GeneticAlgorithm2<IntegerSolution> algorithm = null;
		EpanetAPI epanet = new EpanetAPI();
		epanet.ENopen(inpPath, "ejecucion.rpt", "");

		if (this.gama == null) {
			throw new ApplicationException("There isn't gama file");
		}
		if (this.problem == null) {
			this.problem = new PipeOptimizing(epanet, this.gama.getAbsolutePath(), 30);

		}
		algorithm = new GeneticAlgorithm2<IntegerSolution>(this.problem, 10, selection, crossover, mutation);
		if (numberWithoutImprovement > 0) {
			algorithm.setMaxNumberOfIterationWithoutImprovement(numberWithoutImprovement);
		} else {
			algorithm.setMaxEvaluations(maxEvaluations);

		}
		return algorithm;
	}

	/** {@inheritDoc} */
	@Override
	public PipeOptimizing getProblem() {
		return this.problem;
	}

}
