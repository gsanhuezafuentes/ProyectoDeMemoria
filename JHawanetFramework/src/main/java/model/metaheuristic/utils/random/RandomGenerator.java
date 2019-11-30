package model.metaheuristic.utils.random;

/**
 * A functional interfaces to wrapper a Random function;
 * @author gsanh
 *
 * @param <S>
 */
@FunctionalInterface
public interface RandomGenerator<S extends Number> {

	/**
	 * Method that return a random value 
	 * @return The random element.
	 */
	S getRandomValue();
	
}
