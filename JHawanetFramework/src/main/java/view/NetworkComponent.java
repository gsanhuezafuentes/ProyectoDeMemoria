package view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import model.epanet.element.Network;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import view.utils.NetworkImage;

import java.util.Objects;

public class NetworkComponent extends Canvas {
    @NotNull
    private final ObjectProperty<Network> network;
    @NotNull
    private final ObjectProperty<Object> selected;

    public NetworkComponent() {
        this.selected = new SimpleObjectProperty<Object>();
        this.network = new SimpleObjectProperty<Network>();

        addBindingAndListener();

    }

    /**
     * Add the binding and listener to element in this component
     */
    private void addBindingAndListener() {
        // this property let that canvas resize automatically when size of screen change
        widthProperty().addListener((evt) -> {
            if (network.isNotNull().get()) {
                drawNetwork(this.network.get());
            }
        });
        heightProperty().addListener((evt) -> {
            if (network.isNotNull().get()) {
                drawNetwork(this.network.get());
            }
        });

        this.network.addListener((prop, oldV, newV) -> {
            this.selected.set(null);
            if (newV != null) {
                drawNetwork(newV);
            }
        });

        setOnMouseClicked(evt -> {
            if (network.isNotNull().get()) {
                Object selected = NetworkImage.peekNearest(getWidth(), getHeight(), evt.getX(), evt.getY(),
                        this.network.get());
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

        this.selected.addListener((prop, oldV, newV) -> {
            if (newV != null) {
                drawNetwork(this.network.get());
            } else{
                cleanCanvas();
            }
        });
    }

    /**
     * Get the observable property for the selected item
     *
     * @return the property
     */
    public @NotNull ObjectProperty<Object> selectedProperty() {
        return this.selected;
    }

    /**
     * Get the selected element
     *
     * @return return the selected element
     */
    public Object getSelected() {
        return this.selected.get();
    }

    /**
     * Change the selected element
     *
     * @param selected the selected element
     */
    public void setSelected(Object selected) {
        this.selected.set(selected);
    }

    /**
     * Get the network
     *
     * @return the network
     */
    public @Nullable Network getNetwork() {
        return network.get();
    }

    /**
     * Set the network
     *
     * @param network the network or null if it is not loaded
     */
    public void setNetwork(@Nullable Network network) {
        this.network.set(network);
    }

    /**
     * Get the network property
     *
     * @return the network property
     */
    public @NotNull ObjectProperty<Network> networkProperty() {
        return network;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    /**
     * Clean the canvas
     */
    private void cleanCanvas() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());
    }

    /**
     * Draw the network
     *
     * @param net the network
     * @throws NullPointerException if net is null
     */
    private void drawNetwork(@NotNull Network net) {
        Objects.requireNonNull(net);
        cleanCanvas();
        GraphicsContext gc = getGraphicsContext2D();
        NetworkImage.drawNetwork(gc, getWidth(), getHeight(), net, getSelected());
    }
}
