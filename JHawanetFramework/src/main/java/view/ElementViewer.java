package view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.epanet.element.Network;
import model.epanet.element.networkcomponent.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Component that show the selected element of the network in the main window.
 *
 */
public class ElementViewer extends VBox {
	@NotNull private final  ObjectProperty<Object> selected;
	@NotNull private final  Accordion accordion;
	@NotNull private final  ListView<Junction> junctionList;
	@NotNull private final  ListView<Reservoir> reservoirList;
	@NotNull private final ListView<Tank> tankList;
	@NotNull private final ListView<Pipe> pipeList;
	@NotNull private final ListView<Pump> pumpList;
	@NotNull private final ListView<Valve> valveList;
//	private final ListView<Curve> curveList;
//	private final ListView<Pattern> patternList;
	/*
	 * This list contains Option, QualityOption, ReactionOption, Time, EnergyOption,
	 * Report
	 */
//	private ListView<Object> optionList;

	@NotNull private final ObjectProperty<Network> network;
	@NotNull private final TitledPane junctionTitled;
	@NotNull private final TitledPane reservoirTitled;
	@NotNull private final TitledPane tankTitled;
	@NotNull private final TitledPane pipeTitled;
	@NotNull private final TitledPane pumpTitled;
	@NotNull private final TitledPane valveTitled;
//	private final TitledPane curveTitled;
//	private final TitledPane patternTitled;
//	private final TitledPane optionTitled;

	public ElementViewer() {
		this.selected = new SimpleObjectProperty<Object>();
		this.network = new SimpleObjectProperty<Network>();
		this.accordion = new Accordion();

		this.junctionList = new ListView<Junction>();
		this.reservoirList = new ListView<Reservoir>();
		this.tankList = new ListView<Tank>();
		this.pipeList = new ListView<Pipe>();
		this.pumpList = new ListView<Pump>();
		this.valveList = new ListView<Valve>();
//		this.curveList = new ListView<Curve>();
//		this.patternList = new ListView<Pattern>();
//		this.optionList = new ListView<Object>();

		this.junctionTitled = new TitledPane("Junction", junctionList);
		this.reservoirTitled = new TitledPane("Reservoir", reservoirList);
		this.tankTitled = new TitledPane("Tank", tankList);
		this.pipeTitled = new TitledPane("Pipe", pipeList);
		this.pumpTitled = new TitledPane("Pump", pumpList);
		this.valveTitled = new TitledPane("Valve", valveList);
//		this.curveTitled = new TitledPane("Curve", curveList);
//		this.patternTitled = new TitledPane("Pattern", patternList);
//		this.optionTitled = new TitledPane("Option", optionList);

		VBox.setVgrow(accordion, Priority.ALWAYS);
		configureElementViewer();
		addBindingAndListener();
	}

	/**
	 * Configure the elements in the component
	 */
	private void configureElementViewer() {
		// add the titled to the accordion
		accordion.getPanes().add(this.junctionTitled);
		accordion.getPanes().add(this.reservoirTitled);
		accordion.getPanes().add(this.tankTitled);
		accordion.getPanes().add(this.pipeTitled);
		accordion.getPanes().add(this.pumpTitled);
		accordion.getPanes().add(this.valveTitled);
//		accordion.getPanes().add(this.curveTitled);
//		accordion.getPanes().add(this.patternTitled);
//		accordion.getPanes().add(this.optionTitled);
		getChildren().add(this.accordion);

		/*
		 * Configure what will be showed for each element: Node and link, curves and
		 * pattern show the id of element. Option show the category name
		 */
		this.junctionList.setCellFactory((listView) -> new ListCell<Junction>() {
			@Override
			protected void updateItem(@Nullable Junction item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(item.getId());
				} else {
					setText("");
				}
			}
		});

		this.reservoirList.setCellFactory((listView) -> new ListCell<Reservoir>() {
			@Override
			protected void updateItem(@Nullable Reservoir item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(item.getId());
				} else {
					setText("");
				}
			}
		});

		this.tankList.setCellFactory((listView) -> new ListCell<Tank>() {
			@Override
			protected void updateItem(@Nullable Tank item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(item.getId());
				} else {
					setText("");
				}
			}
		});

		this.pipeList.setCellFactory((listView) -> new ListCell<Pipe>() {
			@Override
			protected void updateItem(@Nullable Pipe item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(item.getId());
				} else {
					setText("");
				}
			}
		});
		this.pumpList.setCellFactory((listView) -> new ListCell<Pump>() {
			@Override
			protected void updateItem(@Nullable Pump item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(item.getId());
				} else {
					setText("");
				}
			}
		});
		this.valveList.setCellFactory((listView) -> new ListCell<Valve>() {
			@Override
			protected void updateItem(@Nullable Valve item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(item.getId());
				} else {
					setText("");
				}
			}
		});

//		this.patternList.setCellFactory((listView) -> new ListCell<Pattern>() {
//			@Override
//			protected void updateItem(Pattern item, boolean empty) {
//				super.updateItem(item, empty);
//				if (item != null) {
//					setText(item.getId());
//				} else {
//					setText("");
//				}
//			}
//		});
//
//		this.curveList.setCellFactory((listView) -> new ListCell<Curve>() {
//			@Override
//			protected void updateItem(Curve item, boolean empty) {
//				super.updateItem(item, empty);
//				if (item != null) {
//					setText(item.getId());
//				} else {
//					setText("");
//				}
//			}
//		});
//
//		this.optionList.setCellFactory((listView) -> new ListCell<Object>() {
//			@Override
//			protected void updateItem(Object item, boolean empty) {
//				super.updateItem(item, empty);
//				if (item != null) {
//					if (item instanceof Option) {
//						setText("Hydraulic");
//
//					} else if (item instanceof QualityOption) {
//						setText("Quality");
//					} else if (item instanceof ReactionOption) {
//						setText("Reaction");
//
//					} else if (item instanceof Time) {
//						setText("Time");
//
//					} else if (item instanceof EnergyOption) {
//						setText("Energy");
//
//					} else if (item instanceof Report) {
//						setText("Report");
//					}
//				} else {
//					setText("");
//				}
//			}
//		});

	}

	/**
	 * Add the binding and listener to element in this component
	 */
	private void addBindingAndListener() {
		// When the network is loaded or change so update the element viewer
		this.network.addListener((prop, oldV, newV) -> {
			cleanLists();
			setSelected(null);
			if (newV != null) {
				fillLists(newV);
			}
		});

		// Configure the action when the selected element change
		this.selected.addListener((prop, oldV, newV) -> {
			/*
			 * If the selected element of this element viewer isn't the selected element of
			 * listview so select the item in listview
			 */
			if (newV instanceof Junction) {
				this.junctionTitled.setExpanded(true);
				if (this.junctionList.getSelectionModel().getSelectedItem() != newV) {
					this.junctionList.getSelectionModel().select((Junction) newV);
					this.junctionList.scrollTo((Junction) newV);

				}
			}
			if (newV instanceof Reservoir) {
				this.reservoirTitled.setExpanded(true);

				if (this.reservoirList.getSelectionModel().getSelectedItem() != newV) {
					this.reservoirList.getSelectionModel().select((Reservoir) newV);
					this.reservoirList.scrollTo((Reservoir) newV);

				}
			}
			if (newV instanceof Tank) {
				this.tankTitled.setExpanded(true);

				if (this.tankList.getSelectionModel().getSelectedItem() != newV) {
					this.tankList.getSelectionModel().select((Tank) newV);
					this.tankList.scrollTo((Tank) newV);

				}
			}
			if (newV instanceof Pipe) {
				this.pipeTitled.setExpanded(true);

				if (this.pipeList.getSelectionModel().getSelectedItem() != newV) {
					this.pipeList.getSelectionModel().select((Pipe) newV);
					this.pipeList.scrollTo((Pipe) newV);

				}
			}
			if (newV instanceof Pump) {
				this.pumpTitled.setExpanded(true);

				if (this.pumpList.getSelectionModel().getSelectedItem() != newV) {
					this.pumpList.getSelectionModel().select((Pump) newV);
					this.pumpList.scrollTo((Pump) newV);

				}
			}
			if (newV instanceof Valve) {
				this.valveTitled.setExpanded(true);

				if (this.valveList.getSelectionModel().getSelectedItem() != newV) {
					this.valveList.getSelectionModel().select((Valve) newV);
					this.valveList.scrollTo((Valve) newV);

				}
			}
			if (newV != null) {
				DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();
				dataWindow.setData(getSelected());
			}
		});

		// when the title get the focus assign focus to element in listview
		this.junctionTitled.expandedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Junction selected = this.junctionList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		this.reservoirTitled.expandedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Reservoir selected = this.reservoirList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		this.tankTitled.expandedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Tank selected = this.tankList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		this.pipeTitled.expandedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Pipe selected = this.pipeList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		this.pumpTitled.expandedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Pump selected = this.pumpList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		this.valveTitled.expandedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Valve selected = this.valveList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
//		this.curveTitled.expandedProperty().addListener((prop, oldv, newv) -> {
//			if (newv) {
//				Curve selected = this.curveList.getSelectionModel().getSelectedItem();
//				if (selected != null) {
//					setSelected(selected);
//				}
//			}
//		});
//		this.patternTitled.expandedProperty().addListener((prop, oldv, newv) -> {
//			if (newv) {
//				Pattern selected = this.patternList.getSelectionModel().getSelectedItem();
//				if (selected != null) {
//					setSelected(selected);
//				}
//			}
//		});
//		this.optionTitled.expandedProperty().addListener((prop, oldv, newv) -> {
//			if (newv) {
//				Object selected = this.optionList.getSelectionModel().getSelectedItem();
//				if (selected != null) {
//					setSelected(selected);
//				}
//			}
//		});

		/*
		 * Listener for junction list to change the selected object
		 */
		this.junctionList.setOnMouseClicked(e -> {
			setSelected(this.junctionList.getSelectionModel().getSelectedItem());

			if (getSelected() != null && e.getClickCount() == 2) {
				DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();
				dataWindow.show();
			}
		});
		this.reservoirList.setOnMouseClicked(e -> {
			setSelected(this.reservoirList.getSelectionModel().getSelectedItem());

			if (getSelected() != null && e.getClickCount() == 2) {
				DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();
				dataWindow.show();
			}
		});
		this.tankList.setOnMouseClicked(e -> {
			setSelected(this.tankList.getSelectionModel().getSelectedItem());

			if (getSelected() != null && e.getClickCount() == 2) {
				DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();
				dataWindow.show();
			}
		});
		this.pipeList.setOnMouseClicked(e -> {
			setSelected(this.pipeList.getSelectionModel().getSelectedItem());

			if (getSelected() != null && e.getClickCount() == 2) {
				DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();
				dataWindow.show();
			}
		});
		this.pumpList.setOnMouseClicked(e -> {
			setSelected(this.pumpList.getSelectionModel().getSelectedItem());

			if (getSelected() != null && e.getClickCount() == 2) {
				DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();
				dataWindow.show();
			}
		});
		this.valveList.setOnMouseClicked(e -> {
			setSelected(this.valveList.getSelectionModel().getSelectedItem());
			if (getSelected() != null && e.getClickCount() == 2) {
				DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();
				dataWindow.show();
			}
		});
//		this.curveList.setOnMouseClicked(e -> {
//			setSelected(this.curveList.getSelectionModel().getSelectedItem());
//			if (getSelected() != null && e.getClickCount() == 2) {
//				DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();
//				dataWindow.show();
//			}
//		});
//		this.patternList.setOnMouseClicked(e -> {
//			setSelected(this.patternList.getSelectionModel().getSelectedItem());
//			if (getSelected() != null && e.getClickCount() == 2) {
//				DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();
//				dataWindow.show();
//			}
//		});
//		this.optionList.setOnMouseClicked(e -> {
//			setSelected(this.optionList.getSelectionModel().getSelectedItem());
//			if (getSelected() != null && e.getClickCount() == 2) {
//				DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();
//				dataWindow.show();
//			}
//		});
	}

	/**
	 * Fill the listview with the element of network and select a element by default
	 * 
	 * @param network the network
	 */
	private void fillLists(@NotNull Network network) {
		// Add elements and try to select the first element if it exist
		junctionList.getItems().addAll(network.getJunctions());
		junctionList.getSelectionModel().select(0);

		reservoirList.getItems().addAll(network.getReservoirs());
		reservoirList.getSelectionModel().select(0);

		tankList.getItems().addAll(network.getTanks());
		tankList.getSelectionModel().select(0);

		pipeList.getItems().addAll(network.getPipes());
		pipeList.getSelectionModel().select(0);

		pumpList.getItems().addAll(network.getPumps());
		pumpList.getSelectionModel().select(0);

		valveList.getItems().addAll(network.getValves());
		valveList.getSelectionModel().select(0);
//
//		curveList.getItems().addAll(network.getCurveList());
//		curveList.getSelectionModel().select(0);
//
//		patternList.getItems().addAll(network.getPatternList());
//		patternList.getSelectionModel().select(0);
//
//		optionList.getItems().addAll(network.getOption(), network.getQualityOption(), network.getReactionOption(),
//				network.getTime(), network.getEnergyOption());
//		optionList.getSelectionModel().select(0);
	}

	/**
	 * Clean all listView
	 */
	private void cleanLists() {
		junctionList.getItems().clear();
		reservoirList.getItems().clear();
		tankList.getItems().clear();
		pipeList.getItems().clear();
		pumpList.getItems().clear();
		valveList.getItems().clear();
//		curveList.getItems().clear();
//		patternList.getItems().clear();
//		optionList.getItems().clear();

	}

	/**
	 * The selected property
	 * 
	 * @return the selected property
	 */
	public @NotNull ObjectProperty<Object> selectedProperty() {
		return this.selected;
	}

	/**
	 * Get the selected object
	 * 
	 * @return the selectedItem
	 */
	public Object getSelected() {
		return selected.get();
	}

	/**
	 * Change the selected object
	 * 
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelected(Object selectedItem) {
		this.selected.set(selectedItem);
	}

	/**
	 * Set the network. If the network is removed so send null.
	 * 
	 * @param network the network or null if network isn't loaded
	 */
	public void setNetwork(@Nullable Network network) {
		this.network.set(network);
	}

	/**
	 * Get the network
	 * @return the network
	 */
	public Network getNetwork() {
		return network.get();
	}

	/**
	 * Get the network property
	 * @return the network property
	 */
	public @NotNull ObjectProperty<Network> networkProperty() {
		return network;
	}
}
