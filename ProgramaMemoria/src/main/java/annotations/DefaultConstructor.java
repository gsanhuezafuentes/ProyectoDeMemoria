package annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * Operators
 *
 */
@Documented
@Retention(RUNTIME)
@Target(CONSTRUCTOR)
public @interface DefaultConstructor {
	// Parameters name 
	String[] value();
}
