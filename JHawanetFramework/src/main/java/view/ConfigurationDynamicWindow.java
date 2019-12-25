package view;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import annotations.operators.DefaultConstructor;
import annotations.registrable.FileInput;
import annotations.registrable.NumberInput;
import annotations.registrable.OperatorInput;
import annotations.registrable.OperatorOption;
import annotations.registrable.Parameters;
import controller.ConfigurationDynamicWindowController;
import controller.problems.Registrable;
import controller.utils.ReflectionUtils;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import view.utils.CustomDialogs;

/**
 * This class build the interface to configure the problems. It use the
 * Registrable class and with reflection read his metadata annotation.
 *
 */
public final class ConfigurationDynamicWindow extends Stage {

	private Class<? extends Registrable> problemClass;
	private ConfigurationDynamicWindowController controller;

	private double defaultSpace = 5;
	private BooleanBinding isRunButtonDisabled;

	private VBox root;
	private GridPane gridLayout;

	private int gridLayoutRowCount;

	/*
	 * It has a reference to textfield with number input. The order of this list is
	 * the same that the order in Parameters annotation (numbers)
	 */
	private List<TextField> numbersTextFieldAdded;
	/*
	 * It has a reference to textfield with fileInput(It shown the path of some
	 * file).The order of this list is the same that the order in Parameters
	 * annotation (files)
	 */
	private List<TextField> filesTextFieldAdded;

	// A map with the value of operator configured in comboBox
	private Map<Class<?>, List<Number>> resultOfOperatorConfiguration;
	/*
	 * it has a reference to all comboboxes added. The order of this list is the
	 * same that the order in Parameters annotation (operators)
	 */
	private List<ComboBox<Class<?>>> comboBoxesAdded;

	/**
	 * Constructor.
	 * @param controller the controller associated to this view.
	 * @param registrable the registrable class to analize.
	 */
	public ConfigurationDynamicWindow(ConfigurationDynamicWindowController controller,
			Class<? extends Registrable> registrable) {
		this.problemClass = Objects.requireNonNull(registrable);
		this.controller = Objects.requireNonNull(controller);

		this.root = new VBox();
		Scene scene = new Scene(root);
		setScene(scene);

		this.gridLayout = new GridPane();
		this.gridLayout.setHgap(defaultSpace);
		this.gridLayout.setVgap(defaultSpace);
		root.getChildren().addAll(this.gridLayout);
		root.setSpacing(defaultSpace);
		root.setPadding(new Insets(defaultSpace));
		this.comboBoxesAdded = new ArrayList<ComboBox<Class<?>>>();
		this.numbersTextFieldAdded = new ArrayList<TextField>();
		this.filesTextFieldAdded = new ArrayList<TextField>();
		this.resultOfOperatorConfiguration = new HashMap<Class<?>, List<Number>>();

		configurateWindow();
		createContentLayout();
		addButton();
	}

	/**
	 * Setting the stage properties.
	 */
	private void configurateWindow() {
		setTitle("Algorithm Configuration");
		sizeToScene();
		setResizable(false);
		initModality(Modality.APPLICATION_MODAL);
		initStyle(StageStyle.UTILITY);
	}

	/**
	 * Read the problem using reflection and build the interface to configure the
	 * problem
	 */
	private void createContentLayout() {
		Constructor<?> constructor = ReflectionUtils.getConstructor(problemClass);
		Parameters parameters = constructor.getAnnotation(Parameters.class);
		if (parameters != null) {
			// It is assume that the order of parameter is object,..., file ... , int or
			// double... (It
			// is validated in method that create this windows in ProblemRegistrar)

			int parameterIndex = constructor.getParameterCount() - 1; // What it the last index of parameter

			// Create the textfield for native input (double, int, etc)
			for (NumberInput operator : parameters.numbers()) {
				numberSection(operator, constructor.getParameterTypes()[parameterIndex]);
				parameterIndex--;
			}

			// Create textfield for show the path selected in a filechooser
			for (FileInput file : parameters.files()) {
				fileSection(file);
				parameterIndex--;
			}

			// Create combobox for object input
			for (OperatorInput operator : parameters.operators()) {
				operatorSection(operator);
				parameterIndex--;
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

		this.numbersTextFieldAdded.add(textfield);
		assert parameterType.getName().matches("int|Integer|double|Double");
		if (parameterType.getName().matches("int|Integer")) {
			textfield.setTextFormatter(createWholeTextFormatter());
		}

		if (parameterType.getName().matches("double|Double")) {
			textfield.setTextFormatter(createDecimalTextFormatter());
		}

		this.gridLayout.addRow(gridLayoutRowCount++, label, textfield);
		gridLayoutRowCount++;
	}

	/**
	 * Setting the file section. It section has textfield and a button to open a
	 * filechooser.
	 * 
	 * @param file the FileInput annotation
	 */
	private void fileSection(FileInput file) {
		Label label = new Label(file.displayName());
		TextField textfield = new TextField();
		textfield.setMaxWidth(Double.MAX_VALUE);
		Button button = new Button("Browse");
		button.setMaxWidth(Double.MAX_VALUE);

		FileChooser fileChooser = new FileChooser();
		button.setOnAction((evt) -> {
			File f = fileChooser.showOpenDialog(this);
			if (f != null) {
				textfield.setText(f.getAbsolutePath());
			}
		});

		this.filesTextFieldAdded.add(textfield);
		this.gridLayout.addRow(gridLayoutRowCount, label, textfield, button);
		gridLayoutRowCount++;
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

		Button configButton = new Button("Configure");
		// Disable by default until select an Operator in comboBox that have parameters.
		configButton.setDisable(true);

		comboBox.getSelectionModel().selectedItemProperty().addListener((prop, oldv, newv) -> {
			if (ReflectionUtils.getNumberOfParameterInDefaultConstructor(newv) > 0) {
				configButton.setDisable(false);
			} else {
				configButton.setDisable(true);
			}
		});

		// Update the isRunButtonDisable property to bind the combobox created in this
		// execution of method. So if all combobox are a selected item the run button is
		// enable.
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

		this.gridLayout.addRow(gridLayoutRowCount, label, comboBox, configButton);
		gridLayoutRowCount++;
	}

	/**
	 * Setting the view showed when a click event is received in the button next to
	 * combobox
	 * 
	 * @param name         Name of operator
	 * @param selectedItem the class of operator
	 */
	private void createAndShowOperatorConfigureDialog(String name, Class<?> selectedItem) {
		//Create a custom dialog to configure the operator's parameters.
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
		if (textFieldOfParameters.size() != 0) {
			// give focus to first textfield
			Platform.runLater(() -> {
				textFieldOfParameters.get(0).requestFocus();
			});
		}

		dialog.getDialogPane().setContent(grid);

		// method used to convert the element in dialog in a result
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				//Get the value of each textfield in the dialog
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
	 * Add button cancel and run at the last of window.
	 */
	private void addButton() {

		Button run = new Button("Run");
		run.setOnAction((evt) -> {
			sendParatemetersToController();
		});
		run.disableProperty().bind(isRunButtonDisabled);
		Button cancel = new Button("Cancel");
		cancel.setOnAction((evt) -> close());

		HBox hbox = new HBox(run, cancel);
		HBox.setHgrow(run, Priority.ALWAYS);
		HBox.setHgrow(cancel, Priority.ALWAYS);
		run.setMaxWidth(100);
		cancel.setMaxWidth(100);
		hbox.setSpacing(defaultSpace);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		this.root.getChildren().add(hbox);

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

	/**
	 * Prepare to send the user input to controller when the run button is pressed.
	 */
	private void sendParatemetersToController() {
		Map<Class<?>, List<Number>> operatorsAndConfig = new LinkedHashMap<Class<?>, List<Number>>(
				this.comboBoxesAdded.size());
		File[] files = new File[this.filesTextFieldAdded.size()];
		Number[] numberInputs = new Number[this.numbersTextFieldAdded.size()];
//		Number[] toggleInputs = new Number[this.numbersToogleFieldAdded.size()];
//		int[] selectedToogleIndex = new int[this.numbersToogleFieldAdded.size()];

		int i = 0;
		for (ComboBox<Class<?>> comboBox : this.comboBoxesAdded) {
			Class<?> operator = comboBox.getSelectionModel().getSelectedItem();
			List<Number> operatorParameters = this.resultOfOperatorConfiguration.get(operator);
			if (operatorParameters == null) {
				CustomDialogs.showDialog("Error", "Error in the creation of the operator.",
						"The operator " + operator.getName() + " can't be configured", AlertType.ERROR);
				return;
			}
			operatorsAndConfig.put(operator, operatorParameters);

		}

		i = 0; // reset the counter
		for (TextField textfield : this.filesTextFieldAdded) {
			if (textfield.getText().isEmpty()) {
				files[i++] = null;
			} else {
				files[i++] = new File(textfield.getText());
			}
		}

		i = 0; // reset the counter
		for (TextField textfield : this.numbersTextFieldAdded) {
			numberInputs[i++] = (Number) textfield.getTextFormatter().getValue();
		}
		controller.onRunButtonClick(operatorsAndConfig, files, numberInputs);
	}
}
