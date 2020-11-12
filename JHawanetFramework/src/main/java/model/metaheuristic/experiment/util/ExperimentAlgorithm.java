/*
 * Base on code from https://github.com/jMetal/jMetal
 *
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall
 * be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. © 2019
 * GitHub, Inc.
 */
package model.metaheuristic.experiment.util;

import epanet.core.EpanetException;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.io.SolutionListOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
/*
 * The same result that call runAlgorithm(Experiment) can be achieved
 * calling {@link #prepareToRun(Experiment)}, so call
 * {@link #runASingleStepOfAlgorithm()} until {@link #algorithmHasANextStep()}
 * will be false and finalize calling {@link #saveSolutionList()}.
 */

/**
 * Class defining tasks for the execution of algorithms in parallel.
 * <p>
 * <br>
 * The method {@link #runASingleStepOfAlgorithm()} run the algorithm step by
 * step. <br>
 * <br>
 * The sequence of call to the method of this class are:<br>
 * <ul>
 *     <li> A call to {@link #prepareToRun}</li>
 *     <li> Many calls to {@link #runASingleStepOfAlgorithm} until {@link #algorithmHasANextStep} is false</li>
 *     <li> A calls to {@link #saveSolutionList}</li>
 * </ul>
 * <p>
 *
 */
public final class ExperimentAlgorithm<S extends Solution<?>> {

    @NotNull private final Algorithm<S> algorithm;
    @NotNull private final String algorithmTag;
    @NotNull private final String problemTag;
    private final int runId;

    /**
     * A StringBuffer that can be used has a log to the operation realize from this
     * object
     */
    @Nullable private ObservableStringBuffer log;
    @Nullable private String funFile;
    @Nullable private String varFile;

    /**
     * Constructor
     *
     * @param algorithm    the algorithm
     * @param algorithmTag the algorithm tag (is used to create the directory)
     * @param problem      the ExperimentProblem setting up with the same problem of the algorithm
     * @param runId        runId the id of execution of this algorithm for a experiment
     * @throws NullPointerException if algorithm, algorithmTag or problem is null.
     */
    public ExperimentAlgorithm(@NotNull Algorithm<S> algorithm, @NotNull String algorithmTag, @NotNull ExperimentProblem<S> problem, int runId) {
        Objects.requireNonNull(algorithm);
        Objects.requireNonNull(algorithmTag);
        Objects.requireNonNull(problem);
        this.algorithm = algorithm;
        if (algorithm.getName().isEmpty()){
            this.algorithmTag = algorithm.getClass().getSimpleName();
        }
        else{
            this.algorithmTag = algorithmTag;
        }
        this.problemTag = problem.getTag();
        this.runId = runId;
    }

    /**
     * @param algorithm the algorithm
     * @param problem   the problem
     * @param runId     the id of execution of this algorithm for a experiment
     */
    public ExperimentAlgorithm(@NotNull Algorithm<S> algorithm, @NotNull ExperimentProblem<S> problem, int runId) {
        this(algorithm, algorithm.getName(), problem, runId);
    }

//    /**
//     * Run the algorithm to start to finish.
//     *
//     * @param experimentData the experiment data
//     * @throws EpanetException if algorithm execution has a problem with simulation
//     * @throws Exception       if algorithm can't close the resource or the solution
//     *                         can't be writed in file
//     */
//    public void runAlgorithm(@NotNull Experiment<?> experimentData) throws EpanetException, Exception {
//        prepareToRun(experimentData);
//        algorithm.run();
//        saveSolutionList();
//    }

    /**
     * Create a output directory to save the result of execution of algorithm using {@link #saveSolutionList()}.
     * So you have to call this method before call {@link #saveSolutionList()}.
     *
     * @param experimentData the experiment data.
     * @throws IllegalArgumentException if experiment base directory set up in experimentData is a empty string.
     */
    public void prepareToRun(@NotNull Experiment<?> experimentData) {
        if (experimentData.getExperimentBaseDirectory().isEmpty()){
            throw new IllegalArgumentException("Experiment base directory is a empty string");
        }
        String outputDirectoryName = experimentData.getExperimentBaseDirectory() + "/data/" + algorithmTag + "/"
                + problemTag;

        File outputDirectory = new File(outputDirectoryName);
        if (!outputDirectory.exists()) {
            boolean result = new File(outputDirectoryName).mkdirs();
            if (result) {
                getLogBuffer().println("Creating " + outputDirectoryName);
            } else {
                getLogBuffer().println("Creating " + outputDirectoryName + " failed");
            }
        }

        this.funFile = outputDirectoryName + "/" + experimentData.getObjectiveOutputFileName() + runId + ".csv";
        this.varFile = outputDirectoryName + "/" + experimentData.getVariablesOutputFileName() + runId + ".csv";
        getLogBuffer().println("- Running algorithm: " + algorithmTag + ", problem: " + problemTag + ", run: " + runId
                + ", funFile: " + funFile);
    }

    /**
     * Run a single step of algorithm.
     *
     * @throws EpanetException if algorithm execution has a problem with simulation
     * @throws Exception       if algorithm can't close the resource
     */
    public void runASingleStepOfAlgorithm() throws EpanetException, Exception {
        algorithm.runSingleStep();
    }

    /**
     * Save the solution of the algorithm in the directory configure for
     * {@link #prepareToRun}. So you has to call first to {@link #prepareToRun}.
     *
     * @throws IOException           If an I/O error occurs
     * @throws FileNotFoundException if the file exists but is a directory rather
     *                               than a regular file, does not exist but can not
     *                               be created, or cannot be opened for any other
     *                               reason.
     * @throws IllegalStateException if prepareToRun has not been called or
     *                               algorithm is not over
     *                               ({@link #algorithmHasANextStep()} is true).
     */
    public void saveSolutionList() throws FileNotFoundException, IOException {
        if (algorithmHasANextStep()) {
            throw new IllegalStateException("Can't call this method if algorithm is not over");
        }

        List<S> population = algorithm.getResult();
        new SolutionListOutput(population).setSeparator(",").setVarFileName(this.varFile).setFunFileName(this.funFile)
                .write();
    }

    /**
     * Return if the algorithm can execute other iteration/generation.
     *
     * @return true if algorithm can execute.
     */
    public boolean algorithmHasANextStep() {
        return !algorithm.isStoppingConditionReached();
    }

    /**
     * Get the algorithm.
     *
     * @return the algorithm.
     */
    public @NotNull Algorithm<S> getAlgorithm() {
        return algorithm;
    }

    /**
     * Get the algorithm tag.
     * @return the algorithm tag.
     */
    public @NotNull String getAlgorithmTag() {
        return algorithmTag;
    }

    /**
     * Get the run id.
     * @return the run id.
     */
    public int getRunId() {
        return this.runId;
    }

    /**
     * Get the log buffer. This property is set up automatically when the algorithm will be execute.
     * @return the log buffer;
     */
    public @NotNull ObservableStringBuffer getLogBuffer() {
        if (this.log == null) {
            this.log = new ObservableStringBuffer();
        }
        return this.log;
    }

    /**
     * Set the log buffer. This property is set up automatically when the algorithm will be execute. So no set up it.
     * @param logBuffer log buffer.
     * @throws NullPointerException if logBuffer is null.
     */
    public void setLogBuffer(ObservableStringBuffer logBuffer) {
        Objects.requireNonNull(logBuffer);
        this.log = logBuffer;
    }

    /**
     * Return the result of this experiment algorithm.
     * <p>
     * If the problem was a single objective the result should be a list with one solution. However
     * if the problem was multiobjective the solution should be a list with the pareto front.
     *
     * @return the result.
     * @see Algorithm
     */
    public @NotNull List<? extends Solution<?>> getResult(){
        return algorithm.getResult();
    }
}
