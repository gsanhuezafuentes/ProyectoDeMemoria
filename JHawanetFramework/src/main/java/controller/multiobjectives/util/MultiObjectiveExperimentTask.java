package controller.multiobjectives.util;

import controller.util.solutionattribute.Generation;
import exception.ApplicationException;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.component.GenerateReferenceParetoFrontInRAM;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ObservableStringBuffer;
import model.metaheuristic.solution.Solution;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MultiObjectiveExperimentTask extends Task<List<? extends Solution<?>>> {
    @NotNull
    private final ObservableStringBuffer taskLog;
    @NotNull
    private final Experiment<?> experiment;

    //**********************************************************************************
    //Additional properties to task
    //**********************************************************************************

    /**
     * Used to send a additional message updates used has a log in a thread-safe
     * manner from the subclass to the FX application thread. AtomicReference is
     * used so as to coalesce updates such that we don't flood the event queue.
     */
    @NotNull
    private final AtomicReference<String> loggerUpdate = new AtomicReference<>();
    @NotNull
    private final StringProperty log = new SimpleStringProperty(this, "log", "");
    //**********************************************************************************

    /**
     * Constructor
     *
     * @param experiment          the experiment.
     * @param returnPartialResult returnPartialResult a boolean property that indicates if partial result should be returned.
     * @throws NullPointerException if experiment is null.
     */
    public MultiObjectiveExperimentTask(@NotNull Experiment<?> experiment, boolean returnPartialResult) {
        this.experiment = Objects.requireNonNull(experiment);
        this.returnPartialResult = returnPartialResult;
        taskLog = new ObservableStringBuffer();
        taskLog.addObserver((o, arg) -> {
            ObservableStringBuffer observable = (ObservableStringBuffer) o;
            updateLogger(observable.getBufferText());
        });
    }

    /**
     * Execute the algorithm in experiment.
     *
     * @return the solution list or a empty list if the task is cancel.
     * @throws Exception if there is a error while execute.
     */
    @Override
    protected List<? extends Solution<?>> call() throws Exception {
        Generation<Solution<?>> generationAttribute = new Generation<>();
        prepareOutputDirectory();

        taskLog.println("ExecuteAlgorithms.");
        // Progress of the count of algorithm finished
        int progress = 0;
        updateProgress(progress, experiment.getAlgorithmList().size());
        for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
            algorithm.setLogBuffer(taskLog);

            // break the for loop if the task is cancelled
            if (this.isCancelled()) {
                break;
            }

            // execute the algorithm
            algorithm.prepareToRun(this.experiment);
            int numberOfGenerations = 0;

            // Run the algorithm
            while (algorithm.algorithmHasANextStep()) {

                // run only a iteration of the current algorithm
                algorithm.runASingleStepOfAlgorithm();

                // update the message of progress of the current algorithm
                updateMessage("Progress of current algorithm:\n" + algorithm.getAlgorithm().getStatusOfExecution());

                // break the while loop if the task is cancelled
                if (this.isCancelled()) {
                    break;
                }
                numberOfGenerations++;
            }

            // Gets the final result of the repetition of the algorithm.
            if (!this.isCancelled()) {
                List<? extends Solution<?>> solutions = algorithm.getResult();
                // add an attribute to solution. It attribute is used in result window to show in which generation the solution was obtained.
                for (Solution<?> solution : solutions) {
                    generationAttribute.setAttribute(solution, numberOfGenerations + 1);
                }
                algorithm.saveSolutionList();
                progress++;
                if (returnPartialResult) {
                    updateValue(solutions);
                }
                updateProgress(progress, experiment.getAlgorithmList().size());
            }
        }

        // close the resources of the problems.
        experiment.getProblem().closeResources();
        // if task is cancelled return a empty list
        if (this.isCancelled()) {
            return Collections.emptyList();
        }

        // return only one pareto front
        GenerateReferenceParetoFrontInRAM reference = new GenerateReferenceParetoFrontInRAM(experiment, taskLog);
        reference.run();

        return reference.getReferenceToParetoFront();
    }

    /**
     * Prepare the temp folder and create in this a temporal directory to save the result of execution.
     */
    private void prepareOutputDirectory() {
        if (!experimentDirectoryExist()) {
            createExperimentDirectory();
        }
        Path experimentDirectory = FileSystems.getDefault().getPath("temp");
        Path tempDirectory;
        try {
            tempDirectory = Files.createTempDirectory(experimentDirectory, null);
        } catch (IOException e) {
            throw new ApplicationException(
                    "Error creating temporary directory in temp folder.");
        }
        this.experiment.setExperimentBaseDirectory(tempDirectory.toFile().getAbsolutePath());
    }

    /**
     * Check if exist the temp directory in the current directory of the application.
     */
    private boolean experimentDirectoryExist() {
        boolean result;
        File experimentDirectory;

        experimentDirectory = new File("temp");
        result = experimentDirectory.exists() && experimentDirectory.isDirectory();

        return result;
    }

    /**
     * Create the experiment directory (temp folder in current directory.).
     *
     * @throws ApplicationException if there is a error to create the experiment
     *                              directory
     */
    private void createExperimentDirectory() {
        File experimentDirectory;
        experimentDirectory = new File("temp");

        if (experimentDirectory.exists()) {
            experimentDirectory.delete();
        }

        boolean result = experimentDirectory.mkdirs();
        if (!result) {
            throw new ApplicationException(
                    "Error creating experiment directory: temp");
        }
    }

    //**********************************************************************************
    // Additional method to new properties of task
    //**********************************************************************************

    public final String getLog() {
        checkThread();
        return log.get();
    }

    public final @NotNull ReadOnlyStringProperty logProperty() {
        checkThread();
        return log;
    }

    /**
     * Updates the <code>logger</code> property. Calls to updateLogger are coalesced
     * and run later on the FX application thread, so calls to updateLogger, even
     * from the FX Application thread, may not necessarily result in immediate
     * updates to this property, and intermediate message values may be coalesced to
     * save on event notifications.
     * <p>
     * <em>This method is safe to be called from any thread.</em>
     * </p>
     *
     * @param log the new message
     */
    protected void updateLogger(String log) {
        if (Platform.isFxApplicationThread()) {
            this.log.set(log);
        } else {
            // As with the workDone, it might be that the background thread
            // will update this message quite frequently, and we need
            // to throttle the updates so as not to completely clobber
            // the event dispatching system.
            if (loggerUpdate.getAndSet(log) == null) {
                Platform.runLater(() -> {
                    final String log1 = loggerUpdate.getAndSet(null);
                    MultiObjectiveExperimentTask.this.log.set(log1);
                });
            }
        }
    }

    private void checkThread() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Task must only be used from the FX Application Thread");
        }
    }

    private final boolean returnPartialResult;
}
