package view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import model.epanet.element.Network;
import model.epanet.element.Selectable;
import view.utils.NetworkImage;

public class NetworkComponent extends Canvas {
	private Network network;
	private ObjectProperty<Selectable> selected;

	public NetworkComponent() {
		this.selected = new SimpleObjectProperty<Selectable>();

		widthProperty().addListener((evt) -> {
			if (network != null) {
				drawNetwork(this.network);
			}
		});
		heightProperty().addListener((evt) -> {
			if (network != null) {
				drawNetwork(this.network);
			}
		});

		setOnMouseClicked(evt -> {
			if (this.network != null) {
				Selectable selected = NetworkImage.peekNearest(getWidth(), getHeight(), evt.getX(), evt.getY(),
						this.network);
				setSelected(selected);
			}
		});

		this.selected.addListener((prop, newv, oldv) -> {
			drawNetwork(this.network);
		});
	}

	public ObjectProperty<Selectable> selectedProperty() {
		return this.selected;
	}

	public void setSelected(Selectable selected) {
		this.selected.set(selected);
	}

	public Selectable getSelected() {
		return this.selected.get();
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	public void drawNetwork(Network net) {
		this.network = net;

		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, this.getWidth(), this.getHeight());
		NetworkImage.drawNetwork(gc, getWidth(), getHeight(), net, getSelected());
	}
}
