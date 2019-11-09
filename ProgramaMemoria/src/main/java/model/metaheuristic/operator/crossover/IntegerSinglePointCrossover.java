package model.metaheuristic.operator.crossover;

import java.util.ArrayList;
import java.util.List;

import model.metaheuristic.utils.random.BoundedRandomGenerator;
import model.metaheuristic.utils.random.JavaRandom;
import model.metaheuristic.utils.random.RandomGenerator;
import model.metaheuristic.solution.IntegerSolution;



public class IntegerSinglePointCrossover implements CrossoverOperator<IntegerSolution> {

private double crossoverProbability; 	
private RandomGenerator<Double> crossoverRandomGenerator ;
private BoundedRandomGenerator<Integer> pointRandomGenerator ;	
	
/** Constructor */
public IntegerSinglePointCrossover(double crossoverProbability) {
	  this(crossoverProbability, () -> JavaRandom.getInstance().nextDouble(), (a, b) -> JavaRandom.getInstance().nextInt(a, b));
}	

/** Constructor */
public IntegerSinglePointCrossover(double crossoverProbability, RandomGenerator<Double> randomGenerator) {
	  this(crossoverProbability, randomGenerator, (a, b) -> JavaRandom.getInstance().nextInt(a, b));
}

/** Constructor */
public IntegerSinglePointCrossover(double crossoverProbability, RandomGenerator<Double> crossoverRandomGenerator, BoundedRandomGenerator<Integer> pointRandomGenerator) {
  if (crossoverProbability < 0) {
    throw new RuntimeException("Crossover probability is negative: " + crossoverProbability) ;
  }
  this.crossoverProbability = crossoverProbability;
  this.crossoverRandomGenerator = crossoverRandomGenerator ;
  this.pointRandomGenerator = pointRandomGenerator ;
}	


/* Getter */
public double getCrossoverProbability() {
  return crossoverProbability;
}

/* Setter */
public void setCrossoverProbability(double crossoverProbability) {
  this.crossoverProbability = crossoverProbability;
}

	@Override
public List<IntegerSolution> execute(List<IntegerSolution> solutions) {
    if (solutions == null) {
      throw new RuntimeException("Null parameter") ;
    } else if (solutions.size() != 2) {
      throw new RuntimeException("There must be two parents instead of " + solutions.size()) ;
    }

    return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1)) ;
}
	
public List<IntegerSolution> doCrossover(double probability, IntegerSolution parent1, IntegerSolution parent2)  {
	 List<IntegerSolution> offspring = new ArrayList<>(2);
	 offspring.add((IntegerSolution) parent1.copy()) ;
	 offspring.add((IntegerSolution) parent2.copy()) ;
	 
	 if (crossoverRandomGenerator.getRandomValue() < probability) {
		 // 1. Get the total number of bits
		 int totalNumberOfVariables = parent1.getNumberOfVariables();
	     
		// 2. Calculate the point to make the crossover
	      int crossoverPoint = pointRandomGenerator.getRandomValue(0, totalNumberOfVariables); // Random between 0 and (totalNumberOfVariables - 1)
	      
	    // crossover 
	    for (int i = crossoverPoint; i <totalNumberOfVariables ; i++) {
			offspring.get(0).setVariable(i, parent2.getVariable(i));
			offspring.get(1).setVariable(i, parent1.getVariable(i));
		}
	 }
	 
	 return offspring;
}
	
	@Override
	public int getNumberOfRequiredParents() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		// TODO Auto-generated method stub
		return 2;
	}

}
