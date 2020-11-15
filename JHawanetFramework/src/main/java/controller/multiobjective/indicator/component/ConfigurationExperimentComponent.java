package controller.multiobjective.indicator.component;

import controller.util.ControllerUtils;
import controller.util.ReflectionUtils;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import registrable.MultiObjectiveRegistrable;

import java.util.*;

public class ConfigurationExperimentComponent extends StackPane {

    @FXML
    private TreeView<Item> experimentTreeView;

    @FXML
    private StackPane configurationPane;

    /**
     * Creates a new SplitPane with no content.
     */
    public ConfigurationExperimentComponent(Map<String, List<List<Class<? extends MultiObjectiveRegistrable>>>> items) {
        Objects.requireNonNull(items);
        ControllerUtils.loadFXML("/view/multiobjective/indicator/component/ConfigureExperimentComponent.fxml", this);
        fillAndConfigureTreeView(items);
    }

    private void fillAndConfigureTreeView(Map<String, List<List<Class<? extends MultiObjectiveRegistrable>>>> items) {
        TreeItem<Item> root = new TreeItem<>(new Item("Experiments", null));
        root.setExpanded(true);
        int numberOfInstanceProblem = 0;
        for (String key : items.keySet()) {
            for (List<Class<? extends MultiObjectiveRegistrable>> problems : items.get(key)) {
                TreeItem<Item> children = new TreeItem<>(new Item(key + " - " + numberOfInstanceProblem, null));
                fillTreeWithAlgorithms(children, problems);
                root.getChildren().add(children);
                numberOfInstanceProblem++;
            }
            numberOfInstanceProblem = 0;
        }
        this.experimentTreeView.setRoot(root);
        this.experimentTreeView.setCellFactory(param -> new TreeCell<Item>(){
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty){
                    setText(item.getName());
                    if (item.getClazz() == null){
                        //remove listener of getItem
                        System.out.println("Remove listener of " + getItem().getName());
                    }else{
                        // add listener to item
                        System.out.println("Add listener to " + item.getName());

                    }
                }else{
                    setText(null);
                }
            }
        });
    }

    private void fillTreeWithAlgorithms(TreeItem<Item> root, List<Class<? extends MultiObjectiveRegistrable>> problems){
        ObservableList<TreeItem<Item>> children = root.getChildren();
        Set<String> seen = new HashSet<>();
        int numberOfInstanceAlgorithm = 0;
        for (Class<? extends MultiObjectiveRegistrable> registrableClass: problems) {
            String nameOfAlgorithm = ReflectionUtils.getNameOfAlgorithm(registrableClass);
            if (!seen.add(nameOfAlgorithm)){
                numberOfInstanceAlgorithm++;
            }else{
                numberOfInstanceAlgorithm = 0;
            }
            children.add(new TreeItem<>(new Item(nameOfAlgorithm + " - " + numberOfInstanceAlgorithm, registrableClass)));
        }
    }


    private class Item {
        private final ReadOnlyStringWrapper name = new ReadOnlyStringWrapper();
        private final Class<? extends MultiObjectiveRegistrable> clazz;

        /**
         * @param name  the name element.
         * @param clazz the class if has or null.
         * @throws NullPointerException     if name is null.
         * @throws IllegalArgumentException if name is empty.
         */
        public Item(String name, Class<? extends MultiObjectiveRegistrable> clazz) {
            Objects.requireNonNull(name);
            if (name.isEmpty()) throw new IllegalArgumentException("The name of problem can't be empty.");
            this.name.set(name);
            this.clazz = clazz;
        }

        public ReadOnlyStringProperty nameProperty() {
            return name.getReadOnlyProperty();
        }

        public String getName() {
            return name.get();
        }

        public Class<? extends MultiObjectiveRegistrable> getClazz() {
            return clazz;
        }
    }

}
