package model.metaheuristic.utils.random;

/**
 * Functional interface to involve a generator with a lowerbound and upperBound
 *
 * @param <Type> Type of the object generated
 */
public interface BoundedRandomGenerator <Type extends Comparable<Type>> {
	/**
	 * Method that return a random value between lowerbound and upperbound exclusive
	 * @param lowerbound lower bound for the generated value
	 * @param upperBound upper bound for the generated value
	 * @return The random element
	 */
	Type getRandomValue(Type lowerbound, Type upperBound);
}
