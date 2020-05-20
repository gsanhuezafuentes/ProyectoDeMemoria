package annotations.registrable;

import registrable.Registrable;

import java.lang.annotation.*;

/**
 * This class is used to indicate the name to problem that the class that inherit of {@link Registrable} set up.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
@Documented
public @interface NewProblem {
	/**
	 * Problem name. It is used as a category name in the GUI.
	 * @return the name of algorithm
	 */
	String displayName() default "";
	
	/**
	 * Algorithm name.
	 * @return the name of algorithm used to solve the problem
	 */
	String algorithmName() default "";
	
}
