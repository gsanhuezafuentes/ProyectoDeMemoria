package registrable.singleobjective;

import annotations.registrable.*;
import epanet.core.EpanetAPI;
import exception.ApplicationException;
import model.metaheuristic.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithm2;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentBuilder;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
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
import model.metaheuristic.utils.evaluator.impl.SequentialSolutionEvaluator;
import registrable.Registrable;
import registrable.SingleObjectiveRegistrable;
import registrable.utils.ExperimentUtils;

import java.io.File;
import java.util.List;

/**
 * Class that describe the problem to be showned in menu of problem in the
 * GUI.<br>
 * <br>
 * <p>
 * The sistem read this class using reflection to get the annotation and create
 * a GUI to configure the experiment and inject the value to injectable method.
 */
public final class PipeOptimizingRegister implements SingleObjectiveRegistrable {
    private final SelectionOperator<List<IntegerSolution>, List<IntegerSolution>> selection;
    private final CrossoverOperator<IntegerSolution> crossover;
    private final MutationOperator<IntegerSolution> mutation;
    private final int numberWithoutImprovement;
    private final int minPressure;
    private final int maxEvaluations;
    private final int independentRun;
    private final int populationSize;
    private final File gama;
    private PipeOptimizing problem;

    /**
     * This constructor define the elements that will be needed to build the
     * algorithm.
     *
     * @param selectionOperator        the selection operator
     * @param crossoverOperator        the crossover operator
     * @param mutationOperator         the mutation operator
     * @param gama                     the File object with the path to file configuration
     * @param minPressure              the min pressure of network
     * @param populationSize           the size of population
     * @param numberWithoutImprovement the number without improvement in the
     *                                 result
     * @param maxEvaluations           the max number of evaluation
     * @throws Exception A exception if there is some error in convert the
     *                   parameters.
     * @see Registrable
     */
    @NewProblem(displayName = "Pipe optimizing", algorithmName = "Genetic Algorithm")
    @Parameters(operators = {
            @OperatorInput(displayName = "Selection Operator", value = {
                    @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
            @OperatorInput(displayName = "Crossover Operator", value = {
                    @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                    @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
            @OperatorInput(displayName = "Mutation Operator", value = {
                    @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                    @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                    @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
            files = {@FileInput(displayName = "Gama")}, //
            numbers = {@NumberInput(displayName = "Independent run"), @NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
            numbersToggle = {
                    @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                    @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
    @SuppressWarnings("unchecked") // The object injected are indicated in operators elements. It guarantee its
    // types.
    public PipeOptimizingRegister(Object selectionOperator, Object crossoverOperator, Object mutationOperator, File gama, int independentRun,
                                  int minPressure, int populationSize, int numberWithoutImprovement, int maxEvaluations) throws Exception {
        System.out.println(selectionOperator);
        System.out.println(crossoverOperator);
        System.out.println(mutationOperator);
        System.out.println(gama);
        System.out.println(independentRun);
        System.out.println(minPressure);
        System.out.println(populationSize);
        System.out.println(numberWithoutImprovement);
        System.out.println(maxEvaluations);
        this.selection = (SelectionOperator<List<IntegerSolution>, List<IntegerSolution>>) selectionOperator; // unchecked cast
        this.crossover = (CrossoverOperator<IntegerSolution>) crossoverOperator; // unchecked cast
        this.mutation = (MutationOperator<IntegerSolution>) mutationOperator; // unchecked cast
        this.independentRun = independentRun;
        this.minPressure = minPressure;
        this.populationSize = populationSize;
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
    public Experiment<IntegerSolution> build(String inpPath) throws Exception {
        if (inpPath == null || inpPath.isEmpty()) {
            throw new ApplicationException("There isn't a network opened");
        }
        EpanetAPI epanet = new EpanetAPI();
        epanet.ENopen(inpPath, "ejecucion.rpt", "");

        if (this.gama == null) {
            throw new ApplicationException("There isn't gama file");
        }
        if (this.problem == null) {
            this.problem = new PipeOptimizing(epanet, this.gama.getAbsolutePath(), this.minPressure);

        }

        ExperimentProblem<IntegerSolution> experimentProblem = new ExperimentProblem<>(this.problem);

        List<ExperimentAlgorithm<IntegerSolution>> experimentAlgorithms = ExperimentUtils.configureAlgorithmList(experimentProblem, this.independentRun,
                () -> {
                    GeneticAlgorithm2<IntegerSolution> algorithm = new GeneticAlgorithm2<>(this.problem, populationSize, selection, crossover, mutation, new SequentialSolutionEvaluator<>());
                    if (this.numberWithoutImprovement != Integer.MIN_VALUE){
                        algorithm.setMaxNumberOfIterationWithoutImprovement(this.numberWithoutImprovement);
                    }
                    else{
                        algorithm.setMaxEvaluations(this.maxEvaluations);
                    }
                    return algorithm;
                });

        Experiment<IntegerSolution> experiment = new ExperimentBuilder<IntegerSolution>("PipeOptimizing")
                .setIndependentRuns(this.independentRun)
                .setAlgorithmList(experimentAlgorithms)
                .setProblem(experimentProblem)
                .build();

        return experiment;
    }
}
