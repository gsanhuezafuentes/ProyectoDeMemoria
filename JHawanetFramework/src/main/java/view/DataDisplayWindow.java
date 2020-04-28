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

		/**
		 * Si quieren editar los valores que muestra este componente para cada elemento
		 * de la red recomiendo reemplazar el tableView por label y textfield
		 */
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
				list.add(new Pair<String, String>("Tag", node.getTag() != null ? node.getTag().getLabel() : ""));

				// specific property of junction
				list.add(new Pair<String, String>("Elevation", Double.toString(node.getElevation())));
				list.add(new Pair<String, String>("Demand", Double.toString(node.getBaseDemand())));
				if (node.getDemandPattern() != null) {
					list.add(new Pair<String, String>("Demand Pattern", node.getDemandPattern()));
				} else {
					list.add(new Pair<String, String>("Demand Pattern", ""));
				}
				list.add(new Pair<String, String>("Demand Categories",
						Integer.toString(node.getDemandCategories().size())));
				Emitter emitter = node.getEmitter();
				list.add(new Pair<String, String>("Emitter Coeff.",
						emitter != null ? Double.toString(emitter.getCoefficient()) : ""));
				Quality quality = node.getInitialQuality();
				list.add(new Pair<String, String>("Initial Quality",
						quality != null ? Double.toString(quality.getInitialQuality()) : ""));
				Source source = node.getSourceQuality();
				list.add(new Pair<String, String>("Source Quality",
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
				list.add(new Pair<String, String>("Reservoir ID", node.getId()));
				list.add(new Pair<String, String>("X-Coordinates", Double.toString(node.getPosition().getX())));
				list.add(new Pair<String, String>("Y-Coordinates", Double.toString(node.getPosition().getY())));
				list.add(new Pair<String, String>("Description", node.getDescription()));
				list.add(new Pair<String, String>("Tag", node.getTag() != null ? node.getTag().getLabel() : ""));

				// specific property of reservoir
				list.add(new Pair<String, String>("Total Head", Double.toString(node.getTotalHead())));

				if (node.getHeadPattern() != null) {
					list.add(new Pair<String, String>("Head Pattern", node.getHeadPattern()));
				} else {
					list.add(new Pair<String, String>("Head Pattern", ""));
				}
				Quality quality = node.getInitialQuality();
				list.add(new Pair<String, String>("Initial Quality",
						quality != null ? Double.toString(quality.getInitialQuality()) : ""));
				Source source = node.getSourceQuality();
				list.add(new Pair<String, String>("Source Quality",
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
				list.add(new Pair<String, String>("Tank ID", node.getId()));
				list.add(new Pair<String, String>("X-Coordinates", Double.toString(node.getPosition().getX())));
				list.add(new Pair<String, String>("Y-Coordinates", Double.toString(node.getPosition().getY())));
				list.add(new Pair<String, String>("Description", node.getDescription()));
				list.add(new Pair<String, String>("Tag", node.getTag() != null ? node.getTag().getLabel() : ""));

				// specific property of reservoir
				list.add(new Pair<String, String>("Elevation", Double.toString(node.getElevation())));
				list.add(new Pair<String, String>("Initial Level", Double.toString(node.getInitialLevel())));
				list.add(new Pair<String, String>("Minumum Level", Double.toString(node.getMinimumLevel())));
				list.add(new Pair<String, String>("Maximum Level", Double.toString(node.getMaximumLevel())));
				list.add(new Pair<String, String>("Diameter", Double.toString(node.getDiameter())));
				list.add(new Pair<String, String>("Minimum Volume", Double.toString(node.getMinimumVolume())));
				if (node.getVolumeCurve() != null) {
					list.add(new Pair<String, String>("Volume Curve", node.getVolumeCurve()));
				} else {
					list.add(new Pair<String, String>("Volume Curve", ""));
				}
				Mixing mixing = node.getMixing();
				list.add(new Pair<String, String>("Mixing Model", mixing.getModel().getName()));
				list.add(new Pair<String, String>("Mixing Fraction",
						mixing.getMixingFraction() != null ? Double.toString(mixing.getMixingFraction()) : ""));
				Double reactionCoeff = node.getReactionCoefficient();
				list.add(new Pair<String, String>("Reaction Coeff.",
						reactionCoeff != null ? reactionCoeff.toString() : ""));
				Quality quality = node.getInitialQuality();
				list.add(new Pair<String, String>("Initial Quality",
						quality != null ? Double.toString(quality.getInitialQuality()) : ""));
				Source source = node.getSourceQuality();
				list.add(new Pair<String, String>("Source Quality",
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

		} else {
			if (selectedItem instanceof Pipe) {
				Pipe link = (Pipe) selectedItem;

				// common properties for link
				list.add(new Pair<String, String>("Pipe ID", link.getId()));
				list.add(new Pair<String, String>("Initial Node", link.getNode1().getId()));
				list.add(new Pair<String, String>("Final Node", link.getNode2().getId()));
				list.add(new Pair<String, String>("Description", link.getDescription()));
				list.add(new Pair<String, String>("Tag", link.getTag() != null ? link.getTag().getLabel() : ""));

				// specific properties for pipe
				list.add(new Pair<String, String>("Lenght", Double.toString(link.getLength())));
				list.add(new Pair<String, String>("Diameter", Double.toString(link.getDiameter())));
				list.add(new Pair<String, String>("Roughness", Double.toString(link.getRoughness())));
				list.add(new Pair<String, String>("Loss Coeficient", Double.toString(link.getLossCoefficient())));
				list.add(new Pair<String, String>("Initial Status", link.getStatus().getName()));
				list.add(new Pair<String, String>("Bulk Coefficient", link.getWallCoefficient() != null ? link.getBulkCoefficient().toString():""));
				list.add(new Pair<String, String>("Bulk Wall", link.getWallCoefficient() != null ? link.getWallCoefficient().toString():""));
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
				list.add(new Pair<String, String>("Pipe ID", link.getId()));
				list.add(new Pair<String, String>("Initial Node", link.getNode1().getId()));
				list.add(new Pair<String, String>("Final Node", link.getNode2().getId()));
				list.add(new Pair<String, String>("Description", link.getDescription()));
				list.add(new Pair<String, String>("Tag", link.getTag() != null ? link.getTag().getLabel() : ""));

				// specific properties for pump

				if (link.getProperty(PumpProperty.HEAD) != null) { // should return id to the curve
					list.add(new Pair<String, String>("Pump curve",
							((String) link.getProperty(PumpProperty.HEAD))));
				} else {
					list.add(new Pair<String, String>("Pump curve", ""));
				}
				if (link.getProperty(PumpProperty.POWER) != null) { // should return double
					list.add(new Pair<String, String>("Power", link.getProperty(PumpProperty.POWER).toString()));
				} else {
					list.add(new Pair<String, String>("Power", ""));

				}
				if (link.getProperty(PumpProperty.SPEED) != null) { // should return double
					list.add(new Pair<String, String>("Speed",
							link.getProperty(PumpProperty.SPEED).toString()));
				} else {
					list.add(new Pair<String, String>("Speed", ""));

				}
				if (link.getProperty(PumpProperty.PATTERN) != null) { // should return a id to the pattern
					list.add(new Pair<String, String>("Pattern", ((String) link.getProperty(PumpProperty.PATTERN))));
				} else {
					list.add(new Pair<String, String>("Pattern", ""));
				}
				list.add(new Pair<String, String>("Initial Status", link.getStatus().getName()));
				list.add(new Pair<String, String>("Effic. Curve", link.getEfficiencyCurve()));
				list.add(new Pair<String, String>("Energy Price", link.getEnergyPrice() != null ? link.getEnergyPrice().toString() : ""));
				list.add(new Pair<String, String>("Price Pattern", link.getPricePattern()));
//				list.add(new Pair<String, String>("Flow", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Headloss", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Quality", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Status", link.getStatus().getName()));

			}
			if (selectedItem instanceof Valve) {
				Valve link = (Valve) selectedItem;

				// common properties for link
				list.add(new Pair<String, String>("Pipe ID", link.getId()));
				list.add(new Pair<String, String>("Initial Node", link.getNode1().getId()));
				list.add(new Pair<String, String>("Final Node", link.getNode2().getId()));
				list.add(new Pair<String, String>("Description", link.getDescription()));
				list.add(new Pair<String, String>("Tag", link.getTag() != null ? link.getTag().getLabel() : ""));

				// specific properties for valve
				list.add(new Pair<String, String>("Diameter", Double.toString(link.getDiameter())));
				list.add(new Pair<String, String>("Type", link.getType().getName()));
				list.add(new Pair<String, String>("Setting", link.getSetting()));
				list.add(new Pair<String, String>("Loss Coeficient", Double.toString(link.getLossCoefficient())));
				list.add(new Pair<String, String>("Fixed Status", link.getFixedStatus().getName()));
//				list.add(new Pair<String, String>("Flow", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Velocity", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Headloss", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Quality", link.getStatus().getName()));
//				list.add(new Pair<String, String>("Status", link.getStatus().getName()));
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
