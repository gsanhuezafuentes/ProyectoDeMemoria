package controller.utils;

import javafx.concurrent.Task;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.solution.Solution;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class is used to perform the algorithm execution in other thread
 * updating the JavaFXApplicationThread.
 *
 */
public class AlgorithmTask extends Task<AlgorithmTask.Result> {

	private final Algorithm<?> algorithm;
	

	public AlgorithmTask(Algorithm<?> algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Execute the algorithm
	 */
	@Override
	protected AlgorithmTask.Result call() throws Exception {
		int numberOfIteration = 0;
		algorithm.runSingleStep(); // run the first step
		updateMessage(this.algorithm.getStatusOfExecution());
		while (!this.algorithm.isStoppingConditionReached()) {
			// Check if the task is cancelled
			if (this.isCancelled()) {
				break;
			}
			algorithm.runSingleStep();
			updateMessage(this.algorithm.getStatusOfExecution());
			
			updateValue(new Result(Collections.unmodifiableList(algorithm.getResult()), numberOfIteration));
			numberOfIteration++;
		}
		algorithm.close();
		
		return new Result(Collections.unmodifiableList(algorithm.getResult()), numberOfIteration);
	}

	/**
	 * This inner class is used to return a pair of values.
	 *
	 */
	public static class Result{
		private final List<? extends Solution<?>> solution;
		private final int numberOfIterations;
		
		/**
		 * 
		 * @param solutionList the solution list
		 * @param numberOfIterations the number of iterations
		 * @throws NullPointerException if solution list is null
		 */
		public Result(List<? extends Solution<?>> solutionList, int numberOfIterations) {
			Objects.requireNonNull(solutionList);
			this.solution = solutionList;
			this.numberOfIterations = numberOfIterations;
		}

		/**
		 * @return the solution
		 */
		public List<? extends Solution<?>> getSolution() {
			return solution;
		}

		/**
		 * @return the numberOfIterations
		 */
		public int getNumberOfIterations() {
			return numberOfIterations;
		}
	}
}
