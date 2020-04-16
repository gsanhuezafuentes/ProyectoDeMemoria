package controller.utils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicReference;

import exception.ApplicationException;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.component.GenerateReferenceParetoFront;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ObservableStringBuffer;
import model.metaheuristic.solution.Solution;

public class ExperimentTask extends Task<List<? extends Solution<?>>> {
    private ObservableStringBuffer taskLog;
    private Experiment<?> experiment;

    //**********************************************************************************
    //Additional properties to task
    //**********************************************************************************

    /**
     * Used to send a additional message updates used has a log in a thread-safe
     * manner from the subclass to the FX application thread. AtomicReference is
     * used so as to coalesce updates such that we don't flood the event queue.
     */
    private AtomicReference<String> loggerUpdate = new AtomicReference<>();
    private final StringProperty log = new SimpleStringProperty(this, "log", "");
    //**********************************************************************************

    public ExperimentTask(Experiment<?> experiment) {
        this.experiment = experiment;
        taskLog = new ObservableStringBuffer();
        taskLog.addObserver(new Observer() {

            @Override
            public void update(Observable o, Object arg) {
                ObservableStringBuffer observable = (ObservableStringBuffer) o;
                updateLogger(observable.getBufferText());
            }
        });
    }

    @Override
    protected List<? extends Solution<?>> call() throws Exception {
        taskLog.println("ExecuteAlgorithms: Preparing output directory");
        prepareOutputDirectory();
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
            while (algorithm.algorithmHasANextStep()) {

                // run only a iteration of the current algorithm
                algorithm.runASingleStepOfAlgorithm();

                // update the message of progress of the current algorithm
                updateMessage("Progress of current algorithm:\n" + algorithm.getAlgorithm().getStatusOfExecution());

                // break the while loop if the task is cancelled
                if (this.isCancelled()) {
                    break;
                }
            }
            if (!this.isCancelled()) {
                algorithm.saveSolutionList();
                progress++;
                updateProgress(progress, experiment.getAlgorithmList().size());
            }
            algorithm.close();
        }

        // if task is cancelled return a empty list
        if (this.isCancelled()) {
            return Collections.emptyList();
        }

        // return only one pareto front
        GenerateReferenceParetoFront reference = new GenerateReferenceParetoFront(experiment);
        reference.run();
        return reference.getReferenceToParetoFront();
    }

    private void prepareOutputDirectory() {
        if (experimentDirectoryDoesNotExist()) {
            createExperimentDirectory();
        }
    }

    private boolean experimentDirectoryDoesNotExist() {
        boolean result;
        File experimentDirectory;

        experimentDirectory = new File(experiment.getExperimentBaseDirectory());
        if (experimentDirectory.exists() && experimentDirectory.isDirectory()) {
            result = false;
        } else {
            result = true;
        }

        return result;
    }

    /**
     * Create the experiment directory
     *
     * @throws ApplicationException if there is a error to create the experiment
     *                              directory
     */
    private void createExperimentDirectory() {
        File experimentDirectory;
        experimentDirectory = new File(experiment.getExperimentBaseDirectory());

        if (experimentDirectory.exists()) {
            experimentDirectory.delete();
        }

        boolean result;
        result = new File(experiment.getExperimentBaseDirectory()).mkdirs();
        if (!result) {
            throw new ApplicationException(
                    "Error creating experiment directory: " + experiment.getExperimentBaseDirectory());
        }
    }

    //**********************************************************************************
    // Additional method to new properties of task
    //**********************************************************************************

    public final String getLog() {
        checkThread();
        return log.get();
    }

    public final ReadOnlyStringProperty logProperty() {
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        final String log = loggerUpdate.getAndSet(null);
                        ExperimentTask.this.log.set(log);
                    }
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
