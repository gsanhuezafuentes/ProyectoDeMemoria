package application;

import registrable.MultiObjectiveRegistrable;
import registrable.SingleObjectiveRegistrable;
import registrable.multiobjective.PumpSchedulingRegister;
import registrable.singleobjective.PipeOptimizingRegister;
import registrable.singleobjective.TestProblemRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contain the problem that can be resolved by this application. 
 *
 */
public final class Configuration {
	public static final List<Class<? extends SingleObjectiveRegistrable>> SINGLEOBJECTIVES_PROBLEMS = new ArrayList<>();
	public static final List<Class<? extends MultiObjectiveRegistrable>> MULTIOBJECTIVES_PROBLEMS = new ArrayList<>();

	//Add here the Registrable clases (SingleObjectives).
	static {
		SINGLEOBJECTIVES_PROBLEMS.add(PipeOptimizingRegister.class);
		SINGLEOBJECTIVES_PROBLEMS.add(TestProblemRegister.class);
	}
	
	// Multiobjectives
	static {
		MULTIOBJECTIVES_PROBLEMS.add(PumpSchedulingRegister.class);
	}
 
}
