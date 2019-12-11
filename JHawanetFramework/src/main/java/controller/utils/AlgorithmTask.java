package controller.utils;

import javafx.concurrent.Task;
import model.metaheuristic.algorithm.Algorithm;

public class AlgorithmTask extends Task<Object> {

	private Algorithm<?> algorithm;

	public AlgorithmTask(Algorithm<?> algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	protected Object call() throws Exception {
		while (!this.algorithm.isStoppingConditionReached()) {
			// Check if the task is cancelled
			if (this.isCancelled()) {
				System.out.println("Tarea cancelada");
				algorithm.close();
				break;
			}
			updateMessage(this.algorithm.getStatusOfExecution());
			algorithm.runSingleStep();

		}
		return algorithm.getResult();
	}

}
