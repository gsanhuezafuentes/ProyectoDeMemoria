package annotations.operators;

import model.metaheuristic.operator.Operator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 
 * This interface denote the default constructor of the {@link Operator}. The constructor
 * where you use this annotation only has to have double or int parameter.
 *
 */
@Documented
@Retention(RUNTIME)
@Target(CONSTRUCTOR)
public @interface DefaultConstructor {
	/**
	 * Array with the names defined for each variable.
	 * @return array with displayNames.
	 */
	String[] value() default {};
}
