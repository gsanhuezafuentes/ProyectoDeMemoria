package registrable.singleobjective;

import annotations.*;
import epanet.core.EpanetAPI;
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
import model.metaheuristic.util.evaluator.impl.SequentialSolutionEvaluator;
import registrable.Registrable;
import registrable.SingleObjectiveRegistrable;
import registrable.utils.ExperimentUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that describe the problem to be showned in menu of problem in the
 * GUI.<br>
 * <br>
 * <p>
 * The sistem read this class using reflection to get the annotation and create
 * a GUI to configure the experiment and inject the value to injectable method.
 */
public final class PipeOptimizingGARegister implements SingleObjectiveRegistrable {
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
     * @param independentRun           the number of independent run.
     * @param minPressure              the min pressure of network
     * @param populationSize           the size of population
     * @param maxEvaluations           the max number of evaluation
     * @param numberWithoutImprovement the number without improvement in the
     *                                 result
     * @throws Exception A exception if there is some error in convert the
     *                   parameters.
     * @see Registrable
     */
    @NewProblem(displayName = "Pipe optimizing", algorithmName = "Genetic Algorithm", description = "The objective of this " +
            "problem is to optimize the cost of construction of the network by " +
            "varying the diameter of the pipe in order to ensure a minimum level of pressure.")
    @Parameters(operators = {
            @OperatorInput(displayName = "Selection Operator", value = {
                    @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)
            }),
            @OperatorInput(displayName = "Crossover Operator", value = {
                    @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class),
                    @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class)
            }), //
            @OperatorInput(displayName = "Mutation Operator", value = {
                    @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class),
                    @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                    @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class)
            })}, //
            files = {@FileInput(displayName = "Gama *")}, //
            numbers = {@NumberInput(displayName = "Independent run", defaultValue = 5),
                    @NumberInput(displayName = "Min pressure", defaultValue = 30),
                    @NumberInput(displayName = "Population Size", defaultValue = 100)}, //
            numbersToggle = {
                    @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation", defaultValue = 25000),
                    @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement", defaultValue = 100)
            })
    @SuppressWarnings("unchecked") // The object injected are indicated in operators elements. It guarantee its
    // types.
    public PipeOptimizingGARegister(Object selectionOperator, Object crossoverOperator, Object mutationOperator, File gama, int independentRun,
                                    int minPressure, int populationSize, int maxEvaluations, int numberWithoutImprovement) throws Exception {
        System.out.println("selectionOperator: " + selectionOperator);
        System.out.println("crossoverOperator: " + crossoverOperator);
        System.out.println("mutationOperator: " + mutationOperator);
        System.out.println("gama: " + gama);
        System.out.println("independentRun: " + independentRun);
        System.out.println("minPressure: " + minPressure);
        System.out.println("populationSize: " + populationSize);
        System.out.println("numberWithoutImprovement: " + numberWithoutImprovement);
        System.out.println("maxEvaluations: " + maxEvaluations);
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
     * @throws IllegalArgumentException if inpPath is empty or null or if gama file is
     *                              null
     */
    @Override
    public Experiment<IntegerSolution> build(String inpPath) throws Exception {
        if (inpPath == null || inpPath.isEmpty()) {
            throw new IllegalArgumentException("There isn't a network opened");
        }
        EpanetAPI epanet = new EpanetAPI();
        epanet.ENopen(inpPath, "ejecucion.rpt", "");

        if (this.gama == null) {
            throw new IllegalArgumentException("There isn't gama file");
        }

        this.problem = new PipeOptimizing(epanet, this.gama.getAbsolutePath(), this.minPressure);

        ExperimentProblem<IntegerSolution> experimentProblem = new ExperimentProblem<>(this.problem);

        List<ExperimentAlgorithm<IntegerSolution>> experimentAlgorithms = ExperimentUtils.configureAlgorithmList(experimentProblem, this.independentRun,
                () -> {
                    GeneticAlgorithm2<IntegerSolution> algorithm = new GeneticAlgorithm2<>(this.problem, populationSize, selection, crossover, mutation, new SequentialSolutionEvaluator<>());
                    if (this.numberWithoutImprovement != Integer.MIN_VALUE) {
                        algorithm.setMaxNumberOfIterationWithoutImprovement(this.numberWithoutImprovement);
                    } else {
                        algorithm.setMaxEvaluations(this.maxEvaluations);
                    }
                    return algorithm;
                });

        return new ExperimentBuilder<IntegerSolution>("PipeOptimizing")
                .setIndependentRuns(this.independentRun)
                .setAlgorithmList(experimentAlgorithms)
                .setProblem(experimentProblem)
                .build();
    }

    /**
     * Get the parameter configured for this problem.
     */
    @Override
    public Map<String, String> getParameters() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("Min Pressure", "" + this.minPressure);
        map.put("Population Size", "" + this.populationSize);
        // see if number without improvement was configure or not
        if (this.numberWithoutImprovement != Integer.MIN_VALUE) {
            map.put("Number without improvement", "" + this.numberWithoutImprovement);
        } else {
            map.put("Number of max evaluations", "" + this.maxEvaluations);
        }

        // for selection
        if (this.selection instanceof UniformSelection) {
            map.put("Selection", "UniformSelection");
            map.put("Uniform Selection Constant", "" + ((UniformSelection<IntegerSolution>) this.selection).getConstant());
        }

        // for crossover
        if (this.crossover instanceof IntegerSBXCrossover) {
            map.put("Crossover", "IntegerSBXCrossover");
            map.put("Crossover Probability", "" + ((IntegerSBXCrossover) this.crossover).getCrossoverProbability());
            map.put("Crossover Distribution Index", "" + ((IntegerSBXCrossover) this.crossover).getDistributionIndex());
        } else if (this.crossover instanceof IntegerSinglePointCrossover) {
            map.put("Crossover", "IntegerSinglePointCrossover");
            map.put("Crossover Probability", "" + ((IntegerSinglePointCrossover) this.crossover).getCrossoverProbability());
        }

        // for mutation
        if (this.mutation instanceof IntegerPolynomialMutation) {
            map.put("Mutation", "IntegerPolynomialMutation");
            map.put("Mutation Probability", "" + ((IntegerPolynomialMutation) this.mutation).getMutationProbability());
            map.put("Mutation Distribution Index", "" + ((IntegerPolynomialMutation) this.mutation).getDistributionIndex());

        } else if (this.mutation instanceof IntegerSimpleRandomMutation) {
            map.put("Mutation", "IntegerSimpleRandomMutation");
            map.put("Mutation Probability", "" + ((IntegerSimpleRandomMutation) this.mutation).getMutationProbability());

        } else if (this.mutation instanceof IntegerRangeRandomMutation) {
            map.put("Mutation", "IntegerRangeRandomMutation");
            map.put("Mutation Probability", "" + ((IntegerRangeRandomMutation) this.mutation).getMutationProbability());
            map.put("Mutation Range", "" + ((IntegerRangeRandomMutation) this.mutation).getRange());
        }
        return map;
    }
}
