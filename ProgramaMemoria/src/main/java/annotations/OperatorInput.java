package annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;

import static java.lang.annotation.ElementType.*;

@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface OperatorInput {
	/**
	 * A name of the parameter.
	 * @return the name
	 */
	String displayName();
	
	/**
	 * The possible operator to this parameter
	 * @return
	 */
	OperatorOption[] value();

}
