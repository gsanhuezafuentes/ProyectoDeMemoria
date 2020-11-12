package model.metaheuristic.experiment;

import exception.InvalidConditionException;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.qualityindicator.impl.GenericIndicator;
import model.metaheuristic.solution.Solution;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class save a list of experiment and the indicators used to evaluate.
 * <p>
 * <Strong>Note:</Strong> <p>
 * * The experiment list only has to have multiobjective algorithms.<br>
 * * All experiments in experiment list has to have the same base directory. <br>
 * * All experiments in experiment list has to have the same number of independent run. <br>
 * * All problems should be resolved with the same algorithm. For example: Problem1 resolved with NSGAII and SPA2; Problem2 resolved with NSGAII and SPA2.
 * if it isn't the case could be a error when compute indicators or while generate the results of this.
 */
/*This class is use with indicators.*/
public class ExperimentSet<S extends Solution<?>> {
    private @NotNull
    final List<Experiment<S>> experimentList;
    private @NotNull
    final List<GenericIndicator<S>> indicatorList;

    /**
     * Contructor.
     *
     * @param experimentList the list with the experiment to execute.
     * @throws NullPointerException     if experimentList or indicatorsList is null.
     * @throws IllegalArgumentException if experiment list or indicatorsList is null.
     */
    public ExperimentSet(@NotNull List<Experiment<S>> experimentList, @NotNull List<GenericIndicator<S>> indicatorsList) {
        Objects.requireNonNull(experimentList);
        Objects.requireNonNull(indicatorsList);
        if (experimentList.isEmpty()) {
            throw new IllegalArgumentException("The experiment list is empty.");
        }
        if (indicatorsList.isEmpty()) {
            throw new IllegalArgumentException("The indicator list is empty.");
        }
        this.experimentList = Collections.unmodifiableList(experimentList);
        this.indicatorList = Collections.unmodifiableList(indicatorsList);
    }

    /**
     * Return a unmodifiable list.
     *
     * @return the experiment list.
     */
    public @NotNull List<Experiment<S>> getExperimentList() {
        return experimentList;
    }

//    /**
//     * Set the indicator list to use.
//     *
//     * @param indicatorsList the indicators list
//     */
//    public void setIndicatorsList(List<GenericIndicator<S>> indicatorsList) {
//        Objects.requireNonNull(indicatorsList);
//        if (indicatorsList.isEmpty()) {
//            throw new IllegalArgumentException("The indicator list is empty.");
//        }
//        this.indicatorList = Collections.unmodifiableList(indicatorsList);
//    }

    /**
     * Get the indicators list as a unmodifiable list.
     *
     * @return the indicator list or a empty list if there isn't indicators.
     */
    public List<GenericIndicator<S>> getIndicatorList() {
        return indicatorList != null ? indicatorList : Collections.emptyList();
    }

    /**
     * The number of experiments in this set.
     *
     * @return the number of experiments.
     */
    public int getNumberOfExperiments() {
        return this.experimentList.size();
    }

    /**
     * Remove duplicated algorithms of each experiment.
     */
    public void removeDuplicatedAlgorithms() {
        experimentList.forEach((experiment) -> experiment.removeDuplicatedAlgorithms());
    }

    /**
     * Get experiment base directory.
     * <p>
     * <Strong>Notes:</Strong>
     * <p>
     * All experiment in this set should have the same experiments base directory.
     *
     * @return The experiment base directory of all problems.
     * @throws InvalidConditionException if all experiment in experiment set hasn't the same experiment base directory.
     */
    public String getExperimentBaseDirectory() {
        boolean isValid = this.experimentList.stream().map(Experiment::getExperimentBaseDirectory)
                .distinct().limit(2).count() == 1;
        if (!isValid) {
            throw new InvalidConditionException("All experiment in Experiment Set hasn't the same base directory.");
        }
        return this.experimentList.get(0).getExperimentBaseDirectory();
    }

    /**
     * Set experiment base directory of all experiments.
     * <p>
     * <Strong>Notes:</Strong>
     * <p>
     * All experiment in this set should have the same experiments base directory.
     *
     * @throws NullPointerException     if directory is null.
     * @throws IllegalArgumentException if directory is empty.
     */
    public void setExperimentBaseDirectory(String directory) {
        Objects.requireNonNull(directory);
        if (directory.isEmpty()) {
            throw new IllegalArgumentException("Directory is empty.");
        }
        this.experimentList.forEach(experiment -> experiment.setExperimentBaseDirectory(directory));
    }

    /**
     * Get an only one instance of each experiment problems.
     *
     * @return the list with all experiments problems without repeat elements.
     */
    public List<ExperimentProblem<S>> getExperimentProblems() {
        /*
         * Get all distincs problem based in problemTag.
         */
        HashSet<String> problemTags = new HashSet<>();
        List<ExperimentProblem<S>> problems = this.experimentList.stream().map(Experiment::getProblem).collect(Collectors.toList());
        problems.removeIf(eProblem -> !problemTags.add(eProblem.getTag()));
        return problems;
    }

    /**
     * Get all algorithms used to the specific problem.
     *
     * @param problem the problem used to search algorithms.
     * @return the list with all experiments problems without repeat elements.
     */
    public List<ExperimentAlgorithm<S>> getExperimentAlgorithms(ExperimentProblem<?> problem) {
        /*
         * Get all algorithms that resolve the same problem. Two problem are the same if has the same problemTag.
         */
        List<ExperimentAlgorithm<S>> algorithms = this.experimentList.stream()
                .filter(experiment -> experiment.getProblem().getTag().equals(problem.getTag()))
                .map(experiment -> experiment.getAlgorithmList().get(0)).collect(Collectors.toList());
        return algorithms;
    }

    /**
     * Get the number of different problems in set. The problems are different if hasn't the same problemTag.
     *
     * @return the number of different problems
     */
    public int getNumberOfProblems() {
        return getExperimentProblems().size();
    }

    /**
     * Get the number of different problems in set. The problems are different if hasn't the same problemTag.
     *
     * @return the number of different problems.
     */
    public int getNumberOfAlgorithms() {
        return getIndependentRuns() * experimentList.size();
    }

    /**
     * Get number of independent run. It has to be the same for all experiments.
     * return number of independent run.
     * return number of independent run of each experiment.
     *
     * @throws InvalidConditionException if the number of independent run isn't the same in all experiments.
     */
    public int getIndependentRuns() {
        Stream<Integer> distinct = this.experimentList.stream().map(Experiment::getIndependentRuns).distinct();
        if (distinct.count() != 1) {
            throw new InvalidConditionException("All experiments hasn't have the same number of independent run.");
        }
        return distinct.collect(Collectors.toList()).get(0);
    }

    /**
     * Close all resource of all experiments problems in each experiment.
     *
     * @throws Exception if there is a problem closing the resources.
     */
    public void closeProblemsResources() throws Exception {
        for (Experiment<S> experiment : this.experimentList) {
            experiment.getProblem().closeResources();
        }
    }

    /**
     * Check if elements in set are the correct.
     *
     * @throws InvalidConditionException if the experiment set is not valid.
     */
    public void validExperiments() {
        getIndependentRuns();
        getExperimentBaseDirectory();
    }
}
