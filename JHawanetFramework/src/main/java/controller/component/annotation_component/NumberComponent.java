package controller.component.annotation_component;

import annotations.NumberInput;
import controller.util.TextInputUtil;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NumberComponent {
    private final NumberInput annotation;
    private final Class<?> parameterType;
    private final Label label;
    private final TextField textfield;
    private Number value;

    /**
     * Constructor
     *
     * @param annotation    the annotation.
     * @param parameterType the type of field. Has to be integer or double types or his wrapper.
     * @throws NullPointerException if annotation or parameterType is null
     * @throws IllegalArgumentException if parameterType isnot a int, Integer, double or Double type.
     */
    private NumberComponent(NumberInput annotation, Class<?> parameterType) {
        this(annotation, annotation.defaultValue(), parameterType);
    }

    /**
     * Constructor
     *
     * @param annotation    the annotation.
     * @param value the value of textfield created.
     * @param parameterType the type of field. Has to be integer or double types or his wrapper.
     * @throws NullPointerException if annotation or parameterType is null
     * @throws IllegalArgumentException if parameterType isnot a int, Integer, double or Double type.
     */
    private NumberComponent(NumberInput annotation, Number value, Class<?> parameterType) {
        Objects.requireNonNull(annotation);
        Objects.requireNonNull(parameterType);
        if (!(parameterType.equals(int.class) || parameterType.equals(Integer.class) || parameterType.equals(double.class) || parameterType.equals(Double.class))) {
            throw new IllegalArgumentException("The parameterType as to be int or double or his wrap type but was " + parameterType.getSimpleName());
        }
        this.annotation = annotation;
        this.parameterType = parameterType;
        this.label = new Label(annotation.displayName());
        this.textfield = new TextField();
        this.value = value;

    }

    private void initialize() {
        textfield.setPromptText("Enter the value");
        textfield.setMaxWidth(Double.MAX_VALUE);

        assert parameterType.getName().matches("int|Integer|double|Double");

        // if the type is int or integer use a validator that only let whole number
        if (this.parameterType.equals(int.class) || this.parameterType.equals(Integer.class)) {
            textfield.setTextFormatter(TextInputUtil.createWholeTextFormatter(value.intValue()));
        }else { // if the type is double use a validator that let real numbers
            textfield.setTextFormatter(TextInputUtil.createDecimalTextFormatter(value.doubleValue()));
        }
    }

    /**
     * Return the value associated to text field.
     * @return the value. It can be a Integer or Double.
     */
    public @NotNull Number getResult() {
        return (Number) textfield.getTextFormatter().getValue();
    }

    /**
     * Create a instance of the component
     *
     * @param annotation    the annotation.
     * @param parameterType the type of field.
     * @param pane the pane where add elements.
     * @param gridRowIndex the row where add the elements.
     * @throws NullPointerException if annotation or parameterType is null
     * @throws IllegalArgumentException if parameterType isn't a int, Integer, double or Double type.
     */
    public static NumberComponent createComponent(NumberInput annotation, Class<?> parameterType, GridPane pane, int gridRowIndex) {
        NumberComponent numberComponent = new NumberComponent(annotation, parameterType);
        numberComponent.initialize();
        pane.addRow(gridRowIndex, numberComponent.label, numberComponent.textfield);
        return numberComponent;
    }

    /**
     * Create a instance of the component
     *
     * @param annotation    the annotation.
     * @param parameterType the type of field.
     * @param pane the pane where add elements.
     * @param gridRowIndex the row where add the elements.
     * @throws NullPointerException if annotation or parameterType is null
     * @throws IllegalArgumentException if parameterType isn't a int, Integer, double or Double type.
     */
    public static NumberComponent createComponent(NumberInput annotation, Number value, Class<?> parameterType, GridPane pane, int gridRowIndex) {
        NumberComponent numberComponent = new NumberComponent(annotation, value, parameterType);
        numberComponent.initialize();
        pane.addRow(gridRowIndex, numberComponent.label, numberComponent.textfield);
        return numberComponent;
    }
}
