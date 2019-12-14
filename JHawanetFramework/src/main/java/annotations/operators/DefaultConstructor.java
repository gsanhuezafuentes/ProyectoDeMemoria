package annotations.operators;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import model.metaheuristic.operator.Operator;

/**
 * 
 * This interface denote the default constructor of the {@link Operator}
 *
 */
@Documented
@Retention(RUNTIME)
@Target(CONSTRUCTOR)
public @interface DefaultConstructor {
	/**
	 * Array with the name defined for each variable
	 * @return array with displayNames
	 */
	String[] value();
}
