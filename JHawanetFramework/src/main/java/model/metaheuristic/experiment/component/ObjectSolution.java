package model.metaheuristic.experiment.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.metaheuristic.solution.Solution;

/**
 * Solution used to wrap any type of decision variable.
 * 
 * <br>
 * <br>
 * <strong>Notes:</strong> <br>
 * This class was created to load solution from files and assume that
 * {@link #getVariable(int)}.toString() return the correct string to represent
 * the value.
 * 
 *
 */
public class ObjectSolution implements Solution<Object> {
	private int numberOfObjectives;
	private List<Object> decisionVariables;
	private double[] objectives;
	protected Map<Object, Object> attributes;
	private int numberOfVariables;

	/**
	 * Constructor
	 *
	 * @param numberOfObjectives
	 */
	public ObjectSolution(int numberOfObjectives, int numberOfVariables) {
		this.numberOfObjectives = numberOfObjectives;
		this.numberOfVariables = numberOfVariables;

		objectives = new double[numberOfObjectives];
		this.decisionVariables = new ArrayList<Object>();
		attributes = new HashMap<>();

		for (int i = 0; i < numberOfVariables; i++) {
			this.decisionVariables.add(i, null);
		}
	}

	/**
	 * Constructor
	 *
	 * @param solution
	 */
	public ObjectSolution(Solution<?> solution) {
		this.numberOfObjectives = solution.getNumberOfObjectives();
		this.numberOfVariables = solution.getNumberOfVariables();
		this.decisionVariables = new ArrayList<Object>();

		objectives = new double[numberOfObjectives];
		for (int i = 0; i < numberOfObjectives; i++) {
			this.objectives[i] = solution.getObjective(i);
		}

		for (int i = 0; i < numberOfVariables; i++) {
			this.decisionVariables.add(i, solution.getVariable(i));
		}
	}

	@Override
	public void setObjective(int index, double value) {
		objectives[index] = value;
	}

	@Override
	public double getObjective(int index) {
		return objectives[index];
	}

	@Override
	public double[] getObjectives() {
		return objectives;
	}

	@Override
	public List<Object> getVariables() {
		return this.decisionVariables;
	}

	@Override
	public Object getVariable(int index) {
		return this.decisionVariables.get(index);
	}

	@Override
	public void setVariable(int index, Object value) {
		this.decisionVariables.set(index, value);
	}

	@Override
	public int getNumberOfVariables() {
		return this.numberOfVariables;
	}

	@Override
	public int getNumberOfObjectives() {
		return numberOfObjectives;
	}

	@Override
	public ObjectSolution copy() {
		return new ObjectSolution(this);
	}

	@Override
	public void setAttribute(Object id, Object value) {
		attributes.put(id, value);
	}

	@Override
	public Object getAttribute(Object id) {
		return attributes.get(id);
	}

	/**
	 * This method compare only decision variables and objectives
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ObjectSolution that = (ObjectSolution) o;

		if (numberOfObjectives != that.numberOfObjectives)
			return false;
		if (numberOfVariables != that.numberOfVariables)
			return false;
		if (!Arrays.equals(objectives, that.objectives))
			return false;
		if (!decisionVariables.equals(that.getVariables())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(objectives);
		result = 31 * result + decisionVariables.hashCode();
		result = 31 * result + attributes.hashCode();
		return result;
	}

	@Override
	public String toString() {
		String result = "Variables: ";
		for (Object var : decisionVariables) {
			result += "" + var + " ";
		}
		result += "Objectives: ";
		for (Double obj : objectives) {
			result += "" + obj + " ";
		}
		result += "\t";
		result += "AlgorithmAttributes: " + attributes + "\n";

		return result;
	}

	@Override
	public Map<Object, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getVariableAsString(int index) {
		return getVariable(index).toString();
	}
}
