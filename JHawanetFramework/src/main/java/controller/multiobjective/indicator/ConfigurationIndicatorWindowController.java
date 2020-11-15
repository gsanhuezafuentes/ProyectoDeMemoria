package controller.multiobjective.indicator;

import application.RegistrableConfiguration;
import controller.multiobjective.indicator.component.ChooseExperimentComponent;
import controller.multiobjective.indicator.component.ConfigurationExperimentComponent;
import controller.multiobjective.indicator.component.IndicatorSelectorComponent;
import controller.util.ControllerUtils;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registrable.MultiObjectiveRegistrable;

import java.util.List;
import java.util.Map;

/**
 * This is the controller used to configure the quality indicators execution.
 */
public class ConfigurationIndicatorWindowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationIndicatorWindowController.class);

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

    private Pane root;
    private Stage window;
    private IndicatorSelectorComponent indicatorsListView;
    private ChooseExperimentComponent chooseExperimentComponent;
    private ConfigurationExperimentComponent configurationExperimentComponent;


    private EventHandler<ActionEvent> runButtonEvent = (event) -> {
        if (this.tabPane.getSelectionModel().getSelectedItem() == this.indicatorTab) {
            // move to next pane
            tabPane.getSelectionModel().select(this.chooseExperimentsTab);
        } else if (this.tabPane.getSelectionModel().getSelectedItem() == this.chooseExperimentsTab) {
            // move to next pane
            tabPane.getSelectionModel().select(this.configurationTab);
            this.configurationExperimentComponent = new ConfigurationExperimentComponent(this.chooseExperimentComponent.getRegistrableClassMap());
            this.configurationTab.setContent(this.configurationExperimentComponent);

        } else {
            System.out.println("Get experiments");
        }
    };

    private EventHandler<ActionEvent> previousEvent = (event) -> {
        if (this.tabPane.getSelectionModel().getSelectedItem() == this.chooseExperimentsTab) {
            // move to next pane
            tabPane.getSelectionModel().select(this.indicatorTab);
        } else if (this.tabPane.getSelectionModel().getSelectedItem() == this.configurationTab) {
            // move to next pane
            tabPane.getSelectionModel().select(this.chooseExperimentsTab);
        }
    };

    private ChangeListener<? super Number> tabPaneListener = (prop, oldV, newV) -> {
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
                    this.configurationExperimentComponent = null;
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
                    this.nextRunButton.setText("OK");
                    break;
            }
        }

    };

    public ConfigurationIndicatorWindowController() {
        this.root = ControllerUtils.loadFXML("/view/multiobjective/indicator/ConfigurationIndicatorWindow.fxml", this);

        // Create the list view with generations indicators.
        this.indicatorsListView = new IndicatorSelectorComponent(RegistrableConfiguration.INDICATORS);
        AnchorPane.setBottomAnchor(this.indicatorsListView, 10d);
        AnchorPane.setLeftAnchor(this.indicatorsListView, 10d);
        AnchorPane.setRightAnchor(this.indicatorsListView, 10d);
        AnchorPane.setTopAnchor(this.indicatorsListView, 10d);
        this.indicatorPane.getChildren().add(this.indicatorsListView);

        // Setting the second tab pane
        this.chooseExperimentComponent = new ChooseExperimentComponent();
        this.chooseExperimentsTab.setContent(this.chooseExperimentComponent);

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
    public void showWindow(Stage stage) {
//        Stage stage = new Stage();
        stage.setScene(new Scene(this.root));
//        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Configure indicators.");

        LOGGER.info("Show ConfigurationIndicatorWindow.");
        this.window = stage;
        stage.show();
    }
}
