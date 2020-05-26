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

import controller.utils.MultiObjectiveExperimentTask;
import exception.ApplicationException;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentComponent;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.experiment.util.ObservableStringBuffer;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.utils.archive.impl.NonDominatedSolutionListArchive;
import model.metaheuristic.utils.io.SolutionListOutput;
import model.metaheuristic.utils.solutionattribute.SolutionAttribute;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

/**
 * This class computes a reference Pareto front from a set of files. Once the
 * algorithms of an experiment have been executed through running an instance of
 * class {@link MultiObjectiveExperimentTask}, all the obtained fronts of all the
 * algorithms are gathered per problem; then, the dominated solutions are
 * removed and the final result is a file per problem containing the reference
 * Pareto front.
 * <p>
 * <p>
 * If {@link Experiment#getExperimentBaseDirectory()}  is a empty string (is not set up) the solution will be collected directly
 * of each algorithms (from memory).
 * <p>
 * If {@link Experiment#getReferenceFrontDirectory()}  is a empty string (is not set up) the final pareto front will not be saved automatically
 * in disk.
 *
 * <strong>Notes:</strong>
 * <p>
 * <ul>
 *  <li>
 *      When the solution are taked directly of algorithms likely the algorithms will realize a sort operation that has a time cost.
 *      If the solution is read of the file so the solution has a cost in IO operation but not in sorting.
 *  </li>
 *  <li>
 *        If run in memory is true so solution are get of algorithms and are not save automatically in disk.
 *  </li>
 * </ul>
 */
public class GenerateReferenceParetoFront implements ExperimentComponent {
    @NotNull
    private final Experiment<?> experiment;
    private final boolean runInMemory;
    private final ObservableStringBuffer taskLog;
    private List<? extends Solution<?>> paretoFront;


    /**
     * Constructor.
     *
     * @param experimentConfiguration the experiment
     * @param taskLog                 the log where print the status of execution
     * @param runInMemory             this parameter indicate if algorithm is executed in memory, i.e. not read of disk.
     * @throws NullPointerException if experimentConfiguration or taskLog is null
     */
    public GenerateReferenceParetoFront(@NotNull Experiment<?> experimentConfiguration, @NotNull ObservableStringBuffer taskLog, boolean runInMemory) {
        this.taskLog = taskLog;
        Objects.requireNonNull(experimentConfiguration);
        Objects.requireNonNull(taskLog);
        this.runInMemory = runInMemory;
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
     * The method read the solution of disk if experiment has set up the {@link Experiment#getExperimentBaseDirectory()}
     * and creates a output directory to save the pareto front if is set up {@link Experiment#getReferenceFrontDirectory()}}.
     * <p>
     * If {@link Experiment#getExperimentBaseDirectory()} is not set up so the solution is read directly on memory.
     * <p>
     * If {@link Experiment#getReferenceFrontDirectory()} is not set up so the solution isn't saved.
     */
    @Override
    public void run() throws IOException {

        String outputDirectoryName = experiment.getReferenceFrontDirectory();
        String inputDirectory = experiment.getExperimentBaseDirectory();
        boolean hasReferenceDirectory = !outputDirectoryName.isEmpty();
        boolean hasInputDirectory = !inputDirectory.isEmpty();

        // if has input directory so it read the solution by disk, so only need one time of algorithm to recover the tag.
        if (hasInputDirectory && !this.runInMemory) {
            experiment.removeDuplicatedAlgorithms();
        }
        ExperimentProblem<?> problem = experiment.getProblem();

        if (problem != null) {
            NonDominatedSolutionListArchive<Solution<?>> nonDominatedSolutionArchive = new NonDominatedSolutionListArchive<>();

            for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
                //read solution of disk
                if (hasInputDirectory && !this.runInMemory) {
                    String problemDirectory = inputDirectory + "/data/"
                            + algorithm.getAlgorithmTag() + "/" + problem.getTag();

                    for (int i = 0; i < experiment.getIndependentRuns(); i++) {
                        String frontFileName = problemDirectory + "/" + experiment.getObjectiveOutputFileName() + i
                                + ".tsv";
                        String setFileName = problemDirectory + "/" + experiment.getVariablesOutputFileName() + i + ".tsv";

                        List<ObjectSolution> solutionList = readSolutionFromFiles(frontFileName, setFileName);
                        SolutionAttribute<ObjectSolution, String> solutionAttribute = new SolutionAttribute<>();

                        for (ObjectSolution solution : solutionList) {
                            // Save algorithm tag
                            solutionAttribute.setAttribute(solution, algorithm.getAlgorithmTag());
                            nonDominatedSolutionArchive.add(solution);
                        }
                    }
                } else {
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
            }


            this.paretoFront = nonDominatedSolutionArchive.getSolutionList();

            if (hasReferenceDirectory && !this.runInMemory) {
                createOutputDirectory(outputDirectoryName);

                String referenceFrontFileName = outputDirectoryName + "/" + problem.getTag() + ".pf";
                new SolutionListOutput(this.paretoFront).printObjectivesToFile(referenceFrontFileName);

                writeFilesWithTheSolutionsContributedByEachAlgorithm(outputDirectoryName, problem, this.paretoFront);
            }
        }
    }

    /**
     * Read the objectives and variables of a FUN and VAR file and return a list with solution.
     *
     * @param frontFileName file path to FUN file
     * @param setFileName   file path to VAR file
     * @return the list of solution
     * @throws IOException           if I/O errors occurs
     * @throws FileNotFoundException if the named file does not exist,is a directory
     *                               rather than a regular file,or for some other
     *                               reason cannot be opened for reading.
     */
    private List<ObjectSolution> readSolutionFromFiles(String frontFileName, String setFileName)
            throws FileNotFoundException, IOException {

        List<ObjectSolution> list = new ArrayList<>();
        int numberOfObjectives = 0;
        int numberOfVariables = 0;

        try (FileReader frontFile = new FileReader(frontFileName);
             BufferedReader frontBuffer = new BufferedReader(frontFile);
             FileReader setFile = new FileReader(setFileName);
             BufferedReader setBuffer = new BufferedReader(setFile)) {

            String frontLine = null;
            String setLine = null;

            while ((frontLine = frontBuffer.readLine()) != null) {
                setLine = setBuffer.readLine();
                if (setLine == null) {
                    throw new ApplicationException("Missing decision variables in file " + setFileName);
                }

                // split the line in tokens
                StringTokenizer frontTokenizer = new StringTokenizer(frontLine);
                StringTokenizer setTokenizer = new StringTokenizer(setLine);

                // initialize the number of objectives
                if (numberOfObjectives == 0) {
                    numberOfObjectives = frontTokenizer.countTokens();

                } else if (numberOfObjectives != frontTokenizer.countTokens()) {
                    throw new ApplicationException("Invalid number of objectives in a line. Expected "
                            + numberOfObjectives + " objectives but received " + frontTokenizer
                            .countTokens());
                }
                // initialize the number of variables
                if (numberOfVariables == 0) {
                    numberOfVariables = setTokenizer.countTokens();

                } else if (numberOfVariables != setTokenizer.countTokens()) {
                    throw new ApplicationException("Invalid number of variables in a line. Expected "
                            + numberOfVariables + " objectives but received " + setTokenizer
                            .countTokens());
                }

                // try create the object more appropiated, i.e. if variable are integer create a
                // IntegerSolution.
                ObjectSolution objectSolution = new ObjectSolution(numberOfObjectives, numberOfVariables);

                int i = 0;
                while (frontTokenizer.hasMoreTokens()) {
                    double value = Double.parseDouble(frontTokenizer.nextToken());
                    objectSolution.setObjective(i++, value);
                }

                i = 0;
                while (setTokenizer.hasMoreTokens()) {
                    Object value = setTokenizer.nextToken();
                    objectSolution.setVariable(i++, value);
                }

                list.add(objectSolution);
            }
        }

        return list;
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
            taskLog.println("- Creating " + outputDirectoryName + ". Status = " + result);
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
