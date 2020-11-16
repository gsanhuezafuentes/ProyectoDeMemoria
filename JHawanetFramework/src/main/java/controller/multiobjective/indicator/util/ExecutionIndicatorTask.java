package controller.multiobjective.indicator.util;

import controller.singleobjective.util.SingleObjectiveExperimentTask;
import exception.ApplicationException;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.util.Pair;
import model.metaheuristic.experiment.ExperimentSet;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ExecutionIndicatorTask extends Task<String> {
    private static Logger LOGGER = LoggerFactory.getLogger(ExecutionIndicatorTask.class);
    @NotNull
    private final ExperimentSet<?> experimentSet;

    //**********************************************************************************
    //Additional properties to task
    //**********************************************************************************

    /**
     * Used to send a partial result of the execution in a thread-safe
     * manner from the subclass to the FX application thread. AtomicReference is
     * used so as to coalesce updates such that we don't flood the event queue.
     */
    @NotNull
    private final AtomicReference<Pair<String, String>> customValueUpdate = new AtomicReference<>();
    @NotNull
    private final ObjectProperty<Pair<String, String>> customValue = new SimpleObjectProperty<>(this, "customValue");
    //**********************************************************************************

    /**
     * Constructor
     *
     * @param experimentSet the experiment set.
     * @throws NullPointerException if experimentSet is null.
     */
    public ExecutionIndicatorTask(@NotNull ExperimentSet<?> experimentSet) {
        this.experimentSet = Objects.requireNonNull(experimentSet);
    }

    /**
     * Execute the algorithm in experiment.
     *
     * @return the temporary folder that where results are saved.
     * @throws Exception if there is a error while execute.
     */
    @Override
    protected String call() throws Exception {
//        prepareOutputDirectory();
//
//        // Progress of the count of algorithm finished
//        int progress = 0;
//        updateProgress(progress, experimentSet.getNumberOfAlgorithms());
//        for (Experiment<?> experiment : experimentSet.getExperimentList()) {
//            for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
//
//                // break the for loop if the task is cancelled
//                if (this.isCancelled()) {
//                    break;
//                }
//
//                // execute the algorithm
//                algorithm.prepareToRun(experiment);
//
//                // Run the algorithm
//                while (algorithm.algorithmHasANextStep()) {
//
//                    // run only a iteration of the current algorithm
//                    algorithm.runASingleStepOfAlgorithm();
//
//                    // update the message of progress of the current algorithm
//                    updateMessage("Progress of current algorithm:\n" + algorithm.getAlgorithm().getStatusOfExecution());
//
//                    // break the while loop if the task is cancelled
//                    if (this.isCancelled()) {
//                        break;
//                    }
//                }
//
//                // Gets the final result of the repetition of the algorithm.
//                if (!this.isCancelled()) {
//                    algorithm.saveSolutionList();
//                    progress++;
//                    updateProgress(progress, experiment.getAlgorithmList().size());
//                }
//            }
//        }
//
//
//        // close the resources of the problems.
//        experimentSet.closeProblemsResources();
//        // if task is cancelled return a empty list
//        if (this.isCancelled()) {
//            return "";
//        }
//
//        // return only one pareto front
//        new GenerateReferenceParetoFrontInDisk(experimentSet).run();
//        new ComputeQualityIndicators(experimentSet).run();
//        return experimentSet.getExperimentBaseDirectory();
        return "D:\\gsanh\\2Desktop\\ProyectosJimmy\\NSGAIIComputingReferenceParetoFrontsStudy";
    }

    /**
     * Prepare the temp folder and create in this a temporal directory to save the result of execution.
     */
    private void prepareOutputDirectory() {
        LOGGER.info("Prepare OutputDirectory.");
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
        LOGGER.info("Experiment base directory {}.", tempDirectory.toFile().getAbsolutePath());

        this.experimentSet.setExperimentBaseDirectory(tempDirectory.toFile().getAbsolutePath());
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

    /**
     * Get the customValue. This value corresponds a partial result of the final list.
     *
     * @return customValue
     */
    public final Pair<String, String> getCustomValue() {
        checkThread();
        return customValue.get();
    }

    /**
     * Get the customValueProperty. This value corresponds a partial result of the final list.
     * <p>
     *
     * @return customValue property
     */
    public final @NotNull ReadOnlyObjectProperty<Pair<String, String>> customValueProperty() {
        checkThread();
        return customValue;
    }

    /**
     * This method return a partial result that can be a instance of the final result list.
     * <p>
     * Updates the <code>customValue</code> property. Calls to updateCustomValueProperty are coalesced
     * and run later on the FX application thread, so calls to updateCustomValueProperty, even
     * from the FX Application thread, may not necessarily result in immediate
     * updates to this property, and intermediate message values may be coalesced to
     * save on event notifications.
     * <p>
     * <em>This method is safe to be called from any thread.</em>
     * </p>
     *
     * @param result is a parcial result of the total solution
     */
    protected void updateCustomValue(Pair<String, String> result) {
        if (Platform.isFxApplicationThread()) {
            this.customValue.set(result);
        } else {
            // As with the workDone, it might be that the background thread
            // will update this message quite frequently, and we need
            // to throttle the updates so as not to completely clobber
            // the event dispatching system.
            if (customValueUpdate.getAndSet(result) == null) {
                Platform.runLater(() -> {
                    this.customValue.setValue(customValueUpdate.getAndSet(null));
                });
            }
        }
    }

    private void checkThread() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Task must only be used from the FX Application Thread");
        }
    }
}
