package registrable;

import annotations.*;
import model.metaheuristic.operator.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import registrable.singleobjective.PipeOptimizingRegister;

import java.util.Map;

/**
 * It is the superinterface to {@link SingleObjectiveRegistrable} and {@link MultiObjectiveRegistrable}. <br>
 * <br>
 * 
 * The class that implement this interface has to have a only public constructor
 * that declare the parameters that is needed to configure the experiment and the
 * problem. Also the constructor has to add a series of annotation. Based in the
 * parameters added in the constructor the experiment have to be configured and
 * returned when {@link #build(String)} will be called.<br>
 * <br>
 * 
 * The annotation that the constructor has to have are:<br>
 * <br>
 * 
 * <ul>
 * <li>{@link NewProblem} : It annotation is used to assign the name to the
 * problem and the name to the algorithm to use within experiment. It will be showed in the menu in
 * the GUI. It annotation is mandatory.</li>
 * 
 * <li>{@link Parameters} : It annotation declare that in this constructor will
 * be injected the parameters. It injection is realized from
 * ConfigurationDynamicWindow that is a GUI dynamically built from the
 * parameters that has been configured in this annotation. Each constructor
 * parameters has to be described through their respective annotation. If a
 * constructor parameter don't have an annotation that describe it then an
 * exception will be thrown. Eg. if a parameter of constructor will be a
 * Object so in the parameter annotation has to be a {@link Operator} annotation
 * the describe the type of element to inject. The {@link Operator} has to be
 * added to {@link Parameters#operators()}. This annotation is optional, but if this
 * notation is not in the constructor then the constructor can't have
 * parameters.</li>
 * </ul>
 * <br>
 * <br>
 * 
 * The order and the type in what the constructor params are added is
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
 * <p>
 * {@link Parameters} has a operators property, files property, numbers
 * property and numberToggle property. The operators property receive
 * {@link OperatorInput}, the files property receive {@link FileInput} and
 * numbers receive {@link NumberInput} and the numberToggle receive
 * {@link NumberToggleInput}.
 * 
 * {@link OperatorInput} denote that a operator will be received and the type of it (Eg. Selection, Mutation, etc). The
 * {@link OperatorInput} let one or more {@link OperatorOption} that indicate
 * what Operator can be used in this problem.<br>
 * <br>
 * 
 * {@link FileInput} denote a file. it let use files with information to
 * execute the algorithm. If {@link FileInput#type()} is {@link FileInput.Type#SAVE}
 * it open the FileChooser window in save mode.
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
 * A example is showed in the class {@link PipeOptimizingRegister}.
 *
 */
public interface Registrable<R> {

	/**
	 * Builds a new experiment and leaves it ready for execution. This method will be
	 * called by the GUI when resolve the problem in the network opened. The call
	 * include the path of the inp file of the opened network.
	 * 
	 * @param inpPath the path of inp file, or null if there isn't a network opened.
	 * @return the experiment ready for execution
	 * @throws Exception if an exception occurs when building the experiment
	 */
	@NotNull
	R build(String inpPath) throws Exception;

	/**
	 * Return a map where save the value of configuration used.
	 *
	 * <p>
	 * This method let add more information to ResultWindow because add new columns with the values added here.
	 * The key will be used has the column name and the value as value of column.
	 * <p>
	 * If the map has no value or the method return null. So the ResultWindow only show objectives and the variables.
	 *
	 * @return the map or null.
	 */
	@Nullable
	default Map<String, String> getParameters(){
		return null;
	}

}
