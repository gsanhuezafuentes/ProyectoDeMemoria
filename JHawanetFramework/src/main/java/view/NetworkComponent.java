package view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import model.epanet.element.Network;
import view.utils.NetworkImage;

public class NetworkComponent extends Canvas {
	private Network network;
	private ObjectProperty<Object> selected;

	public NetworkComponent() {
		this.selected = new SimpleObjectProperty<Object>();

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
				Object selected = NetworkImage.peekNearest(getWidth(), getHeight(), evt.getX(), evt.getY(),
						this.network);
				setSelected(selected);

				if (selected != null) {
					DataDisplayWindow dataWindow = DataDisplayWindow.getInstance();

					if (dataWindow.isShowing()) {
						dataWindow.setData(selected);
					} else if (evt.getClickCount() == 2) {
						dataWindow.setData(selected);
						dataWindow.show();
					}
				}
			}

		});

		this.selected.addListener((prop, newv, oldv) -> {
			drawNetwork(this.network);
		});
	}

	/**
	 * Get the observable property for the selected item
	 * @return the property
	 */
	public ObjectProperty<Object> selectedProperty() {
		return this.selected;
	}

	/**
	 * Change the selected element
	 * @param selected the selected element
	 */
	public void setSelected(Object selected) {
		this.selected.set(selected);
	}

	/**
	 * Get the selected element
	 * @return
	 */
	public Object getSelected() {
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
