package model.epanet.element.systemoperation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class Demand {
    private double demand;
    @NotNull
    private String demandPattern; // ID to the Pattern
    @NotNull
    private String demandCategory;

    public Demand() {
        this.demandPattern = "";
        this.demandCategory = "";
    }

    /**
     * Create a demand with same values that the demand received. This is a deep
     * copy.
     *
     * @param demand the demand to copy
     * @throws NullPointerException if demand is null
     */
    public Demand(@NotNull Demand demand) {
        Objects.requireNonNull(demand);
        this.demand = demand.demand;
        this.demandPattern = demand.demandPattern;
        this.demandCategory = demand.demandCategory;
    }

    /**
     * @return the demand
     */
    public double getDemand() {
        return demand;
    }

    /**
     * @param demand the demand to set
     */
    public void setDemand(double demand) {
        this.demand = demand;
    }

    /**
     * Get the ID to the {@link Pattern}
     *
     * @return the demand pattern
     */
    public @NotNull String getDemandPattern() {
        return demandPattern;
    }

    /**
     * Set the ID to the {@link Pattern}
     *
     * @param demandPattern the demand pattern to set or empty string if it does not exist
     * @throws NullPointerException if demandPattern is null
     */
    public void setDemandPattern(@NotNull String demandPattern) {
        Objects.requireNonNull(demandPattern);
        this.demandPattern = demandPattern;
    }

    /**
     * @return the demandCategory
     */
    public @NotNull String getDemandCategory() {
        return demandCategory;
    }

    /**
     * @param demandCategory the demandCategory to set or a empty string if it not exist
     * @throws NullPointerException if demandCategory is null
     */
    public void setDemandCategory(@NotNull String demandCategory) {
        Objects.requireNonNull(demandCategory);
        this.demandCategory = demandCategory;
    }

    @Override
    public String toString() {
        Map<String, Object> map = new LinkedHashMap<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        map.put("demand", demand);
        map.put("demandPattern", demandPattern);
        map.put("demandCategory", demandCategory);
        return gson.toJson(map);
    }

    /**
     * Copy this instance. This is a shallow copy.
     *
     * @return the copy
     */
    public @NotNull Demand copy() {
        return new Demand(this);
    }
}
