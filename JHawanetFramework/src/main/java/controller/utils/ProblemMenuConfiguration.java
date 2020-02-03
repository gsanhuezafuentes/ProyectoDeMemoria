package controller.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import annotations.registrable.FileInput;
import annotations.registrable.NewProblem;
import annotations.registrable.NumberInput;
import annotations.registrable.OperatorInput;
import annotations.registrable.OperatorOption;
import annotations.registrable.Parameters;
import application.Configuration;
import controller.ConfigurationDynamicWindowController;
import controller.problems.Registrable;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import view.utils.CustomDialogs;

/**
 * In this class are added the problems that will be added to menu using
 * reflection. <br>
 * <br>
 * 
 * To add a new problem go to {@link Configuration}. Using reflection API, the problem and his annotation will be readed
 * to generate a GUI.<br>
 * <br>
 * 
 * 
 */
public class ProblemMenuConfiguration {

	/**
	 * Add to menu a menu item for each problem registered. Also it method add the
	 * setOnAction to add menuItem and when it event will be detected show the
	 * windows of configuration.
	 * 
	 * @param menu           the menu where the problem has been added
	 * @param algorithmEvent the event that will be fired when the
	 *                       RegistrableProblem is created.
	 */
	public void addMonoObjectiveProblems(Menu menu, CustomCallback algorithmEvent) {
		Map<String, Menu> addedMenu = new HashMap<>();
		for (Class<? extends Registrable> registrable : Configuration.MONOOBJECTIVES_PROBLEMS) {
			ReflectionUtils.validateRegistrableProblem(registrable);
			ReflectionUtils.validateOperators(registrable);
			String problemName = ReflectionUtils.getNameOfProblem(registrable);
			if (!addedMenu.containsKey(problemName)) {
				Menu newMenu = new Menu(problemName);
				menu.getItems().add(newMenu);
				addedMenu.put(problemName, newMenu);
			}
			Menu problemMenu = addedMenu.get(problemName);
			MenuItem menuItem = new MenuItem(ReflectionUtils.getNameOfAlgorithm(registrable));
			problemMenu.getItems().add(menuItem);
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
			CustomCallback algorithmEvent) {

		// If the registrable class has a constructor with parameters so a new window to
		// configure its is created,
		if (ReflectionUtils.getNumberOfParameterInRegistrableConstructor(registrable) > 0) {
			ConfigurationDynamicWindowController configurationController = new ConfigurationDynamicWindowController(
					registrable, algorithmEvent);
			configurationController.showAssociatedWindow();
		} else { // If the registrable class has a constructor without parameters
					// algorithmEvent.notify is called.
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