package view.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotations.Injectable;
import annotations.NewProblem;
import exception.ApplicationException;
import view.problems.Registrable;

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
	 * Validate a Registrable problem
	 * @param registrable the registrable problem
	 * @throws ApplicationException if the Registrable problem format is not valid
	 */
	public static void validateRegistrableProblem(Registrable registrable) throws ApplicationException {
		Class<?> objectClass = registrable.getClass();
		Method injectableMethod = null;
		
		//Test if the registrable problem as NewProblem annotation
		int countNewProblemAnnotation = 0;
		int countInjectableAnnotation = 0;
		for (Constructor<?> constructor : objectClass.getConstructors()) {
			NewProblem annotation = constructor.getAnnotation(NewProblem.class);
			if (annotation != null) {
				countNewProblemAnnotation++;
			}
		}

		//Test if the method has the injectable annotation only once
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
			throw new ApplicationException(objectClass.getName() + " has more than one constructor with NewProblem annotation");
		}
		if (countInjectableAnnotation == 0) {
			throw new ApplicationException(objectClass.getName() + " hasn't a method with Injectable annotation");
		}
		if (countInjectableAnnotation > 1) {
			throw new ApplicationException(objectClass.getName() + " has more than one method with Injectable annotation");
		}
		
		// Test the order of the parameters
		// Order: Object, int | double
		
		Class<?>[] parameterTypes = injectableMethod.getParameterTypes();
		
		int objectCount = 0;
		int numberCount = 0;
		for (int i = 0; i < parameterTypes.length; i++) {
			Class<?> parameterType = parameterTypes[i];
			if (parameterType == Object.class) {
				if (numberCount != 0) {
					throw new ApplicationException("The order of operator isn't valid. Confirm that the order is (object ..., number ...) in " + registrable.getClass().getName());
					
				}
				objectCount++;
			} else if (parameterType.getName().matches("int|Integer|double|Double")) {
				numberCount++;
			}			
			else {
				throw new ApplicationException("The type " + parameterType.getName() + " is not valid for the injectable function in "+ registrable.getClass().getName() + ". Only can be used object, int or double");
			}
		}
	}
	
	
	/**
	 * Get the injectable method.
	 * @param classType the class where find the injectable annotation
	 * @return method with the injectable annotation or null if it not exist
	 */
	public static Method getInjectableMethod(Class<?> classType) {
		for (Method method : classType.getMethods()){
			method.getAnnotation(Injectable.class);
			if (method.getAnnotation(Injectable.class) != null) {
				return method;
			}
		}
		return null;
	}

}
