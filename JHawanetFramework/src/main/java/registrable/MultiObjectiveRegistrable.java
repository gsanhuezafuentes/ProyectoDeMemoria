package registrable;

import annotations.registrable.*;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.operator.Operator;
import model.metaheuristic.problem.Problem;
import registrable.multiobjective.PumpSchedulingNSGAIIRegister;

/**
 * It class let configure the algorithm and his operator to a multiobjective
 * problem give to know the problem to the GUI. <br>
 * <br>
 * 
 * The class that implement this interface has to have a only public constructor
 * that declare the parameters that is needed to configure the algorithm and the
 * problem. Also the constructor has to add a series of annotation. Based in the
 * parameters added in the constructor the algorithm have to be configured and
 * returned when {@link #build(String)} will be called.<br>
 * <br>
 * 
 * The annotation that the constructor has to have are:<br>
 * <br>
 * 
 * <ul>
 * <li>{@link NewProblem} : It annotation is used to assign the name to the
 * problem and the name to the algorithm used. It will be showed in the menu in
 * the GUI. It annotation is mandatory.</li>
 * 
 * <li>{@link Parameters} : It annotation declare that in this constructor will
 * be injected the parameters. It injection is realized from
 * ConfigurationDynamicWindow that is a GUI dynamically built from the
 * parameters that has been configured in this annotation. Each constructor
 * parameters has to be described through their respective annotation. If a
 * constructor parameter don't have an annotation that describe it then an
 * exception will be thrown. Eg. if a parameter of constructor will be a
 * {@link Operator} so the constructor has to be a Object parameter and the
 * {@link Parameters} annotation has to configure his
 * {@link Parameters#operators()}. This annotation is optional, but if this
 * notation is not in the constructor then the constructor can't have
 * parameters.</li>
 * </ul>
 * <br>
 * <br>
 * 
 * The order and the type in what the constructor operator are added is
 * important. The order has to be the next:<br>
 * <br>
 * 
 * <ol>
 * <li>Object: The operators will be injected in the parameters of type object.
 * It can be casted to the respective class or superclass. It is described using
 * {@link Parameters#operators()}</li>
 * <li>File: It describe a file that will be injected. Is used if the algorithm
 * need a extra file that has configuration or values. It is described using
 * {@link Parameters#files()}</li>
 * <li>int(or Integer)|double(or Double): It describe a value needed to
 * configure the problem. Eg. the number of iterations, or the number of
 * iteration without improvement. It is described using
 * {@link Parameters#numbers()} or {@link Parameters#numbersToggle()}</li>
 * </ol>
 * 
 * {@link Parameters} has a operators property, files property and numbers
 * property and numberToggle property. The operators property receive
 * {@link OperatorInput}, the files property receive {@link FileInput} and
 * numbers receive {@link NumberInput} and the numberToggle receive
 * {@link NumberToggleInput}.
 * 
 * {@link OperatorInput} denote which operator will be received. The
 * {@link OperatorInput} let one or more {@link OperatorOption} that indicate
 * what Operator can be used in this problem.<br>
 * <br>
 * 
 * {@link FileInput} denote a file. it let indicate files with information to
 * execute the algorithm.
 * 
 * {@link NumberInput} denote a int or a double value that are received by the
 * constructor.<br>
 * <br>
 * 
 * {@link NumberToggleInput} denote a int or a double value that are received by
 * the constructor but it are mutually exclusive if is
 * {@link NumberToggleInput#groupID()} are the same. <br>
 * <br>
 * 
 * A example is showed in the class {@link PumpSchedulingNSGAIIRegister}.
 *
 */
public interface MultiObjectiveRegistrable extends Registrable<Experiment<?>> {

	/**
	 * Builds a new experiment and leaves it ready for execution. This method will be
	 * called by the GUI when resolve the problem in the network opened. The call
	 * include the path of the inp file of the opened network.
	 * 
	 * @param inpPath the path of inp file, or null if there isn't a network opened.
	 * @return the experiment ready for execution
	 * @throws Exception if an exception occurs when building the algorithm
	 */
	@Override
	Experiment<?> build(String inpPath) throws Exception;

	/**
	 * Get the problem associated to {@link Algorithm} when {@link #build(String)}
	 * was called or null if it hasn't be called.
	 * 
	 * @return the associated problem if the algorithm already was created, i.e.,
	 *         {@link #build(String) already was called} by {@link #build(String)}
	 *         or null in other case.
	 */
	@Override
	Problem<?> getProblem();

}
