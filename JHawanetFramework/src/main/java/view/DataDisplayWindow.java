package view;

import java.util.Objects;

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
import model.epanet.element.networkcomponent.Junction;
import model.epanet.element.networkcomponent.Node;
import model.epanet.element.networkcomponent.Pipe;
import model.epanet.element.networkcomponent.Pump;
import model.epanet.element.networkcomponent.Pump.PumpProperty;
import model.epanet.element.networkcomponent.Reservoir;
import model.epanet.element.networkcomponent.Tank;
import model.epanet.element.networkcomponent.Valve;

/**
 * View that show the setting up of the elements of the network. It class use
 * the Singleton pattern because only one instance can be created at the same
 * time but it can be called from many other views.
 * 
 * The classes that call to this are {@link ElementViewer} and
 * {@link NetworkComponent}.
 */
public class DataDisplayWindow extends Stage {

	private static int defaultWidth = 600;
	private static int defaulHeight = 400;

	/*
	 * This Window use the Singleton Pattern because can be called from the Network
	 * component and from the elementViewer when the element is double clicked
	 */
	private static DataDisplayWindow instance;
	HBox root;

	private ObjectProperty<Object> data;
	private TableView<Pair<String, String>> tableView;

	private DataDisplayWindow() {
		initStyle(StageStyle.UTILITY);
		setAlwaysOnTop(true);
		setMinWidth(defaultWidth);
		setMinHeight(defaulHeight);
		setWidth(defaultWidth);
		setHeight(defaulHeight);

		this.tableView = new TableView<Pair<String, String>>();
		this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<Pair<String, String>, String> propertyColumn = new TableColumn<Pair<String, String>, String>(
				"Property");
		propertyColumn.setCellValueFactory(
				(CellDataFeatures<Pair<String, String>, String> solutionData) -> new ReadOnlyObjectWrapper<String>(
						solutionData.getValue().getKey()));

		TableColumn<Pair<String, String>, String> valueColumn = new TableColumn<Pair<String, String>, String>("Value");
		valueColumn.setCellValueFactory(
				(CellDataFeatures<Pair<String, String>, String> solutionData) -> new ReadOnlyObjectWrapper<String>(
						solutionData.getValue().getValue()));

		this.tableView.getColumns().add(propertyColumn);
		this.tableView.getColumns().add(valueColumn);

		this.root = new HBox(this.tableView);
		HBox.setHgrow(tableView, Priority.ALWAYS);
		this.data = new SimpleObjectProperty<Object>();
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
		showingProperty().addListener((prop, oldv, newv) -> {
			updateData(this.data.get());
		});
	}

	/**
	 * Fill the previous created label with his appropriate values.
	 * 
	 * @param selectedItem
	 */
	private void fillData(Object selectedItem) {
		ObservableList<Pair<String, String>> list = this.tableView.getItems();
		if (selectedItem instanceof Node) {
			if (selectedItem instanceof Junction) {
				Junction node = (Junction) selectedItem;

				// common properties for node
				list.add(new Pair<String, String>("Junction ID", node.getId()));
				list.add(new Pair<String, String>("X-Coordinates", Double.toString(node.getPosition().getX())));
				list.add(new Pair<String, String>("Y-Coordinates", Double.toString(node.getPosition().getY())));
				list.add(new Pair<String, String>("Description", node.getDescription()));

				// specific property of junction
				list.add(new Pair<String, String>("Elevation", Double.toString(node.getElevation())));
				list.add(new Pair<String, String>("Demand", Double.toString(node.getBaseDemand())));
				if (node.getDemandPattern() != null) {
					list.add(new Pair<String, String>("Demand Pattern", node.getDemandPattern()));
				} else {
					list.add(new Pair<String, String>("Demand Pattern", ""));

				}

			}
			if (selectedItem instanceof Reservoir) {
				Reservoir node = (Reservoir) selectedItem;

				// common properties for node
				list.add(new Pair<String, String>("Reservoir ID", node.getId()));
				list.add(new Pair<String, String>("X-Coordinates", Double.toString(node.getPosition().getX())));
				list.add(new Pair<String, String>("Y-Coordinates", Double.toString(node.getPosition().getY())));
				list.add(new Pair<String, String>("Description", node.getDescription()));

				// specific property of reservoir
				list.add(new Pair<String, String>("Head", Double.toString(node.getTotalHead())));

				if (node.getHeadPattern() != null) {
					list.add(new Pair<String, String>("Head Pattern", node.getHeadPattern()));
				} else {
					list.add(new Pair<String, String>("Head Pattern", ""));
				}

			}
			if (selectedItem instanceof Tank) {
				Tank node = (Tank) selectedItem;

				// common properties for node
				list.add(new Pair<String, String>("Tank ID", node.getId()));
				list.add(new Pair<String, String>("X-Coordinates", Double.toString(node.getPosition().getX())));
				list.add(new Pair<String, String>("Y-Coordinates", Double.toString(node.getPosition().getY())));
				list.add(new Pair<String, String>("Description", node.getDescription()));

				// specific property of reservoir
				list.add(new Pair<String, String>("Elevation", Double.toString(node.getElevation())));
				list.add(new Pair<String, String>("Initial Level", Double.toString(node.getInitialLevel())));
				list.add(new Pair<String, String>("Min Level", Double.toString(node.getMinimumLevel())));
				list.add(new Pair<String, String>("Max Level", Double.toString(node.getMaximumLevel())));
				list.add(new Pair<String, String>("Diameter", Double.toString(node.getDiameter())));
				list.add(new Pair<String, String>("Min Vol", Double.toString(node.getMinimumVolume())));
				if (node.getVolumeCurve() != null) {
					list.add(new Pair<String, String>("Volumen Curve", node.getVolumeCurve()));
				} else {
					list.add(new Pair<String, String>("Volumen Curve", ""));
				}
			}

		} else {
			if (selectedItem instanceof Pipe) {
				Pipe link = (Pipe) selectedItem;

				// common properties for link
				list.add(new Pair<String, String>("Pipe ID", link.getId()));
				list.add(new Pair<String, String>("Initial Node", link.getNode1().getId()));
				list.add(new Pair<String, String>("Final Node", link.getNode2().getId()));
				list.add(new Pair<String, String>("Description", link.getDescription()));

				// specific properties for pipe
				list.add(new Pair<String, String>("Lenght", Double.toString(link.getLength())));
				list.add(new Pair<String, String>("Diameter", Double.toString(link.getDiameter())));
				list.add(new Pair<String, String>("Roughness", Double.toString(link.getRoughness())));
				list.add(new Pair<String, String>("Loss Coeficient", Double.toString(link.getLossCoefficient())));
				list.add(new Pair<String, String>("Status", link.getStatus().getName()));

			}

			if (selectedItem instanceof Pump) {
				Pump link = (Pump) selectedItem;

				// common properties for link
				list.add(new Pair<String, String>("Pipe ID", link.getId()));
				list.add(new Pair<String, String>("Initial Node", link.getNode1().getId()));
				list.add(new Pair<String, String>("Final Node", link.getNode2().getId()));
				list.add(new Pair<String, String>("Description", link.getDescription()));

				// specific properties for pump

				if (link.getProperty(PumpProperty.HEAD) != null) { // should return id to the curve
					list.add(new Pair<String, String>("Characteristic curve",
							((String) link.getProperty(PumpProperty.HEAD))));
				} else {
					list.add(new Pair<String, String>("Characteristic curve", ""));
				}
				if (link.getProperty(PumpProperty.POWER) != null) { // should return double
					list.add(new Pair<String, String>("Power", link.getProperty(PumpProperty.POWER).toString()));
				} else {
					list.add(new Pair<String, String>("Power", ""));

				}
				if (link.getProperty(PumpProperty.SPEED) != null) { // should return double
					list.add(new Pair<String, String>("Relative speed",
							link.getProperty(PumpProperty.SPEED).toString()));
				} else {
					list.add(new Pair<String, String>("Relative speed", ""));

				}
				if (link.getProperty(PumpProperty.PATTERN) != null) { // should return a id to the pattern
					list.add(new Pair<String, String>("Pattern", ((String) link.getProperty(PumpProperty.PATTERN))));
				} else {
					list.add(new Pair<String, String>("Pattern", ""));
				}

			}
			if (selectedItem instanceof Valve) {
				Valve link = (Valve) selectedItem;

				// common properties for link
				list.add(new Pair<String, String>("Pipe ID", link.getId()));
				list.add(new Pair<String, String>("Initial Node", link.getNode1().getId()));
				list.add(new Pair<String, String>("Final Node", link.getNode2().getId()));
				list.add(new Pair<String, String>("Description", link.getDescription()));

				// specific properties for valve
				list.add(new Pair<String, String>("Diameter", Double.toString(link.getDiameter())));
				list.add(new Pair<String, String>("Type", link.getType().getName()));
				list.add(new Pair<String, String>("Setting", link.getSetting()));
				list.add(new Pair<String, String>("Loss Coeficient", Double.toString(link.getLossCoefficient())));

			}
		}
	}

	/**
	 * Update the gridpane in the view
	 * 
	 * @param element
	 */
	private void updateData(Object element) {
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
