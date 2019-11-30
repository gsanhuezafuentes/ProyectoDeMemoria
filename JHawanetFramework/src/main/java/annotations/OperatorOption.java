package annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * It class describe and option of a type of operator. <br><br>
 * 
 * Eg. a option for a selection operator is UniformSelection.class
 *
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface OperatorOption {
	/**
	 * Operator name
	 * @return name
	 */
	String displayName();
	/**
	 * The operator class
	 * Class of operator.<br><br>
	 * Example: UniformSelection.class
	 * @return the class object
	 */
	Class<?> value();
}
