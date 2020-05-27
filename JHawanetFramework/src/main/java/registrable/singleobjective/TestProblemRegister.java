package registrable.singleobjective;

import annotations.registrable.NewProblem;
import epanet.core.EpanetAPI;
import exception.ApplicationException;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.algorithm.singleobjective.GeneticAlgorithm2;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentBuilder;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.operator.crossover.impl.IntegerSinglePointCrossover;
import model.metaheuristic.operator.mutation.impl.IntegerSimpleRandomMutation;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.operator.selection.impl.UniformSelection;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.problem.impl.PipeOptimizing;
import model.metaheuristic.solution.impl.IntegerSolution;
import registrable.SingleObjectiveRegistrable;
import registrable.utils.ExperimentUtils;

import java.util.ArrayList;
import java.util.List;

public final class TestProblemRegister implements SingleObjectiveRegistrable {
	private PipeOptimizing problem;

	@NewProblem(displayName = "Pipe Optimizing Hanoi", algorithmName = "Genetic Algorithm")
	public TestProblemRegister() {
	}
	
	/** {@inheritDoc}
	 * 	@throws ApplicationException if inpPath is empty or null or if gama file is null
	 */
	@Override
	public Experiment<IntegerSolution> build(String inpPath) throws Exception {
		if (inpPath == null || inpPath.isEmpty()) {
			throw new ApplicationException("There isn't a network opened");
		}
		EpanetAPI epanet;
		epanet = new EpanetAPI();
		epanet.ENopen(inpPath, "ejecucion.rpt", "");

		if (this.problem == null) {
			problem = new PipeOptimizing(epanet, "inp/hanoiHW.Gama", 30);
		}

		ExperimentProblem<IntegerSolution> experimentProblem = new ExperimentProblem<>(this.problem);

		int independentRun = 10;
		List<ExperimentAlgorithm<IntegerSolution>> experimentAlgorithms = configureAlgorithmList(experimentProblem,independentRun);

		Experiment<IntegerSolution> experiment = new ExperimentBuilder<IntegerSolution>("PipeOptimizing")
				.setIndependentRuns(independentRun)
				.setAlgorithmList(experimentAlgorithms)
				.setProblem(experimentProblem)
				.build();

		return experiment;
	}

	/**
	 * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem}
	 * which form part of a {@link ExperimentAlgorithm}, which is a decorator for
	 * class {@link Algorithm}. The {@link ExperimentAlgorithm} has an optional tag
	 * component, that can be set as it is shown in this example, where four
	 * variants of a same algorithm are defined.
	 */
	private List<ExperimentAlgorithm<IntegerSolution>> configureAlgorithmList(ExperimentProblem<IntegerSolution> experimentProblem, int independentRun) {
		List<ExperimentAlgorithm<IntegerSolution>> algorithms = new ArrayList<>(independentRun);

		SelectionOperator<List<IntegerSolution>, List<IntegerSolution>> selection = new UniformSelection<IntegerSolution>(1.6);
		IntegerSinglePointCrossover crossover = new IntegerSinglePointCrossover(0.1); //0.1
		IntegerSimpleRandomMutation mutation = new IntegerSimpleRandomMutation(0.03); //0.03

        /*
         The problem should not have state. If it has state so create a clone for each algorithm of the problem.
         The same apply to the operators.
         */
		Problem<IntegerSolution> problem = experimentProblem.getProblem();

		for (int run = 0; run < independentRun; run++) {

			GeneticAlgorithm2<IntegerSolution> algorithm = new GeneticAlgorithm2<>(problem, 10, selection, crossover, mutation);
			algorithm.setMaxEvaluations(10000);

			algorithms.add(new ExperimentAlgorithm<IntegerSolution>(algorithm, experimentProblem, run));
		}
		return algorithms;
	}
}
