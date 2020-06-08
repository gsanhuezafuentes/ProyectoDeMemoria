package registrable.utils;

import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.experiment.util.ExperimentAlgorithm;
import model.metaheuristic.experiment.util.ExperimentProblem;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A class with utility method to create experiments.
 */
public final class ExperimentUtils {
    private ExperimentUtils() {
    }

    /**
     * This class help to create a list of experiments algorithms on the same problem.
     * <p>
     * This method create so many ExperimentAlgorithms as number of independent run were passed. For each
     * ExperimentAlgorithm a new call on the supplier is realized. So each ExperimentAlgorithm has his own
     * instance of the same algorithm.
     *
     * <p>
     * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem}
     * which form part of a {@link ExperimentAlgorithm}, which is a decorator for
     * class {@link Algorithm}. The {@link ExperimentAlgorithm} has an optional tag
     * component, that can be set as it is shown in this example, where four
     * variants of a same algorithm are defined.
     *
     * @param experimentProblem the experiment problem
     * @param numberOfIndependentRun the number of independent run
     * @param supplier the supplier of algorithm
     * @return a list with the experiment algorithm.
     * @throws NullPointerException if experimentProblem or supplier are null.
     * @param <S> the type of solution.
     */
    public static <S extends Solution<?>> List<ExperimentAlgorithm<S>> configureAlgorithmList(ExperimentProblem<S> experimentProblem,  int numberOfIndependentRun,
                                                                                              Supplier<Algorithm<S>> supplier) {
        Objects.requireNonNull(experimentProblem);
        Objects.requireNonNull(supplier);

        List<ExperimentAlgorithm<S>> algorithms = new ArrayList<>(numberOfIndependentRun);
        for (int run = 0; run < numberOfIndependentRun; run++) {
            algorithms.add(new ExperimentAlgorithm<S>(supplier.get(), experimentProblem, run));
        }
        return algorithms;
    }
}
