package controller.component;

import annotations.*;
import annotations.operator.DefaultConstructor;
import annotations.registrable.*;
import controller.util.ReflectionUtils;
import controller.util.TextInputUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registrable.Registrable;
import view.utils.CustomDialogs;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is a component that show a interface to configure the registrable instance before create it.
 * @param <T> The type of registrable.
 */
public class ExperimentConfigurationComponent<T extends Registrable<?>> extends GridPane {
    private static Logger LOGGER = LoggerFactory.getLogger(ExperimentConfigurationComponent.class);

    private final Class<? extends T> registrableClass;
    private static final double defaultSpaceInConfigurationGrid = 5;
    private int gridLayoutRowCount;


    /*
     * It has references to textfield with number input. The order of this list is
     * the same that the order in Parameters annotation (numbers)
     */
    @NotNull
    private final List<TextField> numbersTextFieldAdded = new ArrayList<>();

    /*
     * It has references to textfield with number input of the number toogle
     * section. The order of this list is the same that the order in Parameters
     * annotation (numbersToogle).
     */
    @NotNull
    private final List<TextField> numbersToogleTextFieldAdded = new ArrayList<>();

    /*
     * It has a reference to textfield with fileInput(It shown the path of some
     * file).The order of this list is the same that the order in Parameters
     * annotation (files)
     */
    @NotNull
    private final List<TextField> filesTextFieldAdded = new ArrayList<>();

    // A map with the value of operator configured in comboBox
    @NotNull
    private final Map<Class<?>, List<Number>> resultOfOperatorConfiguration = new HashMap<>();
    /*
     * it has a reference to all comboboxes added. The order of this list is the
     * same that the order in Parameters annotation (operators)
     */
    @NotNull
    private final List<ComboBox<Class<?>>> comboBoxesAdded = new ArrayList<>();

    /*
     * Used by numberToogleSection to persist the group created from one call to
     * other of the method.
     */
    private final Map<String, ToggleGroup> numberToggleGroupAdded = new HashMap<>();

    public ExperimentConfigurationComponent(Class<? extends T> registrableClass) {
        this.registrableClass = registrableClass;
        setVgap(defaultSpaceInConfigurationGrid);
        setHgap(defaultSpaceInConfigurationGrid);

        //Initialize the gridpane with the controls to configure the experiment
        createContentLayout();
    }

    /**
     * Read the registrable class using reflection and build the interface to configure the
     * experiment.
     *
     * @throws NullPointerException if the ProblemRegister class has no constructor with {@link NewProblem} annotation.
     */
    private void createContentLayout() {
        Constructor<?> constructor = ReflectionUtils.getNewProblemConstructor(registrableClass);
        Parameters parameters = Objects.requireNonNull(constructor).getAnnotation(Parameters.class);
        if (parameters != null) {
            /*
             * It is assume that the order of parameter is object,..., file ... , int or
             * double... (It is validated in method that create this windows in
             * ProblemRegistrar). Get the index when int and double inputs start.
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
        Label label = new Label(annotation.displayName());
        TextField textfield = new TextField();
        textfield.setPromptText("Enter the value");
        textfield.setMaxWidth(Double.MAX_VALUE);

        this.numbersTextFieldAdded.add(textfield);
        assert parameterType.getName().matches("int|Integer|double|Double");
        // if the type is int or integer use a validator that only let whole number
        if (parameterType.getName().matches("int|Integer")) {
            textfield.setTextFormatter(TextInputUtil.createWholeTextFormatter((int) annotation.defaultValue()));
        }
        // if the type is double use a validator that let real numbers
        if (parameterType.getName().matches("double|Double")) {
            textfield.setTextFormatter(TextInputUtil.createDecimalTextFormatter(annotation.defaultValue()));
        }

        // add the row to grid pane
        addRow(gridLayoutRowCount++, label, textfield);
        gridLayoutRowCount++;
    }

    /**
     * Setting the number toggle section
     *
     * @param annotation    the NumberToggleInput annotation
     * @param parameterType the type of parameter
     */
    private void numberToggleSection(NumberToggleInput annotation, Class<?> parameterType) {
        // if the toggle group isn't saved create a new one and add the group name as title of section.
        if (!this.numberToggleGroupAdded.containsKey(annotation.groupID())) {
            ToggleGroup toggleGroup = new ToggleGroup();
            this.numberToggleGroupAdded.put(annotation.groupID(), toggleGroup);

            Label groupTitle = new Label(annotation.groupID());
            addRow(gridLayoutRowCount, groupTitle);
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
            textfield.setTextFormatter(TextInputUtil.createWholeTextFormatter((int) annotation.defaultValue()));
        } else if (parameterType.getName().matches("double|Double")) {
            textfield.setTextFormatter(TextInputUtil.createDecimalTextFormatter(annotation.defaultValue()));
        }

        addRow(gridLayoutRowCount++, radioButton, textfield);
        gridLayoutRowCount++;

    }

    /**
     * Setting the file section. It section has textfield and a button to open a
     * filechooser.
     *
     * @param annotation the FileInput annotation
     */
    private void fileSection(FileInput annotation) {
        Label label = new Label(annotation.displayName());
        TextField textfield = new TextField();
        textfield.setMaxWidth(Double.MAX_VALUE);
        Button button = new Button("Browse");
        button.setMaxWidth(Double.MAX_VALUE);

        button.setOnAction((evt) -> {
            File f;

            if (annotation.type() == FileInput.Type.OPEN) {
                FileChooser fileChooser = new FileChooser();
                f = fileChooser.showOpenDialog(getScene().getWindow());
            } else if (annotation.type() == FileInput.Type.SAVE) {
                FileChooser fileChooser = new FileChooser();
                f = fileChooser.showSaveDialog(getScene().getWindow());
            } else { // is a directory
                DirectoryChooser directoryChooser = new DirectoryChooser();
                f = directoryChooser.showDialog(getScene().getWindow());
            }

            if (f != null) {
                textfield.setText(f.getAbsolutePath());
            }
        });

        this.filesTextFieldAdded.add(textfield);
        addRow(gridLayoutRowCount, label, textfield, button);
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

        comboBox.getSelectionModel().selectedItemProperty().addListener((prop, oldv, newv) -> {
            // load the default value
            if (!this.resultOfOperatorConfiguration.containsKey(newv)) {
                // get constructor with DefaultConstructor annotation of operator.
                Constructor<?> constructor = ReflectionUtils.getDefaultConstructor(newv);
                Objects.requireNonNull(constructor, "The operator " + newv.getSimpleName() + " has no a constructor with DefaultConstructor Annotation");
                // get the parameter types of parameters in constructor
                Class<?>[] parameters = constructor.getParameterTypes();

                // get the annotation default constructor
                DefaultConstructor operatorAnnotation = constructor.getAnnotation(DefaultConstructor.class);

                NumberInput[] numberInputs = operatorAnnotation.value();
                // if the constructor as parameter so get the default value for this and keep it in a list.
                if (parameters.length > 0) {
                    List<Number> defaultValues = new ArrayList<>(parameters.length);
                    // for each number input annotation in default constructor get the default value and cast it in the respective type.
                    for (int i = 0; i < parameters.length; i++) {
                        // cast the default value from double to int (truncating the results)
                        if (parameters[i].getName().matches("int|Integer")) {
                            defaultValues.add((int) numberInputs[i].defaultValue());
                        }

                        if (parameters[i].getName().matches("double|Double")) {
                            defaultValues.add(numberInputs[i].defaultValue());
                        }
                    }
                    this.resultOfOperatorConfiguration.put(newv, defaultValues);
                }
            }

            // If a operator is selected enable the Configure button only if his default constructor need parameters.
            configButton.setDisable(ReflectionUtils.getNumberOfParameterInDefaultConstructor(newv) == 0);
        });

        comboBox.getSelectionModel().select(0);

        // show a window to configure the operator
        configButton.setOnAction((evt) -> createAndShowOperatorConfigureDialog(annotation.displayName(),
                comboBox.getSelectionModel().getSelectedItem()));

        addRow(gridLayoutRowCount, label, comboBox, configButton);
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
        dialog.setTitle("Configure " + name);
        dialog.setContentText("Enter the values");
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
            Label label = new Label(annotation.value()[i].displayName());
            TextField textfield = new TextField();
            textfield.setPromptText("Ingrese el valor");

            // cast the default value from double to int (truncating the results)
            if (parameters[i].getName().matches("int|Integer")) {
                textfield.setTextFormatter(TextInputUtil.createWholeTextFormatter((int) annotation.value()[i].defaultValue()));
            }

            if (parameters[i].getName().matches("double|Double")) {
                textfield.setTextFormatter(TextInputUtil.createDecimalTextFormatter(annotation.value()[i].defaultValue()));
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
     * Create the registrable class based in values of attributes configured.
     *
     * @return the registrable instance or null if wasn't created.
     * @throws InvocationTargetException if there is a error while create the operator or the registrable instance.
     */
    public T getRegistrableInstance() throws InvocationTargetException {
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
                return null;
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
        return createInstance(operatorsAndConfig, files, numberInputs, toggleInputs);
    }

    /**
     * Is called when the run button is pressed in view. It method create the
     * registrable instance based in the input field. When the experiment is created
     * an {@link java.util.function.Consumer} is fired.
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
     * @return The registrable instance or null if can't be created.
     * @throws NullPointerException if some operator hasn't a constructor with {@link DefaultConstructor} annotation or some of the values received is null.
     * @throws InvocationTargetException if there is a error while create the operator or the registrable instance
     */
    private T createInstance(Map<Class<?>, List<Number>> operatorsAndConfig, File[] fileInputs,
                             Number[] numberInputs, Number[] toggleInputs) throws InvocationTargetException {
        Objects.requireNonNull(operatorsAndConfig);
        Objects.requireNonNull(fileInputs);
        Objects.requireNonNull(numberInputs);
        Objects.requireNonNull(toggleInputs);

        int parameterSize = operatorsAndConfig.size() + fileInputs.length + numberInputs.length + toggleInputs.length;
        Object[] parameters = new Object[parameterSize];
        int i = 0;
        // create the operators and add to parameters array
        for (Class<?> operator : operatorsAndConfig.keySet()) {
            Object operatorObject = ReflectionUtils.createOperatorInstance(operator, operatorsAndConfig.get(operator).toArray()); // It can throw InvocationTargetException
            parameters[i++] = operatorObject;
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

        T registrable = ReflectionUtils.createRegistrableInstance(this.registrableClass, parameters); // It can throw InvocationTargetException
        return registrable;

    }
}
