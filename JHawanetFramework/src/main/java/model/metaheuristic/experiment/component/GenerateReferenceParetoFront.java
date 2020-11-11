/* Base on code from https://github.com/jMetal/jMetal
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
package model.metaheuristic.experiment.component;

import controller.multiobjectives.util.MultiObjectiveExperimentTask;
import exception.ApplicationException;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentComponent;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.experiment.util.ObservableStringBuffer;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.archive.impl.NonDominatedSolutionListArchive;
import model.metaheuristic.util.io.SolutionListOutput;
import model.metaheuristic.util.solutionattribute.SolutionAttribute;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class computes a reference Pareto front from a set of files. Once the
 * algorithms of an experiment have been executed through running an instance of
 * class {@link MultiObjectiveExperimentTask}, all the obtained fronts of all the
 * algorithms are gathered per problem; then, the dominated solutions are
 * removed and the final result is a file per problem containing the reference
 * Pareto front.
 * <p>
 * If {@link Experiment#getReferenceFrontDirectory()}  is a empty string (is not set up) the final pareto front will not be saved automatically
 * in disk.
 *
 */
public class GenerateReferenceParetoFront implements ExperimentComponent {
    @NotNull
    private final Experiment<?> experiment;
    private final ObservableStringBuffer taskLog;
    private List<? extends Solution<?>> paretoFront;


    /**
     * Constructor.
     *
     * @param experimentConfiguration the experiment
     * @param taskLog                 the log where print the status of execution
     * @throws NullPointerException if experimentConfiguration or taskLog is null
     */
    public GenerateReferenceParetoFront(@NotNull Experiment<?> experimentConfiguration, @NotNull ObservableStringBuffer taskLog) {
        this.taskLog = taskLog;
        Objects.requireNonNull(experimentConfiguration);
        Objects.requireNonNull(taskLog);
        this.experiment = experimentConfiguration;
    }

    /**
     * Get a solution list with the element of pareto front.
     *
     * @return a list with element of pareto front or a empty list if there is no
     * element or {@link #run()} has not been executed.
     */
    public List<? extends Solution<?>> getReferenceToParetoFront() {
        if (this.paretoFront == null) {
            return Collections.emptyList();
        }
        return this.paretoFront;
    }

    /**
     * This method calculate the final pareto front for each problem register in experiment.
     * <p>
     * The method read the solution of experiment and creates a output directory to save the pareto front
     * if is set up {@link Experiment#getReferenceFrontDirectory()}}.
     * <p>
     * If {@link Experiment#getReferenceFrontDirectory()} is not set up so the solution isn't saved.
     */
    @Override
    public void run() throws IOException {

        String outputDirectoryName = experiment.getReferenceFrontDirectory();
        boolean hasReferenceDirectory = !outputDirectoryName.isEmpty();

        ExperimentProblem<?> problem = experiment.getProblem();

        if (problem != null) {
            NonDominatedSolutionListArchive<Solution<?>> nonDominatedSolutionArchive = new NonDominatedSolutionListArchive<>();

            for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
                for (int i = 0; i < experiment.getIndependentRuns(); i++) {
                    List<? extends Solution<?>> solutionList = algorithm.getResult();
                    SolutionAttribute<Solution<?>, String> solutionAttribute = new SolutionAttribute<>();

                    for (Solution<?> solution : solutionList) {
                        // Save algorithm tag
                        solutionAttribute.setAttribute(solution, algorithm.getAlgorithmTag());
                        nonDominatedSolutionArchive.add(solution);
                    }
                }
            }


            this.paretoFront = nonDominatedSolutionArchive.getSolutionList();

            // If has a reference directory save the solution in disk.
            if (hasReferenceDirectory) {
                createOutputDirectory(outputDirectoryName);
                taskLog.println("- Saving final pareto front in " + outputDirectoryName);

                String referenceFrontFileName = outputDirectoryName + "/" + problem.getTag() + ".pf";
                new SolutionListOutput(this.paretoFront).printObjectivesToFile(referenceFrontFileName);

                writeFilesWithTheSolutionsContributedByEachAlgorithm(outputDirectoryName, problem, this.paretoFront);
            }
        }
    }

    /**
     * Create a new directory to save solution
     *
     * @param outputDirectoryName the directory name
     * @return the created File
     * @throws ApplicationException if the directory cannot be created even if there
     *                              is no directory with that name
     */
    @SuppressWarnings("UnusedReturnValue")
    private File createOutputDirectory(String outputDirectoryName) {
        File outputDirectory = new File(outputDirectoryName);
        if (!outputDirectory.exists()) {
            boolean result = new File(outputDirectoryName).mkdirs();
            taskLog.println("- Creating " + outputDirectory.getAbsolutePath() + ". Status = " + result);
        }

        return outputDirectory;
    }

    /**
     * Write the final pareto front for a specific problem
     *
     * @param outputDirectoryName   the output directory
     * @param problem               the problem
     * @param nonDominatedSolutions the non-dominated solution
     * @throws IOException if IO error happen.
     */
    private void writeFilesWithTheSolutionsContributedByEachAlgorithm(String outputDirectoryName,
                                                                      ExperimentProblem<?> problem,
                                                                      List<? extends Solution<?>> nonDominatedSolutions) throws
            IOException {

        SolutionAttribute<Solution<?>, String> solutionAttribute = new SolutionAttribute<>();

        for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
            List<Solution<?>> solutionsPerAlgorithm = new ArrayList<>();
            for (Solution<?> solution : nonDominatedSolutions) {
                // compare the algorithm tag of algorithm with algorithm task of solution
                if (algorithm.getAlgorithmTag().equals(solutionAttribute.getAttribute(solution))) {
                    solutionsPerAlgorithm.add(solution);
                }
            }

            new SolutionListOutput(solutionsPerAlgorithm).printObjectivesToFile(
                    outputDirectoryName + "/" + problem.getTag() + "." + algorithm.getAlgorithmTag() + ".pf");
        }
    }

}
