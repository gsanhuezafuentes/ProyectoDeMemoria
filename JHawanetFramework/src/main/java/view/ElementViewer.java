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
		this.network.addListener((prop, oldV, newV) -> {
			cleanLists();
			if (newV != null) {
				fillLists(newV);
			}
		});

		this.selected.addListener((prop, oldV, newV) -> {
			System.out.println(newV);
		});

		// when the title get the focus assign focus to element in listview
		this.junctionTitled.focusedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Junction selected = this.junctionList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		this.reservoirTitled.focusedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Reservoir selected = this.reservoirList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		this.tankTitled.focusedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Tank selected = this.tankList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		this.pipeTitled.focusedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Pipe selected = this.pipeList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		this.pumpTitled.focusedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Pump selected = this.pumpList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});
		this.valveTitled.focusedProperty().addListener((prop, oldv, newv) -> {
			if (newv) {
				Valve selected = this.valveList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					setSelected(selected);
				}
			}
		});

		this.junctionList.setOnMouseClicked(e -> setSelected(this.junctionList.getSelectionModel().getSelectedItem()));
		this.reservoirList
				.setOnMouseClicked(e -> setSelected(this.reservoirList.getSelectionModel().getSelectedItem()));
		this.tankList.setOnMouseClicked(e -> setSelected(this.tankList.getSelectionModel().getSelectedItem()));
		this.pipeList.setOnMouseClicked(e -> setSelected(this.pipeList.getSelectionModel().getSelectedItem()));
		this.pumpList.setOnMouseClicked(e -> setSelected(this.pumpList.getSelectionModel().getSelectedItem()));
		this.valveList.setOnMouseClicked(e -> setSelected(this.valveList.getSelectionModel().getSelectedItem()));
	}

	/**
	 * Fill the listview with the element of network
	 * 
	 * @param network
	 */
	private void fillLists(Network network) {
		junctionList.getItems().addAll(network.getJunctions());
		reservoirList.getItems().addAll(network.getReservoirs());
		tankList.getItems().addAll(network.getTanks());
		pipeList.getItems().addAll(network.getPipes());
		pumpList.getItems().addAll(network.getPumps());
		valveList.getItems().addAll(network.getValves());
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
