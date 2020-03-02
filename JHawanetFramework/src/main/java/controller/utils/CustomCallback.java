package controller.utils;

/**
 * It is a custom callback
 *
 */
@FunctionalInterface
public interface CustomCallback<T> {
	void notify(T element);
}
