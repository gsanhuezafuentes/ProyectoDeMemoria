package controller.component.annotation_component;

import annotations.NumberInput;
import annotations.operator.DefaultConstructor;
import annotations.registrable.OperatorInput;
import annotations.registrable.OperatorOption;
import controller.util.ReflectionUtils;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class create the component for {@link OperatorInput}.
 */
public class OperatorComponent {
    private static final double defaultSpaceInConfigurationGrid = 5;
    private final OperatorInput annotation;
    private final Label label;
    private final ComboBox<Class<?>> comboBox;
    private final Button configButton;
    private final Map<Class<?>, List<Object>> valuesOfOperators;

    /**
     * Constructor
     *
     * @param annotation the annotation
     * @throws NullPointerException if annotation is null
     */
    private OperatorComponent(OperatorInput annotation) {
        Objects.requireNonNull(annotation);
        this.annotation = annotation;
        this.label = new Label(annotation.displayName());
        this.comboBox = new ComboBox<>();
        this.configButton = new Button("Configure");
        this.valuesOfOperators = new HashMap<>();
    }

    private void initialize() {
        // Map with key is the class type of operator and value is the name of operator.
        Map<Class<?>, String> operatorMap = new LinkedHashMap<>(this.annotation.value().length);
        for (OperatorOption option : this.annotation.value()) {
            operatorMap.put(option.value(), option.displayName());
        }

        // Setting Combobox
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

        // Disable by default until select an Operator in comboBox that have parameters.
        configButton.setDisable(true);

        comboBox.getSelectionModel().selectedItemProperty().addListener(this::selectedItemBehavior);

        // select the first element by default
        comboBox.getSelectionModel().select(0);

        // show a window to configure the operator
        configButton.setOnAction((evt) -> createAndShowOperatorConfigureDialog(annotation.displayName(),
                comboBox.getSelectionModel().getSelectedItem()));
    }

    private void selectedItemBehavior(ObservableValue<? extends Class<?>> prop, Class<?> oldv, Class<?> newv) {
        // get constructor with DefaultConstructor annotation of operator.
        Constructor<?> constructor = ReflectionUtils.getDefaultConstructor(newv);
        Objects.requireNonNull(constructor, "The operator " + newv.getSimpleName()
                + " has no a constructor with DefaultConstructor Annotation");
        // get the parameter types of parameters in constructor
        Class<?>[] parameters = constructor.getParameterTypes();

        // get the annotation default constructor
        DefaultConstructor operatorAnnotation = constructor.getAnnotation(DefaultConstructor.class);

        NumberInput[] numberInputs = operatorAnnotation.numbers();
        // if the constructor as parameter so get the default value for this and keep it in a list.
        if (parameters.length > 0) {
            List<Object> defaultValues = new ArrayList<>(parameters.length);
            // for each number input annotation in default constructor get the default value and cast it in the respective type.
            for (int i = 0; i < parameters.length; i++) {
                // cast the default value from double to int (truncating the results)
                if (parameters[i].equals(int.class) || parameters[i].equals(Integer.class)) {
                    defaultValues.add((int) numberInputs[i].defaultValue());
                } else if (parameters[i].equals(double.class) || parameters[i].equals(Double.class)) {
                    defaultValues.add(numberInputs[i].defaultValue());

                }
            }
            this.valuesOfOperators.put(newv, defaultValues);
        }

        // If a operator is selected enable the Configure button only if his default constructor need parameters.
        configButton.setDisable(ReflectionUtils.getNumberOfParameterInDefaultConstructor(newv) == 0);
    }

    /**
     * Get the operator created.
     *
     * @return the operator
     * @throws InvocationTargetException if there is a error creating the result.
     * @throws NullPointerException      if the selected item is null.
     */
    public Object getResult() throws InvocationTargetException {
        Class<?> selectedItem = comboBox.getSelectionModel().getSelectedItem();
        Objects.requireNonNull(selectedItem, "The selected item was null.");

        return ReflectionUtils.createOperatorInstance(selectedItem, this.valuesOfOperators.get(selectedItem).toArray());
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
        Dialog<List<Object>> dialog = new Dialog<>();
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

        DefaultConstructor annotation = constructor.getAnnotation(DefaultConstructor.class);
        ArrayList<NumberComponent> numberComponents = new ArrayList<>(annotation.numbers().length);

        Class<?>[] parameters = constructor.getParameterTypes();

        // Look if already has been added a configuration to this operator
        List<Object> previousResults = this.valuesOfOperators.get(selectedItem);

        // Add component to dialog
        for (int i = 0; i < parameters.length; i++) {
            // If there was previous result of a previous configuration so load it.
            numberComponents.add(NumberComponent.createComponent(annotation.numbers()[i]
                    , (Number) previousResults.get(i), parameters[i], grid, i));

        }


        dialog.getDialogPane().setContent(grid);

        // method used to convert the string in dialog in a number result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                List<Number> resultsOfNumber = numberComponents
                        .stream()
                        .map(NumberComponent::getResult)
                        .collect(Collectors.toList());

                List<Object> result = new ArrayList<>();
                result.addAll(resultsOfNumber);
                return result;
            }
            return null;
        });

        Optional<List<Object>> result = dialog.showAndWait();

        // Save the result if exist in the hashmap
        result.ifPresent(results -> this.valuesOfOperators.put(selectedItem, results));
    }

    /**
     * Create a instance of the component
     *
     * @param annotation   the annotation.
     * @param pane         the pane where add elements.
     * @param gridRowIndex the row where add the elements.
     * @throws NullPointerException if annotation is null
     */
    public static OperatorComponent createComponent(OperatorInput annotation, GridPane pane, int gridRowIndex) {
        OperatorComponent operatorComponent = new OperatorComponent(annotation);
        operatorComponent.initialize();
        pane.addRow(gridRowIndex, operatorComponent.label, operatorComponent.comboBox, operatorComponent.configButton);
        return operatorComponent;
    }
}
