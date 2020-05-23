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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Class for describing the configuration of a experiment.
 * <p>
 * A experiment execute sequentially the algorithm configured in it.
 *
 * @see ExperimentBuilder for more detail
 */
public final class Experiment<S extends Solution<?>> {
	private @NotNull final String experimentName;
	private @NotNull List<ExperimentAlgorithm<S>> algorithmList;
	private @NotNull final List<ExperimentProblem<S>> problemList;
	private @NotNull final String experimentBaseDirectory;

	private @NotNull final String objectiveOutputFileName;
	private @NotNull final String variablesOutputFileName;
	private final int independentRuns;

	private @NotNull String referenceFrontDirectory;

	/**
	 * Constructor.
	 * @param builder the builder object.
	 * @throws NullPointerException if builder is null.
	 * @see ExperimentBuilder
	 */
	public Experiment(@NotNull ExperimentBuilder<S> builder) {
		Objects.requireNonNull(builder);
		this.experimentName = builder.getExperimentName();
		this.experimentBaseDirectory = builder.getExperimentBaseDirectory();
		this.algorithmList = builder.getAlgorithmList();
		this.problemList = builder.getProblemList();
		this.independentRuns = builder.getIndependentRuns();
		this.objectiveOutputFileName = builder.getObjectiveOutputFileName();
		this.variablesOutputFileName = builder.getVariablesOutputFileName();
		this.referenceFrontDirectory = builder.getReferenceFrontDirectory();
	}

	/* Getters */

	/**
	 * Get the experiment name.
	 * @return the experiment name.
	 */
	public @NotNull String getExperimentName() {
		return experimentName;
	}


	/**
	 * Get the algorithm list.
	 * @return the algorithm list or a empty list if it isn't set up.
	 */
	public @NotNull List<ExperimentAlgorithm<S>> getAlgorithmList() {
		return algorithmList;
	}

	/**
	 * Get the problem list.
	 * @return the problem list or a empty list if it isn't set up.
	 */
	public @NotNull List<ExperimentProblem<S>> getProblemList() {
		return problemList;
	}

	/**
	 * Get the experiment base directory where the pareto front for each algorithm execution will be stored.
	 * @return the experiment base directory name or a empty string if it isn't set up.
	 */
	public @NotNull String getExperimentBaseDirectory() {
		return experimentBaseDirectory;
	}

	/**
	 * Get the name of output file for objectives.
	 * @return the file name of objective file.
	 */
	public @NotNull String getObjectiveOutputFileName() {
		return objectiveOutputFileName;
	}

	/**
	 * Get the name of output file for variables.
	 * @return the file name of variables file.
	 */
	public @NotNull String getVariablesOutputFileName() {
		return variablesOutputFileName;
	}

	/**
	 * Get the number of independent runs.
	 * @return the number of independent run.
	 */
	public int getIndependentRuns() {
		return independentRuns;
	}

	/**
	 * Get the file name where reference front (Final Pareto Front) will be stored when the problem are multiobjective.
	 * @return the reference file name or a empty string if it isn't set up.
	 */
	public @NotNull String getReferenceFrontDirectory() {
		return referenceFrontDirectory;
	}

	/* Setters */
	/**
	 * Set the reference front directory (pareto front directory) where result will be save when the problem will be multiobjective.
	 * @param referenceFrontDirectory the reference front directory
	 * @throws NullPointerException if referenceFrontDirectory is null.
	 */
	public void setReferenceFrontDirectory(@NotNull String referenceFrontDirectory) {
		Objects.requireNonNull(referenceFrontDirectory);
		this.referenceFrontDirectory = referenceFrontDirectory;
	}

	/**
	 * Set the algorithm list.
	 * @param algorithmList the new algorithm list or empty list.
	 * @throws NullPointerException if algorithmList is null.
	 */
	public void setAlgorithmList(@NotNull List<ExperimentAlgorithm<S>> algorithmList) {
		Objects.requireNonNull(algorithmList);
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
