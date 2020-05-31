package controller;

import annotations.operators.DefaultConstructor;
import annotations.registrable.*;
import controller.utils.CustomCallback;
import controller.utils.ReflectionUtils;
import exception.ApplicationException;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.jetbrains.annotations.Nullable;
import registrable.Registrable;
import view.utils.CustomDialogs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This is the controller of ConfigurationDynamicWindow.
 * @param <T> The type of registrable class
 */
public class ConfigurationDynamicWindowController<T extends Registrable<?>> {
	private final CustomCallback<T> experimentEvent;
	private final Class<? extends T> problemClass;
	private final Pane root;
	private static final double defaultSpaceInConfigurationGrid = 5;
	private int gridLayoutRowCount;

	private @Nullable BooleanBinding isRunButtonDisabled;

	private @Nullable Stage window;

	@FXML
	private Label algorithmNameLabel;

	@FXML
	private Label problemNameLabel;

	@FXML
	private TextArea descriptionTextArea;

	@FXML
	private GridPane configurationGridPane;

	@FXML
	private Button runButton;

	@FXML
	private Button cancelButton;

	/*
	 * It has references to textfield with number input. The order of this list is
	 * the same that the order in Parameters annotation (numbers)
	 */
	private List<TextField> numbersTextFieldAdded;

	/*
	 * It has references to textfield with number input of the number toogle
	 * section. The order of this list is the same that the order in Parameters
	 * annotation (numbersToogle).
	 */
	private List<TextField> numbersToogleTextFieldAdded;

	/*
	 * It has a reference to textfield with fileInput(It shown the path of some
	 * file).The order of this list is the same that the order in Parameters
	 * annotation (files)
	 */
	private List<TextField> filesTextFieldAdded;

	// A map with the value of operator configured in comboBox
	private final Map<Class<?>, List<Number>> resultOfOperatorConfiguration;
	/*
	 * it has a reference to all comboboxes added. The order of this list is the
	 * same that the order in Parameters annotation (operators)
	 */
	private List<ComboBox<Class<?>>> comboBoxesAdded;

	/*
	 * Used by numberToogleSection to persist the group created from one call to
	 * other of the method.
	 */
	private Map<String, ToggleGroup> numberToggleGroupAdded;

	public ConfigurationDynamicWindowController(Class<? extends T> registrable,
			CustomCallback<T> experimentEvent) {
		this.problemClass = Objects.requireNonNull(registrable);
		this.experimentEvent = Objects.requireNonNull(experimentEvent);
		this.resultOfOperatorConfiguration = new HashMap<>();
		this.root = loadFXML();
	}

	/**
	 * Configurate the binding and listener of Run button and cancel button
	 */
	private void addBindingAndListener() {
		//disable the run button until all combobox as a selected operator.
		this.runButton.disableProperty().bind(isRunButtonDisabled);
		this.runButton.setOnAction((evt) -> onRunButtonClick());
		this.cancelButton.setOnAction((evt) -> closeWindow());
	}

	/**
	 * Load the FXML view associated to this controller.
	 *
	 * @return the root pane.
	 * @throws ApplicationException if there is an error in load the .fxml.
	 */
	private Pane loadFXML() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/ConfigurationDynamicWindow.fxml"));
		fxmlLoader.setController(this);
		try {
			return fxmlLoader.load();
		} catch (IOException exception) {
			throw new ApplicationException(exception);
		}
	}

	/**
	 * Is a class to fire the notification when the experiment is created.
	 * 
	 * @param registrable the experiment registrable class
	 * @throws ApplicationException if there isn't register the notification
	 *                              callback
	 */
	private void notifyExperimentCreation(T registrable) throws ApplicationException {

		experimentEvent.notify(registrable);
	}

	/**
	 * Read the problem using reflection and build the interface to configure the
	 * problem
	 * @throws NullPointerException if the ProblemRegister class has no constructor with {@link NewProblem} annotation.
	 */
	private void createContentLayout() {
		Constructor<?> constructor = ReflectionUtils.getConstructor(problemClass);
		Parameters parameters = Objects.requireNonNull(constructor).getAnnotation(Parameters.class);
		if (parameters != null) {
			/*
			 * It is assume that the order of parameter is object,..., file ... , int or
			 * double... (It is validated in method that create this windows in
			 * ProblemRegistrar). Get the index when int and double input start.
			 */
			int parameterIndex = constructor.getParameterCount() - parameters.numbers().length
					- parameters.numbersToggle().length; // What it the last index of parameter

			// Create the textfield for native input (double, int, etc)
			for (NumberInput annotation : parameters.numbers()) {
				numberSection(annotation, constructor.getParameterTypes()[parameterIndex]);
				parameterIndex++;
			}

			// Create the textfield for native input exclusive each other (double, int, etc)
			for (NumberToggleInput annotation : parameters.numbersToggle()) {
				numberToggleSection(annotation, constructor.getParameterTypes()[parameterIndex]);
				parameterIndex++;
			}

			// Create textfield for show the path selected in a filechooser
			for (FileInput annotation : parameters.files()) {
				fileSection(annotation);
			}

			// Create combobox for object input
			for (OperatorInput annotation : parameters.operators()) {
				operatorSection(annotation);
			}

		}
	}

	/**
	 * Setting the number section. It section has textfield to write the numbers.
	 *
	 * @param annotation    the NumberInput annotation
	 * @param parameterType the type of parameter
	 */
	private void numberSection(NumberInput annotation, Class<?> parameterType) {
		// lazy initialization of list
		if (this.numbersTextFieldAdded == null) {
			this.numbersTextFieldAdded = new ArrayList<>();
		}

		Label label = new Label(annotation.displayName());
		TextField textfield = new TextField();
		textfield.setPromptText("Enter the value");
		textfield.setMaxWidth(Double.MAX_VALUE);

		this.numbersTextFieldAdded.add(textfield);
		assert parameterType.getName().matches("int|Integer|double|Double");
		// if the type is int or integer use a validator that only let whole number
		if (parameterType.getName().matches("int|Integer")) {
			textfield.setTextFormatter(createWholeTextFormatter());
		}
		// if the type is double use a validator that let real numbers
		if (parameterType.getName().matches("double|Double")) {
			textfield.setTextFormatter(createDecimalTextFormatter());
		}

		// add the row to grid pane
		this.configurationGridPane.addRow(gridLayoutRowCount++, label, textfield);
		gridLayoutRowCount++;
	}

	/**
	 * Setting the number toggle section
	 * @param annotation the NumberToggleInput annotation
	 * @param parameterType the type of parameter
	 */
	private void numberToggleSection(NumberToggleInput annotation, Class<?> parameterType) {
		// lazy initialization of list
		if (this.numbersToogleTextFieldAdded == null) {
			this.numbersToogleTextFieldAdded = new ArrayList<>();
		}
		if (this.numberToggleGroupAdded == null) {
			this.numberToggleGroupAdded = new HashMap<>();
		}
		// if the toggle group isn't saved create a new one and add the group name as title of section.
		if (!this.numberToggleGroupAdded.containsKey(annotation.groupID())) {
			ToggleGroup toggleGroup = new ToggleGroup();
			this.numberToggleGroupAdded.put(annotation.groupID(), toggleGroup);

			Label groupTitle = new Label(annotation.groupID());
			this.configurationGridPane.addRow(gridLayoutRowCount, groupTitle);
			gridLayoutRowCount++;
		}
		ToggleGroup toggleGroup = this.numberToggleGroupAdded.get(annotation.groupID());

		RadioButton radioButton = new RadioButton(annotation.displayName());
		radioButton.setToggleGroup(toggleGroup);
		// if the radio button is the first element in group so select it by default
		if (toggleGroup.getToggles().size() == 1) {
			radioButton.setSelected(true);
		}

		TextField textfield = new TextField();
		textfield.getProperties().put("type", parameterType);

		//disable textfield if his radio button is not selected
		textfield.disableProperty().bind(radioButton.selectedProperty().not());
		textfield.setMaxWidth(Double.MAX_VALUE);

		this.numbersToogleTextFieldAdded.add(textfield);
		assert parameterType.getName().matches("int|Integer|double|Double");
		//add validator to textfield
		if (parameterType.getName().matches("int|Integer")) {
			textfield.setTextFormatter(createWholeTextFormatter());
		} else if (parameterType.getName().matches("double|Double")) {
			textfield.setTextFormatter(createDecimalTextFormatter());
		}

		this.configurationGridPane.addRow(gridLayoutRowCount++, radioButton, textfield);
		gridLayoutRowCount++;

	}

	/**
	 * Setting the file section. It section has textfield and a button to open a
	 * filechooser.
	 *
	 * @param annotation the FileInput annotation
	 */
	private void fileSection(FileInput annotation) {
		// lazy initialization of list
		if (this.filesTextFieldAdded == null) {
			this.filesTextFieldAdded = new ArrayList<>();
		}
		Label label = new Label(annotation.displayName());
		TextField textfield = new TextField();
		textfield.setMaxWidth(Double.MAX_VALUE);
		Button button = new Button("Browse");
		button.setMaxWidth(Double.MAX_VALUE);
		FileChooser fileChooser = new FileChooser();

		button.setOnAction((evt) -> {
			File f;

			if (annotation.type() == FileInput.FileType.OPEN) {
				f = fileChooser.showOpenDialog(window);
			}
			else {
				f = fileChooser.showSaveDialog(window);
			}

			if (f != null) {
				textfield.setText(f.getAbsolutePath());
			}
		});

		this.filesTextFieldAdded.add(textfield);
		this.configurationGridPane.addRow(gridLayoutRowCount, label, textfield, button);
		gridLayoutRowCount++;
	}

	/**
	 * Setting the operator section. It section has comboBox to choose the operator.
	 * It only configure one parameters.
	 *
	 * @param annotation the operator annotation that contains the option of
	 *                   combobox
	 */
	private void operatorSection(OperatorInput annotation) {
		if (this.comboBoxesAdded == null) {
			this.comboBoxesAdded = new ArrayList<>();
		}
		Map<Class<?>, String> operatorMap = new LinkedHashMap<>(annotation.value().length);
		for (OperatorOption option : annotation.value()) {
			operatorMap.put(option.value(), option.displayName());
		}

		Label label = new Label(annotation.displayName());
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

		// If a operator is selected enable the Configure button only if his default constructor need parameters.
		comboBox.getSelectionModel().selectedItemProperty().addListener((prop, oldv, newv) -> configButton.setDisable(ReflectionUtils.getNumberOfParameterInDefaultConstructor(newv) <= 0));

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
		configButton.setOnAction((evt) -> createAndShowOperatorConfigureDialog(annotation.displayName(),
				comboBox.getSelectionModel().getSelectedItem()));

		this.configurationGridPane.addRow(gridLayoutRowCount, label, comboBox, configButton);
		gridLayoutRowCount++;
	}

	/**
	 * Setting the view showed when a click event is received in the button next to
	 * combobox
	 *
	 * @param name         Name of operator
	 * @param selectedItem the class of operator
	 * @throws NullPointerException if the selectedItem has no a constructor with {@link DefaultConstructor} annotation.
	 */
	private void createAndShowOperatorConfigureDialog(String name, Class<?> selectedItem) {
		// Create a custom dialog to configure the operator's parameters.
		Dialog<List<Number>> dialog = new Dialog<>();
		dialog.setTitle("Configuración " + name);
		dialog.setContentText("Ingrese los valores");
		ButtonType okButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

		GridPane grid = new GridPane();
		grid.setHgap(defaultSpaceInConfigurationGrid);
		grid.setVgap(defaultSpaceInConfigurationGrid);
		grid.setPadding(new Insets(defaultSpaceInConfigurationGrid));

		// Add a textfield for each parameter in the constructor
		Constructor<?> constructor = ReflectionUtils.getDefaultConstructor(selectedItem);
		Objects.requireNonNull(constructor, "The operator " + selectedItem.getSimpleName() + " has no a constructor with DefaultConstructor Annotation");

		// Store the textfield that will be created
		ArrayList<TextField> textFieldOfParameters = new ArrayList<>(constructor.getParameterCount());
		DefaultConstructor annotation = constructor.getAnnotation(DefaultConstructor.class);

		Class<?>[] parameters = constructor.getParameterTypes();

		// Look if already has been added a configuration to this operator
		List<Number> previousResults = this.resultOfOperatorConfiguration.get(selectedItem);

		// create a label and textfield for each parameter and add his validators
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
		if (textFieldOfParameters.size() != 0) {
			// give focus to first textfield
			Platform.runLater(() -> textFieldOfParameters.get(0).requestFocus());
		}

		dialog.getDialogPane().setContent(grid);

		// method used to convert the string in dialog in a number result
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				// Get the value of each textfield in the dialog
				return textFieldOfParameters.stream()
						.map(textField -> (Number) textField.getTextFormatter().getValue()).collect(Collectors.toList());
			}
			return null;
		});

		Optional<List<Number>> result = dialog.showAndWait();

		// Save the result if exist in the hashmap
		result.ifPresent(results -> this.resultOfOperatorConfiguration.put(selectedItem, results));
	}

	/**
	 * Is called when the run button is pressed in view. It method create the
	 * registrable instance based in the input field. When the experiment is created
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
	 * @throws NullPointerException if some operator hasn't a constructor with {@link DefaultConstructor} annotation.
	 */
	private void createInstance(Map<Class<?>, List<Number>> operatorsAndConfig, File[] fileInputs,
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
				Object operatorObject = Objects.requireNonNull(ReflectionUtils.getDefaultConstructor(operator))
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
			closeWindow();
			notifyExperimentCreation(registrable);
		} catch (InvocationTargetException e) {
			CustomDialogs.showExceptionDialog("Error", "Exception throw by the constructor",
					"Can't be created an instance of " + this.problemClass.getName(), e.getCause());
		}

	}

	/**
	 * Collect the user input from the UI controls and call method to create the instance.
	 */
	private void onRunButtonClick() {
		Map<Class<?>, List<Number>> operatorsAndConfig = new LinkedHashMap<>(
				this.comboBoxesAdded.size());
		File[] files = new File[this.filesTextFieldAdded.size()];
		Number[] numberInputs = new Number[this.numbersTextFieldAdded.size()];
		Number[] toggleInputs = new Number[this.numbersToogleTextFieldAdded.size()];

		// Walk through the combobox of the operators
		for (ComboBox<Class<?>> comboBox : this.comboBoxesAdded) {
			Class<?> operator = comboBox.getSelectionModel().getSelectedItem();
			List<Number> operatorParameters = this.resultOfOperatorConfiguration.get(operator);
			if (operatorParameters == null) {
				CustomDialogs.showDialog("Error", "Error in the creation of the operator.",
						"The operator " + operator.getName() + " can't be configured", Alert.AlertType.ERROR);
				return;
			}
			operatorsAndConfig.put(operator, operatorParameters);
		}
		int i;

		// Walk through textfiles that contain the path of files
		i = 0; // reset the counter
		for (TextField textfield : this.filesTextFieldAdded) {
			if (textfield.getText().isEmpty()) {
				files[i++] = null;
			} else {
				files[i++] = new File(textfield.getText());
			}
		}

		// Walk through the numbers
		i = 0; // reset the counter
		for (TextField textfield : this.numbersTextFieldAdded) {
			numberInputs[i++] = (Number) textfield.getTextFormatter().getValue();
		}

		// Walk through the numbers toogle
		i = 0; // reset the counter
		for (TextField textfield : this.numbersToogleTextFieldAdded) {
			if (!textfield.isDisable()) {
				// get the number
				toggleInputs[i++] = (Number) textfield.getTextFormatter().getValue();
			} else {
				// see the type of textfield in a previously added property to add the flag value that indicate that isn't configured.
				Class<?> parameterType = (Class<?>) textfield.getProperties().get("type");
				if (parameterType.getName().matches("int|Integer")) {
					toggleInputs[i++] = Integer.MIN_VALUE;
				} else if (parameterType.getName().matches("double|Double")) {
					toggleInputs[i++] = Double.MIN_VALUE;
				}
			}
		}
		// Notify to controller to build the elements
		createInstance(operatorsAndConfig, files, numberInputs, toggleInputs);
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

		return new TextFormatter<>(converter, 0.0, filter);
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

		return new TextFormatter<>(converter, 0, filter);
	}

	/**
	 * Close the window
	 */
	private void closeWindow(){
		if (this.window != null) {
			this.window.close();
		}
	}

	/**
	 * Show the associated window
	 */
	public void showWindow() {
		Stage stage = new Stage();
		stage.setScene(new Scene(this.root));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Status of execution");
		this.window = stage;

		//Initialize the gridpane with the controls to configure the problem
		createContentLayout();
		//Fill description tab
		fillDescriptionTab();
		//add binding to run and close button
		addBindingAndListener();

		stage.show();
	}

	/**
	 * Add the values to description tab.
	 *
	 */
	private void fillDescriptionTab() {
		algorithmNameLabel.setText(ReflectionUtils.getNameOfAlgorithm(problemClass));
		problemNameLabel.setText(ReflectionUtils.getNameOfProblem(problemClass));
		descriptionTextArea.setText(ReflectionUtils.getDescriptionOfProblem(problemClass));
	}
}
