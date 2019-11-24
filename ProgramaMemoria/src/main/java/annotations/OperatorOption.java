package annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
	 * Class of operator.<br><br>
	 * Example: UniformSelection.class
	 * @return the class object
	 */
	Class<?> value();
}
