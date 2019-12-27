package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import controller.utils.AlgorithmTask;
import epanet.core.EpanetException;
import exception.ApplicationException;
import model.epanet.element.Network;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.solution.Solution;
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
			Object solutionOrSolutionList = e.getSource().getValue();
			System.out.println(solutionOrSolutionList);
			List<Solution<?>> solutionList;
			// test if returned solution is a list
			if (solutionOrSolutionList instanceof List<?>) {
				solutionList = (List<Solution<?>>) solutionOrSolutionList;
			} //if it isn't a list so test if it is a solution.
			else if (solutionOrSolutionList instanceof Solution<?>) {
				Solution<?> solution = (Solution<?>) solutionOrSolutionList;
				solutionList = new ArrayList<>();
				solutionList.add(solution);
			}
			else {
				throw new ApplicationException("The returned solution neither a list of solution nor a solution instance");
			}
			
			ResultWindowController resultWindowController = new ResultWindowController(solutionList);
			resultWindowController.showAssociatedWindow();
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
