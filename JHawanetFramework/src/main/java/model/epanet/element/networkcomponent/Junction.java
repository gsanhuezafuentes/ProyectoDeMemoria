package model.epanet.element.networkcomponent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.systemoperation.Demand;
import model.epanet.element.systemoperation.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class define a junction.
 * <p>
 * The elevation default value is set to 0 <br>
 * The base demand default value is set to 0 <br>
 * <br>
 *
 * <br>
 * <br>
 * <strong>Notes:</strong> <br>
 * baseDemand and demandPattern has the same value that the first element in the
 * list of demandCategory. If you change base demand or demandPattern you change
 * the first element in demandCategory list, and vice versa. <br>
 * <p>
 * demandPattern is a optional<br>
 * <p>
 * emitter is optional<br>
 */
public final class Junction extends Node {
    private static final Logger LOGGER = LoggerFactory.getLogger(Junction.class);

    /**
     * The default value for elevation
     */
    public final static double DEFAULT_ELEVATION = 0;
    /**
     * The default value for base demand
     */
    public final static double DEFAULT_BASE_DEMAND = 0;

    private double elevation;
    //	private double baseDemand;
    @NotNull
    private final List<Demand> demandCategories;
    //	private Pattern demandPattern;
    @Nullable
    private Emitter emitter;

    public Junction() {
        this.elevation = DEFAULT_ELEVATION;
//		this.baseDemand = BASE_DEMAND;
        this.demandCategories = new ArrayList<>();
        Demand defaultDemand = new Demand();
        defaultDemand.setDemand(DEFAULT_BASE_DEMAND);
        this.demandCategories.add(defaultDemand);

    }

    /**
     * Create a junction with the same values that the junction received. This copy
     * is a deep copy of original.
     *
     * @param junction the junction to copy.
     * @throws NullPointerException if junction is null
     */
    public Junction(@NotNull Junction junction) {
        super(Objects.requireNonNull(junction));
        LOGGER.debug("Clonning Junction {}.", junction.getId());

        this.elevation = junction.elevation;
//		this.baseDemand = junction.baseDemand;
//		this.demandPattern = junction.demandPattern;
        this.demandCategories = new ArrayList<>();

        for (Demand demand : junction.demandCategories) {
            this.demandCategories.add(demand.copy());
        }
        if (junction.emitter != null) {
            this.emitter = junction.emitter.copy();
        }

    }

    /**
     * Get the elevation value
     *
     * @return the elevation
     */
    public double getElevation() {
        return elevation;
    }

    /**
     * Set the elevation value
     *
     * @param elev the elevation to set
     */
    public void setElevation(double elev) {
        this.elevation = elev;
    }

    /**
     * Get the base demand. It has the same value that the first element in category
     * demand {@link #getDemandCategories()}.
     *
     * @return the demand
     */
    public double getBaseDemand() {
        // if the demand list hasn't element so add the default element
        if (this.demandCategories.isEmpty()) {
            Demand demand = new Demand();
            demand.setDemand(DEFAULT_BASE_DEMAND);
            this.demandCategories.add(demand);
        }
        return this.demandCategories.get(0).getDemand();
    }

    /**
     * Set the base demand. i.e., set the value of the first element in category
     * demand list {@link #getDemandCategories()}.
     *
     * @param demand the demand to set
     */
    public void setBaseDemand(double demand) {
        // if the demand list hasn't element so add the default element
        if (this.demandCategories.isEmpty()) {
            Demand demandObj = new Demand();
            demandObj.setDemand(demand);
            this.demandCategories.add(demandObj);
            return;
        }
        this.demandCategories.get(0).setDemand(demand);
    }

    /**
     * Get the id to the {@link Pattern}. This has the same pattern id that the
     * first element in category demand list {@link #getDemandCategories()}.
     *
     * @return the pattern
     */
    public @NotNull String getDemandPattern() {
        // if the demand list hasn't element so add the default element
        if (this.demandCategories.isEmpty()) {
            Demand demand = new Demand();
            demand.setDemand(DEFAULT_BASE_DEMAND);
            this.demandCategories.add(demand);
        }
        return this.demandCategories.get(0).getDemandPattern();
    }

    /**
     * Set the id to the {@link Pattern}. <br>
     * <p>
     * The value assigned to this also is the same value that the first element in
     * DemandCategory ({@link #getDemandCategories()}) list.
     *
     * @param pattern the pattern to set
     */
    public void setDemandPattern(@NotNull String pattern) {
        Objects.requireNonNull(pattern);
        // if the demand list hasn't element so add the default element
        if (this.demandCategories.isEmpty()) {
            Demand demand = new Demand();
            demand.setDemand(DEFAULT_BASE_DEMAND);
            this.demandCategories.add(demand);
        }
        this.demandCategories.get(0).setDemandPattern(pattern);
    }

    /**
     * Get the demands.<br>
     * <br>
     * You can get this array and modified. By default this array contains a default
     * demand.
     *
     * @return the demandCategories
     */
    public @NotNull List<Demand> getDemandCategories() {
        return this.demandCategories;
    }

    /**
     * Get the emitter
     *
     * @return the emitter if exist or null in otherwise
     */
    public @Nullable Emitter getEmitter() {
        return this.emitter;
    }

    /**
     * Set the emitter
     *
     * @param emitter the emitter to set or null if not exist
     */
    public void setEmitter(@Nullable Emitter emitter) {
        this.emitter = emitter;
    }

    @Override
    @SuppressWarnings("unchecked") // the superclass also use Gson to generate the string
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, Object> map = new LinkedHashMap<String, Object>(gson.fromJson(super.toString(), LinkedHashMap.class)); //unchecked
        map.put("elevation", elevation);
        if (demandCategories.isEmpty()) {
            map.put("demandCategories", "");
        } else {
            map.put("demandCategories", demandCategories);
        }
        if (emitter == null) {
            map.put("emitter", "");
        } else {
            map.put("emitter", gson.fromJson(emitter.toString(), LinkedHashMap.class)); //unchecked
        }
        return gson.toJson(map);
    }

    /**
     * Realize a shallow copy of this junction.
     *
     * @return the shallow copy
     */
    @Override
    public @NotNull Junction copy() {
        return new Junction(this);

    }

}
