package controller.component.annotation_component;

import annotations.BooleanInput;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * This class create the component for {@link BooleanInput}.
 */
public class BooleanComponent {

    private final BooleanInput annotation;
    private final Label label;
    private final ComboBox<Boolean> comboBox;
    private final boolean defaultValue;

    /**
     * Constructor
     *
     * @param annotation the annotation
     */
    private BooleanComponent(BooleanInput annotation) {
        this(annotation, annotation.defaultValue());
    }

    /**
     * Constructor
     *
     * @param annotation the annotation
     * @throws NullPointerException if annotation or parameterType is null
     */
    private BooleanComponent(BooleanInput annotation, boolean value) {
        Objects.requireNonNull(annotation);
        this.annotation = annotation;
        this.label = new Label(annotation.displayName());
        this.comboBox = new ComboBox();
        this.defaultValue = value;
    }

    private void initialize() {
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setConverter(new StringConverter<Boolean>() {
            @Override
            public String toString(Boolean object) {
                if (object.booleanValue()){
                    return "TRUE";
                }
                else{
                    return "FALSE";
                }
            }

            @Override
            public Boolean fromString(String string) {
                return null;
            }
        });
        comboBox.getItems().addAll(Boolean.FALSE, Boolean.TRUE);
        comboBox.getSelectionModel().select(defaultValue);
    }

    /**
     * Get the result of control.
     *
     * @return the file or null if there isn't a route.
     */
    public @Nullable boolean getResult() {
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
    public static BooleanComponent createComponent(BooleanInput annotation, GridPane pane, int gridRowIndex) {
        BooleanComponent booleanComponent = new BooleanComponent(annotation);
        booleanComponent.initialize();
        pane.addRow(gridRowIndex, booleanComponent.label, booleanComponent.comboBox);
        return booleanComponent;
    }

    /**
     * Create a instance of the component
     *
     * @param annotation   the annotation.
     * @param pane         the pane where add elements.
     * @param gridRowIndex the row where add the elements.
     * @return the component
     */
    public static BooleanComponent createComponent(BooleanInput annotation, boolean value, GridPane pane, int gridRowIndex) {
        BooleanComponent booleanComponent = new BooleanComponent(annotation, value);
        booleanComponent.initialize();
        pane.addRow(gridRowIndex, booleanComponent.label, booleanComponent.comboBox);
        return booleanComponent;
    }

}
