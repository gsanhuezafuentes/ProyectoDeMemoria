package controller;

import controller.component.ExperimentConfigurationComponent;
import controller.util.ControllerUtils;
import controller.util.ReflectionUtils;
import exception.ApplicationException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registrable.Registrable;
import view.utils.CustomDialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This is the controller of ConfigurationDynamicWindow.
 *
 * @param <T> The type of registrable class
 */
public class DynamicConfigurationWindowController<T extends Registrable<?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicConfigurationWindowController.class);

    private final Consumer<T> experimentEvent;
    private final Pane root;
    private static final double defaultSpaceInConfigurationGrid = 5;
    private final Class<? extends T> registrableClass;
    private final ExperimentConfigurationComponent<T> configurationComponent;

    private @Nullable Stage window;

    @FXML
    private ScrollPane configurationPane;

    @FXML
    private Label algorithmNameLabel;

    @FXML
    private Label problemNameLabel;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button runButton;

    @FXML
    private Button cancelButton;

    public DynamicConfigurationWindowController(Class<? extends T> registrable,
                                                Consumer<T> experimentEvent) {
        this.root = ControllerUtils.loadFXML("/view/DynamicConfigurationWindow.fxml",this);
        this.registrableClass = Objects.requireNonNull(registrable);
        this.experimentEvent = Objects.requireNonNull(experimentEvent);
        this.configurationComponent = new ExperimentConfigurationComponent<>(this.registrableClass);
        this.configurationPane.setContent(configurationComponent);
    }

    /**
     * Configurate the binding and listener of Run button and cancel button
     */
    private void addBindingAndListener() {
        this.runButton.setOnAction((evt) -> onRunButtonClick());
        this.cancelButton.setOnAction((evt) -> closeWindow());
    }

    /**
     * Is a class to fire the notification when the experiment is created.
     *
     * @param registrable the experiment registrable class
     * @throws ApplicationException if there isn't register the notification
     *                              callback
     */
    private void notifyExperimentCreation(T registrable) throws ApplicationException {

        experimentEvent.accept(registrable);
    }


    /**
     * Collect the user input from the UI controls and call method to create the instance.
     */
    private void onRunButtonClick() {
        try {
            T registrableInstance = this.configurationComponent.getRegistrableInstance();
            notifyExperimentCreation(registrableInstance);
            closeWindow();
        } catch (InvocationTargetException e) {
            LOGGER.error("Error while create a registrable instance.", e);
            CustomDialogs.showExceptionDialog("Error", "Error in the creation of the experiments.",
                    "A operator or a registrable instance can't be created. Check if all ingresed parameters are correct.", e.getCause());
            return;
        }

    }

    /**
     * Close the window
     */
    private void closeWindow() {
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
        stage.setTitle("Experiment Setup");
        this.window = stage;

        //Fill description tab
        fillDescriptionTab();
        //add binding to run and close button
        addBindingAndListener();

        LOGGER.info("Show DynamicConfigurationWindow for {}.", this.registrableClass.getName());
        stage.show();
    }

    /**
     * Add the values to description tab.
     */
    private void fillDescriptionTab() {
        algorithmNameLabel.setText(ReflectionUtils.getNameOfAlgorithm(registrableClass));
        problemNameLabel.setText(ReflectionUtils.getNameOfProblem(registrableClass));
        descriptionTextArea.setText(ReflectionUtils.getDescriptionOfProblem(registrableClass));
    }
}
