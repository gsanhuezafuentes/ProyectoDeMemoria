package controller.multiobjective.indicator.component;

import application.RegistrableConfiguration;
import controller.util.ControllerUtils;
import controller.util.ReflectionUtils;
import controller.util.TextInputUtil;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import registrable.MultiObjectiveRegistrable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseExperimentComponent extends VBox {

    @FXML
    private TableView<ProblemItem> problemTableView;

    @FXML
    private TableView<AlgorithmItem> algorithmTableView;

    // Contains the a problemItem for each key in algorithmItemMap
    private List<ProblemItem> problemItems;
    // Contains a list for each problem of the algorithm used with its.
    private Map<String, List<AlgorithmItem>> algorithmItemMap;

    /**
     * Creates a VBox layout with spacing = 0 and alignment at TOP_LEFT.
     */
    public ChooseExperimentComponent() {
        ControllerUtils.loadFXML("/view/multiobjective/indicator/component/ChooseExperimentComponent.fxml", this);
        preventColumnReordering(problemTableView);
        preventColumnReordering(algorithmTableView);
        generateItems();
        configureTableViews();
    }

    private void generateItems() {
        List<Class<? extends MultiObjectiveRegistrable>> multiobjectivesProblems = RegistrableConfiguration.MULTIOBJECTIVES_PROBLEMS;

        // For each problem get his algorithm.
        this.algorithmItemMap = new HashMap<>();
        for (Class<? extends MultiObjectiveRegistrable> registrable : multiobjectivesProblems) {
            String problemName = ReflectionUtils.getNameOfProblem(registrable);
            List<AlgorithmItem> problemAlgorithmItems = algorithmItemMap.get(problemName);
            if (problemAlgorithmItems == null) {
                problemAlgorithmItems = new ArrayList<>();
                algorithmItemMap.put(problemName, problemAlgorithmItems);
            }

            String algorithmName = ReflectionUtils.getNameOfAlgorithm(registrable);
            problemAlgorithmItems.add(new AlgorithmItem(algorithmName, registrable));
        }

        problemItems = new ArrayList<>();
        for (String problemName : algorithmItemMap.keySet()) {
            problemItems.add(new ProblemItem(problemName));
        }
    }

    private void configureTableViews() {
        // Configure problem table
        this.problemTableView.getItems().addAll(problemItems);
        TableColumn<ProblemItem, String> problemNameTableColumn = (TableColumn<ProblemItem, String>) this.problemTableView.getColumns().get(0);
        problemNameTableColumn.setCellValueFactory(param -> param.getValue().problemNameProperty());

        TableColumn<ProblemItem, Integer> problemNumberOfInstanceColumn = (TableColumn<ProblemItem, Integer>) this.problemTableView.getColumns().get(1);
        problemNumberOfInstanceColumn.setCellFactory(param -> new TextFieldTableCell<>(TextInputUtil.createIntegerConverter()));
        problemNumberOfInstanceColumn.setCellValueFactory(param -> param.getValue().numberOfInstancesProperty().asObject());
        problemNumberOfInstanceColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ProblemItem, Integer>>() {
                                                   @Override
                                                   public void handle(TableColumn.CellEditEvent<ProblemItem, Integer> t) {
                                                       ((ProblemItem) t.getTableView().getItems().get(
                                                               t.getTablePosition().getRow())
                                                       ).setNumberOfInstances(t.getNewValue());
                                                   }
                                               }
        );

        this.problemTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.algorithmTableView.getItems().clear();
            this.algorithmTableView.getItems().addAll(algorithmItemMap.get(newValue.getProblemName()));
        });

        // Configure algorithm table
        TableColumn<AlgorithmItem, String> algorithmNameTableColumn =  (TableColumn<AlgorithmItem, String>) this.algorithmTableView.getColumns().get(0);
        algorithmNameTableColumn.setCellValueFactory(param -> param.getValue().algorithmNameProperty());

        TableColumn<AlgorithmItem, Integer> algorithmNumberOfInstanceColumn = (TableColumn<AlgorithmItem, Integer>) this.algorithmTableView.getColumns().get(1);
        algorithmNumberOfInstanceColumn.setCellFactory(param -> new TextFieldTableCell<>(TextInputUtil.createIntegerConverter()));
        algorithmNumberOfInstanceColumn.setCellValueFactory(param -> param.getValue().numberOfInstancesProperty().asObject());
        algorithmNumberOfInstanceColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AlgorithmItem, Integer>>() {
                                                            @Override
                                                            public void handle(TableColumn.CellEditEvent<AlgorithmItem, Integer> t) {
                                                                ((AlgorithmItem) t.getTableView().getItems().get(
                                                                        t.getTablePosition().getRow())
                                                                ).setNumberOfInstances(t.getNewValue());
                                                            }
                                                        }
        );
    }

    private static <T> void preventColumnReordering(TableView<T> tableView) {
        Platform.runLater(() -> {
            for (Node header : tableView.lookupAll(".column-header")) {
                header.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);
            }
        });
    }

    /**
     * This class is used to wrap the problem name in Registrable class. This let add the item to tableview.
     */
    private static class ProblemItem {
        private final StringProperty problemName = new SimpleStringProperty();
        private SimpleIntegerProperty numberOfInstances = new SimpleIntegerProperty(0);

        public ProblemItem(String name) {
            problemName.set(name);
            numberOfInstances.addListener((observable, oldValue, newValue) -> System.out.println(problemName.get() + " " + newValue));
        }

        public String getProblemName() {
            return problemName.get();
        }

        public StringProperty problemNameProperty() {
            return problemName;
        }

        public int getNumberOfInstances() {
            return numberOfInstances.get();
        }

        public SimpleIntegerProperty numberOfInstancesProperty() {
            return numberOfInstances;
        }

        public void setNumberOfInstances(int numberOfInstances) {
            this.numberOfInstances.set(numberOfInstances);
        }

        @Override
        public String toString() {
            return "name: " + problemName + "; number of instances: " + numberOfInstances;
        }
    }

    /**
     * This class is used to wrap the algorithm name in Registrable class. This let add the item to tableview.
     */
    private static class AlgorithmItem {
        private final StringProperty algorithmName = new SimpleStringProperty();
        private IntegerProperty numberOfInstances = new SimpleIntegerProperty(0);
        private final Class<? extends MultiObjectiveRegistrable> clazz;

        public AlgorithmItem(String name, Class<? extends MultiObjectiveRegistrable> clazz) {
            this.algorithmName.set(name);
            this.clazz = clazz;
        }

        public String getAlgorithmName() {
            return algorithmName.get();
        }

        public int getNumberOfInstances() {
            return numberOfInstances.get();
        }

        public IntegerProperty numberOfInstancesProperty() {
            return numberOfInstances;
        }

        public void setNumberOfInstances(int numberOfInstances) {
            this.numberOfInstances.set(numberOfInstances);
        }

        public StringProperty algorithmNameProperty() {
            return algorithmName;
        }

        @Override
        public String toString() {
            return "name: " + getAlgorithmName() + "; \nclazz: " + clazz + "; number of instances: " + getNumberOfInstances();
        }
    }
}
