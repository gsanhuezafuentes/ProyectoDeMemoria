package controller.problems;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import annotations.registrable.FileInput;
import annotations.registrable.NewProblem;
import annotations.registrable.NumberInput;
import annotations.registrable.OperatorInput;
import annotations.registrable.OperatorOption;
import annotations.registrable.Parameters;
import controller.ConfigurationDynamicWindowController;
import controller.utils.AlgorithmCreationNotification;
import controller.utils.ReflectionUtils;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import view.ConfigurationDynamicWindow;
import view.utils.CustomDialogs;

/**
 * In this class are added the problems that will be added to menu using
 * reflection. <br>
 * <br>
 * 
 * To add a new problem modify the constructor and add the problem to list of
 * problems. Using reflection API, the problem and his annotation will be readed
 * to generate a GUI.<br>
 * <br>
 * 
 * A problem class has to extend the interface {@link Registrable}. The problem
 * class has to have a public constructor with the {@link NewProblem}
 * annotation.<br>
 * <br>
 * 
 * The same constructor with {@link NewProblem} annotation also has to have the
 * {@link Parameters} annotation if it has parameters. {@link Parameters} add
 * the info about the parameters received by the method. {@link Parameters} has
 * a operators property, files property and numbers property. The operators
 * property receive {@link OperatorInput}, the files property receive
 * {@link FileInput} and numbers receive {@link NumberInput}.
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
 * The constructor has to have first all operator parameters, next to all files
 * parameter and the last all the number parameters.<br>
 * <br>
 * 
 * For example: <br>
 * <br>
 * {@code CostProblem(Object selectionOperator, Object crossoverOperator,
			Object mutationOperator, File gama, int numberWithoutImprovement, int maxEvaluations)}<br>
 * <br>
 * The problem added in this class isn't the model.metaheuristic.problem used by
 * algorithm. It is a class that only is used to describe the problem to be
 * parsed using reflection.
 * 
 */
public class ProblemRegistrar {
	private static ProblemRegistrar instance = new ProblemRegistrar();
	private List<Class<? extends Registrable>> problems;

	/**
	 * Get a instance of this class. It is based in Singleton Pattern
	 * 
	 * @return the instance of this class.
	 */
	public static ProblemRegistrar getInstance() {
		if (instance == null) {
			instance = new ProblemRegistrar();
		}
		return instance;
	}

	private ProblemRegistrar() {
		this.problems = new ArrayList<>();
		this.problems.add(InversionCostRegister.class);
		this.problems.add(TestProblemRegister.class);

	}

	/**
	 * Add to menu a menu item for each problem registered. Also it method add the
	 * setOnAction to add menuItem and when it event will be detected show the
	 * windows of configuration.
	 * 
	 * @param menu           the menu where the problem has been added
	 * @param algorithmEvent the event that will be fired when the
	 *                       RegistrableProblem is created.
	 */
	public void register(Menu menu, AlgorithmCreationNotification algorithmEvent) {
		for (Class<? extends Registrable> registrable : this.problems) {
			ReflectionUtils.validateRegistrableProblem(registrable);
			ReflectionUtils.validateOperators(registrable);
			MenuItem menuItem = new MenuItem(ReflectionUtils.getNameOfProblem(registrable));
			menu.getItems().add(menuItem);
			menuItem.setOnAction(evt -> menuItemEventHander(evt, registrable, algorithmEvent));
		}

	}

	/**
	 * Event handler called when a menu item is touched. It event show a windows
	 * created reading annotation with reflection in registrable object.
	 * 
	 * @param evt            the event info returned to menuItem.setOnAction
	 * @param registrable    the problem class
	 * @param algorithmEvent a event called when the window showed create the
	 *                       algorithm
	 */
	private void menuItemEventHander(ActionEvent evt, Class<? extends Registrable> registrable,
			AlgorithmCreationNotification algorithmEvent) {

		// If the registrable class has a constructor with parameters so a new window to configure its is created,
		if (ReflectionUtils.getNumberOfParameterInRegistrableConstructor(registrable) > 0) {
			ConfigurationDynamicWindowController configurationController = new ConfigurationDynamicWindowController(
					registrable, algorithmEvent);
			configurationController.showAssociatedWindow();
		}
		else { // If the registrable class has a constructor without parameters algorithmEvent.notify is called.
			try {
				Registrable registrableInstance = ReflectionUtils.createRegistrableInstance(registrable);
				algorithmEvent.notify(registrableInstance);
			} catch (InvocationTargetException e) {
				CustomDialogs.showExceptionDialog("Error", "Exception throw by the constructor",
						"Can't be created an instance of " + registrable.getName(), e.getCause());
			}
		}
	}

}