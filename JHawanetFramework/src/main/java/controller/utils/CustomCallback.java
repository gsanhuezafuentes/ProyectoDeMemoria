package controller.utils;

import controller.problems.Registrable;
import view.ConfigurationDynamicWindow;

/**
 * It is a custom callback
 *
 */
@FunctionalInterface
public interface CustomCallback<T> {
	void notify(T element);
}
