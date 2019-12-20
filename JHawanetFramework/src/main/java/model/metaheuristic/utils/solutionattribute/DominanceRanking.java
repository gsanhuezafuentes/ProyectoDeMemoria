package model.metaheuristic.utils.solutionattribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import exception.ApplicationException;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.comparator.DominanceComparator;
import model.metaheuristic.utils.comparator.OverallConstraintViolationComparator;

/**
 * This class implements some facilities for ranking set of solutions. Given a
 * collection of solutions, they are ranked according to scheme proposed in
 * NSGA-II; as an output, a set of subsets are obtained. The subsets are
 * numbered starting from 0 (in NSGA-II, the numbering starts from 1); thus,
 * subset 0 contains the non-dominated solutions, subset 1 contains the
 * non-dominated solutions after removing those belonging to subset 0, and so
 * on.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo <br>
 *         <br>
 * 
 *         <pre>
 * Extract of code from https://github.com/jMetal/jMetal
 * 
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. © 2019 GitHub, Inc.
 *         </pre>
 */
public class DominanceRanking<S extends Solution<?>> extends SolutionAttribute<S, Integer> {
	private Comparator<S> dominanceComparator;
	private static final Comparator<Solution<?>> CONSTRAINT_VIOLATION_COMPARATOR = new OverallConstraintViolationComparator<Solution<?>>();

	private List<ArrayList<S>> rankedSubPopulations;

	/**
	 * Constructor
	 */
	public DominanceRanking(Comparator<S> comparator) {
		this.dominanceComparator = comparator;
		rankedSubPopulations = new ArrayList<>();
	}

	/**
	 * Constructor
	 */
	public DominanceRanking() {
		this(new DominanceComparator<>());
	}

	public DominanceRanking(Object id) {
		super(id);
		rankedSubPopulations = new ArrayList<>();
	}

	/**
	 * Compute the ranking of solution set and save to be access used
	 * {@link #getSubfront(int)} subsequently.
	 * 
	 * @param solutionSet
	 * @return Return this object that contains the solution set splited by ranking.
	 */
	public DominanceRanking<S> computeRanking(List<S> solutionSet) {
		List<S> population = solutionSet;

		// dominateMe[i] contains the number of solutions dominating i
		int[] dominateMe = new int[population.size()];

		// iDominate[k] contains the list of solutions dominated by k
		List<List<Integer>> iDominate = new ArrayList<>(population.size());

		// front[i] contains the list of individuals belonging to the front i
		ArrayList<List<Integer>> front = new ArrayList<>(population.size() + 1);

		// Initialize the fronts
		for (int i = 0; i < population.size() + 1; i++) {
			front.add(new LinkedList<Integer>());
		}

		// Fast non dominated sorting algorithm
		// Contribution of Guillaume Jacquenot
		for (int p = 0; p < population.size(); p++) {
			// Initialize the list of individuals that i dominate and the number
			// of individuals that dominate me
			iDominate.add(new LinkedList<Integer>());
			dominateMe[p] = 0;
		}

		int flagDominate;
		for (int p = 0; p < (population.size() - 1); p++) {
			// For all q individuals , calculate if p dominates q or vice versa
			for (int q = p + 1; q < population.size(); q++) {
				flagDominate = CONSTRAINT_VIOLATION_COMPARATOR.compare(solutionSet.get(p), solutionSet.get(q));
				if (flagDominate == 0) {
					flagDominate = dominanceComparator.compare(solutionSet.get(p), solutionSet.get(q));
				}
				if (flagDominate == -1) {
					iDominate.get(p).add(q);
					dominateMe[q]++;
				} else if (flagDominate == 1) {
					iDominate.get(q).add(p);
					dominateMe[p]++;
				}
			}
		}

		for (int i = 0; i < population.size(); i++) {
			if (dominateMe[i] == 0) {
				front.get(0).add(i);
				solutionSet.get(i).setAttribute(getAttributeIdentifier(), 0);
			}
		}

		// Obtain the rest of fronts
		int i = 0;
		Iterator<Integer> it1, it2; // Iterators
		while (front.get(i).size() != 0) {
			i++;
			it1 = front.get(i - 1).iterator();
			while (it1.hasNext()) {
				it2 = iDominate.get(it1.next()).iterator();
				while (it2.hasNext()) {
					int index = it2.next();
					dominateMe[index]--;
					if (dominateMe[index] == 0) {
						front.get(i).add(index);
						// RankingAndCrowdingAttr.getAttributes(solutionSet.get(index)).setRank(i);
						solutionSet.get(index).setAttribute(getAttributeIdentifier(), i);
					}
				}
			}
		}

		rankedSubPopulations = new ArrayList<>();
		// 0,1,2,....,i-1 are fronts, then i fronts
		for (int j = 0; j < i; j++) {
			rankedSubPopulations.add(j, new ArrayList<S>(front.get(j).size()));
			it1 = front.get(j).iterator();
			while (it1.hasNext()) {
				rankedSubPopulations.get(j).add(solutionSet.get(it1.next()));
			}
		}

		return this;
	}

	/**
	 * Get the solutions contains in the front indicated by rank. <br>
	 * <br>
	 * 
	 * To calculate the front is needed had called a {@link #computeRanking(List)}
	 * before.
	 * 
	 * @param rank - index front
	 * @return the front
	 * @throws ApplicationException if the rank is more than  the number of subPopulation.
	 */
	public List<S> getSubfront(int rank) {
		if (rank >= rankedSubPopulations.size()) {
			throw new ApplicationException(
					"Invalid rank: " + rank + ". Max rank = " + (rankedSubPopulations.size() - 1));
		}
		return rankedSubPopulations.get(rank);
	}

	/**
	 * Get the number of subsfronts.
	 * 
	 * @return the number of subfronts.
	 */
	public int getNumberOfSubfronts() {
		return rankedSubPopulations.size();
	}

}
