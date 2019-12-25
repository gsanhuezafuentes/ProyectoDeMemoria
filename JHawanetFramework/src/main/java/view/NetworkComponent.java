package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import model.epanet.element.Network;
import model.epanet.element.networkcomponent.Node;
import view.utils.NetworkImage;

public class NetworkComponent extends Canvas{
	private Network network;
	private Node selectedNode;

	public NetworkComponent() {
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
				this.selectedNode = NetworkImage.peekNearest(getWidth(), getHeight(), evt.getX(), evt.getY(),
						this.network);
				drawNetwork(this.network);
			}
		});
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	public void drawNetwork(Network net) {
		this.network = net;

		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, this.getWidth(), this.getHeight());
		NetworkImage.drawNetwork(gc, getWidth(), getHeight(), net, this.selectedNode);
	}
}
