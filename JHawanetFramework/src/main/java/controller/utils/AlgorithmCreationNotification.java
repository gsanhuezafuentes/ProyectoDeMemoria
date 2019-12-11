package controller.utils;

import controller.problems.Registrable;

/**
 * It is a event called when the registrable factory of a specific problem with specific configuration is created.
 *
 */
@FunctionalInterface
public interface AlgorithmCreationNotification {
	void notify(Registrable registrableProblem);
}
