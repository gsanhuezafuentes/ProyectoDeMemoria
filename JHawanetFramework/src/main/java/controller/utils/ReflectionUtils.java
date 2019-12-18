package controller.utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashSet;

import annotations.operators.DefaultConstructor;
import annotations.registrable.NewProblem;
import annotations.registrable.OperatorInput;
import annotations.registrable.OperatorOption;
import annotations.registrable.Parameters;
import controller.problems.ProblemRegistrar;
import controller.problems.Registrable;
import exception.ApplicationException;

/**
 * Utility class with method to get info of class using reflection and validate the contract from operators and Registrable problems.
 *
 */
public class ReflectionUtils {
	private static HashSet<Class<?>> verifiedOperators = new HashSet<>();

	/**
	 * Read the {@link NewProblem} annotation from a problem and get the name of the
	 * problem.
	 * 
	 * @param registrable the problem class
	 * @return name of the problem
	 * @throws ApplicationException if the problem hasn't a constructor with
	 *                              {@link ProblemRegistrar} annotation.
	 */
	public static String getNameOfProblem(Class<? extends Registrable> registrable) throws ApplicationException {
		for (Constructor<?> constructor : registrable.getConstructors()) {
			NewProblem annotation = constructor.getAnnotation(NewProblem.class);
			if (annotation != null) {
				return annotation.displayName();
			}
		}

		throw new ApplicationException(registrable.getName() + " hasn't a constructor with NewProblem annotation");
	}

	/**
	 * Validate a Registrable problem. <br>
	 * <br>
	 * 
	 * It validation in: <br>
	 * <br>
	 * 
	 * <pre>
	 *  <ol>
	 *  	<li>Verify if {@code registrable} has only a public constructor</li>
	 *  	<li>Verify if {@code registrable} has {@link NewProblem} annotation in his only one constructor </li>
	 *  	<li>Verify if {@code registrable} has the same number of parameters has values defined in {@link Parameters} annotation in the constructor</li>
	 *  	<li>Verify if {@code registrable} as the parameters in the correct order and if the parameters are only of type Object, File, int or double or his wrapper Integer, Double. The order is (Object..., File ..., int|double ...)</li>
	 *  	<li>Verify if {@code registrable} constructor don't have parameters when {@link Parameters} annotation isn't used</li>
	 *  </ol>
	 * </pre><br><br>
	 * 
	 * The {@link Parameters} will be ignored if it isn't in the same constructor that the {@link NewProblem} annotation.
	 * 
	 * @param registrable the registrable problem class
	 * @throws ApplicationException if any of the conditions to be verified is not
	 *                              fulfilled
	 */
	public static void validateRegistrableProblem(Class<? extends Registrable> registrable)
			throws ApplicationException {

		Constructor<?>[] constructors = registrable.getConstructors();

		// Test if there is more than once public constructor or there aren't public
		// constructor
		if (constructors.length == 0) {
			throw new ApplicationException(registrable.getName() + " hasn't a public constructor");
		} else if (constructors.length > 1) {
			throw new ApplicationException(registrable.getName() + " has more than one public constructor");
		}

		Constructor<?> constructor = constructors[0];

		// Test if the registrable problem as NewProblem annotation
		NewProblem annotation = constructor.getAnnotation(NewProblem.class);
		if (annotation == null) {
			throw new ApplicationException(registrable.getName() + " hasn't a constructor with NewProblem annotation");
		}

		// Test if the only constructor has the same number of constructor parameters
		// that the number of element defined in Parameters annotation. Also the if it
		// are in the correct order. (object,..., file ..., int or double,...)
		Parameters parametersAnnotation = constructor.getAnnotation(Parameters.class);
		if (parametersAnnotation != null) {
			int numberOfParametersInAnnotation = parametersAnnotation.operators().length
					+ parametersAnnotation.files().length //
					+ parametersAnnotation.numbers().length;
			// the constructor's parameter number as to be the same that the numbers of parameters described by annotation.
			if (constructor.getParameterCount() != numberOfParametersInAnnotation) {
				throw new ApplicationException(registrable.getName()
						+ " has missing parameters in the constructor or in annotation. Parameters describe in annotation are "
						+ numberOfParametersInAnnotation + " and parameters in the constructor are "
						+ constructor.getParameterCount());
			}

			// Test the order of the parameters
			// Order: Object, File, int | double
			Class<?>[] parameterTypes = constructor.getParameterTypes();

			int fileCount = 0;
			int numberCount = 0;
			for (int i = 0; i < parameterTypes.length; i++) {
				Class<?> parameterType = parameterTypes[i];
				if (parameterType == Object.class) {
					if (fileCount != 0 || numberCount != 0) {
						throw new ApplicationException(
								"The order of operator isn't valid. Confirm that the order is (Object ..., File ..., double or int ...) in "
										+ registrable.getName());

					}
				} else if (parameterType == File.class) {
					if (numberCount != 0) {
						throw new ApplicationException(
								"The order of operator isn't valid. Confirm that the order is (object ..., File ..., double or int ...) in "
										+ registrable.getName());

					}
					fileCount++;
				} else if (parameterType.getName().matches("int|Integer|double|Double")) {
					numberCount++;
				} else {
					throw new ApplicationException("The type " + parameterType.getName()
							+ " is not valid type in the constructor " + registrable.getName()
							+ ". Only can be used object, file, int(or Integer) or double(Double)");
				}
			}
		} else if (constructor.getParameterCount() != 0) {
			throw new ApplicationException("The constructor of " + registrable.getName()
					+ " has input parameters but there isn't a ParameterAnnotation describing it");
		}
	}

	/**
	 * Validate operators defined in {@link Parameters} in the constructor. The
	 * validation consist in:<br>
	 * <br>
	 * 
	 * <ol>
	 * <li>Verify that all operators has {@link DefaultConstructor} in only one
	 * constructor.</li>
	 * <li>Verify that all operator has only int or double value in the default
	 * constructor</li>
	 * <li>Verify that the {@link DefaultConstructor} has the same number of
	 * elements that the parameters of constructor</li>
	 * </ol>
	 * 
	 * @param registrable the registrable problem that contain the
	 *                    {@link Parameters} with information of operators.
	 * @throws ApplicationException if any of the conditions to be verified is not
	 *                              fulfilled
	 */
	public static void validateOperators(Class<? extends Registrable> registrable) throws ApplicationException {
		Constructor<?> constructor = getConstructor(registrable);

		Parameters annotation = constructor.getAnnotation(Parameters.class);
		// Verify if exist annotation and it has defined operators
		if (annotation != null && annotation.operators().length != 0) {
			// Read each operator input and the option for this
			for (OperatorInput operator : annotation.operators()) {
				for (OperatorOption operatorOption : operator.value()) {
					// Test if operator has a default constructor
					Class<?> operatorClass = operatorOption.value();
					// Test if it operator has been verified before.
					if (!verifiedOperators.contains(operatorClass)) {
						verifiedOperators.add(operatorClass);

						Constructor<?> defaultConstructor = null;
						int defaultConstructCount = 0;
						for (Constructor<?> operatorConstructor : operatorClass.getConstructors()) {
							DefaultConstructor constructorAnnotation = operatorConstructor
									.getAnnotation(DefaultConstructor.class);
							if (constructorAnnotation != null) {
								defaultConstructCount++;
								defaultConstructor = operatorConstructor;
							}
						}

						if (defaultConstructCount == 0) {
							throw new ApplicationException(operatorClass.getName()
									+ " hasn't a public constructor with the DefaultConstructor annotation ");
						}

						if (defaultConstructCount > 1) {
							throw new ApplicationException(operatorClass.getName()
									+ " has more than one constructor with the DefaultConstructor annotation ");
						}

						DefaultConstructor constructorAnnotation = defaultConstructor
								.getAnnotation(DefaultConstructor.class);

						// Test if the number of parameter in default constructor are the same that the
						// defined in DefaultConstructor annotation
						if (defaultConstructor.getParameterCount() != constructorAnnotation.value().length) {
							throw new ApplicationException("The default constructor of " + operatorClass.getName()
									+ " hasn't the same number of parameter that the defined in the DefaultConstructor annotation");

						}

						// Test if each parameter is one of type defined in regex expression
						for (Class<?> type : defaultConstructor.getParameterTypes()) {
							if (!type.getName().matches("int|Integer|double|Double")) {
								throw new ApplicationException("The default constructor of " + operatorClass.getName()
										+ " has parameters with a type is not valid for default constructor. The only valid type are int or double or his wrapper classes(Integer, Double)");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Get the constructor with NewProblem annotation. If it constructor don't exist
	 * so return null.
	 * 
	 * @param registrableClass the registrable class.
	 * @return The constructor with {@link NewProblem} annotation
	 */
	public static Constructor<?> getConstructor(Class<? extends Registrable> registrableClass) {
		for (Constructor<?> constructor : registrableClass.getConstructors()) {
			if (constructor.getAnnotation(NewProblem.class) != null) {
				return constructor;
			}
		}
		return null;
	}

	/**
	 * Search the default constructor
	 * 
	 * @param classType the class where find the default constructor
	 * @return constructor with {@link DefaultConstructor} annotation if exit. Null
	 *         if it don't exist.
	 */
	public static Constructor<?> getDefaultConstructor(Class<?> classType) {
		for (Constructor<?> constructor : classType.getConstructors()) {
			DefaultConstructor annotation = constructor.getAnnotation(DefaultConstructor.class);
			if (annotation != null) {
				return constructor;
			}
		}
		return null;
	}

}
