package controller.component;

import annotations.BooleanInput;
import annotations.EnumInput;
import annotations.NumberInput;
import annotations.registrable.*;
import controller.component.annotation_component.*;
import controller.util.ReflectionUtils;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registrable.Registrable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * This class is a component that show a interface to configure the registrable instance before create it.
 *
 * @param <T> The type of registrable.
 */
public class ExperimentConfigurationComponent<T extends Registrable<?>> extends GridPane {
    private static Logger LOGGER = LoggerFactory.getLogger(ExperimentConfigurationComponent.class);

    private int gridLayoutRowCount;
    private final Class<? extends T> registrableClass;
    private static final double defaultSpaceInConfigurationGrid = 5;

    private final List<BooleanComponent> booleanComponents = new ArrayList<>();
    private final List<NumberComponent> numberComponents = new ArrayList<>();
    private final List<NumberToggleComponent> numberToggleComponents = new ArrayList<>();
    private final List<FileComponent> fileComponents = new ArrayList<>();
    private final List<EnumComponent> enumComponents = new ArrayList<>();
    private final List<OperatorComponent> operatorComponents = new ArrayList<>();

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
             * It is assume that the order of parameter is object,..., file ... , enum..., boolean ..., int or
             * double... (It is validated in method that create this windows in
             * ProblemRegistrar). Get the index when double inputs start.
             */
            int parameterIndex = constructor.getParameterCount() - parameters.numbers().length
                    - parameters.numbersToggle().length; // What it the last index of parameter


            // Create the textfield for native input (double, int, etc)
            for (NumberInput annotation : parameters.numbers()) {
                this.numberComponents.add(NumberComponent.createComponent(
                        annotation,
                        constructor.getParameterTypes()[parameterIndex]
                        , this
                        , this.gridLayoutRowCount++));
                parameterIndex++;
            }

            // Create the textfield for native input exclusive each other (double, int, etc)
            for (NumberToggleInput annotation : parameters.numbersToggle()) {
                ToggleGroup group;
                if (!this.numberToggleGroupAdded.containsKey(annotation.groupID())) {
                    this.numberToggleGroupAdded.put(annotation.groupID(), new ToggleGroup());
                    addRow(this.gridLayoutRowCount++, new Label(annotation.groupID()));
                }
                group = this.numberToggleGroupAdded.get(annotation.groupID());

                this.numberToggleComponents.add(NumberToggleComponent.createComponent(
                        annotation
                        , constructor.getParameterTypes()[parameterIndex]
                        , group
                        , this
                        , this.gridLayoutRowCount++));
                parameterIndex++;
            }

            // Create textfield for show the path selected in a filechooser
            for (FileInput annotation : parameters.files()) {
                fileComponents.add(FileComponent.createComponent(annotation, this, this.gridLayoutRowCount++));
            }

            for (EnumInput annotation : parameters.enums()) {
                enumComponents.add(EnumComponent.createComponent(annotation, this, this.gridLayoutRowCount++));
            }

            for (BooleanInput annotation : parameters.booleans()) {
                this.booleanComponents.add(BooleanComponent.createComponent(
                        annotation
                        , this
                        , this.gridLayoutRowCount++));
            }

            // Create combobox for object input
            for (OperatorInput annotation : parameters.operators()) {
                operatorComponents.add(OperatorComponent.createComponent(annotation, this, this.gridLayoutRowCount++));
            }

        }
    }


    /**
     * Create the registrable class based in values of attributes configured.
     *
     * @return the registrable instance or null if wasn't created.
     * @throws InvocationTargetException if there is a error while create the operator or the registrable instance.
     */
    public T getRegistrableInstance() throws InvocationTargetException {
        int parameterSize = ReflectionUtils.getNumberOfParameterInRegistrableConstructor(this.registrableClass);
        List<Object> parameters = new ArrayList<>(parameterSize);

        for (OperatorComponent operatorComponent : this.operatorComponents) {
            Object operatorComponentResult = operatorComponent.getResult();
            parameters.add(operatorComponentResult);
        }
        this.fileComponents.stream().map(FileComponent::getResult).forEach(result -> parameters.add(result));
        this.enumComponents.stream().map(EnumComponent::getResult).forEach(result -> parameters.add(result));
        this.booleanComponents.stream().map(BooleanComponent::getResult).forEach(result -> parameters.add(result));
        this.numberComponents.stream().map(NumberComponent::getResult).forEach(result -> parameters.add(result));
        this.numberToggleComponents.stream().map(NumberToggleComponent::getResult).forEach(result -> parameters.add(result));

        T registrable = ReflectionUtils.createRegistrableInstance(this.registrableClass, parameters.toArray());
        return registrable;
    }
}
