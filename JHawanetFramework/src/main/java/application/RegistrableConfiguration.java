package application;

import registrable.MultiObjectiveRegistrable;
import registrable.SingleObjectiveRegistrable;
import registrable.multiobjective.PumpSchedulingNSGAIIRegister;
import registrable.multiobjective.PumpSchedulingSMPSORegister;
import registrable.multiobjective.PumpSchedulingSPA2Register;
import registrable.singleobjective.PipeOptimizingRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contain the problem that can be resolved by this application.  The problems
 * added here are showned in menu of the application GUI.
 *
 */
public final class RegistrableConfiguration {
	public static final List<Class<? extends SingleObjectiveRegistrable>> SINGLEOBJECTIVES_PROBLEMS = new ArrayList<>();
	public static final List<Class<? extends MultiObjectiveRegistrable>> MULTIOBJECTIVES_PROBLEMS = new ArrayList<>();

	//Add here the Registrable clases (SingleObjectives).
	static {
		SINGLEOBJECTIVES_PROBLEMS.add(PipeOptimizingRegister.class);
	}
	
	// Multiobjectives
	static {
		MULTIOBJECTIVES_PROBLEMS.add(PumpSchedulingNSGAIIRegister.class);
		MULTIOBJECTIVES_PROBLEMS.add(PumpSchedulingSMPSORegister.class);
		MULTIOBJECTIVES_PROBLEMS.add(PumpSchedulingSPA2Register.class);

	}
 
}
