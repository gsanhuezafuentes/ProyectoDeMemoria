package controller;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import controller.problems.Registrable;
import controller.utils.CustomCallback;
import controller.utils.ReflectionUtils;
import exception.ApplicationException;
import view.ConfigurationDynamicWindow;
import view.utils.CustomDialogs;

public class ConfigurationDynamicWindowController<T extends Registrable<?>> {
	private final CustomCallback<T> algorithmEvent;
	private final Class<? extends T> problemClass;
	private final ConfigurationDynamicWindow<T> view;

	public ConfigurationDynamicWindowController(Class<? extends T> registrable,
			CustomCallback<T> algorithmEvent) {
		this.problemClass = Objects.requireNonNull(registrable);
		this.algorithmEvent = Objects.requireNonNull(algorithmEvent);
		this.view = new ConfigurationDynamicWindow<>(this, registrable);
	}

	/**
	 * Is a class to fire the notification when the algorithm is ready.
	 * 
	 * @param registrable the algorithm
	 * @throws ApplicationException if there isn't register the notification
	 *                              callback
	 */
	private void notifyAlgorithmCreation(T registrable) throws ApplicationException {

		algorithmEvent.notify(registrable);
	}

	/**
	 * Is called when the run button is pressed in view. It method create the
	 * registrable instance based in the input field. When the algorithm is created
	 * an {@link CustomCallback} is fired.
	 * 
	 * @param operatorsAndConfig A map where the key are the operators and the
	 *                           values are the configuration. If there isn't
	 *                           operator configurated an empty map has to be send.
	 * @param fileInputs         An array with file inputs. If there isn't file
	 *                           inputs so receive a empty array of File[]. If there
	 *                           are file inputs but some hasn't been setting up
	 *                           with a path return null in the respective index.
	 * @param numberInputs       An array with the number setting in the view. If
	 *                           there isn't number inputs so receive a empty array
	 *                           of Number[].
	 * @param toggleInputs       An array with the number setting in view for the
	 *                           toggle input.If there isn't number inputs so
	 *                           receive a empty array of Number[].
	 */
	public void onRunButtonClick(Map<Class<?>, List<Number>> operatorsAndConfig, File[] fileInputs,
			Number[] numberInputs, Number[] toggleInputs) {
		Objects.requireNonNull(operatorsAndConfig);
		Objects.requireNonNull(fileInputs);
		Objects.requireNonNull(numberInputs);
		Objects.requireNonNull(toggleInputs);

		int parameterSize = operatorsAndConfig.size() + fileInputs.length + numberInputs.length + toggleInputs.length;
		Object[] parameters = new Object[parameterSize];
		int i = 0;
		// create the operators and add to parameters array
		for (Class<?> operator : operatorsAndConfig.keySet()) {
			try {
				Object operatorObject = ReflectionUtils.getDefaultConstructor(operator)
						.newInstance(operatorsAndConfig.get(operator).toArray());
				parameters[i++] = operatorObject;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				CustomDialogs.showExceptionDialog("Error", "Error in the creation of the operator",
						"The operator " + operator.getName() + " can't be created", e);
				return;
			}
		}

		for (File file : fileInputs) {
			parameters[i++] = file;
		}

		for (Number numberInput : numberInputs) {
			parameters[i++] = numberInput;
		}
		
		for (Number toggleInput : toggleInputs) {
			parameters[i++] = toggleInput;
		}

		try {
			T registrable = ReflectionUtils.createRegistrableInstance(this.problemClass, parameters);
			this.view.close();
			notifyAlgorithmCreation(registrable);
		} catch (InvocationTargetException e) {
			CustomDialogs.showExceptionDialog("Error", "Exception throw by the constructor",
					"Can't be created an instance of " + this.problemClass.getName(), e.getCause());
		}

	}

	/**
	 * Get the associated view component to this controller.
	 * 
	 * @return the window.
	 */
	public ConfigurationDynamicWindow<T> getAssociatedView() {
		return this.view;
	}

	/**
	 * Show the associated window
	 */
	public void showAssociatedWindow() {
		this.view.show();
	}
}
