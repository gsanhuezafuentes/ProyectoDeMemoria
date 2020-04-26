package model.epanet.element.networkcomponent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.networkdesign.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class is used as super class of the component of the network (Links and
 * nodes) and defined some field that are common for this.
 */
public abstract class Component {

    @NotNull
    private String description;
    @Nullable
    private Tag tag;

    public Component() {
        this.description = "";
    }

    /**
     * Copy constructor. This is a deep copy, i.e., If the field value is a
     * reference to an object (e.g., a memory address) it copies the reference. If
     * it is necessary for the object to be completely independent of the original
     * you must ensure that you replace the reference to the contained objects.
     *
     * @param component the component to copy
     * @throws NullPointerException if component is null
     */
    public Component(@NotNull Component component) {
        Objects.requireNonNull(component);
        this.description = component.description;
        if (component.tag != null) {
            this.tag = component.tag.copy();
        }
    }

    /**
     * Get the description or a empty string if it doesn't exist
     *
     * @return the description
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * Set the description
     *
     * @param description the description to set
     * @throws NullPointerException if description is null
     */
    public void setDescription(@NotNull String description) {
        Objects.requireNonNull(description);
        this.description = description;
    }

    /**
     * Get the tag
     *
     * @return the tag if exist or null in otherwise
     */
    public @Nullable Tag getTag() {
        return this.tag;
    }

    /**
     * Set the tag
     *
     * @param tag the tag or null if not exist
     */
    public void setTag(@Nullable Tag tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        Map<String, Object> map = new LinkedHashMap<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        map.put("description", description);
        if (tag == null) {
            map.put("tag", "");
        } else {
            map.put("tag", gson.fromJson(tag.toString(), LinkedHashMap.class)); //unchecked
        }
        return gson.toJson(map);
    }
}
