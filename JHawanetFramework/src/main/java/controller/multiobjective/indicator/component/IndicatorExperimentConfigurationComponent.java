package controller.multiobjective.indicator.component;

import controller.component.ExperimentConfigurationComponent;
import controller.util.ControllerUtils;
import controller.util.ReflectionUtils;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import model.metaheuristic.experiment.Experiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registrable.MultiObjectiveRegistrable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * This component let setting up the configuration of each problem/algorithm
 */
public class IndicatorExperimentConfigurationComponent extends StackPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndicatorExperimentConfigurationComponent.class);

    @FXML
    private TreeView<Item> experimentTreeView;

    @FXML
    private StackPane configurationPane;

    //This list only contains element with class != null
    private List<ExperimentItem> experimentItemList;

    /**
     * Creates a new SplitPane with no content.
     */
    public IndicatorExperimentConfigurationComponent(Map<String, List<List<Class<? extends MultiObjectiveRegistrable>>>> items) {
        LOGGER.debug("Initializing IndicatorExperimentConfigurationComponent.");
        Objects.requireNonNull(items);
        ControllerUtils.loadFXML("/view/multiobjective/indicator/component/IndicatorExperimentConfigurationComponent.fxml", this);
        fillTreeView(items);
        configureTreeView();

    }

    /**
     * Add the first and second level of three view.}
     *
     * @param items the items to show in tree view.
     */
    private void fillTreeView(Map<String, List<List<Class<? extends MultiObjectiveRegistrable>>>> items) {
        TreeItem<Item> root = new TreeItem<>(new Item("Experiments", ""));
        root.setExpanded(true);
        int numberOfInstanceProblem = 0;
        for (String key : items.keySet()) {
            for (List<Class<? extends MultiObjectiveRegistrable>> problems : items.get(key)) {
                TreeItem<Item> children = new TreeItem<>(new Item(key, Integer.toString(numberOfInstanceProblem)));
                fillTreeWithAlgorithms(children, problems, numberOfInstanceProblem);
                root.getChildren().add(children);
                numberOfInstanceProblem++;
            }
            numberOfInstanceProblem = 0;
        }
        this.experimentTreeView.setRoot(root);
    }

    /**
     * Add the third level in tree view
     *
     * @param root     the root element of TreeItem created in this method.
     * @param problems the problem to add.
     */
    private void fillTreeWithAlgorithms(TreeItem<Item> root, List<Class<? extends MultiObjectiveRegistrable>> problems, int numberOfInstanceOfProblem) {
        ObservableList<TreeItem<Item>> children = root.getChildren();
        Set<String> seen = new HashSet<>();
        int numberOfInstanceAlgorithm = 0;
        for (Class<? extends MultiObjectiveRegistrable> registrableClass : problems) {
            String nameOfAlgorithm = ReflectionUtils.getNameOfAlgorithm(registrableClass);
            if (!seen.add(nameOfAlgorithm)) {
                numberOfInstanceAlgorithm++;
            } else {
                numberOfInstanceAlgorithm = 0;
            }
            if (this.experimentItemList == null) this.experimentItemList = new ArrayList<>();
            ExperimentItem item = new ExperimentItem(nameOfAlgorithm, Integer.toString(numberOfInstanceAlgorithm)
                    , Integer.toString(numberOfInstanceOfProblem)
                    , registrableClass);
            this.experimentItemList.add(item);
            children.add(new TreeItem<>(item));
        }
    }

    /**
     * Add the factory to fill each three item.
     */
    private void configureTreeView() {
        this.experimentTreeView.setCellFactory(param -> new TreeCell<Item>() {

            private ChangeListener<? super Boolean> selectedListener = (prop, oldV, newV) -> {
                if (newV) {
                    System.out.println("" + getItem().getName());
                    configurationPane.getChildren().clear();

                    if (getItem() instanceof ExperimentItem) {
                        // Remove previous GUI to add the new.
                        ExperimentItem item = (ExperimentItem) getItem();
                        configurationPane.getChildren().add(item.getGuiComponent());
                    }
                }
            };

            {
                selectedProperty().addListener(selectedListener);
            }

            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        });
    }

    /**
     * Return a list with callback to create each experiment
     *
     * @return the list with callbacks.
     * @throws InvocationTargetException if there is a error while create some of registrable instances or his operators.
     */
    public List<Callable<Experiment<?>>> getRegistrableList(String inpPath) throws InvocationTargetException {
        LOGGER.debug("Creating the registrable list for use with indicators with the network '{}'.", inpPath);

        Objects.requireNonNull(inpPath);
        if (inpPath.isEmpty()) {
            throw new IllegalArgumentException("The inpPath is empty");
        }
        List<Callable<Experiment<?>>> experiments = new ArrayList<>();
        // For each item in the GUI get the Registrable object.
        for (ExperimentItem item : this.experimentItemList) {
            MultiObjectiveRegistrable registrableInstance = item.getGuiComponent().getRegistrableInstance();

            /*
             * Add to the list to return the callbacks to create the experiments.
             * This is done because each experiment requires the EpanetToolkit,
             * but it should not be manipulated by more than one object at the same time, so
             * the creation of the experiment is delayed until it is necessary.
             */
            experiments.add(() -> {
                Experiment<?> experiment = registrableInstance.build(inpPath);
                String oldTagExperiment = experiment.getProblem().getTag();
                experiment.getProblem().setTag(oldTagExperiment + " - " + item.getNumberOfInstanceOfProblemAsString());

                experiment.getAlgorithmList().forEach((experimentAlgorithm) -> {
                    String oldTagAlgorithm = experimentAlgorithm.getAlgorithmTag();
                    experimentAlgorithm.setAlgorithmTag(oldTagAlgorithm + " - " + item.getNumberOfInstanceAsString());

                });
                return experiment;
            });

        }
        return experiments;
    }


    private static class Item {
        private final ReadOnlyStringWrapper name = new ReadOnlyStringWrapper();
        private final String numberOfInstance;

        /**
         * @param name             the name element.
         * @param numberOfInstance the number of instance if it has.
         * @throws NullPointerException     if name or numberOfInstance is null.
         * @throws IllegalArgumentException if name is empty.
         */
        public Item(String name, String numberOfInstance) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(numberOfInstance);
            if (name.isEmpty()) throw new IllegalArgumentException("The name of problem can't be empty.");
            this.name.set((numberOfInstance.isEmpty()) ? name : name + " - " + numberOfInstance);
            this.numberOfInstance = numberOfInstance;
        }

        public ReadOnlyStringProperty nameProperty() {
            return name.getReadOnlyProperty();
        }

        public String getName() {
            return name.get();
        }

        public String getNumberOfInstanceAsString() {
            return numberOfInstance;
        }
    }

    private static class ExperimentItem extends Item {
        private final Class<? extends MultiObjectiveRegistrable> clazz;
        private final String numberOfInstanceOfProblem;
        private ExperimentConfigurationComponent<? extends MultiObjectiveRegistrable> guiComponent;

        /**
         * @param name                      the name element.
         * @param numberOfInstance          the number of instance if it has.
         * @param numberOfInstanceOfProblem the number of the instance of problem.
         * @param clazz                     the class if has or null.
         * @throws NullPointerException     if name or whichever of numberOfInstance is null.
         * @throws IllegalArgumentException if name is empty.
         */
        public ExperimentItem(String name, String numberOfInstance, String numberOfInstanceOfProblem, Class<? extends MultiObjectiveRegistrable> clazz) {
            super(name, numberOfInstance);
            Objects.requireNonNull(numberOfInstanceOfProblem);
            this.clazz = clazz;
            this.numberOfInstanceOfProblem = numberOfInstanceOfProblem;

        }

        public Class<? extends MultiObjectiveRegistrable> getClazz() {
            return clazz;
        }

        public ExperimentConfigurationComponent<? extends MultiObjectiveRegistrable> getGuiComponent() {
            if (guiComponent == null) {
                guiComponent = new ExperimentConfigurationComponent<>(clazz);
            }
            return guiComponent;
        }

        public String getNumberOfInstanceOfProblemAsString() {
            return numberOfInstanceOfProblem;
        }
    }

}
