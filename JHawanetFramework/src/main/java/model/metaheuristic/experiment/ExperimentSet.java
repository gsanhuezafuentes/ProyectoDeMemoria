package model.metaheuristic.experiment;

import exception.ApplicationException;
import exception.InvalidConditionException;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.qualityindicator.impl.GenericIndicator;
import model.metaheuristic.solution.Solution;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * This class save a list of experiment and the indicators used to evaluate.
 * <p>
 * <Strong>Note:</Strong> <p>
 * * The experiment list only has to have multiobjective algorithms.<br>
 * * All experiments in experiment list has to have the same base directory and it is initialized in {@link controller.multiobjective.indicator.util.ExecutionIndicatorTask}. <br>
 * * All experiments in experiment list has to have the same number of independent run. <br>
 * * All problems should be resolved with the same algorithm. For example: Problem1 resolved with NSGAII and SPA2; Problem2 resolved with NSGAII and SPA2.
 * if it isn't the case could be a error when compute indicators or while generate the results of this.
 * <p>
 * {@link #getExperimentList()} can return a empty list if you hasn't iterate using {@link #iterator()}. Iterate using {@link #iterator()} create the element of the
 * experiment list. If you go to the end of iterator (Iterator.hasNext == false) so the {@link #getExperimentList()} return the final elements.
 */
/*This class is use with indicators.*/
public class ExperimentSet<S extends Solution<?>> implements Iterable<Experiment<S>> {
    // This are a list of callback that create each experiment when is needed.
    private @NotNull
    final List<Callable<Experiment<S>>> callbackList;
    private @NotNull
    final List<GenericIndicator> indicatorList;

    // The list is created after browsing it using an iterator
    private @Nullable List<Experiment<S>> experimentList;

    /**
     * Contructor.
     *
     * @param callbackList   the list with the callback to create each experiment.
     * @param indicatorsList the list with the indicators to use.
     * @throws NullPointerException     if experimentList or indicatorsList is null.
     * @throws IllegalArgumentException if experiment list or indicatorsList is null.
     */
    public ExperimentSet(@NotNull List<Callable<Experiment<S>>> callbackList, @NotNull List<GenericIndicator> indicatorsList) {
        Objects.requireNonNull(callbackList);
        Objects.requireNonNull(indicatorsList);
        if (callbackList.isEmpty()) {
            throw new IllegalArgumentException("The experiment list is empty.");
        }
        if (indicatorsList.isEmpty()) {
            throw new IllegalArgumentException("The indicator list is empty.");
        }
        this.callbackList = Collections.unmodifiableList(callbackList);
        this.indicatorList = Collections.unmodifiableList(indicatorsList);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    @Override
    public Iterator<Experiment<S>> iterator() {
        return new Iterator<Experiment<S>>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < callbackList.size();
            }

            @Override
            public Experiment<S> next() {
                if (experimentList == null) experimentList = new ArrayList<>();
                Experiment<S> experiment = null;

                /*
                 * If the element already has been created so get it.
                 */
                if (i < experimentList.size()) {
                    experimentList.get(i);
                } else {
                    /*
                     * If the list hasn't the same size so there is elements that hasn't been created.
                     */
                    Callable<Experiment<S>> experimentCallable = callbackList.get(i);
                    try {
                        experiment = experimentCallable.call();
                    } catch (Exception e) {
                        throw new ApplicationException("Unhandled error when create the experiment for using with indicators.", e);
                    }
                    experimentList.add(experiment);
                }
                i++;
                return experiment;
            }
        };
    }

    /**
     * Return a unmodifiable list with experiments that has been created until the call to this method. The list is filled when you iterate using the {@link #iterator()}
     *
     * @return the experiment list.
     */
    public @NotNull List<Experiment<S>> getExperimentList() {
        return experimentList == null ? Collections.emptyList() : Collections.unmodifiableList(experimentList);
    }

    /**
     * Get the indicators list as a unmodifiable list.
     *
     * @return the indicator list or a empty list if there isn't indicators.
     */
    public List<GenericIndicator> getIndicatorList() {
        return indicatorList;
    }

    /**
     * The number of experiments in this set.
     *
     * @return the number of experiments.
     */
    public int getNumberOfExperiments() {
        return this.callbackList.size();
    }

    /**
     * Call this method after of finish the execution.
     * Remove duplicated algorithms of each experiment.
     */
    public void removeDuplicatedAlgorithms() {
        experimentList.forEach(Experiment::removeDuplicatedAlgorithms);
    }

    /**
     * Get an only one instance of each experiment problems. Two problems are considered equals based in his {@link ExperimentProblem#getTag()}.
     *
     * @return the list with all experiments problems without repeat elements.
     */
    public List<ExperimentProblem<S>> getExperimentProblems() {
        /*
         * Get all distincs problem based in problemTag of problem algorithm.
         */
        HashSet<String> problemTags = new HashSet<>();
        List<ExperimentProblem<S>> problems = getExperimentList().stream().map(Experiment::getProblem).collect(Collectors.toList());
        problems.removeIf(eProblem -> !problemTags.add(eProblem.getTag()));
        return problems;
    }

    /**
     * Get all experiment algorithms without duplicated used to this problem. Two experiment are duplicated
     * if they has the same {@link ExperimentAlgorithm#getAlgorithmTag()}.
     *
     * @return the list with all experiments algorithm without repeat elements.
     */
    public List<ExperimentAlgorithm<S>> getExperimentAlgorithms() {
        /*
         * Get all algorithms that resolve the same problem. Two problem are the same if has the same problemTag.
         */
        HashSet<String> algorithmTags = new HashSet<>();
        // Get all experimentAlgorithm in each experiment into the same list
        List<ExperimentAlgorithm<S>> algorithms = getExperimentList().stream()
                .map(Experiment::getAlgorithmList)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        // remove elements to get only one experiment algorithm for each algorithm tag.
        algorithms.removeIf(eAlgorithm -> !algorithmTags.add(eAlgorithm.getAlgorithmTag()));
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
     *
     * @return number of independent run.
     * @throws InvalidConditionException if the number of independent run isn't the same in all experiments.
     */
    public int getIndependentRuns() {
        List<Integer> result = getExperimentList().stream().map(Experiment::getIndependentRuns).distinct().collect(Collectors.toList());
        if (result.size() != 1) {
            throw new InvalidConditionException("All experiments hasn't have the same number of independent run.");
        }
        return result.get(0);
    }

    /**
     * Get the experiment base directory. It directory should be the same by all experiments in this set.
     * Because the base directory is setting up in {@link controller.multiobjective.indicator.util.ExecutionIndicatorTask}
     * before execute any experiment.
     * <p>
     * Call this method after running at least once the iterator. This method get the experiment base directory of any experiment in this set.
     * @throws NoSuchElementException if there aren't a experiment in this set to get the experiment base directory.
     */
    public String getExperimentBaseDirectory() {
        Optional<Experiment<S>> experiment = getExperimentList().stream()
                .findFirst();
        if (experiment.isPresent()) {
            return experiment.get().getExperimentBaseDirectory();
        }
        throw new NoSuchElementException("There aren't experiment algorithm to get the base directory. Run this set with iterator at least once a time.");
    }
}
