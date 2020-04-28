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
package model.metaheuristic.experiment;

import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for class {@link Experiment}
 */
public class ExperimentBuilder<S extends Solution<?>> {
	private final String experimentName;
	private List<ExperimentAlgorithm<S>> algorithmList;
	private List<ExperimentProblem<S>> problemList;
	private String referenceFrontDirectory;
	private String experimentBaseDirectory;
	private String outputParetoFrontFileName;
	private String outputParetoSetFileName;
	private int independentRuns;

	public ExperimentBuilder(String experimentName) {
		this.experimentName = experimentName;
		this.independentRuns = 1;
		this.referenceFrontDirectory = null;
	}

	public ExperimentBuilder<S> setAlgorithmList(List<ExperimentAlgorithm<S>> algorithmList) {
		this.algorithmList = new ArrayList<>(algorithmList);

		return this;
	}

	public ExperimentBuilder<S> setProblemList(List<ExperimentProblem<S>> problemList) {
		this.problemList = problemList;

		return this;
	}

	public ExperimentBuilder<S> setExperimentBaseDirectory(String experimentBaseDirectory) {
		this.experimentBaseDirectory = experimentBaseDirectory + "/" + experimentName;

		return this;
	}

	public ExperimentBuilder<S> setReferenceFrontDirectory(String referenceFrontDirectory) {
		this.referenceFrontDirectory = referenceFrontDirectory;

		return this;
	}

	public ExperimentBuilder<S> setOutputParetoFrontFileName(String outputParetoFrontFileName) {
		this.outputParetoFrontFileName = outputParetoFrontFileName;

		return this;
	}

	public ExperimentBuilder<S> setOutputParetoSetFileName(String outputParetoSetFileName) {
		this.outputParetoSetFileName = outputParetoSetFileName;

		return this;
	}

	public ExperimentBuilder<S> setIndependentRuns(int independentRuns) {
		this.independentRuns = independentRuns;

		return this;
	}

	public Experiment<S> build() {
		return new Experiment<S>(this);
	}

	/* Getters */
	public String getExperimentName() {
		return experimentName;
	}

	public List<ExperimentAlgorithm<S>> getAlgorithmList() {
		return algorithmList;
	}

	public List<ExperimentProblem<S>> getProblemList() {
		return problemList;
	}

	public String getExperimentBaseDirectory() {
		return experimentBaseDirectory;
	}

	public String getOutputParetoFrontFileName() {
		return outputParetoFrontFileName;
	}

	public String getOutputParetoSetFileName() {
		return outputParetoSetFileName;
	}

	public int getIndependentRuns() {
		return independentRuns;
	}

	public String getReferenceFrontDirectory() {
		return referenceFrontDirectory;
	}

}
