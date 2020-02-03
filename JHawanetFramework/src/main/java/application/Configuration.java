package application;

import java.util.ArrayList;
import java.util.List;

import controller.problems.PipeOptimizingRegister;
import controller.problems.Registrable;
import controller.problems.TestProblemRegister;

/**
 * This class contain the problem that can be resolved by this application. 
 *
 */
public final class Configuration {
	public static final List<Class<? extends Registrable>> MONOOBJECTIVES_PROBLEMS = new ArrayList<>();
	public static final List<Class<? extends Registrable>> MULTIOBJECTIVES_PROBLEMS = new ArrayList<>();

	//Add here the Registrable clases (Monoobjective).
	static {
		MONOOBJECTIVES_PROBLEMS.add(PipeOptimizingRegister.class);
		MONOOBJECTIVES_PROBLEMS.add(TestProblemRegister.class);
	}
	
	// Multiobjectives
	static {
		
	}
 
}
