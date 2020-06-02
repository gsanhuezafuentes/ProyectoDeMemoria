package controller.multiobjectives;

import application.ApplicationSetup;
import controller.ResultController;
import controller.ResultPlotController;
import controller.utils.CustomCallback;
import controller.utils.MultiObjectiveExperimentTask;
import epanet.core.EpanetException;
import exception.ApplicationException;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.epanet.element.Network;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.utils.CustomDialogs;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is the controller for MultiObjectiveRunningWindow. <br>
 * <br>
 * <p>
 * The experiment received by this class will be executed in other thread.<br>
 * <br>
 * <p>
 * When the experiment finishes successfully this controller will open the
 * ResultWindow.
 */
public class MultiObjectiveRunningWindowController {
    @FXML
    private Label headerText;
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
    @FXML
    private Tab chartTab;
    @FXML
    private Label algorithmNameLabel;
    @FXML
    private Label problemNameLabel;

    @NotNull
    private final Pane root;
    @NotNull
    private final CustomCallback<ResultController> callback;
    @Nullable
    private final ResultPlotController resultPlotController;
    @NotNull
    private final MultiObjectiveExperimentTask task;
    @Nullable
    private Stage window;

    @NotNull
    private final Experiment<?> experiment;
    @NotNull
    private final Problem<?> problem;
    @Nullable
    private final Map<String, String> parameters;
    @NotNull
    private final Network network;

    private int numberOfSolutionToReturn;
    private final boolean isNumberOfResultLimited;

    /**
     * Constructor
     *
     * @param experiment the experiment to execute
     * @param parameters the configurations parameter of experiment.
     * @param network    the network opened.
     * @param callback   a callback function to return the result node when task finish
     * @throws NullPointerException     if experiment, experiment problem, network or callback are null.
     * @throws IllegalArgumentException if problem is singleobjective or there aren't element in experiment algorithm.
     */
    public MultiObjectiveRunningWindowController(@NotNull Experiment<?> experiment, @Nullable Map<String, String> parameters, @NotNull Network network, @NotNull CustomCallback<ResultController> callback) {
        this.experiment = Objects.requireNonNull(experiment);
        this.problem = Objects.requireNonNull(experiment.getProblem()).getProblem();
        this.parameters = parameters;
        this.network = Objects.requireNonNull(network);
        this.callback = Objects.requireNonNull(callback);

        if (experiment.getAlgorithmList().isEmpty()){
            throw new IllegalArgumentException("There aren't algorithms configured in experiment");
        }

        if (this.problem.getNumberOfObjectives() == 1) {
            throw new IllegalArgumentException("The number of objective to to this type of Registrable should be 1.");
        }

        this.root = loadFXML(); //initialize root and @FXML by injection

        this.task = new MultiObjectiveExperimentTask(experiment, ApplicationSetup.getInstance().isChartEnable());

        // Create the controller to add point even if plot windows is not showed.
        // Only created if number of objectives is 2
        if (this.problem.getNumberOfObjectives() == 2 && ApplicationSetup.getInstance().isChartEnable()) {
            this.resultPlotController = new ResultPlotController(this.problem.getNumberOfObjectives());
            this.chartTab.setContent(this.resultPlotController.getNode());
        } else {
            this.resultPlotController = null;
            this.chartTab.setDisable(true);
        }

        // limit the number of result to return
        isNumberOfResultLimited = ApplicationSetup.getInstance().isNumberOfMultiObjectiveResultLimited();
        if (isNumberOfResultLimited) {
            this.numberOfSolutionToReturn = ApplicationSetup.getInstance().getNumberOfResultMultiObjectiveProblem();
        }

        //add the name of algorithm and the name of problem. (The experiment should have only one type of algorithm. Eg. NSGAII)
        this.algorithmNameLabel.setText(experiment.getAlgorithmList().get(0).getAlgorithmTag());
        this.problemNameLabel.setText(experiment.getProblem().getTag());
        addBindingAndListener();

    }

    /**
     * Load the FXML view associated to this controller.
     *
     * @return the root pane.
     * @throws ApplicationException if there is an error in load the .fxml.
     */
    private Pane loadFXML() {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/view/MultiObjectiveRunningWindow.fxml"));
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
                CustomDialogs.showExceptionDialog("Error", "Error in the execution of the experiment.",
                        "An error has occurred during the validation of the solutions.", newValue);
            } else {
                CustomDialogs.showExceptionDialog("Error", "Error in the execution of the experiment",
                        "An error has occurred while trying to execute the experiment", newValue);
            }
        });

        // update the progress bar
        task.progressProperty().addListener((prop, old, newv) -> {
            if (newv != null) {
                progressBar.setProgress((double) newv);

            }

            String workDone = (task.getWorkDone() != -1) ? Double.toString(task.getWorkDone()) : "Undefined";
            String totalWork = (task.getTotalWork() != -1) ? Double.toString(task.getTotalWork()) : "Undefined";
            String progressText = workDone + "/" + totalWork;

            progressLabel.setText(progressText);

        });

        // update the chart
        task.valueProperty().addListener((prop, oldv, newv) -> {
            if (resultPlotController != null) {
                this.resultPlotController.addData(newv);
            }
        });

        // listener when task finishes successfully
        task.setOnSucceeded(e -> {
            List<? extends Solution<?>> solutions = task.getValue();
            if (this.resultPlotController != null) {
                this.resultPlotController.addData(solutions);
            }
            // slice the list to a given size
            if (this.isNumberOfResultLimited) {
                solutions = solutions.stream().limit(this.numberOfSolutionToReturn).collect(Collectors.toList());
            }
            ResultController resultController = new ResultController(experiment.getProblem().getTag(), solutions, this.problem,
                    this.network, this.parameters);
            callback.notify(resultController);
        });
    }

    /**
     * Method to handle the view event when Cancel button will be click on.
     */
    @FXML
    private void onCancelButtonClick() {
        // cancel the task
        this.task.cancel();
    }

    /**
     * Method to handle the view event when Close button will be click on.
     */
    @FXML
    private void onCloseButtonClick() {
        // if task is not cancelled, so cancel it.
        if (!task.isCancelled()) {
            task.cancel();
        }
        // close the dialog
        assert this.window != null;
        this.window.close();
    }

    /**
     * Show the associated view in window
     */
    public void showWindowAndRunExperiment() {
        Stage stage = new Stage();
        stage.setScene(new Scene(this.root));
//		stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest((e) -> onCloseButtonClick());
        stage.setTitle("Status of execution");
        stage.show();
        this.window = stage;

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}
