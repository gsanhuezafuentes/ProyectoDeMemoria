package model.metaheuristic.experiment.component;

import controller.multiobjectives.util.MultiObjectiveExperimentTask;
import model.metaheuristic.experiment.ExperimentComponent;
import model.metaheuristic.experiment.ExperimentSet;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.util.archive.impl.NonDominatedSolutionListArchive;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;
import model.metaheuristic.util.front.util.FrontUtils;
import model.metaheuristic.util.io.SolutionListOutput;
import model.metaheuristic.util.point.PointSolution;
import model.metaheuristic.util.solutionattribute.SolutionAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class computes a reference Pareto front from a set of files. Once the
 * algorithms of an experiment have been executed through running an instance of
 * class {@link MultiObjectiveExperimentTask}, all the obtained fronts of all the
 * algorithms are gathered per problem; then, the dominated solutions are
 * removed and the final result is a file per problem containing the reference
 * Pareto front.
 * <Strong>Notes:</Strong>
 * <p>
 * This class use the solution save in disk in temporary folder.
 * <p>
 * This class is used when compare algorithms using quality indicators.
 */
public class GenerateReferenceParetoFrontInDisk implements ExperimentComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateReferenceParetoFrontInDisk.class);
    private final ExperimentSet<?> experimentSet;

    public GenerateReferenceParetoFrontInDisk(ExperimentSet<?> experimentsConfiguration) {
        this.experimentSet = experimentsConfiguration;
        experimentSet.removeDuplicatedAlgorithms();
    }

    /**
     * The run() method creates de output directory and compute the fronts
     */
    @Override
    public void run() throws IOException {
        // All the experiment has the same reference front directory
        String outputDirectoryName = experimentSet.getExperimentBaseDirectory() + "/"
                + experimentSet.getExperimentList().get(0).getReferenceFrontDirectory();

        createOutputDirectory(outputDirectoryName);

        List<String> referenceFrontFileNames = new LinkedList<>();
        for (ExperimentProblem<?> problem : experimentSet.getExperimentProblems()) {
            NonDominatedSolutionListArchive<PointSolution> nonDominatedSolutionArchive =
                    new NonDominatedSolutionListArchive<PointSolution>();

            for (ExperimentAlgorithm<?> algorithm : experimentSet.getExperimentAlgorithms(problem)) {
                String problemDirectory = experimentSet.getExperimentBaseDirectory() + "/data/" +
                        algorithm.getAlgorithmTag() + "/" + problem.getTag();

                for (int i = 0; i < experimentSet.getIndependentRuns(); i++) {
                    // All experiment has the same objective output file name
                    String frontFileName = problemDirectory + "/" + experimentSet.getExperimentList().get(0).getObjectiveOutputFileName() +
                            i + ".csv";
                    Front front = new ArrayFront(frontFileName, ",");
                    List<PointSolution> solutionList = FrontUtils.convertFrontToSolutionList(front);
                    SolutionAttribute<PointSolution, String> solutionAttribute = new SolutionAttribute<PointSolution, String>();

                    for (PointSolution solution : solutionList) {
                        solutionAttribute.setAttribute(solution, algorithm.getAlgorithmTag());
                        nonDominatedSolutionArchive.add(solution);
                    }
                }
            }
            String referenceSetFileName = outputDirectoryName + "/" + problem.getTag() + ".csv";
            referenceFrontFileNames.add(problem.getTag() + ".csv");
            new SolutionListOutput(nonDominatedSolutionArchive.getSolutionList()).setSeparator(",")
                    .printObjectivesToFile(referenceSetFileName);

            writeFilesWithTheSolutionsContributedByEachAlgorithm(outputDirectoryName, problem,
                    nonDominatedSolutionArchive.getSolutionList());
        }

    }

    private File createOutputDirectory(String outputDirectoryName) {
        File outputDirectory;
        outputDirectory = new File(outputDirectoryName);
        if (!outputDirectory.exists()) {
            boolean result = new File(outputDirectoryName).mkdir();
           LOGGER.info("Creating " + outputDirectoryName + ". Status = " + result);
        }

        return outputDirectory;
    }

    private void writeFilesWithTheSolutionsContributedByEachAlgorithm(
            String outputDirectoryName, ExperimentProblem<?> problem,
            List<PointSolution> nonDominatedSolutions) throws IOException {
        SolutionAttribute<PointSolution, String> solutionAttribute = new SolutionAttribute<PointSolution, String>();

        for (ExperimentAlgorithm<?> algorithm : experimentSet.getExperimentAlgorithms(problem)) {
            List<PointSolution> solutionsPerAlgorithm = new ArrayList<>();
            for (PointSolution solution : nonDominatedSolutions) {
                if (algorithm.getAlgorithmTag().equals(solutionAttribute.getAttribute(solution))) {
                    solutionsPerAlgorithm.add(solution);
                }
            }

            new SolutionListOutput(solutionsPerAlgorithm).setSeparator(",")
                    .printObjectivesToFile(
                            outputDirectoryName + "/" + problem.getTag() + "." +
                                    algorithm.getAlgorithmTag() + ".csv");
        }
    }
}
