package controller.component.annotation_component;

import annotations.registrable.NumberToggleInput;
import controller.util.TextInputUtil;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This class create the component for {@link NumberToggleInput}.
 */
public class NumberToggleComponent {

    private final NumberToggleInput annotation;
    private final Class<?> parameterType;
    private final ToggleGroup toggleGroup;
    private final RadioButton radioButton;
    private final TextField textfield;

    /**
     * Constructor
     *
     * @param annotation    the annotation
     * @param parameterType the type that describe the annotation
     * @param toggleGroup   the toggle group where the element of this component has to be added.
     * @throws NullPointerException     if annotation, parameterType or toggleGroup is null
     * @throws IllegalArgumentException if parameterType isn't a int, Integer, double or Double type.
     */
    private NumberToggleComponent(NumberToggleInput annotation, Class<?> parameterType, ToggleGroup toggleGroup) {
        Objects.requireNonNull(annotation);
        Objects.requireNonNull(parameterType);
        Objects.requireNonNull(toggleGroup);
        if (!(parameterType.equals(int.class) || parameterType.equals(Integer.class) || parameterType.equals(double.class) || parameterType.equals(Double.class))) {
            throw new IllegalArgumentException("The parameterType as to be int or double or his wrap type but was " + parameterType.getSimpleName());
        }
        this.annotation = annotation;
        this.parameterType = parameterType;
        this.toggleGroup = toggleGroup;
        this.radioButton = new RadioButton(annotation.displayName());
        this.textfield = new TextField();

    }

    public void initialize() {
        this.radioButton.setToggleGroup(this.toggleGroup);

        // if the radio button is the first element in group so select it by default
        if (toggleGroup.getToggles().size() == 1) {
            this.radioButton.setSelected(true);
        }

        // if the type is int or integer use a validator that only let whole number
        if (this.parameterType.equals(int.class) || this.parameterType.equals(Integer.class)) {
            textfield.setTextFormatter(TextInputUtil.createWholeTextFormatter((int) annotation.defaultValue()));// this cast in mandatory
        } else { // is double or Double
            this.textfield.setTextFormatter(TextInputUtil.createDecimalTextFormatter(annotation.defaultValue()));
        }

        //disable textfield if his radio button is not selected
        this.textfield.disableProperty().bind(radioButton.selectedProperty().not());
        this.textfield.setMaxWidth(Double.MAX_VALUE);
    }

    /**
     * Return the result save in text field.<p>
     * If the radio button is disabled return {@link Integer#MIN_VALUE} or {@link Double#MIN_VALUE} depending on the type of parameter.
     *
     * @return the value.
     */
    public @NotNull Number getResult() {
        if (this.radioButton.isSelected()) {
            return (Number) this.textfield.getTextFormatter().getValue();
        } else if (this.parameterType.equals(int.class) || this.parameterType.equals(Integer.class)) {
            return Integer.MIN_VALUE;
        } else { // is double or Double
            return Double.MIN_VALUE;
        }
    }

//    /**
//     * Setting the number toggle section
//     *
//     * @param annotation    the NumberToggleInput annotation
//     * @param parameterType the type of parameter
//     */
//    private void numberToggleSection(NumberToggleInput annotation, Class<?> parameterType) {
//        // if the toggle group isn't saved create a new one and add the group name as title of section.
//        if (!this.numberToggleGroupAdded.containsKey(annotation.groupID())) {
//            ToggleGroup toggleGroup = new ToggleGroup();
//            this.numberToggleGroupAdded.put(annotation.groupID(), toggleGroup);
//
//            Label groupTitle = new Label(annotation.groupID());
//            addRow(gridLayoutRowCount, groupTitle);
//            gridLayoutRowCount++;
//        }
//        ToggleGroup toggleGroup = this.numberToggleGroupAdded.get(annotation.groupID());
//    }

    /**
     * Create a instance of the component
     *
     * @param annotation    the annotation.
     * @param parameterType the type of field.
     * @param toggleGroup the toggle group used by the radio button created by this component
     * @param pane the pane where add elements.
     * @param gridRowIndex the row where add the elements.
     * @throws NullPointerException     if annotation, parameterType or toggleGroup is null
     * @throws IllegalArgumentException if parameterType isn't a int, Integer, double or Double type.
     */
    public static NumberToggleComponent createComponent(NumberToggleInput annotation, Class<?> parameterType, ToggleGroup toggleGroup, GridPane pane, int gridRowIndex) {
        NumberToggleComponent numberToggleComponent = new NumberToggleComponent(annotation, parameterType, toggleGroup);
        numberToggleComponent.initialize();
        pane.addRow(gridRowIndex, numberToggleComponent.radioButton, numberToggleComponent.textfield);
        return numberToggleComponent;
    }
}
