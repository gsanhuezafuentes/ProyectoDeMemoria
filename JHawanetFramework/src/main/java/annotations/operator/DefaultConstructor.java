package annotations.operator;

import annotations.EnumInput;
import annotations.NumberInput;
import model.metaheuristic.operator.Operator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 
 * This interface denote the default constructor of the {@link Operator}. The constructor
 * where you use this annotation only has to have double or int parameter.
 *
 */
@Documented
@Retention(RUNTIME)
@Target(CONSTRUCTOR)
public @interface DefaultConstructor {
	/**
	 * Get the number input annotation. The number will be injected in the Operator
	 * in the parameter of type int or double.
	 *
	 * @return the NumberInput annotation
	 */
	NumberInput[] numbers() default {};

	/**
	 * Get the enum annotation. This will be injected in a Enum type.
	 *
	 * @return the NumberInput annotation
	 */
	EnumInput[] enums() default {};

	/**
	 * Get the boolean annotation. This will be injected in boolean parameter.
	 *
	 * @return the NumberInput annotation
	 */
	EnumInput[] booleans() default {};

}
