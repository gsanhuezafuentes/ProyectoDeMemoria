package controller.utils;

import java.util.List;

import javafx.concurrent.Task;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.solution.Solution;

/**
 * This class is used to perform the algorithm execution in other thread
 * updating the JavaFXApplicationThread.
 *
 */
public class AlgorithmTask extends Task<List<? extends Solution<?>>> {

	private Algorithm<?> algorithm;

	public AlgorithmTask(Algorithm<?> algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Execute the algorithm
	 */
	@Override
	protected List<? extends Solution<?>> call() throws Exception {
		algorithm.runSingleStep(); // run the first step
		updateMessage(this.algorithm.getStatusOfExecution());
		while (!this.algorithm.isStoppingConditionReached()) {
			// Check if the task is cancelled
			if (this.isCancelled()) {
				break;
			}
			algorithm.runSingleStep();
			updateMessage(this.algorithm.getStatusOfExecution());

		}
		algorithm.close();
		return algorithm.getResult();
	}

}
