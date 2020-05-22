package view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import model.epanet.element.networkcomponent.*;
import model.epanet.element.networkcomponent.Pump.PumpProperty;
import model.epanet.element.waterquality.Mixing;
import model.epanet.element.waterquality.Quality;
import model.epanet.element.waterquality.Source;

import java.util.Objects;

/**
 * View that show the setting up of the elements of the network. It class use
 * the Singleton pattern because only one instance can be created at the same
 * time but it can be called from many other views.
 * <p>
 * The classes that call to this are {@link ElementViewer} and
 * {@link NetworkComponent}.
 */
public class DataDisplayWindow extends Stage {

    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAUL_HEIGHT = 400;

    /*
     * This Window use the Singleton Pattern because can be called from the Network
     * component and from the elementViewer when the element is double clicked
     */
    private static DataDisplayWindow instance;
    final HBox root;

    private final ObjectProperty<Object> data;
    private final TableView<Pair<String, String>> tableView;

    private DataDisplayWindow() {
        initStyle(StageStyle.UTILITY);
        setAlwaysOnTop(true);
        setMinWidth(DEFAULT_WIDTH);
        setMinHeight(DEFAUL_HEIGHT);
        setWidth(DEFAUL_HEIGHT);
        setHeight(DEFAUL_HEIGHT);

        /*
         * Si quieren editar los valores que muestra este componente para cada elemento
         * de la red recomiendo reemplazar el tableView por label y textfield
         */
        this.tableView = new TableView<>();
        this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Pair<String, String>, String> propertyColumn = new TableColumn<>(
                "Property");
        propertyColumn.setCellValueFactory(
                (CellDataFeatures<Pair<String, String>, String> solutionData) -> new ReadOnlyObjectWrapper<>(
                        solutionData.getValue().getKey()));

        TableColumn<Pair<String, String>, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(
                (CellDataFeatures<Pair<String, String>, String> solutionData) -> new ReadOnlyObjectWrapper<>(
                        solutionData.getValue().getValue()));

        this.tableView.getColumns().add(propertyColumn);
        this.tableView.getColumns().add(valueColumn);

        this.root = new HBox(this.tableView);
        HBox.setHgrow(tableView, Priority.ALWAYS);
        this.data = new SimpleObjectProperty<>();
        addBindAndListener();

        Scene scene = new Scene(this.root);
        setScene(scene);
    }

    /**
     * Get the instance of this class
     *
     * @return the instance of this class
     */
    public static DataDisplayWindow getInstance() {
        if (instance == null) {
            instance = new DataDisplayWindow();
        }
        return instance;
    }

    /**
     * Add the bind and listener to nodes in this stage
     */
    private void addBindAndListener() {
        // update the element in the display
        this.data.addListener((prop, oldv, newv) -> {
            if (isShowing()) {
                updateData(newv);
            }
        });

        // if window is been showed so show the data
        showingProperty().addListener((prop, oldv, newv) -> updateData(this.data.get()));
    }

    /**
     * Fill the previous created label with his appropriate values.
     *
     * @param selectedItem the object that can be showned. Can be a {@link Node} or a {@link Link}
     * @throws NullPointerException     if selectedItem is null
     * @throws IllegalArgumentException if selectedItem is other than a {@link Link} or {@link Node} subclass.
     */
    private void fillData(Object selectedItem) {
        Objects.requireNonNull(selectedItem);
        ObservableList<Pair<String, String>> list = this.tableView.getItems();
        if (selectedItem instanceof Node) {
            if (selectedItem instanceof Junction) {
                Junction node = (Junction) selectedItem;

                // common properties for node
                list.add(new Pair<>("Junction ID", node.getId()));
                if (node.getPosition() != null) {
                    list.add(new Pair<>("X-Coordinates", Double.toString(node.getPosition().getX())));
                    list.add(new Pair<>("Y-Coordinates", Double.toString(node.getPosition().getY())));
                } else {
                    list.add(new Pair<>("X-Coordinates", ""));
                    list.add(new Pair<>("Y-Coordinates", ""));
                }
                list.add(new Pair<>("Description", node.getDescription()));
                list.add(new Pair<>("Tag", node.getTag() != null ? node.getTag().getLabel() : ""));

                // specific property of junction
                list.add(new Pair<>("Elevation", Double.toString(node.getElevation())));
                list.add(new Pair<>("Demand", Double.toString(node.getBaseDemand())));
                node.getDemandPattern();
                list.add(new Pair<>("Demand Pattern", node.getDemandPattern()));
                list.add(new Pair<>("Demand Categories",
                        Integer.toString(node.getDemandCategories().size())));
                Emitter emitter = node.getEmitter();
                list.add(new Pair<>("Emitter Coeff.",
                        emitter != null ? Double.toString(emitter.getCoefficient()) : ""));
                Quality quality = node.getInitialQuality();
                list.add(new Pair<>("Initial Quality",
                        quality != null ? Double.toString(quality.getInitialQuality()) : ""));
                Source source = node.getSourceQuality();
                list.add(new Pair<>("Source Quality",
                        source != null ? Double.toString(source.getSourceQuality()) : ""));
//				list.add(new Pair<String, String>("Actual Demand",
//						Integer.toString(node.getDemandCategories().size())));
//				list.add(new Pair<String, String>("Total Head",
//						Integer.toString(node.getDemandCategories().size())));
//				list.add(new Pair<String, String>("Pressure",
//						Integer.toString(node.getDemandCategories().size())));
//				list.add(new Pair<String, String>("Quality",
//						Integer.toString(node.getDemandCategories().size())));

            }
            if (selectedItem instanceof Reservoir) {
                Reservoir node = (Reservoir) selectedItem;

                // common properties for node
                list.add(new Pair<>("Reservoir ID", node.getId()));
                if (node.getPosition() != null) {
                    list.add(new Pair<>("X-Coordinates", Double.toString(node.getPosition().getX())));
                    list.add(new Pair<>("Y-Coordinates", Double.toString(node.getPosition().getY())));
                } else {
                    list.add(new Pair<>("X-Coordinates", ""));
                    list.add(new Pair<>("Y-Coordinates", ""));
                }
                list.add(new Pair<>("Description", node.getDescription()));
                list.add(new Pair<>("Tag", node.getTag() != null ? node.getTag().getLabel() : ""));

                // specific property of reservoir
                list.add(new Pair<>("Total Head", Double.toString(node.getTotalHead())));

                list.add(new Pair<>("Head Pattern", node.getHeadPattern()));
                Quality quality = node.getInitialQuality();
                list.add(new Pair<>("Initial Quality",
                        quality != null ? Double.toString(quality.getInitialQuality()) : ""));
                Source source = node.getSourceQuality();
                list.add(new Pair<>("Source Quality",
                        source != null ? Double.toString(source.getSourceQuality()) : ""));
//				list.add(new Pair<String, String>("Net Flow",
//						Integer.toString(node.getDemandCategories().size())));
//				list.add(new Pair<String, String>("Elevation",
//						Integer.toString(node.getDemandCategories().size())));
//				list.add(new Pair<String, String>("Pressure",
//						Integer.toString(node.getDemandCategories().size())));
//				list.add(new Pair<String, String>("Quality",
//						Integer.toString(node.getDemandCategories().size())));

            }
            if (selectedItem instanceof Tank) {
                Tank node = (Tank) selectedItem;

                // common properties for node
                list.add(new Pair<>("Tank ID", node.getId()));
                if (node.getPosition() != null) {
                    list.add(new Pair<>("X-Coordinates", Double.toString(node.getPosition().getX())));
                    list.add(new Pair<>("Y-Coordinates", Double.toString(node.getPosition().getY())));
                } else {
                    list.add(new Pair<>("X-Coordinates", ""));
                    list.add(new Pair<>("Y-Coordinates", ""));
                }
                list.add(new Pair<>("Description", node.getDescription()));
                list.add(new Pair<>("Tag", node.getTag() != null ? node.getTag().getLabel() : ""));

                // specific property of reservoir
                list.add(new Pair<>("Elevation", Double.toString(node.getElevation())));
                list.add(new Pair<>("Initial Level", Double.toString(node.getInitialLevel())));
                list.add(new Pair<>("Minumum Level", Double.toString(node.getMinimumLevel())));
                list.add(new Pair<>("Maximum Level", Double.toString(node.getMaximumLevel())));
                list.add(new Pair<>("Diameter", Double.toString(node.getDiameter())));
                list.add(new Pair<>("Minimum Volume", Double.toString(node.getMinimumVolume())));
                list.add(new Pair<>("Volume Curve", node.getVolumeCurve()));
                Mixing mixing = node.getMixing();
                list.add(new Pair<>("Mixing Model", mixing.getModel().getName()));
                list.add(new Pair<>("Mixing Fraction",
                        mixing.getMixingFraction() != null ? Double.toString(mixing.getMixingFraction()) : ""));
                Double reactionCoeff = node.getReactionCoefficient();
                list.add(new Pair<>("Reaction Coeff.",
                        reactionCoeff != null ? reactionCoeff.toString() : ""));
                Quality quality = node.getInitialQuality();
                list.add(new Pair<>("Initial Quality",
                        quality != null ? Double.toString(quality.getInitialQuality()) : ""));
                Source source = node.getSourceQuality();
                list.add(new Pair<>("Source Quality",
                        source != null ? Double.toString(source.getSourceQuality()) : ""));
//				list.add(new Pair<String, String>("Net Flow",
//				Integer.toString(node.getDemandCategories().size())));
//		list.add(new Pair<String, String>("Elevation",
//				Integer.toString(node.getDemandCategories().size())));
//		list.add(new Pair<String, String>("Pressure",
//				Integer.toString(node.getDemandCategories().size())));
//		list.add(new Pair<String, String>("Quality",
//				Integer.toString(node.getDemandCategories().size())));
            }

        } else if (selectedItem instanceof Link) {
            if (selectedItem instanceof Pipe) {
                Pipe link = (Pipe) selectedItem;

                // common properties for link
                list.add(new Pair<>("Pipe ID", link.getId()));
                assert link.getNode1() != null;
                list.add(new Pair<>("Initial Node", link.getNode1().getId()));
                assert link.getNode2() != null;
                list.add(new Pair<>("Final Node", link.getNode2().getId()));
                list.add(new Pair<>("Description", link.getDescription()));
                list.add(new Pair<>("Tag", link.getTag() != null ? link.getTag().getLabel() : ""));

                // specific properties for pipe
                list.add(new Pair<>("Lenght", Double.toString(link.getLength())));
                list.add(new Pair<>("Diameter", Double.toString(link.getDiameter())));
                list.add(new Pair<>("Roughness", Double.toString(link.getRoughness())));
                list.add(new Pair<>("Loss Coeficient", Double.toString(link.getLossCoefficient())));
                list.add(new Pair<>("Initial Status", link.getStatus().getName()));
                list.add(new Pair<>("Bulk Coefficient", link.getBulkCoefficient() != null ? link.getBulkCoefficient().toString() : ""));
                list.add(new Pair<>("Bulk Wall", link.getWallCoefficient() != null ? link.getWallCoefficient().toString() : ""));
//				list.add(new Pair<String, String>("Flow", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Velocity", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Unit Headloss", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Friction Factor", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Reaction Rate", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Quality", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Status", link.getStatus().getName()));

            }
            if (selectedItem instanceof Pump) {
                Pump link = (Pump) selectedItem;

                // common properties for link
                list.add(new Pair<>("Pump ID", link.getId()));
                assert link.getNode1() != null;
                list.add(new Pair<>("Initial Node", link.getNode1().getId()));
                assert link.getNode2() != null;
                list.add(new Pair<>("Final Node", link.getNode2().getId()));
                list.add(new Pair<>("Description", link.getDescription()));
                list.add(new Pair<>("Tag", link.getTag() != null ? link.getTag().getLabel() : ""));

                // specific properties for pump

                if (link.getProperty(PumpProperty.HEAD) != null) { // should return id to the curve
                    list.add(new Pair<>("Pump curve",
                            ((String) link.getProperty(PumpProperty.HEAD))));
                } else {
                    list.add(new Pair<>("Pump curve", ""));
                }
                if (link.getProperty(PumpProperty.POWER) != null) { // should return double
                    list.add(new Pair<>("Power", link.getProperty(PumpProperty.POWER).toString()));
                } else {
                    list.add(new Pair<>("Power", ""));

                }
                if (link.getProperty(PumpProperty.SPEED) != null) { // should return double
                    list.add(new Pair<>("Speed",
                            link.getProperty(PumpProperty.SPEED).toString()));
                } else {
                    list.add(new Pair<>("Speed", ""));

                }
                if (link.getProperty(PumpProperty.PATTERN) != null) { // should return a id to the pattern
                    list.add(new Pair<>("Pattern", ((String) link.getProperty(PumpProperty.PATTERN))));
                } else {
                    list.add(new Pair<>("Pattern", ""));
                }
                list.add(new Pair<>("Initial Status", link.getStatus().getName()));
                list.add(new Pair<>("Effic. Curve", link.getEfficiencyCurve()));
                list.add(new Pair<>("Energy Price", link.getEnergyPrice() != null ? link.getEnergyPrice().toString() : ""));
                list.add(new Pair<>("Price Pattern", link.getPricePattern()));
//				list.add(new Pair<String, String>("Flow", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Headloss", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Quality", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Status", link.getStatus().getName()));

            }
            if (selectedItem instanceof Valve) {
                Valve link = (Valve) selectedItem;

                // common properties for link
                list.add(new Pair<>("Pipe ID", link.getId()));
                assert link.getNode1() != null;
                list.add(new Pair<>("Initial Node", link.getNode1().getId()));
                assert link.getNode2() != null;
                list.add(new Pair<>("Final Node", link.getNode2().getId()));
                list.add(new Pair<>("Description", link.getDescription()));
                list.add(new Pair<>("Tag", link.getTag() != null ? link.getTag().getLabel() : ""));

                // specific properties for valve
                list.add(new Pair<>("Diameter", Double.toString(link.getDiameter())));
                list.add(new Pair<>("Type", link.getType().getName()));
                list.add(new Pair<>("Setting", link.getSetting()));
                list.add(new Pair<>("Loss Coeficient", Double.toString(link.getLossCoefficient())));
                list.add(new Pair<>("Fixed Status", link.getFixedStatus().getName()));
//				list.add(new Pair<String, String>("Flow", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Velocity", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Headloss", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Quality", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Status", link.getStatus().getName()));
            }
        } else {
            throw new IllegalArgumentException("The selected item has to be a Node or Junction but it is a " + selectedItem.getClass().getSimpleName());
        }
    }

    /**
     * Update the gridpane in the view
     *
     * @param element the selected item
     * @throws NullPointerException if element is null
     */
    private void updateData(Object element) {
        Objects.requireNonNull(element);
        this.tableView.getItems().clear();
        // fill the labels added
        fillData(element);
    }

    /**
     * Change the data to be showed.
     *
     * @param data the data to show
     * @throws NullPointerException if data is null
     */
    public void setData(Object data) {
        Objects.requireNonNull(data);
        this.data.setValue(data);
    }

}
