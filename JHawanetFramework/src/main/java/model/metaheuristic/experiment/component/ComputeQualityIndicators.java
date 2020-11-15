package model.metaheuristic.experiment.component;

import exception.ApplicationException;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentComponent;
import model.metaheuristic.experiment.ExperimentSet;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.qualityindicator.QualityIndicator;
import model.metaheuristic.qualityindicator.impl.GenericIndicator;
import model.metaheuristic.solution.Solution;
import model.metaheuristic.util.front.Front;
import model.metaheuristic.util.front.impl.ArrayFront;
import model.metaheuristic.util.front.util.FrontNormalizer;
import model.metaheuristic.util.front.util.FrontUtils;
import model.metaheuristic.util.point.PointSolution;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * This class computes the {@link QualityIndicator}s of an experiment. Once the algorithms of an
 * experiment have been executed through running an instance of class {@link controller.multiobjective.util.ExecutionIndicatorTask},
 * the list of indicators in obtained from the {@link ExperimentSet #getIndicatorsList()} method.
 * Then, for every combination algorithm + problem, the indicators are applied to all the FUN files and
 * the resulting values are store in a file called as {@link QualityIndicator #getName()}, which is located
 * in the same directory of the FUN files.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ComputeQualityIndicators<S extends Solution<?>> implements ExperimentComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputeQualityIndicators.class);
    private final ExperimentSet<S> experimentSet;

    public ComputeQualityIndicators(ExperimentSet<S> experimentSet) {
        this.experimentSet = experimentSet;
    }

    @Override
    public void run() throws IOException {
        experimentSet.removeDuplicatedAlgorithms();
        resetIndicatorFiles();

        for (GenericIndicator<S> indicator : experimentSet.getIndicatorList()) {
            LOGGER.info("Computing indicator: " + indicator.getName());
            for (Experiment<S> experiment : experimentSet.getExperimentList()) {
                // for each experiment only there is one algorithm after call experimentSet.removeDuplicatedAlgorithms.
                for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
                    String algorithmDirectory;
                    algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + algorithm.getAlgorithmTag();

                    ExperimentProblem<S> problem = experiment.getProblem();
                    String problemDirectory = algorithmDirectory + "/" + problem.getTag();

                    String referenceFrontDirectory = experiment.getExperimentBaseDirectory() + "/" + experiment.getReferenceFrontDirectory();
                    String referenceFrontName = referenceFrontDirectory + "/" + problem.getTag() + ".csv";
                    LOGGER.debug("indicator - {} : algorithm - {} : reference {}\n", indicator.getName(), algorithm.getAlgorithmTag(), referenceFrontName);

                    LOGGER.info("RF: " + referenceFrontName);

                    Front referenceFront = new ArrayFront(referenceFrontName, ",");

                    FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
                    Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);

                    String qualityIndicatorFile = problemDirectory + "/" + indicator.getName();

                    indicator.setReferenceParetoFront(normalizedReferenceFront);

                    double[] indicatorValues = new double[experiment.getIndependentRuns()];
                    IntStream.range(0, experiment.getIndependentRuns()).forEach(run -> {
                        String frontFileName = problemDirectory + "/" +
                                experiment.getObjectiveOutputFileName() + run + ".csv";
                        Front front = null;
                        try {
                            front = new ArrayFront(frontFileName, ",");
                        } catch (FileNotFoundException e) {
                            throw new ApplicationException("The file " + frontFileName + " can't be found.");
                        } catch (IOException e) {
                            throw new ApplicationException("Error reading the file " + frontFileName, e);
                        }
                        Front normalizedFront = frontNormalizer.normalize(front);
                        List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(normalizedFront);
                        Double indicatorValue = indicator.evaluate((List<S>) normalizedPopulation);
                        LOGGER.info(indicator.getName() + ": " + indicatorValue);
                        indicatorValues[run] = indicatorValue;
                    });

                    for (double indicatorValue : indicatorValues) {
                        writeQualityIndicatorValueToFile(indicatorValue, qualityIndicatorFile);
                    }

                }
            }
        }
        findBestIndicatorFronts(experimentSet);
        writeSummaryFile(experimentSet);
    }

    /**
     * Write the quality indicator value to file
     *
     * @param indicatorValue       the indicator value
     * @param qualityIndicatorFile the quality indicator file.
     * @throws IOException "Error writing indicator file"
     */
    private void writeQualityIndicatorValueToFile(Double indicatorValue, String qualityIndicatorFile) throws IOException {

        try (FileWriter os = new FileWriter(qualityIndicatorFile, true)) {
            os.write("" + indicatorValue + "\n");
        }
    }

    public void findBestIndicatorFronts(ExperimentSet<?> experimentSet) throws IOException {
        for (GenericIndicator<?> indicator : experimentSet.getIndicatorList()) {
            for (Experiment<?> experiment : experimentSet.getExperimentList()) {
                for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
                    String algorithmDirectory;
                    algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" +
                            algorithm.getAlgorithmTag();

                    ExperimentProblem<?> problem = experiment.getProblem();
                    String indicatorFileName =
                            algorithmDirectory + "/" + problem.getTag() + "/" + indicator.getName();
                    Path indicatorFile = Paths.get(indicatorFileName);

                    List<String> fileArray;
                    fileArray = Files.readAllLines(indicatorFile, StandardCharsets.UTF_8);

                    List<Pair<Double, Integer>> list = new ArrayList<>();

                    for (int i = 0; i < fileArray.size(); i++) {
                        Pair<Double, Integer> pair = new ImmutablePair<>(Double.parseDouble(fileArray.get(i)), i);
                        list.add(pair);
                    }

                    list.sort(Comparator.comparingDouble(pair -> Math.abs(pair.getLeft())));
                    String bestFunFileName;
                    String bestVarFileName;
                    String medianFunFileName;
                    String medianVarFileName;

                    String outputDirectory = algorithmDirectory + "/" + problem.getTag();

                    bestFunFileName = outputDirectory + "/BEST_" + indicator.getName() + "_FUN.csv";
                    bestVarFileName = outputDirectory + "/BEST_" + indicator.getName() + "_VAR.csv";
                    medianFunFileName = outputDirectory + "/MEDIAN_" + indicator.getName() + "_FUN.csv";
                    medianVarFileName = outputDirectory + "/MEDIAN_" + indicator.getName() + "_VAR.csv";
                    if (indicator.isTheLowerTheIndicatorValueTheBetter()) {
                        String bestFunFile = outputDirectory + "/" +
                                experiment.getObjectiveOutputFileName() + list.get(0).getRight() + ".csv";
                        String bestVarFile = outputDirectory + "/" +
                                experiment.getVariablesOutputFileName() + list.get(0).getRight() + ".csv";

                        Files.copy(Paths.get(bestFunFile), Paths.get(bestFunFileName), REPLACE_EXISTING);
                        Files.copy(Paths.get(bestVarFile), Paths.get(bestVarFileName), REPLACE_EXISTING);
                    } else {
                        String bestFunFile = outputDirectory + "/" +
                                experiment.getObjectiveOutputFileName() + list.get(list.size() - 1).getRight() + ".csv";
                        String bestVarFile = outputDirectory + "/" +
                                experiment.getVariablesOutputFileName() + list.get(list.size() - 1).getRight() + ".csv";

                        Files.copy(Paths.get(bestFunFile), Paths.get(bestFunFileName), REPLACE_EXISTING);
                        Files.copy(Paths.get(bestVarFile), Paths.get(bestVarFileName), REPLACE_EXISTING);
                    }

                    int medianIndex = list.size() / 2;
                    String medianFunFile = outputDirectory + "/" +
                            experiment.getObjectiveOutputFileName() + list.get(medianIndex).getRight() + ".csv";
                    String medianVarFile = outputDirectory + "/" +
                            experiment.getVariablesOutputFileName() + list.get(medianIndex).getRight() + ".csv";

                    Files.copy(Paths.get(medianFunFile), Paths.get(medianFunFileName), REPLACE_EXISTING);
                    Files.copy(Paths.get(medianVarFile), Paths.get(medianVarFileName), REPLACE_EXISTING);

                }
            }
        }
    }

    /**
     * Deletes the files containing the indicator values if the exist.
     */
    private void resetIndicatorFiles() {
        for (GenericIndicator<S> indicator : experimentSet.getIndicatorList()) {
            for (Experiment<S> experiment : experimentSet.getExperimentList()) {
                // for each experiment only there is one algorithm after call experimentSet.removeDuplicatedAlgorithms.
                for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
                    ExperimentProblem<?> problem = experiment.getProblem();
                    String algorithmDirectory;
                    algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + algorithm.getAlgorithmTag();
                    String problemDirectory = algorithmDirectory + "/" + problem.getTag();
                    String qualityIndicatorFile = problemDirectory + "/" + indicator.getName();

                    resetFile(qualityIndicatorFile);
                }
            }
        }
    }

    /**
     * Deletes a file or directory if it does exist
     *
     * @param file the file path to reset.
     */
    private void resetFile(String file) {
        File f = new File(file);
        if (f.exists()) {
            LOGGER.info("Already existing file " + file);

            if (f.isDirectory()) {
                LOGGER.info("Deleting directory " + file);
                if (f.delete()) {
                    LOGGER.info("Directory successfully deleted.");
                } else {
                    LOGGER.info("Error deleting directory.");
                }
            } else {
                LOGGER.info("Deleting file " + file);
                if (f.delete()) {
                    LOGGER.info("File successfully deleted.");
                } else {
                    LOGGER.info("Error deleting file.");
                }
            }
        } else {
            LOGGER.info("File " + file + " does NOT exist.");
        }
    }

    /**
     * Write the summary file.
     *
     * @param experimentSet the set of experiment.
     * @throws IOException Error writing indicator file
     */
    private void writeSummaryFile(ExperimentSet<S> experimentSet) throws IOException {
        LOGGER.info("Writing org.uma.jmetal.experiment summary file");
        String headerOfCSVFile = "Algorithm,Problem,IndicatorName,ExecutionId,IndicatorValue";
        String csvFileName = this.experimentSet.getExperimentBaseDirectory() + "/QualityIndicatorSummary.csv";
        resetFile(csvFileName);

        try (FileWriter os = new FileWriter(csvFileName, true)) {
            os.write("" + headerOfCSVFile + "\n");

            // All experiment should have the same base directory
            for (GenericIndicator<?> indicator : experimentSet.getIndicatorList()) {
                for (Experiment<S> experiment : experimentSet.getExperimentList()) {
                    for (ExperimentAlgorithm<?> algorithm : experiment.getAlgorithmList()) {
                        String algorithmDirectory;
                        algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" +
                                algorithm.getAlgorithmTag();

                        ExperimentProblem<?> problem = experiment.getProblem();
                        String indicatorFileName =
                                algorithmDirectory + "/" + problem.getTag() + "/" + indicator.getName();
                        Path indicatorFile = Paths.get(indicatorFileName);
                        LOGGER.info("Reading indicator file in {}", indicatorFileName);

                        List<String> fileArray;
                        fileArray = Files.readAllLines(indicatorFile, StandardCharsets.UTF_8);

                        // Should has the same number of elements that independent run in each experiment
                        for (int i = 0; i < fileArray.size(); i++) {
                            String row = algorithm.getAlgorithmTag() + "," + problem.getTag() + "," + indicator.getName() + "," + i + "," + fileArray.get(i);
                            os.write("" + row + "\n");
                        }
                    }
                }
            }
        }
    }
}

