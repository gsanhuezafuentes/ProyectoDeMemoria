package registrable.singleobjective;

import annotations.*;
import epanet.core.EpanetAPI;
import model.metaheuristic.algorithm.singleobjective.differentialevolution.IntegerDifferentialEvolution;
import model.metaheuristic.algorithm.singleobjective.differentialevolution.IntegerDifferentialEvolutionBuilder;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentBuilder;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.operator.crossover.impl.IntegerDifferentialEvolutionCrossover;
import model.metaheuristic.operator.mutation.impl.IntegerPolynomialMutation;
import model.metaheuristic.operator.mutation.impl.IntegerRangeRandomMutation;
import model.metaheuristic.operator.mutation.impl.IntegerSimpleRandomMutation;
import model.metaheuristic.operator.selection.impl.IntegerDifferentialEvolutionSelection;
import model.metaheuristic.problem.impl.PipeOptimizing;
import model.metaheuristic.solution.impl.IntegerSolution;
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
public final class PipeOptimizingDERegister implements SingleObjectiveRegistrable {
    private final IntegerDifferentialEvolutionSelection selection;
    private final IntegerDifferentialEvolutionCrossover crossover;
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
     * @param selectionOperator the selection operator
     * @param crossoverOperator the crossover operator
     * @param gama              the File object with the path to file configuration
     * @param independentRun    the number of independent run.
     * @param minPressure       the min pressure of network
     * @param populationSize    the size of population
     * @param maxEvaluations    the max number of evaluation
     * @throws Exception A exception if there is some error in convert the
     *                   parameters.
     * @see Registrable
     */
    @NewProblem(displayName = "Pipe optimizing", algorithmName = "Differential Evolution", description = "The objective of this " +
            "problem is to optimize the cost of construction of the network by " +
            "varying the diameter of the pipe in order to ensure a minimum level of pressure.")
    @Parameters(operators = {
            @OperatorInput(displayName = "Selection Operator", value = {
                    @OperatorOption(displayName = "Differential Evolution Selection", value = IntegerDifferentialEvolutionSelection.class)
            }),
            @OperatorInput(displayName = "Crossover Operator", value = {
                    @OperatorOption(displayName = "Differential Evolution Crossover", value = IntegerDifferentialEvolutionCrossover.class),
            })},
            files = {@FileInput(displayName = "Gama *")}, //
            numbers = {@NumberInput(displayName = "Independent run", defaultValue = 5),
                    @NumberInput(displayName = "Min pressure", defaultValue = 30),
                    @NumberInput(displayName = "Population Size", defaultValue = 100),
                    @NumberInput(displayName = "Max number of evaluation", defaultValue = 25000)

            })
    @SuppressWarnings("unchecked") // The object injected are indicated in operators elements. It guarantee its
    // types.
    public PipeOptimizingDERegister(Object selectionOperator, Object crossoverOperator, File gama, int independentRun,
                                    int minPressure, int populationSize, int maxEvaluations) throws Exception {

        this.selection = (IntegerDifferentialEvolutionSelection) selectionOperator; // unchecked cast
        this.crossover = (IntegerDifferentialEvolutionCrossover) crossoverOperator; // unchecked cast
        this.independentRun = independentRun;
        this.minPressure = minPressure;
        this.populationSize = populationSize;
        this.maxEvaluations = maxEvaluations;
        this.gama = gama;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if inpPath is empty or null or if gama file is
     *                                  null
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
                    IntegerDifferentialEvolution algorithm = new IntegerDifferentialEvolutionBuilder(this.problem)
                            .setCrossover(crossover)
                            .setSelection(selection)
                            .setMaxEvaluations(this.maxEvaluations)
                            .setPopulationSize(this.populationSize).build();
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
        map.put("Number of max evaluations", "" + this.maxEvaluations);

        // for selection
        map.put("Selection", "Differential Evolution Selection");

        map.put("Crossover", "Differential Evolution Crossover");

        return map;
    }
}
