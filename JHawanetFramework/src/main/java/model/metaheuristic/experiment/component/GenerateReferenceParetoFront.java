package model.metaheuristic.experiment.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import exception.ApplicationException;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentComponent;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.utils.archive.impl.NonDominatedSolutionListArchive;
import model.metaheuristic.utils.io.SolutionListOutput;
import model.metaheuristic.utils.solutionattribute.SolutionAttribute;

/**
 * This class computes a reference Pareto front from a set of files. Once the
 * algorithms of an experiment have been executed through running an instance of
 * class {@link ExecuteAlgorithms}, all the obtained fronts of all the
 * algorithms are gathered per problem; then, the dominated solutions are
 * removed and the final result is a file per problem containing the reference
 * Pareto front.
 *
 * By default, the files are stored in a directory called "referenceFront",
 * which is located in the experiment base directory. Each front is named
 * following the scheme "problemName.rf".
 *
 * <pre>
 * Base on code from https://github.com/jMetal/jMetal
 * 
 * Copyright <2017> <Antonio J. Nebro, Juan J. Durillo>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. © 2019 GitHub, Inc.
 * </pre>
 */
public class GenerateReferenceParetoFront implements ExperimentComponent {
	private final Experiment<?> experiment;
	private List<ObjectSolution> paretoFront;

	public GenerateReferenceParetoFront(Experiment<?> experimentConfiguration) {
		this.experiment = experimentConfiguration;

		experiment.removeDuplicatedAlgorithms();
	}

	/**
	 * The run() method creates de output directory and compute the fronts
	 */
	@Override
	public void run() throws IOException {
		String outputDirectoryName = experiment.getReferenceFrontDirectory();
		createOutputDirectory(outputDirectoryName);

		List<String> referenceFrontFileNames = new LinkedList<>();
		for (ExperimentProblem<?> problem : experiment.getProblemList()) {
			NonDominatedSolutionListArchive<ObjectSolution> nonDominatedSolutionArchive = new NonDominatedSolutionListArchive<>();

			for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
				String problemDirectory = experiment.getExperimentBaseDirectory() + "/data/"
						+ algorithm.getAlgorithmTag() + "/" + problem.getTag();

				for (int i = 0; i < experiment.getIndependentRuns(); i++) {
					String frontFileName = problemDirectory + "/" + experiment.getOutputParetoFrontFileName() + i
							+ ".tsv";
					String setFileName = problemDirectory + "/" + experiment.getOutputParetoSetFileName() + i + ".tsv";

					List<ObjectSolution> solutionList = readSolutionFromFiles(frontFileName, setFileName);
					SolutionAttribute<ObjectSolution, String> solutionAttribute = new SolutionAttribute<ObjectSolution, String>();

					for (ObjectSolution solution : solutionList) {
						solutionAttribute.setAttribute(solution, algorithm.getAlgorithmTag());
						nonDominatedSolutionArchive.add(solution);
					}
				}
			}

			String referenceSetFileName = outputDirectoryName + "/" + problem.getTag() + ".pf";
			referenceFrontFileNames.add(problem.getTag() + ".pf");

			this.paretoFront = nonDominatedSolutionArchive.getSolutionList();
			new SolutionListOutput(this.paretoFront).printObjectivesToFile(referenceSetFileName);

			writeFilesWithTheSolutionsContributedByEachAlgorithm(outputDirectoryName, problem, this.paretoFront);

		}

	}

	/**
	 * Read the objectives of a FUN file and return a list with solution.
	 * 
	 * @param frontFileName file path
	 * @param problem
	 * @return the list of solution
	 * @throws IOException           if I/O errors occurs
	 * @throws FileNotFoundException if the named file does not exist,is a directory
	 *                               rather than a regular file,or for some other
	 *                               reason cannot be opened for reading.
	 */
	private List<ObjectSolution> readSolutionFromFiles(String frontFileName, String setFileName)
			throws FileNotFoundException, IOException {

		List<ObjectSolution> list = new ArrayList<ObjectSolution>();
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
							+ numberOfObjectives + " objectives but received " + frontTokenizer.countTokens());
				}
				// initialize the number of variables
				if (numberOfVariables == 0) {
					numberOfVariables = setTokenizer.countTokens();

				} else if (numberOfVariables != setTokenizer.countTokens()) {
					throw new ApplicationException("Invalid number of variables in a line. Expected "
							+ numberOfVariables + " objectives but received " + setTokenizer.countTokens());
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
	private File createOutputDirectory(String outputDirectoryName) {
		File outputDirectory;
		outputDirectory = new File(outputDirectoryName);
		if (!outputDirectory.exists()) {
			boolean result = new File(outputDirectoryName).mkdir();
			throw new ApplicationException("Creating " + outputDirectoryName + ". Status = " + result);
		}

		return outputDirectory;
	}

	private void writeFilesWithTheSolutionsContributedByEachAlgorithm(String outputDirectoryName,
			ExperimentProblem<?> problem, List<ObjectSolution> nonDominatedSolutions) throws IOException {

		SolutionAttribute<ObjectSolution, String> solutionAttribute = new SolutionAttribute<ObjectSolution, String>();

		for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
			List<ObjectSolution> solutionsPerAlgorithm = new ArrayList<>();
			for (ObjectSolution solution : nonDominatedSolutions) {
				if (algorithm.getAlgorithmTag().equals(solutionAttribute.getAttribute(solution))) {
					solutionsPerAlgorithm.add(solution);
				}
			}

			new SolutionListOutput(solutionsPerAlgorithm).printObjectivesToFile(
					outputDirectoryName + "/" + problem.getTag() + "." + algorithm.getAlgorithmTag() + ".pf");
		}
	}

	/**
	 * Get a solution list with the element of pareto front.
	 * 
	 * @return a list with element of pareto front or a empty list if there is no
	 *         element or {@link #run()} has not been executed
	 */
	public List<ObjectSolution> getReferenceToParetoFront() {
		if (this.paretoFront == null) {
			return new ArrayList<ObjectSolution>();
		}
		return this.paretoFront;
	}
}
