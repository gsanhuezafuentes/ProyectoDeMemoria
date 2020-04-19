package annotations.registrable;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * It class denote a int, double or his wrapper values (Integer, Double) that
 * can be injected to Registrable problem. <br>
 * <br>
 * This notation indicates that there is a set of parameters that are mutually
 * exclusive of each other, i.e. only one parameter can receive the value. It
 * will be showed in the GUI as a radio button for each parameters. The selected
 * radio button will be inject the value write in TextField or 0 if there isn't
 * a value. The unselected radio button will inject Double.NEGATIVE_INFINITY, if
 * the parameter is of type double or Double; or Integer.NEGATIVE_INFINITY if
 * the parameter is of type int or Integer.
 *
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface NumberToggleInput {
	/**
	 * The group id. Elements with the same group id are mutually exclusive of each
	 * other.
	 * 
	 * @return the group id
	 */
	String groupID() default "";

	/**
	 * Parameter's name
	 * 
	 * @return the display name
	 */
	String displayName() default "";
}
