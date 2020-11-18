package controller.multiobjective.indicator;

import controller.ResultController;
import controller.multiobjective.indicator.util.ExecutionIndicatorTask;
import controller.util.ControllerUtils;
import epanet.core.EpanetException;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.metaheuristic.experiment.ExperimentSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.utils.CustomDialogs;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class is the controller for IndicatorRunningWindow.fxml. <br>
 * <br>
 * <p>
 * The experiments (experimentSet) received by this class will be executed in other thread.<br>
 * <br>
 * <p>
 * When the experiment finishes successfully this controller will open the
 * ResultIndicatorWindow.
 */
public class IndicatorRunningWindowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorRunningWindowController.class);

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
    private TextArea textArea;
    @FXML
    private Label algorithmNameLabel;
    @FXML
    private Label problemNameLabel;

    @NotNull
    private final Pane root;
    @NotNull
    private final ExecutionIndicatorTask task;

    @Nullable
    private Stage window;

    private final ExperimentSet<?> experimentSet;

    @NotNull
    private final Consumer<ResultIndicatorController> callback;

    /**
     * Constructor
     *
     * @param experimentSet the set of experiment to execute
     * @param callback   a callback function to return the result node when task finish
     * @throws NullPointerException     if experimentSet or callback are null.
     * @throws IllegalArgumentException if the experimentSet hasn't elements.
     */
    public IndicatorRunningWindowController(@NotNull ExperimentSet<?> experimentSet, @NotNull Consumer<ResultIndicatorController> callback) {
        LOGGER.debug("Initializing IndicatorRunningWindowController.");

        this.experimentSet = Objects.requireNonNull(experimentSet);
        this.callback = Objects.requireNonNull(callback);

        if (experimentSet.getNumberOfExperiments() == 0) {
            throw new IllegalArgumentException("There aren't experiment configured in experimentSet");
        }

        // Used to create a new thread
        this.task = new ExecutionIndicatorTask(experimentSet);

        this.root = ControllerUtils.loadFXML("/view/multiobjective/indicator/IndicatorRunningWindow.fxml", this); //initialize fxml and all parameters defined with @FXML

        addBindingAndListener();
    }

    /**
     * Add binding to task and gui elements.
     */
    private void addBindingAndListener() {
        LOGGER.debug("Initializing extra properties to the IndicatorRunningWindow.");

        // bind the textArea text with the value of message property of task
        textArea.textProperty().bind(this.task.messageProperty());
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
                LOGGER.error("Error in EpanetToolkit.", newValue);
                CustomDialogs.showExceptionDialog("Error", "Error in the simulation.",
                        "An error has occurred during the validation of the solutions with EpanetToolkit.", newValue);
            } else {
                LOGGER.error("Error in the experiment execution thread.", newValue);
                CustomDialogs.showExceptionDialog("Error", "Error in the execution of the algorithm",
                        "An error has occurred while trying to execute the experiment.", newValue);
            }
        });

        // update the progress bar
        task.progressProperty().addListener((prop, old, newv) -> {
            if (newv != null) {
                progressBar.setProgress((double) newv);
            }

            String workDone = (task.getWorkDone() != -1) ? Integer.toString((int)task.getWorkDone()) : "Undefined";
            String totalWork = (task.getTotalWork() != -1) ? Integer.toString((int)task.getTotalWork()) : "Undefined";
            String progressText = workDone + "/" + totalWork;

            progressLabel.setText(progressText);
        });

        // Receive the value of thread and add to chart
        task.customValueProperty().addListener((prop, oldv, newv) -> {
            this.problemNameLabel.setText(newv.getKey());
            this.algorithmNameLabel.setText(newv.getValue());
        });

        // listener when task finishes successfully
        task.setOnSucceeded(e -> {
            LOGGER.info("Experiment execution thread successfully executed.");
            ResultIndicatorController resultController = null;
            try {
                resultController = new ResultIndicatorController(task.getValue());
                callback.accept(resultController);

            } catch (IOException ioException) {
                LOGGER.error("An error has occurred while scanning the files in the temporary folder", ioException);
                CustomDialogs.showExceptionDialog("Error", "Error when scanning the temp folder",
                        "An error has occurred while scanning the files in the temporary folder. The result can't be showed", ioException);
            }
        });
    }

    /**
     * Method to handle the view event when Cancel button will be click on. This method is called by fxml
     */
    @FXML
    private void onCancelButtonClick() {
        LOGGER.debug("Cancelling thread task event.");
        // cancel the task
        this.task.cancel();
    }

    /**
     * Method to handle the view event when Close button will be click on.
     */
    @FXML
    private void onCloseButtonClick() {
        LOGGER.debug("Closed IndicatorRunningWindow event.");

        // if task is not cancelled, so cancel it.
        if (!task.isCancelled()) {
            LOGGER.debug("Cancelling thread task.");
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
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Status of execution");
        stage.setOnCloseRequest((e) -> onCloseButtonClick());

        LOGGER.info("Show IndicatorRunningWindow.");
        stage.show();
        this.window = stage;

        LOGGER.debug("Creating new thread to run the experiment.");
        Thread t = new Thread(task,"Indicator Execution Thread");
        t.setDaemon(true);
        t.start();
    }

}
