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
import model.epanet.element.Gama;
import model.epanet.parser.GamaParser;
import model.metaheuristic.problem.CostConstructionProblem;
import model.metaheuristic.solution.IntegerSolution;

public class MonoObjetiveSolutionEvaluator {
	private EpanetAPI epanet;
	private File inpfile;
	private double minPressure;

	public MonoObjetiveSolutionEvaluator(double minPressure){
		this.minPressure = minPressure;
	}

	public IntegerSolution evaluate(IntegerSolution solution, List<Gama> gamas, EpanetAPI epanet)
			throws EpanetException {
		int n_infactibility = 0;
		int infactibility_grade = 0;
		int nDecisionVariables = solution.getNumberOfVariables();

		// long t, tstep;
		long[] tstep = { 1 };
		long[] t = { 0 };

		epanet.ENopenH();
		epanet.ENinitH(0);
		int count = 1;
		do {
			for (int i = 0; i < nDecisionVariables; i++) {
				float diameter = (float) gamas.get(solution.getVariable(i)).getDiameter();
				epanet.ENsetlinkvalue(i + 1, LinkParameters.EN_DIAMETER, diameter);
			}
			//RecorrerNudos(epanet); // Borrar es solo para probar
			epanet.ENrunH(t);
			int n_nodes = epanet.ENgetcount(Components.EN_NODECOUNT);
			for (int j = 1; j <= n_nodes; j++) {
				float pressure = epanet.ENgetnodevalue(j, NodeParameters.EN_PRESSURE);
				if (pressure < this.minPressure) {
					n_infactibility++;
					infactibility_grade += (this.minPressure - pressure);
					// solution.setObjetiveValue(0, Integer.MAX_VALUE); // Si hay una presion bajo
					// el minimo penalizar el costo.
				}
				//System.out.println("Presion nodo " + j + " es " + pressure);
			}
			epanet.ENnextH(tstep);
//			System.out.println("Ya paso por aqui " + count++ + " veces");
		} while (tstep[0] > 0);
		solution.setAttribute("infactibility", new Infactibility(n_infactibility, infactibility_grade));
		epanet.ENcloseH();
		return solution;
	}

	public void RecorrerNudos(EpanetAPI epanet) throws EpanetException {
		int n_links = epanet.ENgetcount(Components.EN_LINKCOUNT);
		for (int j = 1; j <= n_links; j++) {
			String label = epanet.ENgetlinkid(j);
			float[] value = epanet.ENgetlinkvalue(j, LinkParameters.EN_DIAMETER);
			System.out.printf("Link id = %s de indice %d tiene un valor de diametro de %f\n", label, j, value[0]);
		}
	}

	public static void main(String[] args) throws URISyntaxException, IOException {
		EpanetAPI epanet = new EpanetAPI();
		epanet.ENopen("inp/hanoi-Frankenstein.INP", "inp/hanoi.rpt", "");

		IntegerSolution solution = new IntegerSolution(new CostConstructionProblem(epanet, "inp/hanoiHW.Gama"));

		for (int i = 0; i < 34; i++) {
			solution.getVariables().add(i, 5);
		}

		GamaParser gamaParser = new GamaParser();

		List<Float> lenght = getLenght(epanet);

		try {
			List<Gama> gamas = gamaParser.parser(new File("inp/hanoiHW.Gama"));
			double cost = 0;

			for (int i = 0; i < 32; i++) {
				int index = solution.getVariable(i);
				Gama gama = gamas.get(index);
				cost += lenght.get(i) * gama.getCost();
			}
			solution.setObjective(0, cost);

			MonoObjetiveSolutionEvaluator evaluator;
			evaluator = new MonoObjetiveSolutionEvaluator(30);
			IntegerSolution integerSolution = evaluator.evaluate(solution, gamas, epanet);

			System.out.printf("Cost %f \n", integerSolution.getObjective(0));
			System.out.println((Infactibility) integerSolution.getAttribute("infactibility"));
		} catch (EpanetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static List<Float> getLenght(EpanetAPI epanet) {
		ArrayList<Float> length = new ArrayList<Float>();
		int n_link;
		try {
			n_link = epanet.ENgetcount(Components.EN_LINKCOUNT);
			System.out.println("Numero de links " + n_link);
			for (int i = 1; i <= n_link; i++) {
				float[] value = epanet.ENgetlinkvalue(i, LinkParameters.EN_LENGTH);
				System.out.println("Link " + i + " : " + value[0]);
				length.add(value[0]);
			}
		} catch (EpanetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return length;
	}
}
