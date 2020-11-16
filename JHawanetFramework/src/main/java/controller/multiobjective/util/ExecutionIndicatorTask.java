package controller.multiobjective.util;

import exception.ApplicationException;
import javafx.concurrent.Task;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentSet;
import model.metaheuristic.experiment.component.ComputeQualityIndicators;
import model.metaheuristic.experiment.component.GenerateReferenceParetoFrontInDisk;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ExecutionIndicatorTask extends Task<String> {
    private static Logger LOGGER = LoggerFactory.getLogger(ExecutionIndicatorTask.class);
    @NotNull
    private final ExperimentSet<?> experimentSet;

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
        return "directory";
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
}
