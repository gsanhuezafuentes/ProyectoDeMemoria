package model.metaheuristic.problem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import epanet.core.Components;
import epanet.core.EpanetAPI;
import epanet.core.EpanetException;
import epanet.core.LinkParameters;
import model.epanet.element.Gama;
import model.epanet.parser.GamaParser;
import model.metaheuristic.evaluator.MonoObjetiveSolutionEvaluator;
import model.metaheuristic.solution.IntegerSolution;
import model.metaheuristic.utils.random.BoundedRandomGenerator;
import model.metaheuristic.utils.random.JavaRandom;

public class CostConstructionProblem implements Problem<IntegerSolution> {

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

	public CostConstructionProblem(EpanetAPI epanet, String networkGama, int minPressure) throws IOException, EpanetException {
		if (epanet == null) {
			throw new RuntimeException("EpanetAPI can't be null in CostConstructionProblem");
		}
		if (networkGama == null || networkGama.equals("")) {
			throw new RuntimeException("The parameter networkGama neither can't be null nor empty");
		}
		this.numberOfConstrains = 1;
		this.numberOfObjectives = 1;
		this.networkGama = networkGama;
		this.epanet = epanet;
		this.random = (lowerBound, upperBound) -> JavaRandom.getInstance().nextInt(lowerBound, upperBound);
		this.minPressure = minPressure;
		initialize();
	}

	private void initialize() throws IOException, EpanetException {
		this.numberOfVariables = epanet.ENgetcount(Components.EN_LINKCOUNT);
		GamaParser gamaParser = new GamaParser();
		this.gamas = gamaParser.parser(new File(networkGama));
		this.lowerBound = 1;
		this.upperBound = gamas.size();

		this.LenghtLinks = getLenghtLink(epanet);
	}

	@Override
	public int getNumberOfVariables() {
		return numberOfVariables;
	}

	@Override
	public int getNumberOfObjectives() {
		return numberOfObjectives;
	}

	@Override
	public int getNumberOfConstraints() {
		return numberOfConstrains;
	}

	@Override
	public void evaluate(IntegerSolution solution) { // Puede ser necesario agregar la excepcion de epanet
		MonoObjetiveSolutionEvaluator evaluator = new MonoObjetiveSolutionEvaluator(this.minPressure);
		double cost = 0;

		for (int i = 0; i < getNumberOfVariables(); i++) {
			int index = solution.getVariable(i);
			Gama gama = gamas.get(index-1);
			cost += this.LenghtLinks.get(i) * gama.getCost();
		}
		solution.setObjective(0, cost);

		try {
			evaluator.evaluate(solution, gamas, epanet);
		} catch (EpanetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public IntegerSolution createSolution() {
		IntegerSolution solution = new IntegerSolution(this);
		for (int i = 0; i < getNumberOfVariables(); i++) {
			solution.setVariable(i, random.getRandomValue(this.lowerBound, this.upperBound+1));
		}
		return solution;
	}

	@Override
	public double getLowerBound(int index) {
		return lowerBound;
	}

	@Override
	public double getUpperBound(int index) {
		return upperBound;
	}

	private List<Float> getLenghtLink(EpanetAPI epanet) {
		ArrayList<Float> length = new ArrayList<Float>();
		int n_link;
		try {
			n_link = epanet.ENgetcount(Components.EN_LINKCOUNT);
			System.out.println("Numero de links " + n_link);
			for (int i = 1; i <= n_link; i++) {
				float[] value = epanet.ENgetlinkvalue(i, LinkParameters.EN_LENGTH);
				// System.out.println("Link " + i + " : " + value[0]);
				length.add(value[0]);
			}
		} catch (EpanetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return length;
	}
}
