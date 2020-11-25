package annotations.registrable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * It class describe the possible operator that can be injected into the
 * parameter of the registrable constructor. It will show a combobox whose value
 * will be the operator configured with {@link OperatorOption}
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface OperatorInput {
	/**
	 * A name of the parameter. It is used to show the name in the GUI
	 * @return the name
	 */
	String displayName() default "";
	
	/**
	 * The possible operator that can be injected.
	 * @return the operator array that can be injected
	 */
	OperatorOption[] value();

}
