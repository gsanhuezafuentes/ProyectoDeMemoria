package controller.util;

import annotations.BooleanInput;
import annotations.EnumInput;
import annotations.NumberInput;
import annotations.operator.DefaultConstructor;
import annotations.registrable.*;
import exception.ApplicationException;
import exception.IllegalOperatorException;
import exception.IllegalRegistrableException;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.operator.Operator;
import model.metaheuristic.operator.crossover.impl.IntegerSBXCrossover;
import model.metaheuristic.operator.crossover.impl.IntegerSinglePointCrossover;
import model.metaheuristic.operator.mutation.impl.IntegerPolynomialMutation;
import model.metaheuristic.operator.mutation.impl.IntegerRangeRandomMutation;
import model.metaheuristic.operator.mutation.impl.IntegerSimpleRandomMutation;
import model.metaheuristic.operator.selection.impl.UniformSelection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import registrable.Registrable;
import registrable.SingleObjectiveRegistrable;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilsTest {

    //************************************************************
    // Test of Registrables.
    //************************************************************/

    @Test
    void testGetNameOfProblem() {
        String name = ReflectionUtils.getNameOfProblem(OnlyNewProblemAnnotation.class);
        assertEquals("Test", name);
    }

    @Test
    void testGetNameOfAlgorithm() {
        String name = ReflectionUtils.getNameOfAlgorithm(OnlyNewProblemAnnotation.class);
        assertEquals("NSGAII", name);
    }

    @Test
    void testValidateRegistrableProblem_ConstructorWithoutAnnotation_ApplicationException() {
        assertThrows(ApplicationException.class, () -> ReflectionUtils.validateRegistrableProblem(NoAnnotation.class));
    }

    @Test
    void testValidateRegistrableProblem_OnlyNewProblemAnnotation_NotException() {
        assertDoesNotThrow(() -> ReflectionUtils.validateRegistrableProblem(OnlyNewProblemAnnotation.class));

    }

    @ParameterizedTest
    @ValueSource(classes = {WithParameterInCorrectOrder.class, WithParameterInCorrectOrder2.class})
    void testValidateRegistrableProblem_ParameterInCorrectOrder_NotException(Class<? extends Registrable<?>> clazz) {
        assertDoesNotThrow(() -> ReflectionUtils.validateRegistrableProblem(clazz));
    }

    @ParameterizedTest
    @ValueSource(classes = {WithParameterInWrongOrder.class, WithParameterInWrongOrder2.class, WithParameterInWrongOrder3.class})
    void testValidateRegistrableProblem_ParametersInWrongOrder_IllegalRegistrableException(Class<? extends Registrable<?>> clazz) {
        assertThrows(IllegalRegistrableException.class,
                () -> ReflectionUtils.validateRegistrableProblem(clazz));
    }

    @Test
    void testValidateRegistrableProblem_IncorrectNumberOfParameterInConstructor_IllegalRegistrableException() {
        assertThrows(IllegalRegistrableException.class,
                () -> ReflectionUtils.validateRegistrableProblem(IncorrectNumberOfParameterInConstructor.class));
    }

    @Test
    void testValidateRegistrableProblem_IncorrectNumberOfParameterInAnnotation_IllegalRegistrableException() {
        assertThrows(IllegalRegistrableException.class,
                () -> ReflectionUtils.validateRegistrableProblem(IncorrectNumberOfParameterInAnnotation.class));
    }

    @Test
    void testValidateRegistrableProblem_TwoConstructor_IllegalRegistrableException() {
        assertThrows(IllegalRegistrableException.class,
                () -> ReflectionUtils.validateRegistrableProblem(WithTwoConstructor.class));
    }

    @Test
    void testValidateRegistrableProblem_SameGroupInNumberToggleInputConsecutive_NotException() {
        assertDoesNotThrow(
                () -> ReflectionUtils.validateRegistrableProblem(NumberToggleInputInSameGroupConsecutively.class));
    }

    @Test
    void testValidateRegistrableProblem_SameGroupInNumberToggleInputNotConsecutive_IllegalRegistrableException() {
        assertThrows(IllegalRegistrableException.class,
                () -> ReflectionUtils.validateRegistrableProblem(NumberToggleInputInSameGroupNotConsecutively.class));
    }


    @ParameterizedTest
    @ValueSource(classes = {WithParametersTypeDoesNotCorrespondToTheParametersAnnotation.class,
            WithParametersTypeDoesNotCorrespondToTheParametersAnnotation2.class,
            WithParametersTypeDoesNotCorrespondToTheParametersAnnotation3.class})
    void testValidateRegistrableProblem_WithParametersThatDoesNotCorrespondToAnnotation_IllegalRegistrableException(Class<? extends Registrable<?>> clazz) {
        assertThrows(IllegalRegistrableException.class, () -> ReflectionUtils
                .validateRegistrableProblem(clazz));
    }

    @ParameterizedTest
    @ValueSource(classes = {EnumDefaultValueIncorrect.class})
    void testValidateRegistrableProblem_EnumDefaultValueIncorrect_IllegalRegistrableException(Class<? extends Registrable<?>> clazz) {
        assertThrows(IllegalRegistrableException.class,
                () -> ReflectionUtils.validateRegistrableProblem(clazz));
    }

    static abstract class TestSingleobjective implements SingleObjectiveRegistrable {

        @Override
        public Experiment<?> build(String inpPath) throws Exception {
            // TODO Auto-generated method stub
            return null;
        }
    }

    static class NoAnnotation extends TestSingleobjective {

        public NoAnnotation() {
        }

    }

    static class OnlyNewProblemAnnotation extends TestSingleobjective {
        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        public OnlyNewProblemAnnotation() {
        }

        @Override
        public Experiment<?> build(String inpPath) throws Exception {
            // TODO Auto-generated method stub
            return null;
        }

    }

    static class WithParameterInCorrectOrder extends TestSingleobjective {
        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public WithParameterInCorrectOrder(Object selectionOperator, Object crossoverOperator, Object mutationOperator,
                                           File gama, int minPressure, int populationSize, int numberWithoutImprovement, int maxEvaluations) {
        }

    }

    static class WithParameterInCorrectOrder2 extends TestSingleobjective {
        enum Numbers {ONE, TWO, THREE, FOUR}

        enum Type {TYPEONE, TYPETWO, TYPETHREE, TYPEFOUR}

        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                enums = {@EnumInput(displayName = "Numbers", enumClass = Numbers.class, defaultValue = "ONE"),//
                        @EnumInput(displayName = "Numbers", enumClass = Type.class)
                }, //
                booleans = {@BooleanInput(displayName = "boolResult")},//
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public WithParameterInCorrectOrder2(Object selectionOperator, Object crossoverOperator, Object mutationOperator,
                                            File gama, Numbers numbers, Type types, boolean booolResult, int minPressure, int populationSize, int numberWithoutImprovement, int maxEvaluations) {
        }

    }

    static class WithParameterInWrongOrder extends TestSingleobjective {
        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public WithParameterInWrongOrder(Object selectionOperator, Object crossoverOperator, Object mutationOperator,
                                         int minPressure, File gama, int populationSize, int numberWithoutImprovement, int maxEvaluations) {
        }

    }

    static class WithParameterInWrongOrder2 extends TestSingleobjective {
        enum Numbers {ONE, TWO, THREE, FOUR}

        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                enums = {@EnumInput(displayName = "Numbers", enumClass = Numbers.class, defaultValue = "ONE")}, //
                booleans = {@BooleanInput(displayName = "boolResult")},//
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public WithParameterInWrongOrder2(Object selectionOperator, Object crossoverOperator, Object mutationOperator, Numbers numbers,
                                          File gama, boolean booolResult, int minPressure, int populationSize, int numberWithoutImprovement, int maxEvaluations) {
        }

    }

    static class WithParameterInWrongOrder3 extends TestSingleobjective {
        enum Numbers {ONE, TWO, THREE, FOUR}

        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                enums = {@EnumInput(displayName = "Numbers", enumClass = Numbers.class, defaultValue = "ONE")}, //
                booleans = {@BooleanInput(displayName = "boolResult")},//
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public WithParameterInWrongOrder3(Object selectionOperator, Object crossoverOperator, Object mutationOperator, boolean booolResult,
                                          File gama, Numbers numbers, int minPressure, int populationSize, int numberWithoutImprovement, int maxEvaluations) {
        }

    }

    static class IncorrectNumberOfParameterInConstructor extends TestSingleobjective {
        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public IncorrectNumberOfParameterInConstructor(Object selectionOperator, Object crossoverOperator,
                                                       Object mutationOperator, int minPressure, int populationSize, int numberWithoutImprovement,
                                                       int maxEvaluations) {
        }

    }

    static class IncorrectNumberOfParameterInAnnotation extends TestSingleobjective {
        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public IncorrectNumberOfParameterInAnnotation(Object selectionOperator, Object crossoverOperator,
                                                      Object mutationOperator, File gama, int minPressure, int populationSize, int numberWithoutImprovement,
                                                      int maxEvaluations) {
        }

    }

    static class WithTwoConstructor extends TestSingleobjective {
        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters()
        public WithTwoConstructor() {
        }

        public WithTwoConstructor(int a) {
        }

    }

    static class NumberToggleInputInSameGroupConsecutively extends TestSingleobjective {
        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public NumberToggleInputInSameGroupConsecutively(Object selectionOperator, Object crossoverOperator,
                                                         Object mutationOperator, File gama, int minPressure, int populationSize, int numberWithoutImprovement,
                                                         int maxEvaluations) {
        }

    }

    static class NumberToggleInputInSameGroupNotConsecutively extends TestSingleobjective {
        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition2", displayName = "Number of iteration"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public NumberToggleInputInSameGroupNotConsecutively(Object selectionOperator, Object crossoverOperator,
                                                            Object mutationOperator, File gama, int populationSize, int numberWithoutImprovement,
                                                            int numberOfIteration, int maxEvaluations) {
        }

    }

    static class WithParametersTypeDoesNotCorrespondToTheParametersAnnotation extends TestSingleobjective {
        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public WithParametersTypeDoesNotCorrespondToTheParametersAnnotation(Object selectionOperator,
                                                                            Object crossoverOperator, File file2, File gama, int minPressure, int populationSize,
                                                                            int numberWithoutImprovement, int maxEvaluations) {
        }
    }

    static class WithParametersTypeDoesNotCorrespondToTheParametersAnnotation2 extends TestSingleobjective {
        enum Numbers {ONE, TWO, THREE, FOUR}

        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                enums = {@EnumInput(displayName = "Numbers", enumClass = Numbers.class, defaultValue = "ONE")}, //
                booleans = {@BooleanInput(displayName = "boolResult")},//
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public WithParametersTypeDoesNotCorrespondToTheParametersAnnotation2(Object selectionOperator, Object crossoverOperator, Object mutationOperator,
                                                                             File gama, File file2, boolean booolResult, int minPressure, int populationSize, int numberWithoutImprovement, int maxEvaluations) {
        }

    }

    static class WithParametersTypeDoesNotCorrespondToTheParametersAnnotation3 extends TestSingleobjective {
        enum Numbers {ONE, TWO, THREE, FOUR}

        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                enums = {@EnumInput(displayName = "Numbers", enumClass = Numbers.class, defaultValue = "ONE")}, //
                booleans = {@BooleanInput(displayName = "boolResult")},//
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public WithParametersTypeDoesNotCorrespondToTheParametersAnnotation3(Object selectionOperator, Object crossoverOperator, Object mutationOperator,
                                                                             File gama, Numbers numbers, int intt, int minPressure, int populationSize, int numberWithoutImprovement, int maxEvaluations) {
        }

    }

    static class EnumDefaultValueIncorrect extends TestSingleobjective {
        enum Numbers {ONE, TWO, THREE, FOUR}

        @NewProblem(displayName = "Test", algorithmName = "NSGAII")
        @Parameters(operators = {
                @OperatorInput(displayName = "Selection Operator", value = {
                        @OperatorOption(displayName = "Uniform Selection", value = UniformSelection.class)}),
                @OperatorInput(displayName = "Crossover Operator", value = {
                        @OperatorOption(displayName = "Integer SBX Crossover", value = IntegerSBXCrossover.class),
                        @OperatorOption(displayName = "Integer Single Point Crossover", value = IntegerSinglePointCrossover.class)}), //
                @OperatorInput(displayName = "Mutation Operator", value = {
                        @OperatorOption(displayName = "Integer Polynomial Mutation", value = IntegerPolynomialMutation.class),
                        @OperatorOption(displayName = "Integer Range Random Mutation", value = IntegerRangeRandomMutation.class),
                        @OperatorOption(displayName = "Integer Simple Random Mutation", value = IntegerSimpleRandomMutation.class)})}, //
                files = {@FileInput(displayName = "Gama")}, //
                enums = {@EnumInput(displayName = "Numbers", enumClass = Numbers.class, defaultValue = "FIVE")}, //
                booleans = {@BooleanInput(displayName = "boolResult")},//
                numbers = {@NumberInput(displayName = "Min pressure"), @NumberInput(displayName = "Population Size")}, //
                numbersToggle = {
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Number of iteration without improvement"),
                        @NumberToggleInput(groupID = "Finish Condition", displayName = "Max number of evaluation")})
        public EnumDefaultValueIncorrect(Object selectionOperator, Object crossoverOperator, Object mutationOperator,
                                         File gama, Numbers numbers, boolean booolResult, int minPressure, int populationSize, int numberWithoutImprovement, int maxEvaluations) {
        }
    }


    //************************************************************
    // Test of Operators.
    //************************************************************/

    @ParameterizedTest
    @ValueSource(classes = {OperatorEmptyConstructor.class})
    void testValidateOperator_OperatorWithEmptyConstructor_NotException(Class<?> clazz) {
        assertDoesNotThrow(() -> ReflectionUtils.validateOperator(clazz));
    }

    @ParameterizedTest
    @ValueSource(classes = {OperatorWithoutDefaultConstructorAnnotation.class})
    void testValidateOperator_OperatorWithoutDefaultConstructorAnnotation_IllegalOperatorException(Class<?> clazz) {
        assertThrows(IllegalOperatorException.class, () -> ReflectionUtils.validateOperator(clazz));
    }

    @ParameterizedTest
    @ValueSource(classes = {OperatorParameterInCorrectOrder1.class
            , OperatorParameterInCorrectOrder2.class
            , OperatorParameterInCorrectOrder3.class
    })
    void testValidateOperator_ParameterInCorrectOrder_NotException(Class<?> clazz) {
        assertDoesNotThrow(() -> ReflectionUtils.validateOperator(clazz));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            OperatorParameterInWrongOrder.class
            , OperatorParameterInWrongOrder2.class
    })
    void testValidateOperator_ParameterInWrongOrder_IllegalOperatorException(Class<?> clazz) {
        assertThrows(IllegalOperatorException.class, () -> ReflectionUtils.validateOperator(clazz));
    }

    @ParameterizedTest
    @ValueSource(classes = {
            MissingBooleanParameterInAnnotation.class
            , MissingBooleanParameterInConstructor.class
    })
    void testValidateOperator_MissingValuesInConstructorOrAnnotation_IllegalOperatorException(Class<?> clazz) {
        assertThrows(IllegalOperatorException.class, () -> ReflectionUtils.validateOperator(clazz));
    }


    @ParameterizedTest
    @ValueSource(classes = {
            OperatorParameterWithWrongType.class
            , OperatorParameterWithWrongType2.class
    })
    void testValidateOperator_OperatorParameterWithWrongType_IllegalOperatorException(Class<?> clazz) {
        assertThrows(IllegalOperatorException.class, () -> ReflectionUtils.validateOperator(clazz));
    }

    static abstract class TestOperador implements Operator<Object, Object> {
        /**
         * Execute the operator on source
         *
         * @param o element to operates.
         * @return Result of operation
         */
        @Override
        public Object execute(Object o) {
            return null;
        }
    }

    static class OperatorEmptyConstructor extends TestOperador {
        @DefaultConstructor
        public OperatorEmptyConstructor() {
        }
    }

    static class OperatorWithoutDefaultConstructorAnnotation extends TestOperador {
        public OperatorWithoutDefaultConstructorAnnotation() {
        }
    }

    static class OperatorParameterInCorrectOrder1 extends TestOperador {
        @DefaultConstructor(numbers = {@NumberInput(), @NumberInput()})
        public OperatorParameterInCorrectOrder1(int number1, int number2) {
        }
    }

    static class OperatorParameterInCorrectOrder2 extends TestOperador {
        enum Type {Zero, ONE, TWO, THREE}

        @DefaultConstructor(numbers = {@NumberInput(), @NumberInput()},
                enums = {@EnumInput(enumClass = Type.class)}
        )
        public OperatorParameterInCorrectOrder2(int number1, int number2, Type type1) {
        }
    }

    static class OperatorParameterInCorrectOrder3 extends TestOperador {
        enum Type {Zero, ONE, TWO, THREE}

        @DefaultConstructor(numbers = {@NumberInput(), @NumberInput()}
                , enums = {@EnumInput(enumClass = Type.class)}
                , booleans = {@BooleanInput()}
        )
        public OperatorParameterInCorrectOrder3(int number1, int number2, Type type1, boolean isValid) {
        }
    }

    static class OperatorParameterInWrongOrder extends TestOperador {
        enum Type {Zero, ONE, TWO, THREE}

        @DefaultConstructor(numbers = {@NumberInput(), @NumberInput()}
                , enums = {@EnumInput(enumClass = Type.class)}
                , booleans = {@BooleanInput()}
        )
        public OperatorParameterInWrongOrder(int number1, Type type1, int number2, boolean isValid) {
        }
    }

    static class OperatorParameterInWrongOrder2 extends TestOperador {
        enum Type {Zero, ONE, TWO, THREE}

        @DefaultConstructor(numbers = {@NumberInput(), @NumberInput()}
                , enums = {@EnumInput(enumClass = Type.class)}
                , booleans = {@BooleanInput()}
        )
        public OperatorParameterInWrongOrder2(int number1, boolean isValid, int number2, Type type1) {
        }
    }

    static class MissingBooleanParameterInAnnotation extends TestOperador {
        enum Type {Zero, ONE, TWO, THREE}

        @DefaultConstructor(numbers = {@NumberInput(), @NumberInput()}
                , enums = {@EnumInput(enumClass = Type.class)}
        )
        public MissingBooleanParameterInAnnotation(int number1, int number2, Type type1, boolean isValid) {
        }
    }

    static class MissingBooleanParameterInConstructor extends TestOperador {
        enum Type {Zero, ONE, TWO, THREE}

        @DefaultConstructor(numbers = {@NumberInput(), @NumberInput()}
                , enums = {@EnumInput(enumClass = Type.class)}
                , booleans = {@BooleanInput}
        )
        public MissingBooleanParameterInConstructor(int number1, int number2, Type type1) {
        }
    }

    static class OperatorParameterWithWrongType extends TestOperador {
        enum Type {Zero, ONE, TWO, THREE}

        @DefaultConstructor(numbers = {@NumberInput(), @NumberInput()},
                enums = {@EnumInput(enumClass = Type.class)}
        )
        public OperatorParameterWithWrongType(float number1, int number2, Type type1) {
        }
    }

    static class OperatorParameterWithWrongType2 extends TestOperador {
        enum Type {Zero, ONE, TWO, THREE}

        @DefaultConstructor(numbers = {@NumberInput(), @NumberInput()},
                enums = {@EnumInput(enumClass = Type.class)}
        )
        public OperatorParameterWithWrongType2(int number1, int number2, Object type1) {
        }
    }
}
