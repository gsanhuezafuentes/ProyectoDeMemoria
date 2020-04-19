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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.solution.Solution;

/**
 * Class for describing the configuration of a jMetal experiment.
 *
 */
public class Experiment<S extends Solution<?>> {
	private final String experimentName;
	private List<ExperimentAlgorithm<S>> algorithmList;
	private final List<ExperimentProblem<S>> problemList;
	private final String experimentBaseDirectory;

	private final String outputParetoFrontFileName;
	private final String outputParetoSetFileName;
	private final int independentRuns;

	private String referenceFrontDirectory;

	/**
	 * Constructor
	 * @param builder the builder object
	 */
	public Experiment(ExperimentBuilder<S> builder) {
		this.experimentName = builder.getExperimentName();
		this.experimentBaseDirectory = builder.getExperimentBaseDirectory();
		this.algorithmList = builder.getAlgorithmList();
		this.problemList = builder.getProblemList();
		this.independentRuns = builder.getIndependentRuns();
		this.outputParetoFrontFileName = builder.getOutputParetoFrontFileName();
		this.outputParetoSetFileName = builder.getOutputParetoSetFileName();
		this.referenceFrontDirectory = builder.getReferenceFrontDirectory();
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

	/* Setters */
	public void setReferenceFrontDirectory(String referenceFrontDirectory) {
		this.referenceFrontDirectory = referenceFrontDirectory;
	}

	public void setAlgorithmList(List<ExperimentAlgorithm<S>> algorithmList) {
		this.algorithmList = algorithmList;
	}

	/**
	 * The list of algorithms contain an algorithm instance per problem. This is not
	 * convenient for calculating statistical data, because a same algorithm will
	 * appear many times. This method remove duplicated algorithms and leave only an
	 * instance of each one.
	 */
	public void removeDuplicatedAlgorithms() {
		List<ExperimentAlgorithm<S>> algorithmList = new ArrayList<>();
		HashSet<String> algorithmTagList = new HashSet<>();

		getAlgorithmList().removeIf(alg -> !algorithmTagList.add(alg.getAlgorithmTag()));
	}
}
