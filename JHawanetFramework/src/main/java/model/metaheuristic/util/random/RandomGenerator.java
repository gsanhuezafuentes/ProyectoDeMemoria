package model.metaheuristic.util.random;

/**
 * A functional interfaces to wrapper a Random function;
 *
 * @param <S> the type of to object to generate
 */
@FunctionalInterface
public interface RandomGenerator<S extends Number> {

	/**
	 * Method that return a random value 
	 * @return The random element.
	 */
	S getRandomValue();
	
}
