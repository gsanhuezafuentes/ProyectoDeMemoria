package model.metaheuristic.operator.mutation;

import model.metaheuristic.solution.IntegerSolution;
import model.metaheuristic.utils.random.BoundedRandomGenerator;
import model.metaheuristic.utils.random.JavaRandom;
import model.metaheuristic.utils.random.RandomGenerator;

public class IntegerSimpleRandomMutation implements MutationOperator<IntegerSolution>  {
	
  private double mutationProbability ;
  private RandomGenerator<Double> randomGenerator ;
  private BoundedRandomGenerator<Integer> pointRandomGenerator ;

  /**  Constructor */
  public IntegerSimpleRandomMutation(double probability) {
	  this(probability, () -> JavaRandom.getInstance().nextDouble() , (a, b) -> JavaRandom.getInstance().nextInt(a, b));
  }
  
  /**  Constructor */
  public IntegerSimpleRandomMutation(double probability, RandomGenerator<Double> randomGenerator,BoundedRandomGenerator<Integer> pointRandomGenerator) {
    if (probability < 0) {
      throw new RuntimeException("Mutation probability is negative: " + mutationProbability) ;
    }

  	this.mutationProbability = probability ;
    this.randomGenerator = randomGenerator ;
    this.pointRandomGenerator = pointRandomGenerator;
  }
  
  /* Getters */
  public double getMutationProbability() {
    return mutationProbability;
  }

  /* Setters */
  public void setMutationProbability(double mutationProbability) {
    this.mutationProbability = mutationProbability;
  }

	@Override
	public IntegerSolution execute(IntegerSolution solution) throws RuntimeException {
	  if (null == solution) {
	      throw new RuntimeException("Null parameter") ;
	    }

	    doMutation(mutationProbability, solution) ;
	    
	    return solution;
	}
	
	/** Implements the mutation operation */
	private void doMutation(double probability, IntegerSolution solution) {
		
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (randomGenerator.getRandomValue() <= probability) {
      		Integer value = pointRandomGenerator.getRandomValue(solution.getLowerBound(i), solution.getUpperBound(i) + 1); // The last element is exclude for it is needed the +1     	
      	solution.setVariable(i, value) ;
      }
    }
    
	}

}
