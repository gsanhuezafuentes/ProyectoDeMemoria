package controller.multiobjective.indicator;

import application.RegistrableConfiguration;
import controller.multiobjective.indicator.component.ExperimentChooserComponent;
import controller.multiobjective.indicator.component.IndicatorExperimentConfigurationComponent;
import controller.multiobjective.indicator.component.IndicatorSelectorComponent;
import controller.util.ControllerUtils;
import controller.util.ReflectionUtils;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.metaheuristic.experiment.Experiment;
import model.metaheuristic.experiment.ExperimentSet;
import model.metaheuristic.qualityindicator.impl.GenericIndicator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.utils.CustomDialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * This is the controller used to configure the quality indicators execution.
 */
public class IndicatorConfigurationWindowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorConfigurationWindowController.class);
    @NotNull private String inpPath;
    @NotNull
    private Consumer<ExperimentSet> indicatorsEvent;
    @Nullable
    private Stage window;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab indicatorTab;

    @FXML
    private Tab chooseExperimentsTab;

    @FXML
    private Tab configurationTab;

    @FXML
    private AnchorPane indicatorPane;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextRunButton;

    private final Pane root;

    // Show in first tab
    private IndicatorSelectorComponent indicatorsListView;

    // Show in second tab
    private ExperimentChooserComponent experimentChooserComponent;

    // Show in third tab
    private @Nullable IndicatorExperimentConfigurationComponent indicatorExperimentConfigurationComponent;

    // Event when next or ok button is pressed
    private @NotNull
    final EventHandler<ActionEvent> runButtonEvent = (event) -> {
        if (this.tabPane.getSelectionModel().getSelectedItem() == this.indicatorTab) {
            // move to next pane
            this.tabPane.getSelectionModel().select(this.chooseExperimentsTab);
        } else if (this.tabPane.getSelectionModel().getSelectedItem() == this.chooseExperimentsTab) {
            // move to next pane
            this.tabPane.getSelectionModel().select(this.configurationTab);
            this.indicatorExperimentConfigurationComponent = new IndicatorExperimentConfigurationComponent(this.experimentChooserComponent.getRegistrableClassMap());
            this.configurationTab.setContent(this.indicatorExperimentConfigurationComponent);

        } else {
            List<Callable<Experiment<?>>> registrableList = this.indicatorExperimentConfigurationComponent.getRegistrableList(inpPath);
            ObservableList<Class<? extends GenericIndicator>> selectedIndicators = this.indicatorsListView.getSelectedIndicators();
            List<GenericIndicator> indicators = new ArrayList<>();
            for (Class<? extends GenericIndicator> indicator : selectedIndicators) {
                try {
                    indicators.add((GenericIndicator) ReflectionUtils.createIndicatorInstance(indicator));
                } catch (InvocationTargetException e) {
                    LOGGER.error("Error in the creation of {} instance", indicator.getSimpleName() , e);
                    CustomDialogs.showDialog("Error"
                            , "Error in indicator creation"
                            ,"The indicator " + indicator.getSimpleName() + " can't be created", Alert.AlertType.ERROR);
                    return;
                }
            }
            this.indicatorsEvent.accept(new ExperimentSet(registrableList, indicators));
            // close the window
            this.window.close();
        }
    };

    // Event when previousBotton is pressed
    private @NotNull
    final EventHandler<ActionEvent> previousEvent = (event) -> {
        if (this.tabPane.getSelectionModel().getSelectedItem() == this.chooseExperimentsTab) {
            // move to next pane
            tabPane.getSelectionModel().select(this.indicatorTab);
        } else if (this.tabPane.getSelectionModel().getSelectedItem() == this.configurationTab) {
            // move to next pane
            tabPane.getSelectionModel().select(this.chooseExperimentsTab);
        }
    };

    // Event when change of tab
    private @NotNull
    final ChangeListener<? super Number> tabPaneListener = (prop, oldV, newV) -> {
        // When move to the previos tab
        if (newV.intValue() < oldV.intValue()) {
            switch (newV.intValue()) {
                case 0:
                    this.chooseExperimentsTab.setDisable(true);
                    this.previousButton.setDisable(true);
                case 1:
                    this.configurationTab.setDisable(true);
                    this.nextRunButton.setText("Next");

                    // Remove the content of third pane
                    this.indicatorExperimentConfigurationComponent = null;
                    this.configurationTab.setContent(null);
            }
        }
        // When move to next tab
        else if (newV.intValue() > oldV.intValue()){
            switch (newV.intValue()) {
                case 1:
                    this.chooseExperimentsTab.setDisable(false);
                    this.previousButton.setDisable(false);
                    break;
                case 2:
                    this.configurationTab.setDisable(false);
                    this.nextRunButton.setText("Run");
                    break;
            }
        }

    };

    /**
     *
     * @param inpPath the inpPath
     * @throws NullPointerException if inpPath or indicatorsEvent is null.
     * @throws IllegalArgumentException if inpPath is empty.
     */
    public IndicatorConfigurationWindowController(@NotNull String inpPath, @NotNull Consumer<ExperimentSet> indicatorsEvent) {
        Objects.requireNonNull(inpPath, "Inp path is null.");
        Objects.requireNonNull(indicatorsEvent);
        if (inpPath.isEmpty()) throw new IllegalArgumentException("Inp path is empty.");
        this.inpPath = inpPath;
        this.indicatorsEvent = indicatorsEvent;

        this.root = ControllerUtils.loadFXML("/view/multiobjective/indicator/IndicatorConfigurationWindow.fxml", this);

        // Create the list view with generations indicators.
        this.indicatorsListView = new IndicatorSelectorComponent(RegistrableConfiguration.INDICATORS);
        AnchorPane.setBottomAnchor(this.indicatorsListView, 10d);
        AnchorPane.setLeftAnchor(this.indicatorsListView, 10d);
        AnchorPane.setRightAnchor(this.indicatorsListView, 10d);
        AnchorPane.setTopAnchor(this.indicatorsListView, 10d);
        this.indicatorPane.getChildren().add(this.indicatorsListView);

        // Setting the second tab pane
        this.experimentChooserComponent = new ExperimentChooserComponent();
        this.chooseExperimentsTab.setContent(this.experimentChooserComponent);

        // The setting of third tab is in the event of nextRunButton

        configureListenerAndEvents();
    }

    private void configureListenerAndEvents() {
        this.nextRunButton.setOnAction(runButtonEvent);
        this.previousButton.setOnAction(previousEvent);
        this.tabPane.getSelectionModel().selectedIndexProperty().addListener(this.tabPaneListener);
    }

    /**
     * Show the associated view in window
     */
    public void showWindow() {
        Stage stage = new Stage();
        stage.setScene(new Scene(this.root));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Configure indicators.");
        this.window = stage;
        LOGGER.info("Show IndicatorConfigurationWindowController.");
        stage.show();
    }
}
