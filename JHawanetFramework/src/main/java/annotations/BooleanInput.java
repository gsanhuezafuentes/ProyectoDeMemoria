package annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * It class denote boolean that can
 * be injected to Registrable problem or Operator.
 * <p>
 * It will show in GUI a CheckBox that will inject the writed value to constructor.
 *
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface BooleanInput {
    /**
     * The name of parameters
     * @return the name of parameters
     */
    String displayName() default "";

    boolean defaultValue() default false;
}
