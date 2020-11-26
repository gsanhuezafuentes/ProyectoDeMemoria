package annotations.registrable;

import annotations.EnumInput;
import annotations.NumberInput;
import registrable.Registrable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * It annotation has to be used by {@link Registrable} object. And denota the
 * type of parameters that will be injected to the constructor of Registrable
 * problem.
 * 
 */
@Documented
@Retention(RUNTIME)
@Target(CONSTRUCTOR)
public @interface Parameters {
	/**
	 * Get the operator input annotation. Each OperatorInput indicate the
	 * alternatives of operator that can be injected. The operator will be injected
	 * in registrable problem in the parameter of type Object.
	 * 
	 * @return The OperatorInput annotation
	 */
	OperatorInput[] operators() default {};

	/**
	 * Get the file input annotation. The file will be injected in registrable
	 * problem in the parameter of type File.
	 * 
	 * @return the FileInput annotation
	 */
	FileInput[] files() default {};

	/**
	 * Get the enum input annotation. The enum value will be injected in registrable in the specific enum type.
	 * @return the EnumInput annotation.
	 */
	EnumInput[] enums() default {};

	/**
	 * Get the number input annotation. The number will be injected in registrable
	 * problem in the parameter of type int or double.
	 * 
	 * @return the NumberInput annotation
	 */
	NumberInput[] numbers() default {};
	
	/**
	 * Get the number toggle input annotation. The number will be injected in registrable
	 * problem in the parameter of type int or double.
	 * 
	 * @return the NumberInput annotation
	 */
	NumberToggleInput[] numbersToggle() default {};
}
