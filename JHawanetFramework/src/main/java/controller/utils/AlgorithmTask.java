package controller.utils;

import javafx.concurrent.Task;
import model.metaheuristic.algorithm.Algorithm;

/**
 * This class is used to perform the algorithm execution in other thread updating the JavaFXApplicationThread.
 *
 */
public class AlgorithmTask extends Task<Object> {

	private Algorithm<?> algorithm;

	public AlgorithmTask(Algorithm<?> algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Execute the algorithm
	 */
	@Override
	protected Object call() throws Exception {
		algorithm.runSingleStep(); // run the first step
		updateMessage(this.algorithm.getStatusOfExecution());
		while (!this.algorithm.isStoppingConditionReached()) {
			// Check if the task is cancelled
			if (this.isCancelled()) {
				algorithm.close();
				break;
			}
			algorithm.runSingleStep();
			updateMessage(this.algorithm.getStatusOfExecution());

		}
		return algorithm.getResult();
	}

}
