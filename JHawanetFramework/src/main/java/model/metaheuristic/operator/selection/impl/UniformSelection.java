package model.metaheuristic.operator.selection.impl;

import annotations.DefaultConstructor;
import annotations.NumberInput;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.comparator.DominanceComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Operator class. 
 *
 * @param <S> Type of solution
 */
public class UniformSelection<S extends Solution<?>> implements SelectionOperator<List<S>, List<S>> {

	private final Comparator<S> comparator;
	private final double constant;

	/**
	 * Constructor
	 * @param constant the value constant. it has to be between the range [1.5, 2]
	 * @throws IllegalArgumentException if constant is not between the range [1.5, 2]
	 */
	@DefaultConstructor(@NumberInput(displayName = "constant", defaultValue = 1.5))
	public UniformSelection(double constant) {
		this(constant, new DominanceComparator<S>());

	}

	/**
	 * Constructor
	 * @param constant the value constant. it has to be between the range [1.5, 2]
	 * @param comparator the comparator to use.
	 * @throws IllegalArgumentException if constant is not between the range [1.5, 2]
	 */
	public UniformSelection(double constant, Comparator<S> comparator) {
		this.comparator = comparator;
		if (constant < 1.5 || constant > 2) {
			throw new IllegalArgumentException("constant out of range, the range value is between 1.5 and 2");
		}
		this.constant = constant;
	}

	/**
	 * Get the constant value used.
	 * @return the constant value.
	 */
	public double getConstant() {
		return constant;
	}

	/** Execute method */
	@Override
	public List<S> execute(List<S> solutionList) {
		int populationSize = solutionList.size();
		Collections.sort(solutionList, this.comparator);
		double pmin = minProbability(solutionList);
		double pmax = maxProbability(solutionList);
		List<S> selectedList = new ArrayList<S>();
		double probability;
		double pi;
		for (int i = 0; i < solutionList.size(); i++) {
			pi = probabilitySelectionOfIndex(i + 1, pmin, pmax, populationSize);
			probability = pi * populationSize;
			if (probability >= 1.5) {
				selectedList.add(solutionList.get(i));
				selectedList.add(solutionList.get(i));
			} else if (probability >= 0.5 && probability < 1.5 && selectedList.size() < populationSize) {
				selectedList.add(solutionList.get(i));
			}
		}
		assert selectedList.size() == populationSize : "the resulting population hasn't the same size that the received population";
		return selectedList;
	}

	/**
	 * Calculate the max probability value on solutionList
	 * 
	 * @param solutionList the solutionList
	 */
	private double maxProbability(List<S> solutionList) {
		int numberOfSolution = solutionList.size();
		//noinspection UnnecessaryLocalVariable
		double pmax = constant / numberOfSolution;
		return pmax;
	}

	/**
	 * Calculate the min probability value on solutionList
	 * 
	 * @param solutionList the solutionList
	 */
	private double minProbability(List<S> solutionList) {
		int numberOfSolution = solutionList.size();
		//noinspection UnnecessaryLocalVariable
		double pmin = (2 - constant) / numberOfSolution;
		return pmin;
	}

	/**
	 * Calculate the probability of i-th(index) elements.
	 * 
	 * @param index            the index of i-th element
	 * @param pmin             the min probability
	 * @param pmax             the max probability
	 * @param numberOfSolution the number of solutions
	 * @return The probability of the i-th element.
	 */
	private double probabilitySelectionOfIndex(int index, double pmin, double pmax, int numberOfSolution) {
		//noinspection UnnecessaryLocalVariable
		double pi = pmin + (pmax - pmin) * (numberOfSolution - index) / (numberOfSolution - 1);
		return pi;
	}
}
