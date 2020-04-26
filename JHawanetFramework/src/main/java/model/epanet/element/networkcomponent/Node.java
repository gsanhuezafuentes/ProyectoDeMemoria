package model.epanet.element.networkcomponent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.utils.Point;
import model.epanet.element.waterquality.Quality;
import model.epanet.element.waterquality.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Node extends Component {
    @NotNull
    private String id;
    @Nullable
    private Point position;
    @Nullable
    private Quality initialQuality;
    @Nullable
    private Source sourceQuality;

    public Node() {
        this.id = "";
    }

    /**
     * Copy constructor. This is a deep copy, i.e., If the field value is a
     * reference to an object (e.g., a memory address) it copies the reference. If
     * it is necessary for the object to be completely independent of the original
     * you must ensure that you replace the reference to the contained objects.
     *
     * @param node the node to copy
     * @throws NullPointerException if node is null
     */
    public Node(@NotNull Node node) {
        super(Objects.requireNonNull(node));
        this.id = node.id;
        this.position = node.position;
        if (node.initialQuality != null) {
            this.initialQuality = node.initialQuality.copy();

        }
        if (node.sourceQuality != null) {
            this.sourceQuality = node.sourceQuality.copy();
        }
    }

    /**
     * Get the position of the node
     *
     * @return the position of the node if exist or null
     */
    public @Nullable
    final Point getPosition() {
        return position;
    }

    /**
     * Set the position of the node
     *
     * @param position the position of the node
     */
    public final void setPosition(@Nullable Point position) {
        this.position = position;
    }

    /**
     * Get the id
     *
     * @return the id or a empty string if it doesn't exist
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * Set the id
     *
     * @param id the id to set
     * @throws NullPointerException if id is null
     */
    public void setId(@NotNull String id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    /**
     * Get initial quality
     *
     * @return the initialQuality if exist or null
     */
    public @Nullable Quality getInitialQuality() {
        return initialQuality;
    }

    /**
     * @param initialQuality the initialQuality to set or null if it does not exist
     */
    public void setInitialQuality(@Nullable Quality initialQuality) {
        this.initialQuality = initialQuality;
    }

    /**
     * Get the source quality
     *
     * @return the sourceQuality if exist or null
     */
    public @Nullable Source getSourceQuality() {
        return sourceQuality;
    }

    /**
     * Set the source quality
     *
     * @param sourceQuality the sourceQuality to set or null if it does not exist
     */
    public void setSourceQuality(@Nullable Source sourceQuality) {
        this.sourceQuality = sourceQuality;
    }

    @Override
    @SuppressWarnings("unchecked") // the superclass also use Gson to generate the string
    public String toString() {
        Map<String, Object> map = new LinkedHashMap<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        map.put("id", id);
        map.putAll(gson.fromJson(super.toString(), LinkedHashMap.class));//unchecked
        if (position == null) {
            map.put("position", "");
        } else {
            map.put("position", gson.fromJson(position.toString(), LinkedHashMap.class));//unchecked
        }
        if (initialQuality == null) {
            map.put("initialQuality", "");
        } else {
            map.put("initialQuality", gson.fromJson(initialQuality.toString(), LinkedHashMap.class));//unchecked
        }
        if (sourceQuality == null) {
            map.put("sourceQuality", "");
        } else {
            map.put("sourceQuality", gson.fromJson(sourceQuality.toString(), LinkedHashMap.class));//unchecked
        }
        return gson.toJson(map);
    }

    /**
     * Copy this node. This is a shallow copy.
     *
     * @return the copy
     */
    public abstract @NotNull Node copy();

}
