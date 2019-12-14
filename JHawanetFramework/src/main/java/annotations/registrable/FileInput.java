package annotations.registrable;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * It class denote a file input injected in the constructor of registrable problem.
 *
 */
@Documented
@Retention(CLASS)
@Target(CONSTRUCTOR)
public @interface FileInput {
	/**
	 * A name of the parameter.
	 * @return the name
	 */
	String displayName();
}
