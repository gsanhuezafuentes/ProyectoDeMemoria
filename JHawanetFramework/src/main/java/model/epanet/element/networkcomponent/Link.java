package model.epanet.element.networkcomponent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.result.LinkSimulationResult;
import model.epanet.element.utils.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Link extends Component {
    @NotNull
    private String id;
    @Nullable private Node node1;
    @Nullable private Node node2;
    private final @NotNull List<Point> vertices;

    Link() {
        this.id = "";
        this.vertices = new ArrayList<>();
    }

    /**
     * Copy constructor. Realize a copy setting the same values that the link
     * received. This is a shallow copy, i.e., If the field value is a reference to
     * an object (e.g., a memory address) it copies the reference. If it is
     * necessary for the object to be completely independent of the original you
     * must ensure that you replace the reference to the contained objects.
     * <p>
     * You must replace node1 and node2 to do the copy independent of the original
     *
     * @param link the object to copy
     * @throws NullPointerException if link is null
     */
    Link(@NotNull Link link) {
        super(Objects.requireNonNull(link));
        this.vertices = new ArrayList<>();
        this.id = link.id;
        this.node1 = link.node1; //shallow copy
        this.node2 = link.node2; //shallow copy
        this.vertices.addAll(link.vertices);
    }

    /**
     * Get vertices that contains this links.
     *
     * @return a list with the vertices of the link
     */
    public final @NotNull List<Point> getVertices() {
        return vertices;
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
     * Get the start node
     *
     * @return the node1
     */
    public @Nullable Node getNode1() {
        return node1;
    }

    /**
     * Set the start node
     *
     * @param node1 the node1 to set
     * @throws NullPointerException if node1 is null
     */
    public void setNode1(@NotNull Node node1) {
        Objects.requireNonNull(node1);
        this.node1 = node1;
    }

    /**
     * Get the end node
     *
     * @return the node2
     */
    public @Nullable Node getNode2() {
        return node2;
    }

    /**
     * Set the end node
     *
     * @param node2 the node2 to set
     * @throws NullPointerException if node2 is null
     */
    public void setNode2(@NotNull Node node2) {
        Objects.requireNonNull(node2);
        this.node2 = node2;
    }

    @Override
    @SuppressWarnings("unchecked") // the superclass also use Gson to generate the string
    public String toString() {
        Map<String, Object> map = new LinkedHashMap<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        map.put("id", id);

        map.putAll(gson.fromJson(super.toString(), LinkedHashMap.class)); //unchecked
        if (node1 == null) {
            map.put("node1", "");
        } else {
            map.put("node1", node1.getId());//
        }
        if (node2 == null) {
            map.put("node2", "");
        } else {
            map.put("node2", node2.getId());//
        }
        if (vertices.isEmpty()) {
            map.put("vertices", "");
        } else {
            map.put("vertices", vertices);
        }
        return gson.toJson(map);
    }

    /**
     * Copy this link realizing a shallow copy
     *
     * @return the copy
     */
    public abstract @NotNull Link copy();
}
