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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import epanet.core.EpanetException;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.io.SolutionListOutput;

/**
 * Class defining tasks for the execution of algorithms in parallel.
 *
 * The method {@link #runAlgorithm(Experiment)} runs the algorithm from start to
 * finish and save the solution in the created directory<br>
 * <br>
 * The method {@link #runASingleStepOfAlgorithm()} run the algorithm step by
 * step. <br>
 * <br>
 * 
 * The same result that call {@link #runAlgorithm(Experiment)} can be achieved
 * calling {@link #prepareToRun(Experiment)}, so call
 * {@link #runASingleStepOfAlgorithm()} until {@link #algorithmHasANextStep()}
 * will be false and finalize calling {@link #saveSolutionList()}.
 *
 */
public class ExperimentAlgorithm<S extends Solution<?>> {
	private final Algorithm<S> algorithm;
	private final String algorithmTag;
	private final String problemTag;
	private final String referenceParetoFront;
	private final int runId;

	/**
	 * A StringBuffer that can be used has a log to the operation realize from this
	 * object
	 */
	private ObservableStringBuffer log;
	private String funFile;
	private String varFile;

	/**
	 * Constructor
	 * 
	 * @param algorithm the algorithm
	 * @param algorithmTag the algorithm tag (is used to create the directory)
	 * @param problem the ExperimentProblem setting up with the same problem of the algorithm
	 * @param runId runId the id of execution of this algorithm for a experiment
	 */
	public ExperimentAlgorithm(Algorithm<S> algorithm, String algorithmTag, ExperimentProblem<S> problem, int runId) {
		this.algorithm = algorithm;
		this.algorithmTag = algorithmTag;
		this.problemTag = problem.getTag();
		this.referenceParetoFront = problem.getReferenceFront();
		this.runId = runId;
	}

	/**
	 * 
	 * @param algorithm the algorithm
	 * @param problem the problem
	 * @param runId the id of execution of this algorithm for a experiment
	 */
	public ExperimentAlgorithm(Algorithm<S> algorithm, ExperimentProblem<S> problem, int runId) {

		this(algorithm, algorithm.getName(), problem, runId);

	}

	/**
	 * Run the algorithm to start to finish.
	 * 
	 * 
	 * @param experimentData the experiment data
	 * @throws EpanetException if algorithm execution has a problem with simulation
	 * @throws Exception       if algorithm can't close the resource or the solution
	 *                         can't be writed in file
	 */
	public void runAlgorithm(Experiment<?> experimentData) throws EpanetException, Exception {
		prepareToRun(experimentData);
		algorithm.run();
		saveSolutionList();
	}

	/**
	 * Create a output directory to save the result of execution of algorithm
	 * 
	 * @param experimentData the experiment data
	 */
	public void prepareToRun(Experiment<?> experimentData) {
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

		funFile = outputDirectoryName + "/FUN" + runId + ".tsv";
		varFile = outputDirectoryName + "/VAR" + runId + ".tsv";
		getLogBuffer().println(" Running algorithm: " + algorithmTag + ", problem: " + problemTag + ", run: " + runId
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
	 * {@link #prepareToRun}
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
		new SolutionListOutput(population).setSeparator("\t").setVarFileName(this.varFile).setFunFileName(this.funFile)
				.write();
	}

	/**
	 * Return if the algorithm can execute other iteration
	 * 
	 * @return true if algorithm can execute
	 */
	public boolean algorithmHasANextStep() {
		return !algorithm.isStoppingConditionReached();
	}

	/**
	 * Get the algorithm
	 * 
	 * @return the algorithm
	 */
	public Algorithm<S> getAlgorithm() {
		return algorithm;
	}

	public String getAlgorithmTag() {
		return algorithmTag;
	}

	public String getProblemTag() {
		return problemTag;
	}

	public String getReferenceParetoFront() {
		return referenceParetoFront;
	}

	public int getRunId() {
		return this.runId;
	}

	public void setLogBuffer(ObservableStringBuffer logBuffer) {
		this.log = logBuffer;
	}

	public ObservableStringBuffer getLogBuffer() {
		if (this.log == null) {
			this.log = new ObservableStringBuffer();
		}
		return this.log;
	}

	/**
	 * Close the resource used by algorithm
	 * 
	 * @throws Exception if there is a exception to try close resource
	 */
	public void close() throws Exception {
		this.algorithm.close();
	}
}
