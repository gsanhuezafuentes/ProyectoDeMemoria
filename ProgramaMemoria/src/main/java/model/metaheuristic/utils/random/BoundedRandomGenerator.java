package model.metaheuristic.utils.random;

/**
 * Functional interface to involve a generator with a lowerbound and upperBound
 *
 * @param <Type>
 */
public interface BoundedRandomGenerator <Type extends Comparable<Type>> {
	/**
	 * Method that return a random value between lowerbound and upperbound exclusive
	 * @param lowerbound
	 * @param upperBound
	 * @return The random element
	 */
	Type getRandomValue(Type lowerbound, Type upperBound);
}
