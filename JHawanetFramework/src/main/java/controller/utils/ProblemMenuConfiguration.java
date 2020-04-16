package controller.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import application.Configuration;
import controller.ConfigurationDynamicWindowController;
import controller.problems.MonoObjectiveRegistrable;
import controller.problems.MultiObjectiveRegistrable;
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
 * To add a new problem go to {@link Configuration}. Using reflection API, the
 * problem and his annotation will be readed to generate a GUI.<br>
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
	public void addMonoObjectiveProblems(Menu menu, CustomCallback<MonoObjectiveRegistrable> algorithmEvent) {
		Map<String, Menu> addedMenu = new HashMap<>();
		for (Class<? extends MonoObjectiveRegistrable> registrable : Configuration.MONOOBJECTIVES_PROBLEMS) {
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
	 * Add to menu a menu item for each problem registered. Also it method add the
	 * setOnAction to add menuItem and when it event will be detected show the
	 * windows of configuration.
	 * 
	 * @param menu           the menu where the problem has been added
	 * @param experimentEvent the event that will be fired when the
	 *                       RegistrableProblem is created.
	 */
	public void addMultiObjectiveProblems(Menu menu, CustomCallback<MultiObjectiveRegistrable> experimentEvent) {
		Map<String, Menu> addedMenu = new HashMap<>();
		for (Class<? extends MultiObjectiveRegistrable> registrable : Configuration.MULTIOBJECTIVES_PROBLEMS) {
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
			menuItem.setOnAction(evt -> menuItemEventHander(evt, registrable, experimentEvent));
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
	private <T extends Registrable<?>> void menuItemEventHander(ActionEvent evt, Class<? extends T> registrable,
			CustomCallback<T> algorithmEvent) {

		// If the registrable class has a constructor with parameters so a new window to
		// configure its is created,
		if (ReflectionUtils.getNumberOfParameterInRegistrableConstructor(registrable) > 0) {
			ConfigurationDynamicWindowController<T> configurationController = new ConfigurationDynamicWindowController<>(
					registrable, algorithmEvent);
			configurationController.showAssociatedWindow();
		} else { // If the registrable class has a constructor without parameters
					// algorithmEvent.notify is called.
			try {
				T registrableInstance = ReflectionUtils.createRegistrableInstance(registrable);
				algorithmEvent.notify(registrableInstance);
			} catch (InvocationTargetException e) {
				CustomDialogs.showExceptionDialog("Error", "Exception throw by the constructor",
						"Can't be created an instance of " + registrable.getName(), e.getCause());
			}
		}
	}

}