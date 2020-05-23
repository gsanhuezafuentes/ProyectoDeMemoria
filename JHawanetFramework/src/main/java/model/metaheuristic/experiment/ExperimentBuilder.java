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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Builder for class {@link Experiment}
 * <p>
 * The default values are
 * <p>
 * independantRuns = 1 <br>
 * objectiveOuputFileName = FUN <br>
 * variablesOutputFileName = VAR <br>
 * referenceFrontDirectory = "" <br>
 * experiment base directory = "" <br>
 * <p>
 * <strong>Notes:</strong>
 * <p>
 * <ul>
 * 	<li>
 *  	When the problem is a single objective has no effect set up referenceFrontDirectory. If the problem is multiobjective
 *      so this directory will be used to save the final pareto front (the non-dominated solution after join the result
 *      of all algorithms).
 * 	</li>
 * 	<li>
 *    	When the problem is a single objective has no effect set up experimentBaseDirectory. If the problem is multiobjective
 *      so this directory will be used to save the pareto front for algorithms.
 *  </li>
 *  <li>
 *      Set up experimentBaseDirectory and referenceFrontDirectory only store solution automatically in disk when the problem to resolve
 *      is set up has multiobjective and show the final pareto front in the GUI. When the problem is single objective this are not stored
 *      automatically but show the result of all algorithm execution.
 *   </li>
 *   <li>
 *       Set up experimentBaseDirectory and referenceFrontDirectory only store solution automatically in disk when the problem to resolve
 *       is set up has multiobjective and show the final pareto front in the GUI. When the problem is single objective this are not stored
 *       automatically but show the result of all algorithm execution.
 *   </li>
 *   <li>
 *        If experimentBaseDirectory and referenceFrontDirectory are not set up for the multiobjective problem the result of
 *        each algorithm will be stored in memory (RAM). Where all algorithm finish the execution the final pareto front will be
 *        showned.
 *   </li>
 *   <li>
 *    	Each algorithm, when the problem is multiobjective, will returned a pareto front of that execution.
 *    	The final pareto front is created joinning each pareto front and get the non-dominated solutions of the final set.
 *   </li>
 * </ul>
 *
 *
 * @param <S>
 */
public final class ExperimentBuilder<S extends Solution<?>> {
    @NotNull private final String experimentName;
    @Nullable private List<ExperimentAlgorithm<S>> algorithmList;
    @Nullable private List<ExperimentProblem<S>> problemList;
    @NotNull private String referenceFrontDirectory;
    @NotNull private String experimentBaseDirectory;
    @NotNull private String objectiveOutputFileName;
    @NotNull private String variablesOutputFileName;
    private int independentRuns;

    /**
     * Constructor.
     * @param experimentName The experiment name.
     * @throws NullPointerException if experimentName is null.
     * @throws IllegalArgumentException if experimentName is a empty string.
     */
    public ExperimentBuilder(@NotNull String experimentName) {
        Objects.requireNonNull(experimentName);
        if (experimentName.isEmpty()){
            throw new IllegalArgumentException("experimentName can't be empty string");
        }
        this.experimentName = experimentName;
        this.independentRuns = 1;
        this.objectiveOutputFileName = "FUN";
        this.variablesOutputFileName = "VAR";
        this.referenceFrontDirectory = "";
        this.experimentBaseDirectory = "";
    }

    /**
     * Set the algorithm list. This method copy the elements of received list to a new one.
     * @param algorithmList the algorithm list or an empty list.
     * @return this object.
     * @throws NullPointerException if algorithmList is null
     */
    public @NotNull ExperimentBuilder<S> setAlgorithmList(@NotNull List<ExperimentAlgorithm<S>> algorithmList) {
        Objects.requireNonNull(algorithmList);
        this.algorithmList = new ArrayList<>(algorithmList);

        return this;
    }

    /**
     * Set the problem list.
     * @param problemList the problem list or an empty list.
     * @return this object.
     * @throws NullPointerException if problemList is null.
     */
    public @NotNull ExperimentBuilder<S> setProblemList(@NotNull List<ExperimentProblem<S>> problemList) {
        Objects.requireNonNull(problemList);
        this.problemList = problemList;

        return this;
    }

    /**
     * Set the experiment base directory where result will be stored.
     * @param experimentBaseDirectory the directory
     * @return this object
     * @throws  if experimentBaseDirectory is null
     */
    public @NotNull ExperimentBuilder<S> setExperimentBaseDirectory(@NotNull String experimentBaseDirectory) {
        Objects.requireNonNull(experimentBaseDirectory);
        this.experimentBaseDirectory = experimentBaseDirectory + "/" + experimentName;

        return this;
    }

    /**
     * Set the reference front directory (pareto front directory) where result will be save when the problem will be multiobjective.
     * @param referenceFrontDirectory the reference front directory
     * @return this object
     * @throws NullPointerException if referenceFrontDirectory is null.
     */
    public @NotNull ExperimentBuilder<S> setReferenceFrontDirectory(@NotNull String referenceFrontDirectory) {
        Objects.requireNonNull(referenceFrontDirectory);
        this.referenceFrontDirectory = referenceFrontDirectory;

        return this;
    }

    /**
     * Set the file name where the objectives will be stored. If the value received is a empty string the default value is used.
     * @param objectiveOutputFileName the objective output file name
     * @return this objects
     * @throws NullPointerException if referenceFrontDirectory is null.
     */
    public @NotNull ExperimentBuilder<S> setObjectiveOutputFileName(@NotNull String objectiveOutputFileName) {
        Objects.requireNonNull(objectiveOutputFileName);
        if (objectiveOutputFileName.isEmpty()){
            objectiveOutputFileName = "FUN";
        }
        this.objectiveOutputFileName = objectiveOutputFileName;

        return this;
    }

    /**
     * Set the file name where the objectives will be stored. If the value received is a empty string the default value is used.
     * @param variablesOutputFileName the objective output file name
     * @return this objects
     * @throws NullPointerException if referenceFrontDirectory is null.
     */
    public @NotNull ExperimentBuilder<S> setVariablesOutputFileName(@NotNull String variablesOutputFileName) {
        Objects.requireNonNull(variablesOutputFileName);
        if (variablesOutputFileName.isEmpty()){
            variablesOutputFileName = "VAR";
        }
        this.variablesOutputFileName = variablesOutputFileName;

        return this;
    }

    /**
     * Set the number of independent run.
     * @param independentRuns number of independent run.
     * @return this object.
     *
     */
    public @NotNull ExperimentBuilder<S> setIndependentRuns(int independentRuns) {
        if (independentRuns <= 0){
            throw new IllegalArgumentException("The number of independent run can't be less or equal than 0 but was " + independentRuns);
        }
        this.independentRuns = independentRuns;

        return this;
    }

    /**
     * Build the experiment.
     * @return the experiment.
     */
    public @NotNull Experiment<S> build() {
        return new Experiment<S>(this);
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
        if (algorithmList != null) {
            return algorithmList;
        }
        return Collections.emptyList();
    }

    /**
     * Get the problem list.
     * @return the problem list or a empty list if it isn't set up.
     */
    public @NotNull List<ExperimentProblem<S>> getProblemList() {
        if (problemList != null) {
            return problemList;
        }
        return Collections.emptyList();
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

}
