package registrable.multiobjective;

import annotations.registrable.NewProblem;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.algorithm.multiobjective.NSGAII;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentBuilder;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.operator.crossover.impl.IntegerSBXCrossover;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.mutation.impl.IntegerPolynomialMutation;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.impl.TournamentSelection;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.problem.impl.VanzylOriginal;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.utils.comparator.DominanceComparator;
import registrable.MultiObjectiveRegistrable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PumpSchedulingRegister implements MultiObjectiveRegistrable {

	private static final int INDEPENDENT_RUNS = 2;
	private VanzylOriginal problem;

	@NewProblem(displayName = "Pumping Scheduling", algorithmName = "NSGA-II")
	public PumpSchedulingRegister() {
	}

	@Override
	public Experiment<?> build(String inpPath) throws Exception {
		String experimentBaseDirectory = "C:\\Users\\gsanh\\Desktop\\Memoria\\NoGit\\Experiment";

		/* *******************vanzylOriginal ***************************/

		String inpPathVanzyl = inpPath; // "src/resources/vanzylOriginal.inp";

		// Ingreso de valores manualmente (comentar en caso de usar archivo PSE)

		int numPumps = 3;
		int totalOptimizationTime = 86400;
		int intervalOptimizationTime = 3600;
		double[] energyCostPerTime = { 0.0244, 0.0244, 0.0244, 0.0244, 0.0244, 0.0244, 0.0244, 0.1194, 0.1194, 0.1194,
				0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194, 0.1194,
				0.1194 };
		double maintenanceCost = 1;
		int minNodePressure = 15;
		int numConstraints = 218;
		double[] maxFlowrateEachPump = { 300, 300, 150 };
		double[] minTank = { 0, 0 };
		double[] maxTank = { 10, 5 };

		VanzylOriginal vanzylObj = new VanzylOriginal(numPumps, totalOptimizationTime, intervalOptimizationTime,
				energyCostPerTime, maintenanceCost, minNodePressure, numConstraints, minTank, maxTank,
				maxFlowrateEachPump, inpPathVanzyl);

		this.problem = vanzylObj;

		// Ingreso de valores a trav�s de archivo PSE (comentar en caso de ingresar
		// manualmente)
		// String psePath = "src/resources/Sotelo2001.pse";
		// PumpScheduling pumpScheduling = new PumpScheduling(psePath, inpPath);

		ExperimentProblem<IntegerSolution> experimentProblem = new ExperimentProblem<>(vanzylObj, "vanzylOriginal");

		List<ExperimentAlgorithm<IntegerSolution>> algorithmList = configureAlgorithmList(experimentProblem);

		Experiment<IntegerSolution> experiment = new ExperimentBuilder<IntegerSolution>("PSMOStudy")
				.setAlgorithmList(algorithmList).setProblem(experimentProblem)
				.setExperimentBaseDirectory(experimentBaseDirectory)
				.setObjectiveOutputFileName("FUN")
				.setVariablesOutputFileName("VAR")
				.setReferenceFrontDirectory(experimentBaseDirectory + "/PSMOStudy/referenceFronts")
				.setIndependentRuns(INDEPENDENT_RUNS).build();

		return experiment;
	}

	/**
	 * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem}
	 * which form part of a {@link ExperimentAlgorithm}, which is a decorator for
	 * class {@link Algorithm}. The {@link ExperimentAlgorithm} has an optional tag
	 * component, that can be set as it is shown in this example, where four
	 * variants of a same algorithm are defined.
	 */
	static List<ExperimentAlgorithm<IntegerSolution>> configureAlgorithmList(
			ExperimentProblem<IntegerSolution> experimentProblem) {
		List<ExperimentAlgorithm<IntegerSolution>> algorithms = new ArrayList<>();

		Problem<IntegerSolution> problem = experimentProblem.getProblem();
		for (int run = 0; run < INDEPENDENT_RUNS; run++) {
				SelectionOperator<List<IntegerSolution>, IntegerSolution> selection = new TournamentSelection<>(
						2);
				CrossoverOperator<IntegerSolution> crossover = new IntegerSBXCrossover(0.9, 20);
				MutationOperator<IntegerSolution> mutation = new IntegerPolynomialMutation(
						1.0 / problem.getNumberOfVariables(), 20);
				Comparator<IntegerSolution> comparator = new DominanceComparator<>();

				Algorithm<IntegerSolution> algorithm = new NSGAII<>(problem, 1000, 100, 100, 100, crossover, mutation,
						selection, comparator);
				algorithms.add(new ExperimentAlgorithm<>(algorithm, experimentProblem, run));

		}
		return algorithms;
	}

}
