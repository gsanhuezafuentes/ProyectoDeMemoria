package view;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.epanet.element.Network;
import model.epanet.element.Selectable;
import model.epanet.element.networkcomponent.Junction;
import model.epanet.element.networkcomponent.Pipe;
import model.epanet.element.networkcomponent.Pump;
import model.epanet.element.networkcomponent.Reservoir;
import model.epanet.element.networkcomponent.Tank;
import model.epanet.element.networkcomponent.Valve;

/**
 * Graphics component that show the element of the network.
 *
 */
public class ElementViewer extends VBox {
	private ObjectProperty<Selectable> selected;
	private Accordion accordion;
	private ListView<Junction> junctionList;
	private ListView<Reservoir> reservoirList;
	private ListView<Tank> tankList;
	private ListView<Pipe> pipeList;
	private ListView<Pump> pumpList;
	private ListView<Valve> valveList;
	private ObjectProperty<Network> network;
	private TitledPane junctionTitled;
	private TitledPane reservoirTitled;
	private TitledPane tankTitled;
	private TitledPane pipeTitled;
	private TitledPane pumpTitled;
	private TitledPane valveTitled;

	public ElementViewer() {
		this.selected = new SimpleObjectProperty<Selectable>();
		this.network = new SimpleObjectProperty<Network>();
		this.accordion = new Accordion();

		this.junctionList = new ListView<Junction>();
		this.reservoirList = new ListView<Reservoir>();
		this.tankList = new ListView<Tank>();
		this.pipeList = new ListView<Pipe>();
		this.pumpList = new ListView<Pump>();
		this.valveList = new ListView<Valve>();

		this.junctionTitled = new TitledPane("Junction", junctionList);
		this.reservoirTitled = new TitledPane("Reservoir", reservoirList);
		this.tankTitled = new TitledPane("Tank", tankList);
		this.pipeTitled = new TitledPane("Pipe", pipeList);
		this.pumpTitled = new TitledPane("Pump", pumpList);
		this.valveTitled = new TitledPane("Valve", valveList);

		VBox.setVgrow(accordion, Priority.ALWAYS);
		configureElementViewer();
		addBindingAndListener();
	}

	/**
	 * Configure the elements in the component
	 */
	private void configureElementViewer() {
		accordion.getPanes().add(this.junctionTitled);
		accordion.getPanes().add(this.reservoirTitled);
		accordion.getPanes().add(this.tankTitled);
		accordion.getPanes().add(this.pipeTitled);
		accordion.getPanes().add(this.pumpTitled);
		accordion.getPanes().add(this.valveTitled);
		getChildren().add(this.accordion);

		this.junctionList.setCellFactory((listView) -> new ListCell<Junction>() {
			@Override
			protected void updateItem(Junction item, boolean empty) {
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
			protected void updateItem(Reservoir item, boolean empty) {
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
			protected void updateItem(Tank item, boolean empty) {
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
			protected void updateItem(Pipe item, boolean empty) {
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
			protected void updateItem(Pump item, boolean empty) {
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
			protected void updateItem(Valve item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(item.getId());
				} else {
					setText("");
				}
			}
		});

	}

	/**
	 * Add the binding and listener to element in this component
	 */
	private void addBindingAndListener() {
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

		this.network.addListener((prop, oldV, newV) -> {
			cleanLists();
			setSelected(null);
			if (newV != null) {
				fillLists(newV);
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
	}

	/**
	 * Fill the listview with the element of network
	 * 
	 * @param network
	 */
	private void fillLists(Network network) {
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

	}

	/**
	 * The selected property
	 * 
	 * @return
	 */
	public ObjectProperty<Selectable> selectedProperty() {
		return this.selected;
	}

	/**
	 * Get the selected object
	 * 
	 * @return the selectedItem
	 */
	public Selectable getSelected() {
		return selected.get();
	}

	/**
	 * Change the selected object
	 * 
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelected(Selectable selectedItem) {
		this.selected.set(selectedItem);
	}

	/**
	 * Set the network. If the network is removed so send null.
	 * 
	 * @param network the network
	 */
	public void setNetwork(Network network) {
		this.network.set(network);
	}

}
