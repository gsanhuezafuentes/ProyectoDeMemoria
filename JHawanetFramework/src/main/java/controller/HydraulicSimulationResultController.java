package controller;

import exception.ApplicationException;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.epanet.element.networkcomponent.Component;
import model.epanet.element.result.LinkSimulationResult;
import model.epanet.element.result.NodeSimulationResult;
import model.epanet.element.utils.HydraulicSimulation;
import view.utils.CustomDialogs;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class HydraulicSimulationResultController {

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
    public HydraulicSimulationResultController(HydraulicSimulation resultSimulation, ObjectProperty<Object> selectableItem) {
        Objects.requireNonNull(resultSimulation);
        Objects.requireNonNull(selectableItem); // selectableItem type should be a Node class or a Link class
        this.resultSimulation = resultSimulation;
        this.selectableItem = selectableItem;
        this.root = loadFXML(); // initialize fxml component

        timeSeriesPane.setVisible(resultSimulation.getTimes().size() > 1);
        timeSeriesPane.setManaged(timeSeriesPane.isVisible());
        addBindingAndListener();
        fillComboBox();
    }

    private void fillComboBox() {
        List<String> times = this.resultSimulation.getTimes();
        this.timesComboBox.getItems().addAll(times);
        this.timesComboBox.getSelectionModel().select(0);
    }

    private void addBindingAndListener() {
        this.changeListener = (observable, oldValue, newValue) -> {
            if (newValue instanceof Component) { // IS LINK OR NODE
                Component comp = (Component) newValue;
                idTextField.setText(comp.getId());
            }
        };
        this.selectableItem.addListener(changeListener);
    }

    /**
     * Load the FXML view associated to this controller.
     *
     * @return the root pane.
     * @throws ApplicationException if there is an error in load the .fxml.
     */
    private Pane loadFXML() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/HydraulicSimulationResultWindow.fxml"));
        fxmlLoader.setController(this);
        try {
            return fxmlLoader.load();
        } catch (IOException exception) {
            throw new ApplicationException(exception);
        }
    }

    @FXML // This method is configure from fxml file
    private void onCancelButtonClick() {
        this.selectableItem.removeListener(changeListener); // remove the listener when window is closed
        assert this.window != null;
        // close the dialog
        this.window.close();
    }

    @FXML // This method is configure from fxml file
    private void onOKButtonClick() {
        Toggle selectedToggle = this.toggleGroup.getSelectedToggle();
        boolean showPane = true;

        if (selectedToggle == this.networkNodeAtButton) {
            List<NodeSimulationResult> nodeResultInTime = this.resultSimulation.getNodeResultInTime(this.timesComboBox.getValue());
            fillTableWithNodeResult(nodeResultInTime, false);
        } else if (selectedToggle == this.networkLinkAtButton) {
            List<LinkSimulationResult> linkResultInTime = this.resultSimulation.getLinkResultInTime(this.timesComboBox.getValue());
            fillTableWithLinkResult(linkResultInTime, false);
        } else {
            String id = this.idTextField.getText();
            if (selectedToggle == this.timeSeriesNodeButton) {
                List<NodeSimulationResult> timeSeriesForNode = this.resultSimulation.getTimeSeriesForNode(id);
                if (!timeSeriesForNode.isEmpty()){
                    fillTableWithNodeResult(timeSeriesForNode, true);
                } else{
                    CustomDialogs.showDialog("Error", "", "There is no node " + id, Alert.AlertType.ERROR, this.window);
                    showPane = false;
                }
            } else if (selectedToggle == this.timeSeriesLinkButton) {
                List<LinkSimulationResult> timeSeriesForLink = this.resultSimulation.getTimeSeriesForLink(id);
                if (!timeSeriesForLink.isEmpty()){
                    fillTableWithLinkResult(timeSeriesForLink,true);
                }else {
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
        stage.show();
        stage.setTitle("Result of execution");
        Platform.runLater(() -> {
            this.window.setMinWidth(this.root.getWidth());
            this.window.setMinHeight(this.root.getHeight());
        });
        this.window = stage;
    }
}
