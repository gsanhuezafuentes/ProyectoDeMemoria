package annotations.registrable;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * It class denote a int, double or his wrapper values (Integer, Double) that can be injected to Registrable problem.
 * It will show in GUI a TextField that will inject the writed value to constructor.
 *
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface NumberInput {
	/**
	 * The name of parameters
	 * @return the name of parameters
	 */
	String displayName();
}
