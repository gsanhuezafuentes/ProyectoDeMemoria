package registrable.multiobjective;

import annotations.*;
import model.io.JsonSimpleReader;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.algorithm.multiobjective.spa2.SPEA2Builder;
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
import model.metaheuristic.problem.impl.VanzylOriginal;
import model.metaheuristic.solution.impl.IntegerSolution;
import registrable.MultiObjectiveRegistrable;
import registrable.utils.ExperimentUtils;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class PumpSchedulingSPA2Register implements MultiObjectiveRegistrable {

    private VanzylOriginal problem;
    private final CrossoverOperator<IntegerSolution> crossover;
    private final MutationOperator<IntegerSolution> mutation;
    private final File baseDirectory;
    private final File json;
    private final int independentRun;

    @NewProblem(displayName = "Pumping Scheduling", algorithmName = "SPA2", description = "Solve the PumpScheduling Problem.\n" +
            "Objective1: energy cost\n" +
            "Objective2: maintenance Cost")
    @Parameters(
            operators = {
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
            files = {@FileInput(displayName = "Base directory", type = FileInput.Type.Directory), @FileInput(displayName = "Configuration file (json) *")}, //
            numbers = {
            		@NumberInput(displayName = "Independent run", defaultValue = 10)
            }
    )
    public PumpSchedulingSPA2Register(Object crossover, Object mutation, File baseDirectory, File json, int independentRun) {
        this.crossover = (CrossoverOperator<IntegerSolution>) crossover;
        this.mutation = (MutationOperator<IntegerSolution>) mutation;
        this.baseDirectory = baseDirectory;
        this.json = Objects.requireNonNull(json, "The json configuration file was not indicated");
        this.independentRun = independentRun;
    }

    @Override
    public Experiment<?> build(String inpPath) throws Exception {
        String experimentBaseDirectory;
        if (baseDirectory == null)
            experimentBaseDirectory = "";
        else experimentBaseDirectory = baseDirectory.getAbsolutePath();

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

        this.problem = vanzylObj;

        // Ingreso de valores a través de archivo PSE (comentar en caso de ingresar
        // manualmente)
        // String psePath = "src/resources/Sotelo2001.pse";
        // PumpScheduling pumpScheduling = new PumpScheduling(psePath, inpPath);

        ExperimentProblem<IntegerSolution> problem = new ExperimentProblem<>(vanzylObj, "vanzylOriginal");

        List<ExperimentAlgorithm<IntegerSolution>> algorithmList = ExperimentUtils.configureAlgorithmList(problem, independentRun, () -> {
            Algorithm<IntegerSolution> algorithm = new SPEA2Builder<IntegerSolution>(
                    this.problem, this.crossover,
                    this.mutation)
                    .build();
            return algorithm;
        });

        ExperimentBuilder<IntegerSolution> builder = new ExperimentBuilder<IntegerSolution>("PSMOStudy")
                .setAlgorithmList(algorithmList)
                .setProblem(problem)
                .setIndependentRuns(independentRun);
        // if baseDirectory isn't null so add the output directory.
        if (baseDirectory != null) {
            builder.setExperimentBaseDirectory(experimentBaseDirectory)
                    .setObjectiveOutputFileName("FUN")
                    .setVariablesOutputFileName("VAR")
                    .setReferenceFrontDirectory(experimentBaseDirectory + "/PSMOStudy/referenceFronts");
        }
        Experiment<IntegerSolution> experiment = builder.build();

        return experiment;
    }
}
