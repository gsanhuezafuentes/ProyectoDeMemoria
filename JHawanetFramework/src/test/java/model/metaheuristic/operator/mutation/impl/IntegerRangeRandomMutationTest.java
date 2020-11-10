package model.metaheuristic.operator.mutation.impl;

import epanet.core.EpanetException;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.util.random.BoundedRandomGenerator;
import model.metaheuristic.util.random.RandomGenerator;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

class IntegerRangeRandomMutationTest {
    @Mock private RandomGenerator<Double> randomGenerator;
    @Mock private BoundedRandomGenerator<Integer> pointRandomGenerator;

    @BeforeEach
    void setupAll(){
        MockitoAnnotations.initMocks(this);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-2,-0.5, -0.1})
    void shouldConstructorReturnExceptionWhenIsUsedANagativeProbability(double mutationProbability) {
        assertThrows(RuntimeException.class, () -> new IntegerRangeRandomMutation(mutationProbability, 0));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1,-2,-5})
    void shouldConstructorReturnExceptionWhenIsUsedANegativeRange(int range) {
        assertThrows(RuntimeException.class, () -> new IntegerRangeRandomMutation(0.1, range));
    }

    @Test
    void souldExecuteMutateTheVariablesWhenRandomGeneratorGenerateBelowValuesThanTheMutationProbability() {
        MockProblem problem = new MockProblem(3);
        IntegerSolution solution = problem.createSolution();
        solution.setVariable(0, 0);
        solution.setVariable(1, 2);
        solution.setVariable(2, 4);

        //init mock
        when(randomGenerator.getRandomValue()).thenReturn(0.2, 0.2, 0.4);
        when(pointRandomGenerator.getRandomValue(anyInt(), anyInt())).thenReturn(2, 0, 5);

        IntegerRangeRandomMutation mutation = new IntegerRangeRandomMutation(0.3, 2,randomGenerator, pointRandomGenerator);
        mutation.execute(solution);

        int[] result = solution.getVariables().stream().mapToInt(value -> (int) value).toArray();
        assertArrayEquals(new int[]{2,0,4}, result);
    }

    @Test
    void souldExecuteDoNotMutateTheVariablesWhenRandomGeneratorGenerateAllValuesAboveThanTheMutationProbability() {
        MockProblem problem = new MockProblem(3);
        IntegerSolution solution = problem.createSolution();
        solution.setVariable(0, 0);
        solution.setVariable(1, 2);
        solution.setVariable(2, 4);

        //init mock
        when(randomGenerator.getRandomValue()).thenReturn(0.5, 0.4, 0.4);
        when(pointRandomGenerator.getRandomValue(anyInt(), anyInt())).thenReturn(2, 0, 5);

        IntegerRangeRandomMutation mutation = new IntegerRangeRandomMutation(0.3, 2,randomGenerator, pointRandomGenerator);
        mutation.execute(solution);

        int[] result = solution.getVariables().stream().mapToInt(value -> (int) value).toArray();
        assertArrayEquals(new int[]{0,2,4}, result);
    }

    @Test
    void souldExecuteReturnTheSameValuesWhenRangeOfMutationIsZero() {
        MockProblem problem = new MockProblem(3);
        IntegerSolution solution = problem.createSolution();
        solution.setVariable(0, 0);
        solution.setVariable(1, 2);
        solution.setVariable(2, 4);

        //init mock
        when(randomGenerator.getRandomValue()).thenReturn(0.1, 0.2, 0.3);

        IntegerRangeRandomMutation mutation = new IntegerRangeRandomMutation(0.3, 0,randomGenerator);
        mutation.execute(solution);

        int[] result = solution.getVariables().stream().mapToInt(value -> (int) value).toArray();
        assertArrayEquals(new int[]{0,2,4}, result);
    }


    private static class MockProblem implements Problem<IntegerSolution>{
        private final int numberOfVariables;
        private final int numberOfObjectives;
        private final int mumberOfConstrains;
        private final int[] lowerBound;
        private final int[] upperBound;

        MockProblem(int numberOfVariables){
            this.numberOfVariables = numberOfVariables;
            this.numberOfObjectives = 2;
            this.mumberOfConstrains = 1;
            this.lowerBound = new int[this.numberOfVariables];
            this.upperBound = new int[this.numberOfVariables];

            for (int i = 0; i < this.numberOfVariables; i++){
                this.lowerBound[i] = 0;
                this.upperBound[i] = 10;
            }

        }

        @Override
        public int getNumberOfVariables() {
            return this.numberOfVariables;
        }

        @Override
        public int getNumberOfObjectives() {
            return this.numberOfObjectives;
        }

        @Override
        public int getNumberOfConstraints() {
            return this.getNumberOfConstraints();
        }

        @Override
        public void evaluate(IntegerSolution solution) throws EpanetException {
            solution.setObjective(0, 2);
            solution.setObjective(1, 4);
        }

        @NotNull
        @Override
        public IntegerSolution createSolution() {
            return new IntegerSolution(this);
        }

        @Override
        public double getLowerBound(int index) {
            return this.lowerBound[index];
        }

        @Override
        public double getUpperBound(int index) {
            return this.upperBound[index];
        }

        @Override
        public @NotNull String getName() {
            return "test class";
        }


    }
}