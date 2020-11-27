package registrable.multiobjective;

import annotations.NumberInput;
import annotations.registrable.*;
import model.io.JsonSimpleReader;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.algorithm.multiobjective.nsga.NSGAII;
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
import model.metaheuristic.operator.selection.impl.TournamentSelection;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.problem.impl.VanzylOriginal;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.util.comparator.DominanceComparator;
import model.metaheuristic.util.evaluator.impl.SequentialSolutionEvaluator;
import registrable.MultiObjectiveRegistrable;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class PumpSchedulingNSGAIIRegister implements MultiObjectiveRegistrable {

    private final SelectionOperator<List<IntegerSolution>, IntegerSolution> selection;
    private final CrossoverOperator<IntegerSolution> crossover;
    private final MutationOperator<IntegerSolution> mutation;
    private final File json;
    private final int independentRun;
    private final int maxEvaluation;
    private final int populationSize;

    @NewProblem(displayName = "Pumping Scheduling", algorithmName = "NSGA-II", description = "Solve the PumpScheduling Problem.\n" +
            "Objective1: energy cost\n" +
            "Objective2: maintenance Cost")
    @Parameters(
            operators = {
                    @OperatorInput(displayName = "Selection Operator", value = {
                            @OperatorOption(displayName = "Tournament Selection", value = TournamentSelection.class)
                    }),
                    @OperatorInput(displayName = "Crossover Operator", value = {
                            @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                            @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)
                    }), //
                    @OperatorInput(displayName = "Mutation Operator", value = {
                            @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                            @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class),
                            @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class)
                    })
            }, //
            files = {@FileInput(displayName = "Configuration file (json) *")}, //
            numbers = {@NumberInput(displayName = "Independent run", defaultValue = 10)
                    , @NumberInput(displayName = "Max number of evaluation", defaultValue = 25000)
                    , @NumberInput(displayName = "Population Size", defaultValue = 100)
            }
    )
    public PumpSchedulingNSGAIIRegister(Object selection, Object crossover, Object mutation, File json, int independentRun, int maxEvaluation, int populationSize) {
        this.selection = (SelectionOperator<List<IntegerSolution>, IntegerSolution>) selection;
        this.crossover = (CrossoverOperator<IntegerSolution>) crossover;
        this.mutation = (MutationOperator<IntegerSolution>) mutation;
        this.json = Objects.requireNonNull(json, "The json configuration file was not indicated");
        this.independentRun = independentRun;
        this.maxEvaluation = maxEvaluation;
        this.populationSize = populationSize;
    }

    @Override
    public Experiment<?> build(String inpPath) throws Exception {

        /* *******************vanzylOriginal ***************************/

        String inpPathVanzyl = inpPath; // "src/resources/vanzylOriginal.inp";

        // Ingreso de valores manualmente (comentar en caso de usar archivo PSE)
        JsonSimpleReader config = JsonSimpleReader.read(json.getAbsolutePath());
        int numPumps = config.getInt("numPumps");//3;
        int totalOptimizationTime = config.getInt("totalOptimizationTime"); //86400;
        int intervalOptimizationTime = config.getInt("intervalOptimizationTime"); //3600;
        double[] energyCostPerTime = config.getDoubleArray("energyCostPerTime"); //{0.0244, 0.0244, 0.0244, 0.0244, 0.0244, 0.0244, 0.0244, 0.1194, 0.1194, 0.1194,
        //0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194,
        //0.1194};
        double maintenanceCost = config.getDouble("maintenanceCost"); //1;
        int minNodePressure = config.getInt("minNodePressure"); //15;
        int numConstraints = config.getInt("numConstraints"); //218;
        double[] maxFlowrateEachPump = config.getDoubleArray("maxFlowrateEachPump"); //{300, 300, 150};
        double[] minTank = config.getDoubleArray("minTank"); //{0, 0};
        double[] maxTank = config.getDoubleArray("maxTank"); //{10, 5};

        VanzylOriginal vanzylObj = new VanzylOriginal(numPumps, totalOptimizationTime, intervalOptimizationTime,
                energyCostPerTime, maintenanceCost, minNodePressure, numConstraints, minTank, maxTank,
                maxFlowrateEachPump, inpPathVanzyl);

        // Ingreso de valores a traves de archivo PSE (comentar en caso de ingresar
        // manualmente)
        // String psePath = "src/resources/Sotelo2001.pse";
        // PumpScheduling pumpScheduling = new PumpScheduling(psePath, inpPath);

        ExperimentProblem<IntegerSolution> experimentProblem = new ExperimentProblem<>(vanzylObj);

        List<ExperimentAlgorithm<IntegerSolution>> algorithmList = configureAlgorithmList(experimentProblem);

        ExperimentBuilder<IntegerSolution> builder = new ExperimentBuilder<IntegerSolution>("PSMOStudy")
                .setAlgorithmList(algorithmList)
                .setProblem(experimentProblem)
                .setIndependentRuns(independentRun);
        Experiment<IntegerSolution> experiment = builder.build();

        return experiment;
    }

    /**
     * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem}
     * which form part of a {@link ExperimentAlgorithm}, which is a decorator for
     * class {@link Algorithm}. The {@link ExperimentAlgorithm} has an optional tag
     * component, that can be set as it is shown in this example, where four
     * variants of a same algorithm are defined.
     */
    private List<ExperimentAlgorithm<IntegerSolution>> configureAlgorithmList(
            ExperimentProblem<IntegerSolution> experimentProblem) {
        List<ExperimentAlgorithm<IntegerSolution>> algorithms = new ArrayList<>();

        Problem<IntegerSolution> problem = experimentProblem.getProblem();
        for (int run = 0; run < independentRun; run++) {
            SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = this.selection;//new TournamentSelection<>(2);
            CrossoverOperator<IntegerSolution> crossover = this.crossover;//new IntegerSBXCrossover(0.9, 20);
            MutationOperator<IntegerSolution> mutation = this.mutation;//new IntegerPolynomialMutation(1.0 / problem.getNumberOfVariables(), 20);
            Comparator<IntegerSolution> comparator = new DominanceComparator<>();

            Algorithm<IntegerSolution> algorithm = new NSGAII<IntegerSolution>(problem, this.maxEvaluation, this.populationSize, this.populationSize, this.populationSize//(problem, 25000, 100, 100, 100
                    , selection, crossover, mutation
                    , comparator, new SequentialSolutionEvaluator<>());
            algorithms.add(new ExperimentAlgorithm<>(algorithm, experimentProblem, run));

        }
        return algorithms;
    }
}
