package view;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import annotations.DefaultConstructor;
import annotations.NumberInput;
import annotations.OperatorInput;
import annotations.OperatorOption;
import annotations.Parameters;
import exception.ApplicationException;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.metaheuristic.algorithm.Algorithm;
import view.problems.Registrable;
import view.utils.AlgorithmCreationNotification;
import view.utils.CustomDialogs;
import view.utils.ReflectionUtils;

public class ConfigurationDynamicWindow extends VBox {

	private final Stage window;

	private AlgorithmCreationNotification algorithmEvent;
	private Registrable problem;
	private GridPane operatorLayout;
	private GridPane primitiveLayout;
	private int operatorGridRowCount;
	private int numberGridRowCount;
	private double defaultSpace = 5;
	private BooleanBinding isRunButtonDisabled;
	private Map<Class<?>, List<Number>> resultOfOperatorConfiguration;
	// it has a reference to all comboboxes added in operatorLayout. The order of
	// this list is the same that the order in Parameters annotation (operators)
	private List<ComboBox<Class<?>>> comboBoxesAdded;
	// it has a reference to all textfield added in primitiveLayout. The order of
	// this list is the same that the order in Parameters annotation (numbers)
	private List<TextField> textFieldAdded;

	public ConfigurationDynamicWindow(Registrable registrableProblem, Stage thisWindow) {
		this.operatorGridRowCount = 0;
		this.numberGridRowCount = 0;
		this.problem = registrableProblem;
		this.primitiveLayout = new GridPane();
		this.operatorLayout = new GridPane();

		this.window = thisWindow;

		this.primitiveLayout.setHgap(defaultSpace);
		this.primitiveLayout.setVgap(defaultSpace);

		this.operatorLayout.setHgap(defaultSpace);
		this.operatorLayout.setVgap(defaultSpace);

		setSpacing(defaultSpace);
		setPadding(new Insets(defaultSpace));

		getChildren().addAll(primitiveLayout, operatorLayout);

		this.comboBoxesAdded = new ArrayList<ComboBox<Class<?>>>();
		this.textFieldAdded = new ArrayList<TextField>();
		this.resultOfOperatorConfiguration = new HashMap<Class<?>, List<Number>>();

		createContentLayout();
		addButton();
	}

	/**
	 * Read the problem using reflection and build the interface to configure the
	 * problem
	 */
	private void createContentLayout() {
		Method method = ReflectionUtils.getInjectableMethod(problem.getClass());
		Parameters parameters = method.getAnnotation(Parameters.class);
		if (parameters != null) {
			// It is assume that the order of parameter is object,... , int or double... (It
			// is validated in method that create this windows in ProblemRegistrar)

			int parameterIndex = 0; // count how many parameters have been readed

			// Create combobox for object input
			for (OperatorInput operator : parameters.operators()) {
				operatorSection(operator);
				parameterIndex++;
			}

			// Create the textfield for native input (double, int, etc)
			for (NumberInput operator : parameters.numbers()) {
				numberSection(operator, method.getParameterTypes()[parameterIndex]);
				parameterIndex++;
			}
		}
	}

	/**
	 * Setting the number section. It section has textfield to write the numbers.
	 * 
	 * @param operator      the NumberInput annotation
	 * @param parameterType the type of parameter
	 */
	private void numberSection(NumberInput operator, Class<?> parameterType) {
		Label label = new Label(operator.displayName());
		TextField textfield = new TextField();
		textfield.setPromptText("Enter the value");
		textfield.setMaxWidth(Double.MAX_VALUE);

		this.textFieldAdded.add(textfield);
		assert parameterType.getName().matches("int|Integer|double|Double");
		if (parameterType.getName().matches("int|Integer")) {
			textfield.setTextFormatter(createWholeTextFormatter());
		}

		if (parameterType.getName().matches("double|Double")) {
			textfield.setTextFormatter(createDecimalTextFormatter());
		}

		this.primitiveLayout.addRow(numberGridRowCount, label, textfield);
		numberGridRowCount++;
	}

	/**
	 * Setting the operator section. It section has comboBox to choose the operator.
	 * It only configure one parameters.
	 * 
	 * @param operator the operator annotation that contains the option of combobox
	 */
	private void operatorSection(OperatorInput operator) {
		Map<Class<?>, String> operatorMap = new LinkedHashMap<>(operator.value().length);
		for (OperatorOption option : operator.value()) {
			operatorMap.put(option.value(), option.displayName());
		}

		Label label = new Label(operator.displayName());
		ComboBox<Class<?>> comboBox = new ComboBox<>();
		comboBox.setMaxWidth(Double.MAX_VALUE);
		comboBox.getItems().addAll(operatorMap.keySet());
		comboBox.setConverter(new StringConverter<Class<?>>() {
			@Override
			public String toString(Class<?> object) {
				return operatorMap.get(object);
			}

			@Override
			public Class<?> fromString(String string) {
				// the combobox isn't editable so it method is not necessary
				return null;
			}
		});

		this.comboBoxesAdded.add(comboBox);

		Button configButton = new Button("Configurar");
		// If a item is selected in combobox so enable config button
		configButton.disableProperty().bind(comboBox.getSelectionModel().selectedItemProperty().isNull());

		// Update the isRunButtonDisable property to bind the combobox created in this
		// execution of method
		if (isRunButtonDisabled == null) {
			this.isRunButtonDisabled = comboBox.getSelectionModel().selectedItemProperty().isNull();
		} else {
			this.isRunButtonDisabled = this.isRunButtonDisabled
					.or(comboBox.getSelectionModel().selectedItemProperty().isNull());
		}

		// show a window to configure the operator
		configButton.setOnAction((evt) -> {
			createAndShowOperatorConfigureDialog(operator.displayName(),
					comboBox.getSelectionModel().getSelectedItem());
		});

		this.operatorLayout.addRow(operatorGridRowCount, label, comboBox, configButton);
		operatorGridRowCount++;
	}

	/**
	 * Setting the view showed when a click event is received in the button next to
	 * combobox
	 * 
	 * @param name         Name of operator
	 * @param selectedItem the class of operator
	 */
	private void createAndShowOperatorConfigureDialog(String name, Class<?> selectedItem) {
		Dialog<List<Number>> dialog = new Dialog<>();
		dialog.setTitle("Configuración " + name);
		dialog.setContentText("Ingrese los valores");
		ButtonType okButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

		GridPane grid = new GridPane();
		grid.setHgap(defaultSpace);
		grid.setVgap(defaultSpace);
		grid.setPadding(new Insets(defaultSpace));

		// Add a textfield for each parameter in the constructor
		Constructor<?> constructor = ReflectionUtils.getDefaultConstructor(selectedItem);
		ArrayList<TextField> textFieldOfParameters = new ArrayList<>(constructor.getParameterCount());
		if (constructor != null) {
			DefaultConstructor annotation = constructor.getAnnotation(DefaultConstructor.class);
			Class<?>[] parameters = constructor.getParameterTypes();

			List<Number> previousResults = this.resultOfOperatorConfiguration.get(selectedItem);

			for (int i = 0; i < parameters.length; i++) {
				Label label = new Label(annotation.value()[i]);
				TextField textfield = new TextField();
				textfield.setPromptText("Ingrese el valor");

				if (parameters[i].getName().matches("int|Integer")) {
					textfield.setTextFormatter(createWholeTextFormatter());
				}

				if (parameters[i].getName().matches("double|Double")) {
					textfield.setTextFormatter(createDecimalTextFormatter());
				}

				// If there was previous result of a previous configuration so load it.
				if (previousResults != null) {
					textfield.setText(previousResults.get(i).toString());
				}

				textFieldOfParameters.add(textfield);
				grid.addRow(i, label, textfield);
			}
		}

		dialog.getDialogPane().setContent(grid);

		// method used to convert the element in dialog in a result
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				List<Number> results = textFieldOfParameters.stream()
						.map(textField -> (Number) textField.getTextFormatter().getValue())
						.collect(Collectors.toList());
				return new ArrayList<Number>(results);
			}
			return null;
		});

		Optional<List<Number>> result = dialog.showAndWait();

		// Save the result if exist in the hashmap
		result.ifPresent(results -> {
			this.resultOfOperatorConfiguration.put(selectedItem, results);
		});
	}

	/**
	 * Add button at the last of window
	 */
	private void addButton() {

		Button run = new Button("Ejecutar");
		run.setOnAction((evt) -> {
			createAlgorithm();
		});
		run.disableProperty().bind(isRunButtonDisabled);
		Button cancel = new Button("Cancelar");
		cancel.setOnAction((evt) -> closeWindow());

		HBox hbox = new HBox(run, cancel);
		hbox.setSpacing(defaultSpace);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.setPadding(new Insets(defaultSpace));

		getChildren().add(hbox);

	}

	/**
	 * Read all input field and create the algorithm based in the input field. When
	 * the algorithm is created an {@link AlgorithmCreationNotification} is fired.
	 */
	private void createAlgorithm() {
		// Read the values configurated and selected
		List<Object> parameters = new ArrayList<>();

		for (ComboBox<Class<?>> comboBox : this.comboBoxesAdded) {
			Class<?> operator = comboBox.getSelectionModel().getSelectedItem();
			List<Number> operatorParameters = this.resultOfOperatorConfiguration.get(operator);
			try {
				if (operatorParameters == null) {
					CustomDialogs.showDialog("Error", "Error in the creation of the operator.",
							"The operator " + operator.getName() + " can't be configured", AlertType.ERROR);
					return;
				}
				Object operatorObject = ReflectionUtils.getDefaultConstructor(operator)
						.newInstance(operatorParameters.toArray());
				parameters.add(operatorObject);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				CustomDialogs.showExceptionDialog("Error", "Error in the creation of the operator",
						"The operator " + operator.getName() + " can't be created", e);
				return;
			}
		}

		for (TextField textfield : this.textFieldAdded) {
			parameters.add(textfield.getTextFormatter().getValue());
		}

		Method injectableMethod = ReflectionUtils.getInjectableMethod(this.problem.getClass());
		try {
			Algorithm<?> algorithm = (Algorithm<?>) injectableMethod.invoke(this.problem, parameters.toArray());
			closeWindow();
			notifyAlgorithmCreation(algorithm);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			CustomDialogs.showExceptionDialog("Error", "Error in the creation of the algorithm", "The algorithm can't be created",
					e);
		}

	}

	/**
	 * Add a callback to notify when the algorithm is created.
	 * 
	 * @param algorithmEvent
	 */
	public void setAlgorithmCreationNotification(AlgorithmCreationNotification algorithmEvent) {
		this.algorithmEvent = algorithmEvent;
	}

	/**
	 * Is a class to fire the notification when the algorithm is ready.
	 * 
	 * @param algorithm the algorithm
	 * @throws ApplicationException if there isn't register the notification
	 *                              callback
	 */
	private void notifyAlgorithmCreation(Algorithm<?> algorithm) throws ApplicationException {
		if (algorithmEvent == null) {
			throw new ApplicationException("There isn't register this notify function in " + this.getClass().getName());
		}
		algorithmEvent.notify(algorithm);
	}

	/**
	 * Create a DecimalFormater. It when is attached a textfield only let valid
	 * values for a decimal
	 * 
	 * @return the formatter
	 */
	private TextFormatter<Double> createDecimalTextFormatter() {
		Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

		UnaryOperator<TextFormatter.Change> filter = c -> {
			String text = c.getControlNewText();
			if (validEditingState.matcher(text).matches()) {
				return c;
			} else {
				return null;
			}
		};

		StringConverter<Double> converter = new StringConverter<Double>() {

			@Override
			public Double fromString(String s) {
				if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
					return 0.0;
				} else {
					return Double.valueOf(s);
				}
			}

			@Override
			public String toString(Double d) {
				return d.toString();
			}
		};

		TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);
		return textFormatter;
	}

	/**
	 * Create a WholeFormatter. It when is attached a textfield only let valid
	 * values for a whole number
	 * 
	 * @return the formatter
	 */
	private TextFormatter<Integer> createWholeTextFormatter() {
		Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?");

		UnaryOperator<TextFormatter.Change> filter = c -> {
			String text = c.getControlNewText();
			if (validEditingState.matcher(text).matches()) {
				return c;
			} else {
				return null;
			}
		};

		StringConverter<Integer> converter = new StringConverter<Integer>() {

			@Override
			public Integer fromString(String s) {
				if (s.isEmpty() || "-".equals(s)) {
					return 0;
				} else {
					return Integer.valueOf(s);
				}
			}

			@Override
			public String toString(Integer i) {
				return i.toString();
			}
		};

		TextFormatter<Integer> textFormatter = new TextFormatter<>(converter, 0, filter);
		return textFormatter;
	}

	private void closeWindow() {
		this.window.close();
	}
}
