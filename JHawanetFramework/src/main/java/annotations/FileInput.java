package annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * It class denote a file input injected in the constructor of registrable
 * problem. It will showed a textfield and a button which open a FileChooser to
 * search the file to load.
 *
 */
@Documented
@Retention(CLASS)
@Target(CONSTRUCTOR)
public @interface FileInput {
	public enum Type {
		/**
		 * Indicate that the Dialog will allow choose a file to open.
		 */
		OPEN,
		/**
		 * Indicate that the Dialog will allow choose a file name to save the result.
		 */
		SAVE,
		/**
		 * Indicate that the File Chooser will allow choose a directory.
		 */
		Directory
	}

	/**
	 * A name of the parameter.
	 * 
	 * @return the name
	 */
	String displayName() default "";
	
	Type type() default Type.OPEN;
}
