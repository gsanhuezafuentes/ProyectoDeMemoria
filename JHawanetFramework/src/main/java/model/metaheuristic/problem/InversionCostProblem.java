package model.metaheuristic.problem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import epanet.core.Components;
import epanet.core.EpanetAPI;
import epanet.core.EpanetException;
import epanet.core.LinkParameters;
import exception.ApplicationException;
import model.epanet.element.Gama;
import model.epanet.io.GamaParser;
import model.metaheuristic.evaluator.MonoObjetiveSolutionEvaluator;
import model.metaheuristic.solution.IntegerSolution;
import model.metaheuristic.utils.random.BoundedRandomGenerator;
import model.metaheuristic.utils.random.JavaRandom;
/**
 * Class that implement the problem of inversion cost.
 * @author gsanh
 *
 */
public class InversionCostProblem implements Problem<IntegerSolution> {

	private int numberOfVariables;
	private int numberOfObjectives;
	private int numberOfConstrains;
	private int lowerBound;
	private int upperBound;
	private String networkGama;

	private EpanetAPI epanet;
	BoundedRandomGenerator<Integer> random;

	private List<Gama> gamas;
	private List<Float> LenghtLinks;

	private int minPressure;

	public InversionCostProblem(EpanetAPI epanet, String networkGama, int minPressure)
			throws IOException, EpanetException {
		Objects.requireNonNull(epanet, "EpanetAPI can't be null in CostConstructionProblem");
		Objects.requireNonNull(networkGama);
		if (networkGama.equals("")) {
			throw new ApplicationException("The parameter networkGama can't be empty");
		}
		this.numberOfConstrains = 1;
		this.numberOfObjectives = 1;
		this.networkGama = networkGama;
		this.epanet = epanet;
		this.random = (lowerBound, upperBound) -> JavaRandom.getInstance().nextInt(lowerBound, upperBound);
		this.minPressure = minPressure;
		initialize();
	}

	/**
	 * Initialize values needed to the problem.
	 * 
	 * @throws IOException
	 * @throws EpanetException
	 */
	private void initialize() throws IOException, EpanetException {
		this.numberOfVariables = epanet.ENgetcount(Components.EN_LINKCOUNT);
		GamaParser gamaParser = new GamaParser();
		this.gamas = gamaParser.parser(new File(networkGama));
		this.lowerBound = 1;
		this.upperBound = gamas.size();

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
		MonoObjetiveSolutionEvaluator evaluator = new MonoObjetiveSolutionEvaluator(this.minPressure);
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
		IntegerSolution solution = new IntegerSolution(this);
		for (int i = 0; i < getNumberOfVariables(); i++) {
			solution.setVariable(i, random.getRandomValue(this.lowerBound, this.upperBound + 1));
		}
		return solution;
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
	 * @param epanet
	 * @return
	 */
	private List<Float> getLengthLink(EpanetAPI epanet) {
		ArrayList<Float> length = new ArrayList<Float>();
		int n_link;
		try {
			n_link = epanet.ENgetcount(Components.EN_LINKCOUNT);
			for (int i = 1; i <= n_link; i++) {
				float[] value = epanet.ENgetlinkvalue(i, LinkParameters.EN_LENGTH);
				length.add(value[0]);
			}
		} catch (EpanetException e) {
			e.printStackTrace();
		}

		return length;
	}

	/**
	 * Override the default method close. It close epanet if is called.
	 */
	@Override
	public void close() throws Exception {
		epanet.ENclose();
	}
}
