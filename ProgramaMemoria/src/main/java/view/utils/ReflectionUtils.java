package view.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import annotations.DefaultConstructor;
import annotations.Injectable;
import annotations.NewProblem;
import annotations.OperatorInput;
import annotations.OperatorOption;
import annotations.Parameters;
import exception.ApplicationException;
import view.problems.Registrable;

/**
 * Utility class with method to get info of class using reflection.
 *
 */
public class ReflectionUtils {

	/**
	 * Read the {@link RegisterProblem} annotation from a problem and get the name
	 * of the problem.
	 * 
	 * @param registrable the problem
	 * @return name of the problem
	 * @throws ApplicationException if the problem hasn't a constructor with
	 *                              {@link RegisterProblem} annotation.
	 */
	public static String getNameOfProblem(Registrable registrable) throws ApplicationException {
		Class<?> objectClass = registrable.getClass();
		for (Constructor<?> constructor : objectClass.getConstructors()) {
			NewProblem annotation = constructor.getAnnotation(NewProblem.class);
			if (annotation != null) {
				return annotation.displayName();
			}
		}

		throw new ApplicationException(objectClass.getName() + " hasn't a constructor with NewProblem annotation");
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
	 *  	<li>Verify if {@code registrable} has {@link NewProblem} annotation in only one constructor </li>
	 *  	<li>Verify if {@code registrable} has a method with {@link Injectable} annotation in only one method</li>
	 *  	<li>Verify if {@code registrable} has the same number of parameters has values defined in {@link Parameters} annotation in the injectable method</li>
	 *  	<li>Verify if {@code registrable} in his injectable method as the parameters in the correct order and only is contain Object, int or double or his wrapper Integer, Double. It order is (Object..., int|double ...)</li>
	 *  	<li>Verify if {@code registrable} in his injectable method don't have parameters when {@link Parameters} annotation isn't used</li>
	 *  </ol>
	 * </pre>
	 * 
	 * @param registrable the registrable problem
	 * @throws ApplicationException if any of the conditions to be verified is not
	 *                              fulfilled
	 */
	public static void validateRegistrableProblem(Registrable registrable) throws ApplicationException {
		Class<?> objectClass = registrable.getClass();
		Method injectableMethod = null;

		// Test if the registrable problem as NewProblem annotation
		int countNewProblemAnnotation = 0;
		int countInjectableAnnotation = 0;
		for (Constructor<?> constructor : objectClass.getConstructors()) {
			NewProblem annotation = constructor.getAnnotation(NewProblem.class);
			if (annotation != null) {
				countNewProblemAnnotation++;
			}
		}

		// Test if the method has the injectable annotation only once
		for (Method method : objectClass.getMethods()) {
			Injectable annotation = method.getAnnotation(Injectable.class);
			if (annotation != null) {
				countInjectableAnnotation++;
				injectableMethod = method;
			}
		}

		if (countNewProblemAnnotation == 0) {
			throw new ApplicationException(objectClass.getName() + " hasn't a constructor with NewProblem annotation");
		}
		if (countNewProblemAnnotation > 1) {
			throw new ApplicationException(
					objectClass.getName() + " has more than one constructor with NewProblem annotation");
		}
		if (countInjectableAnnotation == 0) {
			throw new ApplicationException(objectClass.getName() + " hasn't a method with Injectable annotation");
		}
		if (countInjectableAnnotation > 1) {
			throw new ApplicationException(
					objectClass.getName() + " has more than one method with Injectable annotation");
		}

		// Test if the injectable method with parameters annotation has the correct
		// number of parameters and the order of it.
		Parameters parametersAnnotation = injectableMethod.getAnnotation(Parameters.class);
		if (parametersAnnotation != null) {
			int numberOfParametersInAnnotation = parametersAnnotation.operators().length
					+ parametersAnnotation.numbers().length;
			if (injectableMethod.getParameterCount() != numberOfParametersInAnnotation) {
				throw new ApplicationException(objectClass.getName()
						+ " missing parameters in injectable method or in annotation. Parameters describe in annotation are "
						+ numberOfParametersInAnnotation + " and parameters in method are "
						+ injectableMethod.getParameterCount());
			}

			// Test the order of the parameters
			// Order: Object, int | double
			Class<?>[] parameterTypes = injectableMethod.getParameterTypes();

			int numberCount = 0;
			for (int i = 0; i < parameterTypes.length; i++) {
				Class<?> parameterType = parameterTypes[i];
				if (parameterType == Object.class) {
					if (numberCount != 0) {
						throw new ApplicationException(
								"The order of operator isn't valid. Confirm that the order is (object ..., number ...) in "
										+ registrable.getClass().getName());

					}
				} else if (parameterType.getName().matches("int|Integer|double|Double")) {
					numberCount++;
				} else {
					throw new ApplicationException(
							"The type " + parameterType.getName() + " is not valid for the injectable function in "
									+ registrable.getClass().getName() + ". Only can be used object, int or double");
				}
			}
		} else if (injectableMethod.getParameterCount() != 0) {
			throw new ApplicationException("The injectable method of " + objectClass.getName()
					+ " has input parameters but there isn't a ParameterAnnotation describing it");
		}
	}

	/**
	 * Validate operators defined in {@link Parameters} in the injectable method.
	 * The validation consist in:<br>
	 * <br>
	 * 
	 * <pre>
	 * 	<ol>
	 * 		<li>Verify that all operators has {@link DefaultConstructor} in only one constructor.</li>
	 * 		<li>Verify that all operator has only int or double value in the default constructor</li>
	 * 		<li>Verify that the {@link DefaultConstructor} has the same number of elements that the parameters of constructor</li>
	 * 	</ol>
	 * </pre>
	 * 
	 * @param registrable the registrable problem that contain the
	 *                    {@link Parameters} with information of operators.
	 * @throws ApplicationException if any of the conditions to be verified is not
	 *                              fulfilled
	 */
	public static void validateOperators(Registrable registrable) throws ApplicationException {
		Class<?> objectClass = registrable.getClass();
		Method injectableMethod = getInjectableMethod(objectClass);

		Parameters annotation = injectableMethod.getAnnotation(Parameters.class);
		// Verify if exist annotation and it has defined operators
		if (annotation != null && annotation.operators().length != 0) {
			// Read each operator input and the option for this
			for (OperatorInput operator : annotation.operators()) {
				for (OperatorOption operatorOption : operator.value()) {
					// Test if operator has a default constructor
					Class<?> operatorClass = operatorOption.value();
					Constructor<?> defaultConstructor = null;
					int defaultConstructCount = 0;
					for (Constructor<?> constructor : operatorClass.getConstructors()) {
						DefaultConstructor constructorAnnotation = constructor.getAnnotation(DefaultConstructor.class);
						if (constructorAnnotation != null) {
							defaultConstructCount++;
							defaultConstructor = constructor;
						}
					}
					
					if (defaultConstructCount > 1) {
						throw new ApplicationException(operatorClass.getName()
								+ " has more than one constructor with the DefaultConstructor annotation ");
					}
					
					DefaultConstructor constructorAnnotation = defaultConstructor.getAnnotation(DefaultConstructor.class);
					
					// Test if the number of parameter in default constructor are the same that the defined in DefaultConstructor annotation
					if (defaultConstructor.getParameterCount() != constructorAnnotation.value().length) {
						throw new ApplicationException("The default constructor of " + operatorClass.getName()
								+ " hasn't the same number of parameter that the defined in the DefaultConstructor annotation");

					}
					
					//Test if each parameter is one of type defined in regex expression
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

	/**
	 * Get the injectable method.
	 * 
	 * @param classType the class where find the injectable annotation
	 * @return method with the injectable annotation or null if it not exist
	 */
	public static Method getInjectableMethod(Class<?> classType) {
		for (Method method : classType.getMethods()) {
			method.getAnnotation(Injectable.class);
			if (method.getAnnotation(Injectable.class) != null) {
				return method;
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
