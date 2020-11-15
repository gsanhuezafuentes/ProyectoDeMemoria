package controller.multiobjective.indicator;

import application.RegistrableConfiguration;
import controller.multiobjective.indicator.component.ChooseExperimentComponent;
import controller.multiobjective.indicator.component.IndicatorSelectorComponent;
import controller.util.ControllerUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the controller used to configure the quality indicators execution.
 */
public class ConfigurationIndicatorWindowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationIndicatorWindowController.class);
    @FXML
    private Tab indicatorExperimentsTab;

    @FXML
    private AnchorPane indicatorPane;

    @FXML
    private Tab chooseExperimentsTab;

    @FXML
    private Tab configurationTab;

    @FXML
    private AnchorPane experimentTreeViewPane;

    @FXML
    private TreeView<?> experimentTreeView;

    @FXML
    private GridPane configurePane;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextRunButton;

    private Pane root;
    private Stage window;
    private IndicatorSelectorComponent indicatorsListView;
    private ChooseExperimentComponent chooseExperimentComponent;

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
