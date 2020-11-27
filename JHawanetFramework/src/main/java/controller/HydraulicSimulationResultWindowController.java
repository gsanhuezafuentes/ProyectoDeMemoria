package controller;

import controller.util.ControllerUtils;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.epanet.element.networkcomponent.Component;
import model.epanet.hydraulicsimulation.HydraulicSimulation;
import model.epanet.hydraulicsimulation.LinkSimulationResult;
import model.epanet.hydraulicsimulation.NodeSimulationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.utils.CustomDialogs;

import java.util.List;
import java.util.Objects;

/**
 * This is the controller for HydraulicSimulationResultWindow
 */
public class HydraulicSimulationResultWindowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HydraulicSimulationResultWindowController.class);


    @FXML
    private StackPane tablePane;

    @FXML
    private RadioButton networkNodeAtButton;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private RadioButton networkLinkAtButton;

    @FXML
    private ComboBox<String> timesComboBox;

    @FXML
    private RadioButton timeSeriesNodeButton;

    @FXML
    private RadioButton timeSeriesLinkButton;

    @FXML
    private TextField idTextField;

    @FXML
    private VBox timeSeriesPane;

    private Stage window;
    private final Pane root;
    private final HydraulicSimulation resultSimulation;
    private final ObjectProperty<Object> selectableItem;
    private ChangeListener<Object> changeListener;


    /**
     * Constructor
     *
     * @param resultSimulation the HydraulicSimulation that store the results of execution
     * @param selectableItem   the selectable property that indicate if node or link is selected
     * @throws NullPointerException if resultSimulation is null or selectableItem is null
     */
    public HydraulicSimulationResultWindowController(HydraulicSimulation resultSimulation, ObjectProperty<Object> selectableItem) {
        LOGGER.debug("Initializing HydraulicSimulationController.");

        Objects.requireNonNull(resultSimulation);
        Objects.requireNonNull(selectableItem); // selectableItem type should be a Node class or a Link class

        this.resultSimulation = resultSimulation;
        this.selectableItem = selectableItem;
        this.root = ControllerUtils.loadFXML("/view/HydraulicSimulationResultWindow.fxml", this); // initialize fxml component

        // Enable time series for problem with more than one simulation time.

        LOGGER.debug("The result of hydraulic simulation has {} periods.", resultSimulation.getTimes().size());
        timeSeriesPane.setVisible(resultSimulation.getTimes().size() > 1);
        timeSeriesPane.setManaged(timeSeriesPane.isVisible());
        addBindingAndListener();
        fillComboBox();
    }

    /**
     * Add to the combobox of times the possible values.
     */
    private void fillComboBox() {
        List<String> times = this.resultSimulation.getTimes();
        this.timesComboBox.getItems().addAll(times);
        this.timesComboBox.getSelectionModel().select(0);
    }

    /**
     * Add the binding and listener to components.
     */
    private void addBindingAndListener() {
        // When the simulation has more than one time of simulation this property change the selected element
        // of time series.
        this.changeListener = (observable, oldValue, newValue) -> {
            if (newValue instanceof Component) { // IS LINK OR NODE
                Component comp = (Component) newValue;
                idTextField.setText(comp.getId());
            }
        };
        this.selectableItem.addListener(changeListener);
    }

    /**
     * Method called when cancel button is pressed. This method close the windows.
     */
    @FXML // This method is configure from fxml file
    private void onCancelButtonClick() {
        this.selectableItem.removeListener(changeListener); // remove the listener when window is closed
        assert this.window != null;
        // close the dialog
        this.window.close();
    }

    /**
     * Method called when ok button is pressed.
     * Fill the table of results to show.
     */
    @FXML // This method is configure from fxml file
    private void onOKButtonClick() {
        Toggle selectedToggle = this.toggleGroup.getSelectedToggle();
        boolean showPane = true;

        if (selectedToggle == this.networkNodeAtButton) {
            // Get the result for all node in a specific time to show in the table.
            List<NodeSimulationResult> nodeResultInTime = this.resultSimulation.getNodeResultsInTime(this.timesComboBox.getValue());
            fillTableWithNodeResult(nodeResultInTime, false);
        } else if (selectedToggle == this.networkLinkAtButton) {
            // Get the result for all links in a specific time to show in the table.
            List<LinkSimulationResult> linkResultInTime = this.resultSimulation.getLinkResultInTime(this.timesComboBox.getValue());
            fillTableWithLinkResult(linkResultInTime, false);
        } else {
            // Get the result for a specific node in each period.
            String id = this.idTextField.getText();
            if (selectedToggle == this.timeSeriesNodeButton) {
                List<NodeSimulationResult> timeSeriesForNode = this.resultSimulation.getTimeSeriesForNode(id);
                if (!timeSeriesForNode.isEmpty()){
                    fillTableWithNodeResult(timeSeriesForNode, true);
                } else{
                    LOGGER.debug("There is no node '{}' to show the hydraulic results.", id);
                    CustomDialogs.showDialog("Error", "", "There is no node " + id, Alert.AlertType.ERROR, this.window);
                    showPane = false;
                }
            } else if (selectedToggle == this.timeSeriesLinkButton) {
                // Get the result for a specific links in each period.

                List<LinkSimulationResult> timeSeriesForLink = this.resultSimulation.getTimeSeriesForLink(id);
                if (!timeSeriesForLink.isEmpty()){
                    fillTableWithLinkResult(timeSeriesForLink,true);
                }else {
                    LOGGER.debug("There is no link '{}' to show the hydraulic results.", id);
                    CustomDialogs.showDialog("Error", "", "There is no link " + id, Alert.AlertType.ERROR, this.window);
                    showPane = false;
                }
            }
        }
        if (showPane){
            this.tablePane.setManaged(true);
            this.tablePane.setVisible(true);
            this.window.sizeToScene();
        }
    }

    /**
     * Method used to fill the table with the result of simulation for nodes.
     * @param results the results of simulation.
     * @param showAsTimeSerie a boolean indicated if show the column as NodeID(false) or show as Time(true).
     */
    private void fillTableWithNodeResult(List<NodeSimulationResult> results, boolean showAsTimeSerie) {
        TableView<NodeSimulationResult> table = new TableView<>();
        table.getItems().clear();
        table.getItems().addAll(results);
        TableColumn<NodeSimulationResult,String> idOrTimeCol;
        TableColumn<NodeSimulationResult,String> demandCol = new TableColumn<>("Demand");
        TableColumn<NodeSimulationResult,String> headCol = new TableColumn<>("Head");
        TableColumn<NodeSimulationResult,String> pressureCol = new TableColumn<>("Pressure");
        TableColumn<NodeSimulationResult,String> qualityCol = new TableColumn<>("Quality");

        if (!showAsTimeSerie){
            idOrTimeCol = new TableColumn<>("Node ID");
            idOrTimeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getId()));
        } else {
            idOrTimeCol = new TableColumn<>("Time");
            idOrTimeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getTimeString()));
        }
        demandCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(Double.toString(param.getValue().getDemand())));
        headCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(Double.toString(param.getValue().getHead())));
        pressureCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(Double.toString(param.getValue().getPressure())));
        qualityCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(Double.toString(param.getValue().getQuality())));

        table.getColumns().clear();
        table.getColumns().addAll(idOrTimeCol, demandCol, headCol, pressureCol, qualityCol);
        this.tablePane.getChildren().clear(); // remove the previus table
        this.tablePane.getChildren().addAll(table);
    }

    /**
     * Method used to fill the table with the result of simulation for links.
     * @param results the results of simulation.
     * @param showAsTimeSerie a boolean indicated if show the column as NodeID(false) or show as Time(true).
     */
    private void fillTableWithLinkResult(List<LinkSimulationResult> results, boolean showAsTimeSerie) {

        TableView<LinkSimulationResult> table = new TableView<>();
        table.getItems().addAll(results);

        TableColumn<LinkSimulationResult,String> idOrTimeCol;
        TableColumn<LinkSimulationResult,String> flowCol = new TableColumn<>("Flow");
        TableColumn<LinkSimulationResult,String> velocityCol = new TableColumn<>("Velocity");
        TableColumn<LinkSimulationResult,String> headlossCol = new TableColumn<>("Headloss");
        TableColumn<LinkSimulationResult,String> statusCol = new TableColumn<>("Status");

        if (!showAsTimeSerie){
            idOrTimeCol = new TableColumn<>("Node ID");
            idOrTimeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getId()));
        } else {
            idOrTimeCol = new TableColumn<>("Time");
            idOrTimeCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getTimeString()));
        }
        flowCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(Double.toString(param.getValue().getFlow())));
        velocityCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(Double.toString(param.getValue().getVelocity())));
        headlossCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(Double.toString(param.getValue().getHeadloss())));
        statusCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getStatus().getName()));

        table.getColumns().clear();
        table.getColumns().addAll(idOrTimeCol, flowCol, velocityCol, headlossCol, statusCol);

        this.tablePane.getChildren().clear(); // remove the previus table
        this.tablePane.getChildren().addAll(table);
    }

    /**
     * Show the associated view in window
     */
    public void showWindow() {
        Stage stage = new Stage();
        stage.setScene(new Scene(this.root));
        stage.setAlwaysOnTop(true);
//		stage.initStyle(StageStyle.UTILITY);
        stage.setOnCloseRequest((e) -> onCancelButtonClick());
        LOGGER.info("Show HydraulicSimulationResultWindow.");

        stage.show();
        stage.setTitle("Result of execution");
        Platform.runLater(() -> {
            this.window.setMinWidth(this.root.getWidth());
            this.window.setMinHeight(this.root.getHeight());
        });
        this.window = stage;
    }
}
