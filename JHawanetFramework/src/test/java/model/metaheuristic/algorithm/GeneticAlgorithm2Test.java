package model.metaheuristic.algorithm;

import exception.ApplicationException;
import model.metaheuristic.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithm2;
import model.metaheuristic.operator.crossover.CrossoverOperator;
import model.metaheuristic.operator.mutation.MutationOperator;
import model.metaheuristic.operator.selection.SelectionOperator;
import model.metaheuristic.problem.Problem;
import model.metaheuristic.solution.impl.IntegerSolution;
import model.metaheuristic.utils.evaluator.SequentialSolutionEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeneticAlgorithm2Test {

	@Mock
	Problem<IntegerSolution> problem;
	@Mock
	SelectionOperator<List<IntegerSolution>, List<IntegerSolution>> selectionOperator;
	@Mock
	CrossoverOperator<IntegerSolution> crossoverOperator;
	@Mock
	MutationOperator<IntegerSolution> mutationOperator;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test if {@link GeneticAlgorithm2#setMaxEvaluations(int)} fail with negative
	 * value.
	 * @throws Exception 
	 */
	@Test
	void setMaxEvaluations_LessThanZero_Exception() throws Exception {
		GeneticAlgorithm2<IntegerSolution> algorithm = new GeneticAlgorithm2<IntegerSolution>(problem, 10,
				selectionOperator, crossoverOperator, mutationOperator, new SequentialSolutionEvaluator<>());
		assertThrows(ApplicationException.class, () -> algorithm.setMaxEvaluations(-1));
	}

	/**
	 * Test if
	 * {@link GeneticAlgorithm2#setMaxNumberOfIterationWithoutImprovement(int)} fail
	 * with negative value.
	 */
	@Test
	void setMaxNumberOfIterationWithoutImprovement_LessThanZero_Exception() {
		GeneticAlgorithm2<IntegerSolution> algorithm = new GeneticAlgorithm2<IntegerSolution>(problem, 10,
				selectionOperator, crossoverOperator, mutationOperator,  new SequentialSolutionEvaluator<>());
		assertThrows(ApplicationException.class, () -> algorithm.setMaxNumberOfIterationWithoutImprovement(-1));
	}

	/**
	 * Test if
	 * {@link GeneticAlgorithm2#setMaxNumberOfIterationWithoutImprovement(int)}
	 * after use {@link GeneticAlgorithm2#setMaxEvaluations(int)} disable
	 * {@link GeneticAlgorithm2#setMaxEvaluations(int)}.
	 */
	@Test
	void SetMaxNumberOfIterationWithoutImprovement_AfterUseSetMaxEvaluations_MaxEvaluationConditionZero() {
		GeneticAlgorithm2<IntegerSolution> algorithm = new GeneticAlgorithm2<IntegerSolution>(problem, 10,
				selectionOperator, crossoverOperator, mutationOperator,  new SequentialSolutionEvaluator<>());
		algorithm.setMaxEvaluations(10000);
		algorithm.setMaxNumberOfIterationWithoutImprovement(10000);
		assertEquals(0, algorithm.getMaxEvaluations());
		assertEquals(10000, algorithm.getMaxNumberOfIterationWithoutImprovement());
	}

	/**
	 * Test if {@link GeneticAlgorithm2#setMaxEvaluations(int)} after use
	 * {@link GeneticAlgorithm2#setMaxNumberOfIterationWithoutImprovement(int)}
	 * disable
	 * {@link GeneticAlgorithm2#setMaxNumberOfIterationWithoutImprovement(int)}.
	 */
	@Test
	void setMaxEvaluations_AfterUseSetMaxNumberOfIterationWithoutImprovement_MaxNumberOfIterationWithoutImprovementZero() {
		GeneticAlgorithm2<IntegerSolution> algorithm = new GeneticAlgorithm2<IntegerSolution>(problem, 10,
				selectionOperator, crossoverOperator, mutationOperator,  new SequentialSolutionEvaluator<>());
		algorithm.setMaxNumberOfIterationWithoutImprovement(1000);
		algorithm.setMaxEvaluations(10000);
		assertEquals(0, algorithm.getMaxNumberOfIterationWithoutImprovement());
		assertEquals(10000, algorithm.getMaxEvaluations());
	}
}
