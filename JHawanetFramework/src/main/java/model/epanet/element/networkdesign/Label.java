package model.epanet.element.networkdesign;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.utils.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class Label {
    private static final Logger LOGGER = LoggerFactory.getLogger(Label.class);


    @Nullable
    private Point position;
    @NotNull
    private String label;
    @NotNull
    private String anchorNode; // This is the ID of a Node (Junction or Reservoir or Tank)

    public Label() {
        this.label = "";
        this.anchorNode = "";
    }

    /**
     * Copy constructor. This is a deep copy.
     *
     * @param label the object to copy
     * @throws NullPointerException if label is null.
     */
    public Label(@NotNull Label label) {
        Objects.requireNonNull(label);
        LOGGER.debug("Clonning Label.");

        this.position = label.position;
        this.label = label.label;
        this.anchorNode = label.anchorNode;
    }

    /**
     * Get the position of label. The initial value of position is a (0, 0)
     *
     * @return the point. it can be null if hasn't been initialized
     */
    public @Nullable Point getPosition() {
        return position;
    }

    /**
     * Set the position of the label
     *
     * @param point the point to set
     * @throws NullPointerException if point is null
     */
    public void setPosition(@NotNull Point point) {
        Objects.requireNonNull(point);
        this.position = point;
    }

    /**
     * Get the text of label
     *
     * @return the text or a empty string
     */
    public @NotNull String getLabel() {
        return label;
    }

    /**
     * Set the text of label
     *
     * @param text the text to set
     * @throws NullPointerException if text is null
     */
    public void setLabel(@NotNull String text) {
        Objects.requireNonNull(text);
        this.label = text;
    }

    /**
     * Get the anchor Node id
     *
     * @return the anchorNode or a empty string if there isn't a anchor node
     */
    public @NotNull String getAnchorNode() {
        return anchorNode;
    }

    /**
     * Set the anchor node id
     *
     * @param anchorNode the anchorNode to set
     * @throws NullPointerException if anchorNode is null
     */
    public void setAnchorNode(@NotNull String anchorNode) {
        Objects.requireNonNull(anchorNode);
        this.anchorNode = anchorNode;
    }

    @Override
    public String toString() {
        Map<String, Object> map = new LinkedHashMap<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (position == null) {
            map.put("position", "");
        } else {
            map.put("position", gson.fromJson(position.toString(), LinkedHashMap.class)); //unchecked
        }
        map.put("label", label);
        map.put("anchorNode", anchorNode);
        return gson.toJson(map);
    }

    /**
     * Copy this object. This is a shallow copy.
     *
     * @return the copy.
     */
    public @NotNull Label copy() {
        return new Label(this);
    }
}
