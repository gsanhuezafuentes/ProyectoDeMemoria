package controller.problems;

import annotations.registrable.NewProblem;
import annotations.registrable.Parameters;
import model.metaheuristic.algorithm.Algorithm;
import model.metaheuristic.operator.Operator;

/**
 * It class let configure the algorithm and his operator to a problem give to
 * know the problem to the GUI. <br>
 * <br>
 * 
 * It interface is based in factory pattern. The class that implement this
 * interface has to have a only public constructor that declare the parameters
 * that is needed to configure the algorithm and the problem. Also the
 * constructor has to add a series of annotation. Based in the parameters added
 * in the constructor the algorithm have to be configured and returned when
 * {@link #build(String)} will be called.<br>
 * <br>
 * 
 * The annotation that the constructor has to have are:<br>
 * <br>
 * 
 * <ul>
 * <li>{@link NewProblem} : It annotation is used to assign the name to the
 * problem that will be showned in the menu in the GUI. It annotation is
 * mandatory.</li>
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
 * {@link Parameters#numbers()}</li>
 * </ol>
 * 
 * 
 * 
 *
 */
public interface Registrable {

	/**
	 * Builds a new algorithm and leaves it ready for execution. This method will be
	 * called by the GUI when resolve the problem in the network opened. The call
	 * include the path of the inp file of the opened network.
	 * 
	 * @param String with the path of inp file, or null if there isn't a network opened.
	 * @return the algorithm ready for execution
	 * @throws if an exception occurs when building the algorithm
	 */
	Algorithm<?> build(String inpPath) throws Exception;
}
