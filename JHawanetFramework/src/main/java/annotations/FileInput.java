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
	public enum FileType{
		OPEN, SAVE
	}

	/**
	 * A name of the parameter.
	 * 
	 * @return the name
	 */
	String displayName() default "";
	
	FileType type() default FileType.OPEN;
}
