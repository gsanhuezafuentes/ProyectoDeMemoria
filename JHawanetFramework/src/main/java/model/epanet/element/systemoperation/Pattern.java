package model.epanet.element.systemoperation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.Network;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class Pattern {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pattern.class);

    @NotNull
    private String id;
    @NotNull
    private List<Double> multipliers;

    public Pattern() {
        this.id = "";
        this.multipliers = new ArrayList<>();
    }

    /**
     * Copy constructor.
     *
     * @param pattern the object to copy
     * @throws NullPointerException if pattern is null
     */
    public Pattern(@NotNull Pattern pattern) {
        this();
        Objects.requireNonNull(pattern);
        LOGGER.debug("Clonning Pattern {}.", pattern.getId());

        this.id = pattern.id;
        this.multipliers.addAll(pattern.multipliers);
    }


    /**
     * Get the id
     * @return the id
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * Set the id
     * @param id the id to set
     * @throws NullPointerException if id is null
     */
    public void setId(@NotNull String id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    /**
     * @return the multipliers
     */
    public @NotNull List<Double> getMultipliers() {
        return multipliers;
    }

    /**
     * Set the multipliers
     *
     * @param multipliers the multipliers to set
     * @throws NullPointerException if multipliers is null
     */
    public void setMultipliers(@NotNull List<Double> multipliers) {
        Objects.requireNonNull(multipliers);
        this.multipliers = multipliers;
    }

    /**
     * Add a multiplier to multipliers list
     *
     * @param multiplier the multiplier to add
     */
    public void addMultipliers(double multiplier) {
        this.multipliers.add(multiplier);
    }

    @Override
    public String toString() {
        Map<String, Object> map = new LinkedHashMap<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        map.put("id", id);
        if (multipliers.isEmpty()) {
            map.put("multipliers", "");
        } else {
            map.put("multipliers", multipliers);
        }
        return gson.toJson(map);
    }

    /**
     * Create a copy of this object.
     *
     * @return the copy
     */
    public @NotNull Pattern copy() {
        return new Pattern(this);
    }
}
