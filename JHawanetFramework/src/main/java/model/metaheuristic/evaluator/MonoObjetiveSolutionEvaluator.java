package model.metaheuristic.evaluator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import epanet.core.Components;
import epanet.core.EpanetAPI;
import epanet.core.EpanetException;
import epanet.core.LinkParameters;
import epanet.core.NodeParameters;
import epanet.core.NodeTypes;
import model.epanet.element.Gama;
import model.epanet.parser.GamaParser;
import model.metaheuristic.problem.CostConstructionProblem;
import model.metaheuristic.solution.IntegerSolution;
import model.metaheuristic.utils.solutionattribute.NumberOfViolatedConstraints;
import model.metaheuristic.utils.solutionattribute.OverallConstraintViolation;

public class MonoObjetiveSolutionEvaluator {
	private double minPressure;

	public MonoObjetiveSolutionEvaluator(double minPressure) {
		this.minPressure = minPressure;
	}

	/**
	 * Evaluate the factibility of solution. It add a {@link OverallConstraintViolation} and  {@link NumberOfViolatedConstraints} to {@code solution}.
	 * @param solution the solution to evaluate
	 * @param gamas the gamas to map the solution
	 * @param epanet the simulator
	 * @return the solution
	 * @throws EpanetException if there is and error in the simulator
	 */
	public IntegerSolution evaluate(IntegerSolution solution, List<Gama> gamas, EpanetAPI epanet)
			throws EpanetException {
		int numberOfInfactibilities = 0;
		double infactibilityGrade = 0;
		int nDecisionVariables = solution.getNumberOfVariables();

		// long t, tstep;
		long[] tstep = { 1 };
		long[] t = { 0 };

		epanet.ENopenH();
		epanet.ENinitH(0);
		do {
			for (int i = 0; i < nDecisionVariables; i++) {
				float diameter = (float) gamas.get(solution.getVariable(i) - 1).getDiameter();
				epanet.ENsetlinkvalue(i + 1, LinkParameters.EN_DIAMETER, diameter);
			}
//			RecorrerNudos(epanet); // Borrar es solo para probar
//			System.out.println(solution);
			epanet.ENrunH(t);
			int n_nodes = epanet.ENgetcount(Components.EN_NODECOUNT);
			for (int j = 1; j <= n_nodes; j++) {
				// If the node is a reservoir or a tank so continue to next node
				if (epanet.ENgetnodetype(j) != NodeTypes.EN_JUNCTION) {
					continue;
				}
				float pressure = epanet.ENgetnodevalue(j, NodeParameters.EN_PRESSURE);
				if (pressure < this.minPressure) {
					numberOfInfactibilities++;
					infactibilityGrade += (this.minPressure - pressure);			
				}
//				System.out.println("Presion nodo " + j + " id " + epanet.ENgetnodeid(j) + " es " + pressure);
			}
			epanet.ENnextH(tstep);
//			System.out.println("Ya paso por aqui " + count++ + " veces");
		} while (tstep[0] > 0);
		OverallConstraintViolation<IntegerSolution> infactibilityConstrains = new OverallConstraintViolation<IntegerSolution>();
		infactibilityConstrains.setAttribute(solution, - infactibilityGrade);
		
		NumberOfViolatedConstraints<IntegerSolution> numberOfViolatedConstraints = new NumberOfViolatedConstraints<IntegerSolution>();
		numberOfViolatedConstraints.setAttribute(solution, numberOfInfactibilities);
		epanet.ENcloseH();
		return solution;
	}

	private void RecorrerNudos(EpanetAPI epanet) throws EpanetException {
		int n_links = epanet.ENgetcount(Components.EN_LINKCOUNT);
		for (int j = 1; j <= n_links; j++) {
			String label = epanet.ENgetlinkid(j);
			float[] value = epanet.ENgetlinkvalue(j, LinkParameters.EN_DIAMETER);
			System.out.printf("Link id = %10s de indice %10d tiene un valor de diametro de %-11f\n", label, j, value[0]);
		}
	}

	public static void main(String[] args) throws URISyntaxException, IOException {
		EpanetAPI epanet = new EpanetAPI();
//		epanet.ENopen("inp/hanoi-Frankenstein.INP", "inp/hanoi.rpt", "");

//		IntegerSolution solution = new IntegerSolution(new CostConstructionProblem(epanet, "inp/hanoiHW.Gama", 30));
		epanet.ENopen("inp/ny.inp", "inp/ny.rpt", "");
		IntegerSolution solution = new IntegerSolution(new CostConstructionProblem(epanet, "inp/NY-HW.Gama", 30));

//		for (int i = 0; i < 34; i++) {
//			solution.setVariable(i, 6);
//		}
		//*****
		solution.setVariable(0 ,16 );
		solution.setVariable(1 ,3 );
		solution.setVariable(2 ,14 );
		solution.setVariable(3 ,5);
		solution.setVariable(4 ,9);
		solution.setVariable(5 ,15);
		solution.setVariable(6 ,6);
		solution.setVariable(7 ,15);
		solution.setVariable(8 ,2);
		solution.setVariable(9 ,16);
		solution.setVariable(10 ,14);
		solution.setVariable(11 ,1);
		solution.setVariable(12 ,15);
		solution.setVariable(13 ,7);
		solution.setVariable(14 ,16);
		solution.setVariable(15 ,15);
		solution.setVariable(16 ,9);
		solution.setVariable(17 ,1);
		solution.setVariable(18 ,6);
		solution.setVariable(19 ,1);
		solution.setVariable(20 ,8);
		solution.setVariable(21 ,8);
		solution.setVariable(22 ,7);
		solution.setVariable(23 ,3);
		solution.setVariable(24 ,10);
		solution.setVariable(25 ,7);
		solution.setVariable(26 ,4);
		solution.setVariable(27 ,9);
		solution.setVariable(28 ,1);
		solution.setVariable(29 ,6);
		solution.setVariable(30 ,16);
		solution.setVariable(31 ,16);
		solution.setVariable(32 ,16);
		solution.setVariable(33 ,4 );
		solution.setVariable(34 ,16 );
		solution.setVariable(35 ,16);
		solution.setVariable(36 ,4 );
		solution.setVariable(37 ,1);
		solution.setVariable(38 ,3);
		solution.setVariable(39 ,2 );
		solution.setVariable(40 ,3 );
		solution.setVariable(41 ,5);
		//*****

		GamaParser gamaParser = new GamaParser();

		List<Float> lenght = getLenght(epanet);

		try {
			List<Gama> gamas = gamaParser.parser(new File("inp/NY-HW.Gama"));
			double cost = 0;

			for (int i = 0; i < 42; i++) {
				int index = solution.getVariable(i);
				Gama gama = gamas.get(index - 1);
				cost += lenght.get(i) * gama.getCost();
			}
			solution.setObjective(0, cost);

			MonoObjetiveSolutionEvaluator evaluator;
			evaluator = new MonoObjetiveSolutionEvaluator(30);
			IntegerSolution integerSolution = evaluator.evaluate(solution, gamas, epanet);

			System.out.println(integerSolution);
		} catch (EpanetException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static List<Float> getLenght(EpanetAPI epanet) {
		ArrayList<Float> length = new ArrayList<Float>();
		int n_link;
		try {
			n_link = epanet.ENgetcount(Components.EN_LINKCOUNT);
//			System.out.println("Numero de links " + n_link);
			for (int i = 1; i <= n_link; i++) {
				float[] value = epanet.ENgetlinkvalue(i, LinkParameters.EN_LENGTH);
//				System.out.println("Link " + i + " : " + value[0]);
				length.add(value[0]);
			}
		} catch (EpanetException e) {
			e.printStackTrace();
		}

		return length;
	}
}
