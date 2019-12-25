package controller;

import java.util.Objects;

import controller.utils.AlgorithmTask;
import epanet.core.EpanetException;
import model.epanet.element.Network;
import model.metaheuristic.algorithm.Algorithm;
import view.RunningDialog;
import view.utils.CustomDialogs;

/**
 * This class is the controller for RunningDialog. <br>
 * <br>
 * 
 * The algorithm received by this class will be executed in other thread.<br>
 * <br>
 * 
 * When the algorithm finishes successfully this controller will open the
 * ResultWindow.
 *
 */
public class RunningDialogController {

	private RunningDialog view;
	private AlgorithmTask task;

	/**
	 * Constructor
	 * 
	 * @param algorithm the algorithm to execute
	 * @param network   the network opened.
	 */
	public RunningDialogController(Algorithm<?> algorithm, Network network) {
		Objects.requireNonNull(algorithm);
		Objects.requireNonNull(network);
		this.task = new AlgorithmTask(algorithm);
		this.view = new RunningDialog(this, task);

		addBindingAndListener();
	}

	/**
	 * Add binding to task
	 */
	private void addBindingAndListener() {
		// listener to handle when a exception is generated in the other thread.
		task.exceptionProperty().addListener((property, oldValue, newValue) -> {
			if (newValue instanceof EpanetException) {
				CustomDialogs.showExceptionDialog("Error", "Error in the execution of the algorithm.",
						"An error has occurred during the validation of the solutions.", newValue);
			} else {
				CustomDialogs.showExceptionDialog("Error", "Error in the execution of the algorithm",
						"An error has occurred while trying to close the resources of the problem.", newValue);
			}
		});

		// listener when task finishes succesfully
		task.setOnSucceeded(e -> {
			System.out.println(e.getSource().getValue());
		});
	}

	/**
	 * Method to handle the view event when Show Chart button will be click on.
	 */
	public void onShowChartButtonClick() {
		
	}

	
	
	/**
	 * Method to handle the view event when Cancel button will be click on.
	 */
	public void onCancelButtonClick() {
		//cancel the task
		this.task.cancel();
	}

	/**
	 * Method to handle the view event when Close button will be click on.
	 */
	public void onCloseButtonClick() {
		// if task is not cancelled, so cancel it.
		if (!task.isCancelled()) {
			task.cancel();
		}
		//close the dialog
		this.view.close();
	}

	/**
	 * Get the associated window to this controller.
	 * 
	 * @return the window.
	 */
	public RunningDialog getAssociatedView() {
		return this.view;
	}

	/**
	 * Show the associated view in window
	 */
	public void showWindowAndRunAlgorithm() {
		this.view.show();
		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
}
