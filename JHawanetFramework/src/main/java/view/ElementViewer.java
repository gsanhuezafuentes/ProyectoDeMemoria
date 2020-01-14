package view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Accordion;
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
 * Graphics component that show the element of the network
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

	public ElementViewer() {
		this.selected = new SimpleObjectProperty<Selectable>();
		this.network = new SimpleObjectProperty<Network>();
		this.accordion = new Accordion();
		VBox.setVgrow(accordion, Priority.ALWAYS);
		configureElementViewer();
		addBindingAndListener();
	}

	private void configureElementViewer() {
		accordion.getPanes().addAll(new TitledPane("Junction", junctionList), //
				new TitledPane("Reservoir", reservoirList), //
				new TitledPane("Tank", tankList), //
				new TitledPane("Pipe", pipeList), //
				new TitledPane("Pump", pumpList), //
				new TitledPane("Valve", valveList));
		getChildren().add(this.accordion);
	}

	private void addBindingAndListener() {
		this.network.addListener((prop, oldV, newV) -> {
			cleanLists();
			if (newV != null) {
				fillLists(newV);
			}
		});
		this.selected.addListener((prop, oldV, newV) -> System.out.println(newV));
		this.junctionList.getSelectionModel().selectedItemProperty().addListener((prop, oldV, newV) -> setSelected(newV));
		this.reservoirList.getSelectionModel().selectedItemProperty().addListener((prop, oldV, newV) -> setSelected(newV));
		this.tankList.getSelectionModel().selectedItemProperty().addListener((prop, oldV, newV) -> setSelected(newV));
		this.pipeList.getSelectionModel().selectedItemProperty().addListener((prop, oldV, newV) -> setSelected(newV));
		this.pumpList.getSelectionModel().selectedItemProperty().addListener((prop, oldV, newV) -> setSelected(newV));
		this.valveList.getSelectionModel().selectedItemProperty().addListener((prop, oldV, newV) -> setSelected(newV));
	}

	private void fillLists(Network network) {
		junctionList.getItems().addAll(network.getJunctions());
		reservoirList.getItems().addAll(network.getReservoirs());
		tankList.getItems().addAll(network.getTanks());
		pipeList.getItems().addAll(network.getPipes());
		pumpList.getItems().addAll(network.getPumps());
		valveList.getItems().addAll(network.getValves());
	}

	private void cleanLists() {
		junctionList.getItems().clear();
		reservoirList.getItems().clear();
		tankList.getItems().clear();
		pipeList.getItems().clear();
		pumpList.getItems().clear();
		valveList.getItems().clear();

	}

	/**
	 * 
	 * @return
	 */
	public ObjectProperty<Selectable> selectedProperty() {
		return this.selected;
	}

	/**
	 * @return the selectedItem
	 */
	public Selectable getSelected() {
		return selected.get();
	}

	/**
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelected(Selectable selectedItem) {
		this.selected.set(selectedItem);
	}

	/**
	 * 
	 * @param network
	 */
	public void setNetwork(Network network) {
		this.network.set(network);
	}

}
