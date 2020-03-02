package controller.multiobjectives;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import controller.ResultPlotWindowController;
import controller.ResultWindowController;
import controller.utils.ExperimentTask;
import epanet.core.EpanetException;
import exception.ApplicationException;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.epanet.element.Network;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
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
public class MultiObjectiveRunningWindowController {
	@FXML
	private Label headerText;
	@FXML
	private Button showChartButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Button closeButton;
	@FXML
	private ProgressIndicator progressIndicator;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label progressLabel;
	@FXML
	private TextArea algorithmStatusTextArea;
	@FXML
	private TextArea logExperimentTextArea;

	private Pane root;
	private Experiment<?> experiment;
	private Problem<?> problem;
	private ExperimentTask task;
	private Network network;
	private ResultPlotWindowController resultPlotWindowController;
	private Stage window;

	/**
	 * Constructor
	 * 
	 * @param algorithm the algorithm to execute
	 * @param problem   the problem that the algorithm has configured
	 * @param network   the network opened.
	 * @throws NullPointerException if algorithm is null or problem is null or
	 *                              network is null
	 */
	public MultiObjectiveRunningWindowController(Experiment<?> experiment, Problem<?> problem, Network network) {
		this.experiment = Objects.requireNonNull(experiment);
		this.problem = Objects.requireNonNull(problem);
		this.network = Objects.requireNonNull(network);

		this.root = loadFXML();

		this.task = new ExperimentTask(experiment);

		// Create the controller to add point even if plot windows is not showned
		this.resultPlotWindowController = new ResultPlotWindowController(this.problem.getNumberOfObjectives());

		addBindingAndListener();

		/**
		 * Only add the the showChartButton if the number of objectives is less than 2.
		 */
		if (this.problem.getNumberOfObjectives() == 2) {
			this.showChartButton.setVisible(true);
			this.showChartButton.setDisable(true);
		}

	}

	/**
	 * Load the FXML view associated to this controller.
	 * 
	 * @return the root pane.
	 * @throws ApplicationException if there is an error in load the .fxml.
	 */
	private Pane loadFXML() {
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("/view/multiobjective/MultiObjectiveRunningWindow.fxml"));
		fxmlLoader.setController(this);
		try {
			return fxmlLoader.load();
		} catch (IOException exception) {
			throw new ApplicationException(exception);
		}
	}

	/**
	 * Add binding to task and gui elements
	 */
	private void addBindingAndListener() {
		// bind the textArea text with the value of message property of task
		algorithmStatusTextArea.textProperty().bind(this.task.messageProperty());
		logExperimentTextArea.textProperty().bind(this.task.logProperty());
		
		cancelButton.disableProperty().bind(task.stateProperty().isNotEqualTo(State.RUNNING));

		// Add listener to detect when the task has finished and change the
		// progressIndicator icon and the header text.
		task.runningProperty().addListener((prop, old, newv) -> {
			if (!newv) {
				this.headerText.setText("Execution Finished");
				this.progressIndicator.setProgress(1);
			}
		});

		// listener to handle when a exception is generated in the other thread.
		task.exceptionProperty().addListener((property, oldValue, newValue) -> {
			if (newValue instanceof EpanetException) {
				CustomDialogs.showExceptionDialog("Error", "Error in the execution of the algorithm.",
						"An error has occurred during the validation of the solutions.", newValue);
			} else {
				CustomDialogs.showExceptionDialog("Error", "Error in the execution of the algorithm",
						"An error has occurred while trying to execute the algorithm", newValue);
			}
		});

		task.progressProperty().addListener((prop, old, newv) -> {
			if (newv != null) {
				progressBar.setProgress((double) newv);
				
			}

			String workDone = (task.getWorkDone() != -1) ? Double.toString(task.getWorkDone()) : "Undefined";
			String totalWork = (task.getTotalWork() != -1) ? Double.toString(task.getTotalWork()) : "Undefined";
			String progressText = workDone + "/" + totalWork;

			progressLabel.setText(progressText);

		});

		// listener when task finishes successfully
		task.setOnSucceeded(e -> {
			List<? extends Solution<?>> solutions = task.getValue();
			this.showChartButton.setDisable(false);
			this.resultPlotWindowController.addData(solutions, 0);

			ResultWindowController resultWindowController = new ResultWindowController(solutions, this.problem,
					this.network);
			resultWindowController.showAssociatedWindow();
		});
	}

	/**
	 * Method to handle the view event when Show Chart button will be click on.
	 */
	public void onShowChartButtonClick() {
		this.resultPlotWindowController.showAssociatedWindow();
	}

	/**
	 * Method to handle the view event when Cancel button will be click on.
	 */
	public void onCancelButtonClick() {
		// cancel the task
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
		// close the dialog
		this.window.close();
	}

	/**
	 * Show the associated view in window
	 */
	public void showWindowAndRunAlgorithm() {
		Stage stage = new Stage();
		stage.setScene(new Scene(this.root));
//		stage.initStyle(StageStyle.UTILITY);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setOnCloseRequest((e) -> onCloseButtonClick());
		stage.show();
		this.window = stage;

		Thread t = new Thread(task);
		t.setDaemon(true);
		t.start();
	}
}
