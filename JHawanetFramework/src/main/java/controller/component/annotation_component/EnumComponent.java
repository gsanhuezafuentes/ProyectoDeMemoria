package controller.component.annotation_component;

import annotations.EnumInput;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * This class create the component for {@link EnumInput}.
 */
public class EnumComponent {
    private final EnumInput annotation;
    private final Label label;
    private final ComboBox<Enum<?>> comboBox;
    private final Enum<?> defaultValue;

    /**
     * Constructor
     *
     * @param annotation the annotation.
     * @throws NullPointerException if annotation is null.
     */
    private EnumComponent(EnumInput annotation, Enum<?> value) {
        this.annotation = annotation;
        this.label = new Label(annotation.displayName());
        this.comboBox = new ComboBox();
        this.defaultValue = value;
    }

    /**
     * Initialize the component behaviour.
     * @throws IllegalArgumentException if the default value isn't of the type of annotation enum type.
     */
    private void initialize() {
        comboBox.setMaxWidth(Double.MAX_VALUE);

        // Check type of defaultValue
        if (this.defaultValue.getDeclaringClass() != this.annotation.enumClass()){
            throw new IllegalArgumentException("The default value is a " + this.defaultValue.getDeclaringClass()
                    + " but was expected a " + this.annotation.enumClass());
        }

        comboBox.getItems().addAll(this.annotation.enumClass().getEnumConstants());
        comboBox.getSelectionModel().select(this.defaultValue);
    }

    /**
     * Get the result of control.
     *
     * @return the file or null if there isn't a route.
     */
    public @Nullable Enum<?> getResult() {
        return this.comboBox.getSelectionModel().getSelectedItem();
    }

    /**
     * Create a instance of the component
     *
     * @param annotation   the annotation.
     * @param pane         the pane where add elements.
     * @param gridRowIndex the row where add the elements.
     * @return
     */
    public static EnumComponent createComponent(EnumInput annotation, GridPane pane, int gridRowIndex) {
        Objects.requireNonNull(annotation);
        String defaultValue = annotation.defaultValue();
        Enum<?> defaultEnum;
        if (defaultValue == null || defaultValue.isEmpty()){
            defaultEnum = annotation.enumClass().getEnumConstants()[0];
        } else{
            defaultEnum = Arrays.stream(annotation.enumClass().getEnumConstants())
                    .filter(anEnum -> anEnum.name().equals(defaultValue))
                    .findFirst()
                    .get();
        }

        EnumComponent enumComponent = new EnumComponent(annotation, defaultEnum);
        enumComponent.initialize();
        pane.addRow(gridRowIndex, enumComponent.label, enumComponent.comboBox);
        return enumComponent;
    }

    /**
     * Create a instance of the component
     *
     * @param annotation   the annotation.
     * @param pane         the pane where add elements.
     * @param gridRowIndex the row where add the elements.
     * @return the EnumComponent
     * @throws NullPointerException if annotation is null.
     */
    public static EnumComponent createComponent(EnumInput annotation, Enum<?> value, GridPane pane, int gridRowIndex) {
        EnumComponent enumComponent = new EnumComponent(annotation, value);
        enumComponent.initialize();
        pane.addRow(gridRowIndex, enumComponent.label, enumComponent.comboBox);
        return enumComponent;
    }
}
