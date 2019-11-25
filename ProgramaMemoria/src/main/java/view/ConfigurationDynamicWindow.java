package view;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import annotations.DefaultConstructor;
import annotations.NumberInput;
import annotations.OperatorInput;
import annotations.OperatorOption;
import annotations.Parameters;
import exception.ApplicationException;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import model.metaheuristic.algorithm.Algorithm;
import view.problems.AlgorithmCreationNotification;
import view.problems.Registrable;
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

	public ConfigurationDynamicWindow(Registrable registrableProblem, Stage thisWindow) {
		this.operatorGridRowCount = 0;
		this.numberGridRowCount = 0;
		this.problem = registrableProblem;
		this.operatorLayout = new GridPane();
		this.primitiveLayout = new GridPane();

		this.window = thisWindow;

		initialize();
		make();
	}

	private void initialize() {
		this.operatorLayout.setHgap(defaultSpace);
		this.operatorLayout.setVgap(defaultSpace);

		this.primitiveLayout.setHgap(defaultSpace);
		this.primitiveLayout.setVgap(defaultSpace);

		setSpacing(defaultSpace);
		setPadding(new Insets(defaultSpace));

		Button run = new Button("Ejecutar");
		run.setOnAction((evt) -> {
			System.out.println("Implementar ejecutar");
			notifyAlgorithmCreation(null);
		});

		Button cancel = new Button("Cancelar");
		cancel.setOnAction((evt) -> this.window.close());
		HBox hbox = new HBox(run, cancel);
		hbox.setSpacing(defaultSpace);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		hbox.setPadding(new Insets(defaultSpace));

		getChildren().addAll(primitiveLayout, operatorLayout, hbox);

	}

	/**
	 * Read the problem using reflection and build the interface
	 */
	private void make() {
		Method method = ReflectionUtils.getInjectableMethod(problem.getClass());
		Parameters parameters = method.getAnnotation(Parameters.class);
		if (parameters != null) {
			int parameterIndex = 0;
			// It assume that the order of parameter is object,... , int or double... (It is
			// validated in method that create this windows in ProblemRegistrar)
			for (OperatorInput operator : parameters.operators()) {
				operatorSection(operator);
				parameterIndex++;
			}

			for (NumberInput operator : parameters.numbers()) {
				numberSection(operator, method.getParameterTypes()[parameterIndex]);
				parameterIndex++;
			}
		}
	}

	/**
	 * Setting the operator section. It section has comboBox to choose the operator
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
				// TODO Auto-generated method stub
				return null;
			}
		});
		Button configButton = new Button("Configurar");
		configButton.disableProperty().bind(comboBox.getSelectionModel().selectedItemProperty().isNull());

		configButton.setOnAction((evt) -> {
			configureOperator(operator.displayName(), comboBox.getSelectionModel().getSelectedItem());
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
	private void configureOperator(String name, Class<?> selectedItem) {
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

		Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
		okButton.setDisable(true);

		// Add a textfield for each parameter in the constructor
		Constructor<?> constructor = ReflectionUtils.getDefaultConstructor(selectedItem);
		ArrayList<TextField> textFieldOfParameters = new ArrayList<>(constructor.getParameterCount());
		if (constructor != null) {
			DefaultConstructor annotation = constructor.getAnnotation(DefaultConstructor.class);
			Class<?>[] parameters = constructor.getParameterTypes();
			for (int i = 0; i < parameters.length; i++) {
				Label label = new Label(annotation.value()[i]);
				TextField textfield = new TextField();
				textfield.setPromptText("Ingrese el valor");

				int parameterIndex = i;
				textfield.textProperty().addListener((observable, oldValue, newValue) -> {
					if (parameters[parameterIndex].getName().matches("int|Integer")) {
						validateWholeNumberTextField(textfield, observable, oldValue, newValue);
					} else if (parameters[parameterIndex].getName().matches("double|Double")) {
						validateDecimalNumberTextField(textfield, observable, oldValue, newValue);
					}
				});

				textFieldOfParameters.add(textfield);
				grid.addRow(i, label, textfield);
			}
		}

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				NumberStringConverter converter = new NumberStringConverter();
				List<Number> results = textFieldOfParameters.stream()
						.map(textField -> converter.fromString(textField.getText())).collect(Collectors.toList());
				return new ArrayList<Number>(results);
			}
			return null;
		});

		Optional<List<Number>> result = dialog.showAndWait();

		result.ifPresent(results -> {
			results.forEach(System.out::println);
		});
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
		textfield.setPromptText("Ingrese el valor");
		textfield.setMaxWidth(Double.MAX_VALUE);

		assert parameterType.getName().matches("int|Integer|double|Double");

		textfield.textProperty().addListener((observable, oldValue, newValue) -> {
			if (parameterType.getName().matches("int|Integer")) {
				validateWholeNumberTextField(textfield, observable, oldValue, newValue);
			} else if (parameterType.getName().matches("double|Double")) {
				validateDecimalNumberTextField(textfield, observable, oldValue, newValue);
			}
		});

		this.primitiveLayout.addRow(numberGridRowCount, label, textfield);
		numberGridRowCount++;
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
	 * Validate that a textfield only has character valid for a Whole number.
	 * 
	 * @param node
	 * @param observable
	 * @param oldValue
	 * @param newValue
	 */
	private void validateWholeNumberTextField(TextField node, ObservableValue<? extends String> observable,
			String oldValue, String newValue) {
		if (!newValue.matches("^(-?)\\d*$")) {
			node.setText(newValue.replaceAll("[^\\d-]", ""));
		}
	}

	/**
	 * Validate that a textfield only has character valid for a decimal number
	 * 
	 * @param node
	 * @param observable
	 * @param oldValue
	 * @param newValue
	 */
	private void validateDecimalNumberTextField(TextField node, ObservableValue<? extends String> observable,
			String oldValue, String newValue) {
		if (!newValue.matches("^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$")) {
			node.setText(newValue.replaceAll("[^\\d.-]", ""));
		}
	}

	/**
	 * Parse a number string to a Number object. The string has to use dot has decimal separator.
	 * @param numberString
	 * @return
	 */
	private Number numberConverter(String numberString) {
		DecimalFormat format= (DecimalFormat) DecimalFormat.getInstance();
		DecimalFormatSymbols custom=new DecimalFormatSymbols();
		custom.setDecimalSeparator('.');
		format.setDecimalFormatSymbols(custom);
		NumberStringConverter numberStringConverter = new NumberStringConverter(format);
		return numberStringConverter.fromString(numberString);
	}
}
