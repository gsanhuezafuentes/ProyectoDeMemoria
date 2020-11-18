package model.metaheuristic.problem.impl;

import epanet.core.*;
import model.epanet.element.Gama;
import model.epanet.element.Network;
import model.epanet.element.networkcomponent.Pipe;
import model.io.GamaParser;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.problem.evaluator.PipeOptimizingSolutionEvaluator;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.solution.impl.IntegerSolution;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Class that implement the problem of inversion cost.
 *
 */
public class PipeOptimizing implements Problem<IntegerSolution> {

	private int numberOfVariables;
	private final int numberOfObjectives;
	private final int numberOfConstrains;
	private int lowerBound;
	private int upperBound;
	private final String networkGama;
	private final PipeOptimizingSolutionEvaluator evaluator;
	private final EpanetAPI epanet;

	private List<Gama> gamas;
	private List<Float> LenghtLinks;

	/**
	 * Constructor
	 * 
	 * @param epanet the hydraulic simulation api
	 * @param networkGama the name of file with additional data of the network
	 * @param minPressure the min pressure of network
	 * @throws IOException          if there is a error in the io operator over the
	 *                              gama.
	 * @throws EpanetException      if there is a error in epatoolkit
	 * @throws NullPointerException if epanet is null or networkGama is null
	 * @throws IllegalArgumentException if networkGama is empty
	 */
	public PipeOptimizing(EpanetAPI epanet, String networkGama, int minPressure) throws IOException, EpanetException {
		Objects.requireNonNull(epanet, "EpanetAPI can't be null in CostConstructionProblem");
		Objects.requireNonNull(networkGama);
		if (networkGama.equals("")) {
			throw new IllegalArgumentException("The parameter networkGama can't be empty");
		}
		this.numberOfConstrains = 1;
		this.numberOfObjectives = 1;
		this.networkGama = networkGama;
		this.epanet = epanet;
		this.evaluator = new PipeOptimizingSolutionEvaluator(minPressure);
		initialize();
	}

	/**
	 * Initialize values needed to the problem.
	 * 
	 * @throws IOException     if there is a error in the io operator over the gama.
	 * @throws EpanetException if there is a error in epatoolkit.
	 */
	private void initialize() throws IOException, EpanetException {
		this.numberOfVariables = epanet.ENgetcount(Components.EN_LINKCOUNT);
		GamaParser gamaParser = new GamaParser();
		this.gamas = gamaParser.parser(new File(networkGama));
		this.lowerBound = 1;
		this.upperBound = gamas.size();

//		System.out.println("t " + this.numberOfVariables);

		this.LenghtLinks = getLengthLink(epanet);
	}

	/** {@inheritDoc} */
	@Override
	public int getNumberOfVariables() {
		return numberOfVariables;
	}

	/** {@inheritDoc} */
	@Override
	public int getNumberOfObjectives() {
		return numberOfObjectives;
	}

	/** {@inheritDoc} */
	@Override
	public int getNumberOfConstraints() {
		return numberOfConstrains;
	}

	/** {@inheritDoc} */
	@Override
	public void evaluate(IntegerSolution solution) throws EpanetException { // Puede ser necesario agregar la excepcion
																			// de epanet
		double cost = 0;

		for (int i = 0; i < getNumberOfVariables(); i++) {
			int index = solution.getVariable(i);
			Gama gama = gamas.get(index - 1);
			cost += this.LenghtLinks.get(i) * gama.getCost();
		}
		solution.setObjective(0, cost);
		evaluator.evaluate(solution, gamas, epanet);
	}

	/** {@inheritDoc} */
	@Override
	public @NotNull IntegerSolution createSolution() {
		return new IntegerSolution(this);
	}

	/** {@inheritDoc} */
	@Override
	public double getLowerBound(int index) {
		return lowerBound;
	}

	/** {@inheritDoc} */
	@Override
	public double getUpperBound(int index) {
		return upperBound;
	}

	/**
	 * Get the length of the link. Use the epanet toolkit.
	 * 
	 * @param epanet the hydraulic simulator api
	 * @return a list with the lenght of links
	 * @throws EpanetException if there is an error with EpaToolkit function.
	 */
	private List<Float> getLengthLink(EpanetAPI epanet) throws EpanetException {
		ArrayList<Float> length = new ArrayList<Float>();
		int n_link;
		n_link = epanet.ENgetcount(Components.EN_LINKCOUNT);
//		System.out.println("Link count " + n_link);
		for (int i = 1; i <= n_link; i++) {
			LinkTypes type = epanet.ENgetlinktype(i);
			if (type == LinkTypes.EN_PIPE){
				float[] value = epanet.ENgetlinkvalue(i, LinkParameters.EN_LENGTH);
				length.add(value[0]);
			}
//			System.out.println(i + " " + value[0]);
		}
//		System.out.println("Link pipes " + length.size());
		return length;
	}

	/**
	 * Override the default method close. It close epanet if is called.
	 */
	@Override
	public void closeResources() throws Exception {
		epanet.ENclose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Network applySolutionToNetwork(Network network, Solution<?> solution) {
		IntegerSolution iSolution = (IntegerSolution) solution;
		Collection<Pipe> pipes = network.getPipes();
		int i = 0;
		for (Pipe pipe : pipes) {
			double diameter = this.gamas.get(iSolution.getVariable(i) - 1).getDiameter();
			pipe.setDiameter(diameter);
			i++;
		}
		return network;
	}

	@Override
	public @NotNull String getName() {
		return "Pipe Optimizing";
	}
}
