package view.utils;

import model.metaheuristic.algorithm.Algorithm;

/**
 * It is a event to called when a algorithm is created.
 *
 */
@FunctionalInterface
public interface AlgorithmCreationNotification {
	void notify(Algorithm<?> algorithm);
}
