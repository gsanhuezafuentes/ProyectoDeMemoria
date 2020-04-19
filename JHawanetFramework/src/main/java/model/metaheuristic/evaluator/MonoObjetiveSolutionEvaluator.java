package model.metaheuristic.evaluator;

import java.util.List;

import epanet.core.Components;
import epanet.core.EpanetAPI;
import epanet.core.EpanetException;
import epanet.core.LinkParameters;
import epanet.core.NodeParameters;
import epanet.core.NodeTypes;
import model.epanet.element.Gama;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.utils.solutionattribute.NumberOfViolatedConstraints;
import model.metaheuristic.utils.solutionattribute.OverallConstraintViolation;

public class MonoObjetiveSolutionEvaluator {
	private final double minPressure;

	public MonoObjetiveSolutionEvaluator(double minPressure) {
		this.minPressure = minPressure;
	}

	/**
	 * Evaluate the factibility of solution. It add a {@link OverallConstraintViolation} and  {@link NumberOfViolatedConstraints} to {@code solution}.
	 * @param solution the solution to evaluate
	 * @param gamas the gamas to map the solution
	 * @param epanet the simulator
	 * @throws EpanetException if there is and error in the simulator
	 */
	public void evaluate(IntegerSolution solution, List<Gama> gamas, EpanetAPI epanet)
			throws EpanetException {
		int numberOfInfactibilities = 0;
		double infactibilityGrade = 0;
		int nDecisionVariables = solution.getNumberOfVariables();

		// long t, tstep;
		long[] tstep = { 1 };
		long[] t = { 0 };

		epanet.ENopenH();
		epanet.ENinitH(0);
		// Set the diameter of network to the solutions
		for (int i = 0; i < nDecisionVariables; i++) {
			float diameter = (float) gamas.get(solution.getVariable(i) - 1).getDiameter();
			epanet.ENsetlinkvalue(i + 1, LinkParameters.EN_DIAMETER, diameter);
		}
		do {
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
	}

	private void RecorrerNudos(EpanetAPI epanet) throws EpanetException {
		int n_links = epanet.ENgetcount(Components.EN_LINKCOUNT);
		for (int j = 1; j <= n_links; j++) {
			String label = epanet.ENgetlinkid(j);
			float[] value = epanet.ENgetlinkvalue(j, LinkParameters.EN_DIAMETER);
			System.out.printf("Link id = %10s de indice %10d tiene un valor de diametro de %-11f\n", label, j, value[0]);
		}
	}

}
