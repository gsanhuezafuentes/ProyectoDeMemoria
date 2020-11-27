package controller.component.annotation_component;

import annotations.registrable.FileInput;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

/**
 * This class create the component for {@link FileInput}.
 */
public class FileComponent {

    private final FileInput annotation;
    private final Label label;
    private final TextField textfield;
    private final Button button;

    /**
     * Constructor
     *
     * @param annotation the annotation
     * @throws NullPointerException if annotation is null
     */
    private FileComponent(FileInput annotation) {
        Objects.requireNonNull(annotation);
        this.annotation = annotation;
        this.label = new Label(annotation.displayName());
        this.textfield = new TextField();
        this.button = new Button("Browse");
    }

    private void initialize() {
        textfield.setMaxWidth(Double.MAX_VALUE);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction((evt) -> {
            File f;
            if (annotation.type() == FileInput.Type.OPEN) {
                FileChooser fileChooser = new FileChooser();
                f = fileChooser.showOpenDialog(label.getScene().getWindow());
            } else if (annotation.type() == FileInput.Type.SAVE) {
                FileChooser fileChooser = new FileChooser();
                f = fileChooser.showSaveDialog(label.getScene().getWindow());
            } else { // is a directory
                DirectoryChooser directoryChooser = new DirectoryChooser();
                f = directoryChooser.showDialog(label.getScene().getWindow());
            }

            if (f != null) {
                textfield.setText(f.getAbsolutePath());
            }
        });
    }

    /**
     * Get the result of control.
     *
     * @return the file or null if there isn't a route.
     */
    public @Nullable File getResult() {
        return new File(this.textfield.getText());
    }

    /**
     * Create a instance of the component
     *
     * @param annotation   the annotation.
     * @param pane         the pane where add elements.
     * @param gridRowIndex the row where add the elements.
     * @return the component
     * @throws NullPointerException if annotation is null
     */
    public static FileComponent createComponent(FileInput annotation, GridPane pane, int gridRowIndex) {
        FileComponent fileComponent = new FileComponent(annotation);
        fileComponent.initialize();
        pane.addRow(gridRowIndex, fileComponent.label, fileComponent.textfield, fileComponent.button);
        return fileComponent;
    }
}
