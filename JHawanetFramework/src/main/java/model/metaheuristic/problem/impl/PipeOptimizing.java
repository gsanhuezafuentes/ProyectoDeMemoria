package model.metaheuristic.problem.impl;

import epanet.core.Components;
import epanet.core.EpanetAPI;
import epanet.core.EpanetException;
import epanet.core.LinkParameters;
import exception.ApplicationException;
import model.epanet.element.Gama;
import model.epanet.element.Network;
import model.epanet.element.networkcomponent.Link;
import model.epanet.element.networkcomponent.Pipe;
import model.epanet.element.networkcomponent.Valve;
import model.epanet.io.GamaParser;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.problem.evaluator.PipeOptimizingSolutionEvaluator;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.solution.impl.IntegerSolution;

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
	private int numberOfObjectives;
	private int numberOfConstrains;
	private int lowerBound;
	private int upperBound;
	private String networkGama;
	private final PipeOptimizingSolutionEvaluator evaluator;
	private EpanetAPI epanet;

	private List<Gama> gamas;
	private List<Float> LenghtLinks;

	private int minPressure;

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
	 * @throws ApplicationException if networkGama is empty
	 */
	public PipeOptimizing(EpanetAPI epanet, String networkGama, int minPressure) throws IOException, EpanetException {
		Objects.requireNonNull(epanet, "EpanetAPI can't be null in CostConstructionProblem");
		Objects.requireNonNull(networkGama);
		if (networkGama.equals("")) {
			throw new ApplicationException("The parameter networkGama can't be empty");
		}
		this.numberOfConstrains = 1;
		this.numberOfObjectives = 1;
		this.networkGama = networkGama;
		this.epanet = epanet;
		this.minPressure = minPressure;
		this.evaluator = new PipeOptimizingSolutionEvaluator(this.minPressure);
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
	public IntegerSolution createSolution() {
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
		for (int i = 1; i <= n_link; i++) {
			float[] value = epanet.ENgetlinkvalue(i, LinkParameters.EN_LENGTH);
			length.add(value[0]);
//			System.out.println(i + " " + value[0]);
		}

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
		Collection<Link> links = network.getLinks();
		int i = 0;
		for (Link link : links) {
			double diameter = this.gamas.get(iSolution.getVariable(i) - 1).getDiameter();
			/*
			 * Only pipe and valve has diameter. For this only in that elements the diameter
			 * is set. If link is a Pump so the diameter of this is ignored.
			 */
			if (link instanceof Pipe) {
				Pipe pipe = (Pipe) link;
				pipe.setDiameter(diameter);
			} else if (link instanceof Valve) {
				Valve valve = (Valve) link;
				valve.setDiameter(diameter);
			}
			i++;
		}
		return network;
	}

	@Override
	public String getName() {
		return "Pipe Optimizing";
	}
}
