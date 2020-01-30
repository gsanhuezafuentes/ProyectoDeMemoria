package annotations.registrable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import controller.problems.Registrable;

/**
 * This class is used to indicate the name to problem that the class that inherit of {@link Registrable} set up.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface NewProblem {
	/**
	 * Problem name. It is used as a category name.
	 */
	String displayName();
	
	/**
	 * Algorithm name.
	 */
	String algorithmName();
	
}
