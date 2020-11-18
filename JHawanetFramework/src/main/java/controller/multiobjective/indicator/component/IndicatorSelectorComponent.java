package controller.multiobjective.indicator.component;

import application.RegistrableConfiguration;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import model.metaheuristic.qualityindicator.impl.GenericIndicator;

import java.util.List;

public class IndicatorSelectorComponent extends ListView<Class<? extends GenericIndicator>>{
    private ObservableList<Class<? extends GenericIndicator>> selectedIndicators = FXCollections.observableArrayList();

    /**
     * Creates a default ListView which will display contents stacked vertically.
     * As no {@link ObservableList} is provided in this constructor, an empty
     * ObservableList is created, meaning that it is legal to directly call
     * {@link #getItems()} if so desired. However, as noted elsewhere, this
     * is not the recommended approach
     * (instead call {@link #setItems(ObservableList)}).
     *
     * <p>Refer to the {@link ListView} class documentation for details on the
     * default state of other properties.
     */
    public IndicatorSelectorComponent(List<Class<? extends GenericIndicator>> indicators) {
        getItems().addAll(indicators);
        configure();
    }

    /**
     * Configure the view of the list.
     */
    private void configure() {
        setCellFactory(param -> new CheckBoxListCell<Class<? extends GenericIndicator>>(createSelectionCallback()) {

            @Override
            public void updateItem(Class<? extends GenericIndicator> item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) {
                    setText(item.getSimpleName());
                }
            }
        });
    }

    public ObservableList<Class<? extends GenericIndicator>> getSelectedIndicators() {
        return selectedIndicators;
    }

    /**
     * Create the selection callback used in cell factory of this ListView.
     * @return the callback.
     */
    private Callback<Class<? extends GenericIndicator>, ObservableValue<Boolean>> createSelectionCallback() {
        SimpleBooleanProperty selectionProperty = new SimpleBooleanProperty(false);
        return (param) -> {
            selectionProperty.addListener((e) -> {
                if (selectionProperty.get()) {
                    this.selectedIndicators.add(param);
                } else {
                    this.selectedIndicators.remove(param);
                }
            });
            return selectionProperty;
        };
    }

}
