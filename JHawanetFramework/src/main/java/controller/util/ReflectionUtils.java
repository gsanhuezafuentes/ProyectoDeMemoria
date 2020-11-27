package controller.util;

import annotations.EnumInput;
import annotations.NumberInput;
import annotations.operator.DefaultConstructor;
import annotations.registrable.*;
import exception.ApplicationException;
import exception.IllegalOperatorException;
import exception.IllegalRegistrableException;
import model.metaheuristic.qualityindicator.impl.GenericIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registrable.Registrable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Utility class with method to get info of class using reflection and validate
 * the contract from operators and Registrable problems.
 */
public class ReflectionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);

    /**
     * A list with the type of operators already checked. Avoid checking every time an operator appears in a Registrable class.
     */
    private static final HashSet<Class<?>> verifiedOperators = new HashSet<>();

    /**
     * Read the {@link NewProblem} annotation from a problem and get the name of the
     * problem.
     *
     * @param registrable the problem class
     * @return name of the problem
     * @throws IllegalRegistrableException if the problem hasn't a constructor with
     *                                     {@link NewProblem} annotation.
     * @throws NullPointerException        if registrable is null.
     */
    public static String getNameOfProblem(Class<? extends Registrable<?>> registrable) {
        Objects.requireNonNull(registrable);
        for (Constructor<?> constructor : registrable.getConstructors()) {
            NewProblem annotation = constructor.getAnnotation(NewProblem.class);
            if (annotation != null) {
                return annotation.displayName();
            }
        }

        throw new IllegalRegistrableException(registrable.getName() + " hasn't a constructor with NewProblem annotation");
    }

    /**
     * Read the {@link NewProblem} annotation from a problem and get the name of the
     * algorithm.
     *
     * @param registrable the problem class
     * @return name of the algorithm
     * @throws IllegalRegistrableException if the problem hasn't a constructor with
     *                                     {@link NewProblem} annotation.
     * @throws NullPointerException        if registrable is null.
     */
    public static String getNameOfAlgorithm(Class<? extends Registrable<?>> registrable) {
        Objects.requireNonNull(registrable);
        for (Constructor<?> constructor : registrable.getConstructors()) {
            NewProblem annotation = constructor.getAnnotation(NewProblem.class);
            if (annotation != null) {
                return annotation.algorithmName();
            }
        }

        throw new IllegalRegistrableException(registrable.getName() + " hasn't a constructor with NewProblem annotation");
    }

    /**
     * Read the {@link NewProblem} annotation from a problem and get the description of the
     * problem.
     *
     * @param registrable the problem class
     * @return name of the algorithm
     * @throws IllegalRegistrableException if the problem hasn't a constructor with
     *                                     {@link NewProblem} annotation.
     * @throws NullPointerException        if registrable is null.
     */
    public static String getDescriptionOfProblem(Class<? extends Registrable<?>> registrable) {
        Objects.requireNonNull(registrable);
        for (Constructor<?> constructor : registrable.getConstructors()) {
            NewProblem annotation = constructor.getAnnotation(NewProblem.class);
            if (annotation != null) {
                return annotation.description();
            }
        }

        throw new IllegalRegistrableException(registrable.getName() + " hasn't a constructor with NewProblem annotation");
    }

    /**
     * Validate a Registrable problem. <br>
     * <br>
     * <p>
     * It validation in: <br>
     * <br>
     *
     * <ol>
     * 		<li>Verify if {@code registrable} has only a public constructor</li>
     * 		<li>Verify if {@code registrable} has {@link NewProblem} annotation in his only one constructor </li>
     * 		<li>Verify if {@code registrable} has the same number of parameters has values defined in {@link Parameters}
     * 		annotation in the constructor</li>
     * 		<li>Verify if {@code registrable} has the parameters in the correct order and if the parameters are only of
     * 		type Object, File, Enumerator, boolean, int or double or his wrapper Integer, Double. The order is (Object...,
     * 		File ..., Enumerator, boolean int|double ...)</li>
     * 		<li>Verify if {@code registrable}'s constructor parameters correspond to the type defined by {@link Parameters}</li>
     * 		<li>Verify if {@code registrable} constructor doesn't have parameters when {@link Parameters} annotation isn't used</li>
     * </ol>
     *
     * <br>
     * <br>
     * <p>
     * The {@link Parameters} will be ignored if it isn't in the same constructor
     * that the {@link NewProblem} annotation.
     *
     * @param registrable the registrable problem class.
     * @throws IllegalRegistrableException if any of the conditions to be verified is not
     *                                     fulfilled.
     */
    public static void validateRegistrableProblem(Class<? extends Registrable<?>> registrable) {

        Constructor<?>[] constructors = registrable.getConstructors();

        // Test if there is more than once public constructor or there aren't public
        // constructor
        if (constructors.length == 0) {
            throw new IllegalRegistrableException(registrable.getName() + " hasn't a public constructor");
        } else if (constructors.length > 1) {
            throw new IllegalRegistrableException(registrable.getName() + " has more than one public constructor");
        }

        Constructor<?> constructor = constructors[0];

        // Test if the registrable problem as NewProblem annotation
        NewProblem annotation = constructor.getAnnotation(NewProblem.class);
        if (annotation == null) {
            throw new IllegalRegistrableException(registrable.getName() + " hasn't a constructor with NewProblem annotation");
        }

        // Test if the only constructor has the same number of constructor parameters
        // that the number of element defined in Parameters annotation. Also the if it
        // are in the correct order. (object,..., file ..., int or double,...)
        Parameters parametersAnnotation = constructor.getAnnotation(Parameters.class);
        if (parametersAnnotation != null) {
            int numberOfParametersInAnnotation = getNumberOfParameterInParametersAnnotation(parametersAnnotation);

            // the constructor's parameter number as to be the same that the numbers of
            // parameters described by annotation.
            if (constructor.getParameterCount() != numberOfParametersInAnnotation) {
                throw new IllegalRegistrableException(registrable.getName()
                        + " has missing parameters in the constructor or in annotation. Parameters describe in annotation are "
                        + numberOfParametersInAnnotation + " and parameters in the constructor are "
                        + constructor.getParameterCount());
            }

            // Test the order of the parameters
            // Order: Object, File, enum, boolean, int | double
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            int objectCount = 0;
            int fileCount = 0;
            int enumCount = 0;
            int booleanCount = 0;
            int numberCount = 0;
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (parameterType == Object.class) {
                    if (fileCount != 0 || enumCount != 0 || booleanCount != 0 || numberCount != 0) {
                        throw new IllegalRegistrableException(
                                "The order of operator isn't valid. Confirm that the order is (Object ..., File ..., " +
                                        "Enumerator ..., boolean ..., double or int ...) in "
                                        + registrable.getName());

                    }
                    objectCount++;
                } else if (parameterType == File.class) {
                    if (enumCount != 0 || booleanCount != 0 || numberCount != 0) {
                        throw new IllegalRegistrableException(
                                "The order of operator isn't valid. Confirm that the order is (Object ..., File ..., " +
                                        "Enumerator ..., boolean ..., double or int ...) in "
                                        + registrable.getName());

                    }
                    fileCount++;
                } else if (parameterType.isEnum()) {
                    if (booleanCount != 0 || numberCount != 0) {
                        throw new IllegalRegistrableException(
                                "The order of operator isn't valid. Confirm that the order is (Object ..., File ..., " +
                                        "Enumerator ..., boolean ..., double or int ...) in "
                                        + registrable.getName());
                    }
                    enumCount++;
                } else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
                    if (numberCount != 0) {
                        throw new IllegalRegistrableException(
                                "The order of operator isn't valid. Confirm that the order is (Object ..., File ..., " +
                                        "Enumerator ..., boolean ..., double or int ...) in "
                                        + registrable.getName() + ".");
                    }
                    booleanCount++;
                } else if (parameterType.equals(int.class)
                        || parameterType.equals(Integer.class)
                        || parameterType.equals(double.class)
                        || parameterType.equals(Double.class)) {

                    numberCount++;

                } else {
                    throw new IllegalRegistrableException("The type " + parameterType.getName()
                            + " is not valid type in the constructor " + registrable.getName()
                            + ". Only can be used Object, File, Enumerator, boolean (or Boolean), int(or Integer) or double(Double)");
                }
            }

            // checks if the type of parameters correspond to the type of annotation in
            // Parameters annotation
            if (objectCount != parametersAnnotation.operators().length) {
                throw new IllegalRegistrableException("The number of " + OperatorInput.class.getName()
                        + " doesn't correspond to the number of Object parameter in constructor");
            }

            if (fileCount != parametersAnnotation.files().length) {
                throw new IllegalRegistrableException("The number of " + FileInput.class.getName()
                        + " doesn't correspond to the number of File parameter in constructor");
            }

            if (enumCount != parametersAnnotation.enums().length) {
                throw new IllegalRegistrableException("The number of " + EnumInput.class.getName()
                        + " doesn't correspond to the number of enum types parameters in constructor");
            }

            if (booleanCount != parametersAnnotation.booleans().length) {
                throw new IllegalRegistrableException("The number of " + Boolean.class.getName()
                        + " doesn't correspond to the number of boolean parameter in constructor");
            }

            if (numberCount != parametersAnnotation.numbers().length + parametersAnnotation.numbersToggle().length) {
                throw new IllegalRegistrableException("The number of " + NumberInput.class.getName() + " plus the number of "
                        + NumberToggleInput.class.getName()
                        + " doesn't correspond to the number of int|Integer or double|Double parameter in constructor.");
            }

            // checks that entries with the same group id are consecutively
            if (parametersAnnotation.numbersToggle().length != 0) {
                Set<String> addedGroupId = new HashSet<>();
                String lastAdded = null;
                for (NumberToggleInput numberToggle : parametersAnnotation.numbersToggle()) {
                    if (!numberToggle.groupID().equals(lastAdded)) {
                        addedGroupId.add(lastAdded);
                        lastAdded = numberToggle.groupID();
                        if (addedGroupId.contains(lastAdded)) {
                            throw new IllegalRegistrableException(
                                    "The NumberToggleInput with the same group id are not consecutive.");
                        }
                    }
                }
            }

            // checks that default value of Enum is correct.
            if (parametersAnnotation.enums().length != 0) {
                for (EnumInput enumInput : parametersAnnotation.enums()) {
                    String defaultValue = enumInput.defaultValue();
                    if (!(defaultValue == null || defaultValue.isEmpty())) {
                        boolean isValid = Arrays.stream(enumInput.enumClass().getEnumConstants())
                                .anyMatch(anEnum -> anEnum.name().equals(defaultValue));

                        if (!isValid) {
                            throw new IllegalRegistrableException("The EnumInput default value for " + enumInput.enumClass().getCanonicalName() +
                                    " isn't correct because not exist a enum constant named " + defaultValue + ".");
                        }
                    }
                }
            }

        } else if (constructor.getParameterCount() != 0) {
            throw new IllegalRegistrableException("The constructor of " + registrable.getName()
                    + " has input parameters but there isn't a ParameterAnnotation describing it.");
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
     * <li>Verify that all operator has the parameter in the next order: int, enum, boolean in his constructor</li>
     * <li>Verify that the {@link DefaultConstructor} has the same number of
     * elements that the parameters of constructor</li>
     * </ol>
     *
     * @param registrable the registrable problem that contain the
     *                    {@link Parameters} with information of operators.
     * @throws IllegalOperatorException if any of the conditions to be verified is not
     *                                  fulfilled.
     */
    public static void validateOperators(Class<? extends Registrable<?>> registrable) {
        Constructor<?> constructor = getNewProblemConstructor(registrable);

        Parameters annotation = constructor.getAnnotation(Parameters.class);
        // Verify if exist annotation and it has defined operators
        if (annotation != null && annotation.operators().length != 0) {
            // Read each operator input and the option for this
            for (OperatorInput operator : annotation.operators()) {
                for (OperatorOption operatorOption : operator.value()) {
                    // Test if operator has a default constructor
                    Class<?> operatorClass = operatorOption.value();
                    validateOperator(operatorClass);
                }
            }
        }

    }

    /**
     * @see #validateOperators(Class)
     * @param operatorClass the operator class
     */
    public static void validateOperator(Class<?> operatorClass){
        // Test if it operator has been verified before.
        if (!verifiedOperators.contains(operatorClass)) {
            verifiedOperators.add(operatorClass);

            Constructor<?> defaultConstructor = null;
            int defaultConstructCount = 0;
            // count the number of default constructor
            for (Constructor<?> operatorConstructor : operatorClass.getConstructors()) {
                DefaultConstructor constructorAnnotation = operatorConstructor
                        .getAnnotation(DefaultConstructor.class);
                if (constructorAnnotation != null) {
                    defaultConstructCount++;
                    defaultConstructor = operatorConstructor;
                }
            }

            if (defaultConstructCount == 0) {
                throw new IllegalOperatorException(operatorClass.getName()
                        + " hasn't a public constructor with the DefaultConstructor annotation ");
            }

            if (defaultConstructCount > 1) {
                throw new IllegalOperatorException(operatorClass.getName()
                        + " has more than one constructor with the DefaultConstructor annotation ");
            }

            DefaultConstructor constructorAnnotation = defaultConstructor
                    .getAnnotation(DefaultConstructor.class);

            int numberOfParameters = constructorAnnotation.numbers().length
                    + constructorAnnotation.enums().length + constructorAnnotation.booleans().length;

            // Test if the number of parameter in default constructor are the same that the
            // defined in DefaultConstructor annotation
            if (defaultConstructor.getParameterCount() != numberOfParameters) {
                throw new IllegalOperatorException("The default constructor of " + operatorClass.getName()
                        + " hasn't the same number of parameter that the defined in the DefaultConstructor annotation");

            }

            // Test the order of the parameters
            // Order: int | double, enum, boolean
            Class<?>[] parameterTypes = defaultConstructor.getParameterTypes();
            int numberCount = 0;
            int enumCount = 0;
            int booleanCount = 0;
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (parameterType.equals(int.class)
                        || parameterType.equals(Integer.class)
                        || parameterType.equals(double.class)
                        || parameterType.equals(Double.class)) {

                    if (enumCount != 0 || booleanCount != 0) {
                        throw new IllegalOperatorException(
                                "The order of operator isn't valid. Confirm that the order is (double or int ... " +
                                        "Enumerator ..., boolean ...) in "
                                        + operatorClass.getName());
                    }
                    numberCount++;
                } else if (parameterType.isEnum()) {
                    if (booleanCount != 0) {
                        throw new IllegalOperatorException(
                                "The order of operator isn't valid. Confirm that the order is (double or int ... " +
                                        "Enumerator ..., boolean ...) in "
                                        + operatorClass.getName());
                    }
                    enumCount++;
                } else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
                    booleanCount++;
                } else {
                    throw new IllegalOperatorException("The type " + parameterType.getName()
                            + " is not valid type in the constructor " + operatorClass.getName()
                            + ". Only can be used int(or Integer) or double(Double), Enumerator, boolean (or Boolean)");
                }
            }

            if (numberCount != constructorAnnotation.numbers().length) {
                throw new IllegalOperatorException("The number of " + NumberInput.class.getName()
                        + " doesn't correspond to the number of int|Integer or double|Double parameter in constructor.");
            }

            if (enumCount != constructorAnnotation.enums().length) {
                throw new IllegalOperatorException("The number of " + EnumInput.class.getName()
                        + " doesn't correspond to the number of enum types parameters in constructor");
            }

            if (booleanCount != constructorAnnotation.booleans().length) {
                throw new IllegalOperatorException("The number of " + Boolean.class.getName()
                        + " doesn't correspond to the number of boolean parameter in constructor");
            }
        }
    }

    /**
     * Get the constructor with NewProblem annotation. If it constructor doesn't
     * exist so return null.
     *
     * @param registrableClass the registrable class.
     * @return The constructor with {@link NewProblem} annotation
     * @throws NullPointerException if registrableClass is null.
     */
    public static Constructor<?> getNewProblemConstructor(Class<? extends Registrable<?>> registrableClass) {
        Objects.requireNonNull(registrableClass);
        for (Constructor<?> constructor : registrableClass.getConstructors()) {
            if (constructor.getAnnotation(NewProblem.class) != null) {
                return constructor;
            }
        }
        return null;
    }

    /**
     * Search the default constructor. The constructor with {@link DefaultConstructor} annotation.
     *
     * @param classType the class where find the default constructor
     * @return constructor with {@link DefaultConstructor} annotation if exit. null
     * if it doesn't exist.
     * @throws NullPointerException if classType is null.
     */
    public static Constructor<?> getDefaultConstructor(Class<?> classType) {
        Objects.requireNonNull(classType);
        for (Constructor<?> constructor : classType.getConstructors()) {
            DefaultConstructor annotation = constructor.getAnnotation(DefaultConstructor.class);
            if (annotation != null) {
                return constructor;
            }
        }
        return null;
    }

    /**
     * Return the number of parameters in the only constructor of registrable
     * problem.
     *
     * @param registrable the registrable class.
     * @return the number of parameters in registrable problem.
     * @throws NullPointerException if registrable is null.
     */
    public static int getNumberOfParameterInRegistrableConstructor(Class<? extends Registrable<?>> registrable) {
        Objects.requireNonNull(registrable);
        Constructor<?>[] constructors = registrable.getConstructors();
        if (constructors.length != 1) {
            throw new IllegalOperatorException("Registrable class has to have only one constructor.");
        }
        Constructor<?> constructor = constructors[0];

        return constructor.getParameterCount();
    }

    /**
     * Return the number of parameters in the {@link Parameter} annotation.
     *
     * @param parametersAnnotation the {@link Parameter} object.
     * @return the number of parameters in parameter annotation
     * @throws NullPointerException if parametersAnnotation is null.
     */
    public static int getNumberOfParameterInParametersAnnotation(Parameters parametersAnnotation) {
        Objects.requireNonNull(parametersAnnotation);
        int numberOfParametersInAnnotation = parametersAnnotation.operators().length
                + parametersAnnotation.files().length //
                + parametersAnnotation.enums().length //
                + parametersAnnotation.booleans().length //
                + parametersAnnotation.numbers().length//
                + parametersAnnotation.numbersToggle().length;
        return numberOfParametersInAnnotation;
    }

    /**
     * Return the number of parameters in the operator default constructor.
     *
     * @param classType The class where {@link DefaultConstructor} was used.
     * @return the number of parameters in operator default constructor.
     * @throws NullPointerException if classType is null or there isn't a default
     *                              constructor.
     */
    public static int getNumberOfParameterInDefaultConstructor(Class<?> classType) {
        Objects.requireNonNull(classType);
        Constructor<?> constructor = getDefaultConstructor(classType);
        Objects.requireNonNull(constructor);
        return constructor.getParameterCount();
    }

    /**
     * Create a new instance of Registrable problem when it has parameters.
     *
     * @param registrableClass the registrable class.
     * @param parameters       the parameters of constructor of registrable class. If there is no parameter receive a empty array (new Object[0]).
     * @param <T>              The type of class.
     * @return the registrable instance.
     * @throws ApplicationException      if there are any exceptions when the new
     *                                   instance is being created.
     * @throws InvocationTargetException if the underlying constructor throws an
     *                                   exception.
     * @throws NullPointerException      if registrableClass or parameters is null.
     */
    public static <T extends Registrable<?>> T createRegistrableInstance(Class<? extends Registrable<?>> registrableClass,
                                                                         Object[] parameters) throws InvocationTargetException {
        Objects.requireNonNull(registrableClass);
        Objects.requireNonNull(parameters);
        LOGGER.debug("Creating registrable {} with parameters {}.", registrableClass.getName(), parameters);


        Constructor<?> constructor = ReflectionUtils.getNewProblemConstructor(registrableClass);
        Object registrable;
        try {
            registrable = constructor.newInstance(parameters);
            return (T) registrable;// unchecked cast
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            throw new ApplicationException("Error in reflection call", e);
        }
    }

    /**
     * Create a new instance of Registrable problem when it hasn't parameters.
     *
     * @param registrableClass the registrable class.
     * @param <T>              The type of class.
     * @return the registrable instance.
     * @throws ApplicationException      if there are any exceptions when the new
     *                                   instance is being createds
     * @throws InvocationTargetException if the underlying constructor throws an
     *                                   exception.
     * @throws NullPointerException      if registrableClass is null.
     */
    public static <T extends Registrable<?>> T createRegistrableInstance(Class<T> registrableClass)
            throws InvocationTargetException {
        Objects.requireNonNull(registrableClass);
        Object registrable = createRegistrableInstance(registrableClass, new Object[0]);
        return (T) registrable; // unchecked cast
    }

    /**
     * Create a new instance of operator problem when it has parameters.
     *
     * @param operatorClass the operator class
     * @param parameters    the parameters of constructor of registrable class. If there is no parameter receive a empty array (new Object[0]).
     * @return the operator instance.
     * @throws ApplicationException      if there are any exceptions when the new
     *                                   instance is being created.
     * @throws InvocationTargetException if the underlying constructor throws an
     *                                   exception.
     * @throws NullPointerException      if operatorClass or parameters is null.
     */
    public static Object createOperatorInstance(Class<?> operatorClass, Object[] parameters) throws InvocationTargetException {
        Objects.requireNonNull(operatorClass);
        Objects.requireNonNull(parameters);
        LOGGER.debug("Creating operator {} with parameters {}.", operatorClass.getName(), parameters);

        Constructor<?> constructor = ReflectionUtils.getDefaultConstructor(operatorClass);
        Object operator;

        try {
            operator = constructor.newInstance(parameters);
            return operator;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            throw new ApplicationException("Error in reflection call", e);
        }
    }

    /**
     * Create a new instance of a generic indicators.
     *
     * @param indicatorClass the indicator class
     * @return the operator instance.
     * @throws ApplicationException      if there are any exceptions when the new
     *                                   instance is being created.
     * @throws InvocationTargetException if the underlying constructor throws an
     *                                   exception.
     * @throws NullPointerException      if operatorClass or parameters is null.
     */
    public static Object createIndicatorInstance(Class<? extends GenericIndicator> indicatorClass) throws InvocationTargetException {
        Objects.requireNonNull(indicatorClass);
        LOGGER.debug("Creating indicator {}.", indicatorClass.getName());
        try {
            // Call to default constructor without parameters.
            Constructor<?> constructor = indicatorClass.getConstructor();
            Object indicator;

            indicator = constructor.newInstance();
            return indicator;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException e) {
            throw new ApplicationException("Error in reflection call", e);
        }
    }
}
