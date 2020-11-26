package application;

import model.metaheuristic.qualityindicator.impl.*;
import model.metaheuristic.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import model.metaheuristic.qualityindicator.impl.hypervolume.impl.WFGHypervolume;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registrable.MultiObjectiveRegistrable;
import registrable.SingleObjectiveRegistrable;
import registrable.multiobjective.PumpSchedulingNSGAIIRegister;
import registrable.multiobjective.PumpSchedulingSMPSORegister;
import registrable.multiobjective.PumpSchedulingSPA2Register;
import registrable.singleobjective.BorrameRegistrable;
import registrable.singleobjective.PipeOptimizingRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contain the problem that can be resolved by this application.  The problems
 * added here are showned in menu of the application GUI.
 *
 */
public final class RegistrableConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(RegistrableConfiguration.class);

	public static final List<Class<? extends SingleObjectiveRegistrable>> SINGLEOBJECTIVES_PROBLEMS = new ArrayList<>();
	public static final List<Class<? extends MultiObjectiveRegistrable>> MULTIOBJECTIVES_PROBLEMS = new ArrayList<>();
	public static final List<Class<? extends GenericIndicator>> INDICATORS = new ArrayList<>();

	//Add here the Registrable clases (SingleObjectives).
	static {
		LOGGER.debug("Initializing SINGLEOBJECTIVES_PROBLEMS in RegistrableConfiguration class.");
		SINGLEOBJECTIVES_PROBLEMS.add(PipeOptimizingRegister.class);
		SINGLEOBJECTIVES_PROBLEMS.add(BorrameRegistrable.class);
	}
	
	// Multiobjectives
	static {
		LOGGER.debug("Initializing MULTIOBJECTIVES_PROBLEMS in RegistrableConfiguration class.");
		MULTIOBJECTIVES_PROBLEMS.add(PumpSchedulingNSGAIIRegister.class);
		MULTIOBJECTIVES_PROBLEMS.add(PumpSchedulingSMPSORegister.class);
		MULTIOBJECTIVES_PROBLEMS.add(PumpSchedulingSPA2Register.class);
	}

	// Indicators used with multiobjectives value. This as to be a empty constructor.
	static {
		LOGGER.debug("Initializing INDICATORS in RegistrableConfiguration class.");
		INDICATORS.add(NormalizedHypervolume.class);
		INDICATORS.add(InvertedGenerationalDistance.class);
		INDICATORS.add(Epsilon.class);
		INDICATORS.add(Spread.class);
		INDICATORS.add(InvertedGenerationalDistancePlus.class);
		INDICATORS.add(WFGHypervolume.class);
		INDICATORS.add(PISAHypervolume.class);
		INDICATORS.add(GeneralizedSpread.class);
		INDICATORS.add(GenerationalDistance.class);
	}
 
}
