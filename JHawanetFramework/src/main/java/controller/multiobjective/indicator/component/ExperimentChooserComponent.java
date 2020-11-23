package controller.multiobjective.indicator.component;

import application.RegistrableConfiguration;
import controller.util.ControllerUtils;
import controller.util.ReflectionUtils;
import controller.util.TextInputUtil;
import exception.UnfullfilledRestrictionException;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import registrable.MultiObjectiveRegistrable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is the component showed in Configuration windows of indicators. It show the problems added in the app (registrable instance) and his
 * algorithm reading it from {@link RegistrableConfiguration#MULTIOBJECTIVES_PROBLEMS} and let configure the number of instances of each one.
 */
public class ExperimentChooserComponent extends VBox {

    @FXML
    private TableView<ProblemItem> problemTableView;

    @FXML
    private TableView<AlgorithmItem> algorithmTableView;

    // Contains the a problemItem for each key in algorithmItemMap
    private List<ProblemItem> problemItemsList;
    // Contains a list for each problem of the algorithm used with its.
    private Map<String, List<AlgorithmItem>> algorithmItemMap;

    /**
     * Creates a VBox layout with spacing = 0 and alignment at TOP_LEFT.
     */
    public ExperimentChooserComponent() {
        ControllerUtils.loadFXML("/view/multiobjective/indicator/component/ExperimentChooserComponent.fxml", this);
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
            List<AlgorithmItem> problemAlgorithmItems = algorithmItemMap.computeIfAbsent(problemName, k -> new ArrayList<>());
            String algorithmName = ReflectionUtils.getNameOfAlgorithm(registrable);
            problemAlgorithmItems.add(new AlgorithmItem(algorithmName, registrable));
        }

        problemItemsList = new ArrayList<>();
        for (String problemName : algorithmItemMap.keySet()) {
            problemItemsList.add(new ProblemItem(problemName));
        }
    }

    private void configureTableViews() {
        // Configure problem table
        this.problemTableView.getItems().addAll(problemItemsList);
        TableColumn<ProblemItem, String> problemNameTableColumn = (TableColumn<ProblemItem, String>) this.problemTableView.getColumns().get(0);
        problemNameTableColumn.setCellValueFactory(param -> param.getValue().problemNameProperty());

        TableColumn<ProblemItem, Integer> problemNumberOfInstanceColumn = (TableColumn<ProblemItem, Integer>) this.problemTableView.getColumns().get(1);
        problemNumberOfInstanceColumn.setCellFactory(param -> new TextFieldTableCell<>(TextInputUtil.createIntegerConverter()));
        problemNumberOfInstanceColumn.setCellValueFactory(param -> param.getValue().numberOfInstancesProperty().asObject());
        problemNumberOfInstanceColumn.setOnEditCommit(t -> {
            // a valid value for this component is greater or equals than 0
            if (t.getNewValue() >= 0) {
                t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).setNumberOfInstances(t.getNewValue());
            } else {
                // A trick to update the component with the value saved in the item.
                t.getTableColumn().setVisible(false);
                t.getTableColumn().setVisible(true);
            }
        }
        );

        this.problemTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.algorithmTableView.getItems().clear();
            this.algorithmTableView.getItems().addAll(algorithmItemMap.get(newValue.getProblemName()));
        });

        // Configure algorithm table
        TableColumn<AlgorithmItem, String> algorithmNameTableColumn = (TableColumn<AlgorithmItem, String>) this.algorithmTableView.getColumns().get(0);
        algorithmNameTableColumn.setCellValueFactory(param -> param.getValue().algorithmNameProperty());

        TableColumn<AlgorithmItem, Integer> algorithmNumberOfInstanceColumn = (TableColumn<AlgorithmItem, Integer>) this.algorithmTableView.getColumns().get(1);
        algorithmNumberOfInstanceColumn.setCellFactory(param -> new TextFieldTableCell<>(TextInputUtil.createIntegerConverter()));
        algorithmNumberOfInstanceColumn.setCellValueFactory(param -> param.getValue().numberOfInstancesProperty().asObject());
        algorithmNumberOfInstanceColumn.setOnEditCommit(t -> {
            if (t.getNewValue() >= 0) {
                t.getTableView().getItems().get(
                        t.getTablePosition().getRow()).setNumberOfInstances(t.getNewValue());
            } else {
                t.getTableColumn().setVisible(false);
                t.getTableColumn().setVisible(true);
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
     * Get the registrables for each problems. The same problem can be repeats "number of instances" times.
     * <p>
     * Example:<p>
     *
     * <pre>
     * {
     *      // Two Pumping Schedule problems
     *      PumpingSchedule : {
     *          // The list of algorithms. There is two versions of NSGAII.
     *          {NSGAII, NSGAII, SPA2}
     *         ,{NSGAII, NSGAII, SPA2}
     *      }
     * }
     * </pre>
     *
     * @return the registrable for each repetitions of each problems.
     * @throws UnfullfilledRestrictionException if the algorithms for each problem with a number distinct to zero are not the same or do not have the same amount of algorithm.
     */
    public Map<String, List<List<Class<? extends MultiObjectiveRegistrable>>>> getRegistrableClassMap() throws UnfullfilledRestrictionException {
        validateAlgorithmRestriction();
        Map<String, List<List<Class<? extends MultiObjectiveRegistrable>>>> result = new HashMap<>();

        for (ProblemItem problems : this.problemItemsList) {
            List<List<Class<? extends MultiObjectiveRegistrable>>> listOfRegistrableList = null;
            int numberOfInstances = problems.getNumberOfInstances();
            if (numberOfInstances > 0) {
                List<Class<? extends MultiObjectiveRegistrable>> registrablesForProblem = getRegistrablesForProblem(problems.getProblemName());
                if (registrablesForProblem != null) {
                    listOfRegistrableList = new ArrayList<>(Collections.nCopies(numberOfInstances, registrablesForProblem));
                }
            }
            if (listOfRegistrableList != null) {
                result.put(problems.getProblemName(), listOfRegistrableList);
            }
        }
        return result;
    }


    /**
     * Create a list with registrable of the specific problems. Each registrable is "number of instances" repeats.
     *
     * @param problemName the problem name
     * @return the list of registrables.
     */
    private List<Class<? extends MultiObjectiveRegistrable>> getRegistrablesForProblem(String problemName) {
        List<Class<? extends MultiObjectiveRegistrable>> result = null;
        for (AlgorithmItem algorithmItem : this.algorithmItemMap.get(problemName)) {
            int numberOfInstances = algorithmItem.getNumberOfInstances();
            if (numberOfInstances > 0) {
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.addAll(Collections.nCopies(numberOfInstances, algorithmItem.getClazz()));
            }
        }
        return result;
    }

    /**
     * Check if each problem with the number distinct to zero has the same algorithms in the same amount.
     * This check is based in the name of algorithm or problems.
     *
     * @throws UnfullfilledRestrictionException if the restriction isn't fulfilled.
     */
    private void validateAlgorithmRestriction() throws UnfullfilledRestrictionException {
        List<ProblemItem> problemItems = this.problemItemsList
                .stream()
                .filter(problemItem -> problemItem.getNumberOfInstances() > 0)
                .collect(Collectors.toList());

        if (problemItems.size() == 0) { // if there aren't problem configured
            throw new UnfullfilledRestrictionException("There aren't problem instances configured.");
        } else if (problemItems.size() == 1) { // if there is only one problem type check that the number of algorithms are greater than 2.
            List<AlgorithmItem> algorithmItems = this.algorithmItemMap.get(problemItems.get(0).getProblemName());
            // Check if for the problems there is algorithm configured
            if (algorithmItems != null){
                int sum = algorithmItems
                        .stream()
                        .mapToInt(AlgorithmItem::getNumberOfInstances)
                        .sum();
                if (sum <= 1) {
                    throw new UnfullfilledRestrictionException("There must be 2 instances of the algorithm, either of the same or different type.");
                }
            }else{ // if there isn't configured algorithm.
                throw new UnfullfilledRestrictionException("Configure a algorithm for selected problem.");
            }
            return;
        }

        // if there are more than one type of problem
        HashMap<String, Integer> added = new HashMap<>();
        boolean isFirstIteration = true;
        for (ProblemItem problem : problemItems) {
            List<AlgorithmItem> algorithmItems = this.algorithmItemMap
                    .get(problem.getProblemName()).stream().filter(algorithmItem -> algorithmItem.getNumberOfInstances() > 0)
                    .collect(Collectors.toList());
            // Get the algorithm type in the first iteration
            if (isFirstIteration) {
                isFirstIteration = false;
                algorithmItems.forEach(algorithmItem -> {
                    added.put(algorithmItem.getAlgorithmName(), algorithmItem.getNumberOfInstances());
                });
            } else {
                int numberOfAlgorithm = 0; // Check that each loop as the same number of algorithm

                // Check if the algorithm in other iteration are the same that in the first iteration and has the same number.
                for (AlgorithmItem algorithm : algorithmItems) {
                    Integer numberOfInstances = added.get(algorithm.getAlgorithmName());

                    System.out.println(algorithm.getAlgorithmName());
                    // if there is a algorithm that isn't in first iteration
                    if (numberOfInstances == null){
                        throw new UnfullfilledRestrictionException("There aren't the same algorithm configured for each problem.");
                    }

                    // if the saved value in first iteration for the algorithm name isn't equals to the
                    // saved value for the same algorithm in other iteration
                    if (! (numberOfInstances.compareTo(algorithm.getNumberOfInstances()) == 0) ) {
                        throw new UnfullfilledRestrictionException("The number of instance for the same algorithm in different problems isn't the same.");
                    }
                    numberOfAlgorithm++;
                }

                // Check if the number of algorithm in first loop isn't the same that in the map.
                if (numberOfAlgorithm != added.size()) {
                    throw new UnfullfilledRestrictionException("Not the same algorithms are configured for each problem");
                }
            }
        }
        if (added.size() == 0){
            throw new UnfullfilledRestrictionException("There aren't configured algorithm for any problems.");
        }
        // If there is only one algorithm check that there are more than one number of instance of it.
        if ((added.size() == 1) && added.values().stream().findFirst().get().compareTo(1) == 0){ // sorry demeter =P
            throw new UnfullfilledRestrictionException("It is necessary more than one type of algorithm or more than 2 instances of the same algorithm.");
        }
    }

    /**
     * This class is used to wrap the problem name in Registrable class. This let add the item to tableview.
     */
    private static class ProblemItem {
        private final ReadOnlyStringWrapper problemName = new ReadOnlyStringWrapper();
        private final SimpleIntegerProperty numberOfInstances = new SimpleIntegerProperty(0);

        /**
         * @param name the name of problem.
         * @throws NullPointerException     if name is null.
         * @throws IllegalArgumentException if name is empty.
         */
        public ProblemItem(String name) {
            Objects.requireNonNull(name);
            if (name.isEmpty()) throw new IllegalArgumentException("name can't be null");
            problemName.set(name);
        }

        public String getProblemName() {
            return problemName.get();
        }

        public ReadOnlyStringProperty problemNameProperty() {
            return problemName.getReadOnlyProperty();
        }

        public int getNumberOfInstances() {
            return numberOfInstances.get();
        }

        public SimpleIntegerProperty numberOfInstancesProperty() {
            return numberOfInstances;
        }

        /**
         * @param numberOfInstances the number of instances of the problem
         * @throws IllegalArgumentException if numberOfInstances is negative.
         */
        public void setNumberOfInstances(int numberOfInstances) {
            if (numberOfInstances < 0) {
                throw new IllegalArgumentException("The number of instance can't be negative but was " + numberOfInstances);
            }
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
        private final ReadOnlyStringWrapper algorithmName = new ReadOnlyStringWrapper();
        private final IntegerProperty numberOfInstances = new SimpleIntegerProperty(0);
        private final Class<? extends MultiObjectiveRegistrable> clazz;

        /**
         * @param name  the name of algorithm.
         * @param clazz the class used to create it.
         * @throws NullPointerException     if name or clazz is null.
         * @throws IllegalArgumentException if name is empty.
         */
        public AlgorithmItem(String name, Class<? extends MultiObjectiveRegistrable> clazz) {
            this.algorithmName.set(Objects.requireNonNull(name));
            this.clazz = Objects.requireNonNull(clazz);
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

        /**
         * @param numberOfInstances the number of instances of algorithm for each problem associated.
         * @throws IllegalArgumentException if numberOfInstances is negative.
         */
        public void setNumberOfInstances(int numberOfInstances) {
            if (numberOfInstances < 0) {
                throw new IllegalArgumentException("The number of instance can't be negative but was " + numberOfInstances);
            }
            this.numberOfInstances.set(numberOfInstances);
        }

        public ReadOnlyStringProperty algorithmNameProperty() {
            return algorithmName.getReadOnlyProperty();
        }

        public Class<? extends MultiObjectiveRegistrable> getClazz() {
            return clazz;
        }

        @Override
        public String toString() {
            return "name: " + getAlgorithmName() + "; \nclazz: " + clazz + "; number of instances: " + getNumberOfInstances();
        }
    }
}
