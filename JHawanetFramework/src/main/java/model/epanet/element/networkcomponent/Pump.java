package model.epanet.element.networkcomponent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.Network;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class Pump extends Link {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pump.class);

    /**
     * Valid options to properties.
     *
     */
    public enum PumpProperty {
        /*
         * When this property is added the value has to be the id (an String) of Curve
         *
         */
        HEAD("HEAD"),
        /*
         * When this property is added the value as to be a Double.
         * The value associated to this property is saved in [PUMP] section
         * and in [Status] section
         */
        SPEED("SPEED"),
        /*
         *  When this property is added the value as to be a Double.
         */
        POWER("POWER"),
        /*
         * When this property is added the value has to be the id (an String) of Pattern
         */
        PATTERN("PATTERN");

        private final String name;

        PumpProperty(String name) {
            this.name = name;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Parse the string to the enum
         * @param name the name
         * @return the associated enum
         * @throws IllegalArgumentException if name is not valid
         */
        public static @NotNull PumpProperty parse(String name) {
            for (PumpProperty object : PumpProperty.values()) {
                if (object.getName().equalsIgnoreCase(name)) {
                    return object;
                }
            }
            throw new IllegalArgumentException("There are not a valid element with the name " + name);
        }

    }

    /**
     * A enumerator that to define the status of Pump. This is saved in [Status]
     * section of inp.
     */
    public enum PumpStatus {
        OPEN("OPEN"), CLOSED("CLOSED");

        private final String name;

        PumpStatus(String name) {
            this.name = name;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Parse the string to the enum
         * @param name the name
         * @return the associated enum
         * @throws IllegalArgumentException if name is not valid
         */
        public static @NotNull PumpStatus parse(String name) {
            for (PumpStatus object : PumpStatus.values()) {
                if (object.getName().equalsIgnoreCase(name)) {
                    return object;
                }
            }
            throw new IllegalArgumentException("There are not a valid element with the name " + name);
        }

    }

    public static final PumpStatus DEFAULT_STATUS = PumpStatus.OPEN;

    @NotNull private final Map<PumpProperty, Object> properties;
    @NotNull private PumpStatus status;

    /*
     * This field is saved in [Energy] section using the key Pump
     */
    @NotNull private String efficiencyCurve;
    /*
     * This field is saved in [Energy] section using the key Pump
     */
    @Nullable private Double energyPrice;
    /*
     * This field is saved in [Energy] section using the key Pump
     */
    @NotNull private String pricePattern;

    public Pump() {
        this.status = DEFAULT_STATUS;
        this.efficiencyCurve = "";
        this.pricePattern = "";
        this.properties = new HashMap<>();
    }

    /**
     * Create a pump with same values that the pump received. This is a shallow
     * copy, i.e., If the field value is a reference to an object (e.g., a memory
     * address) it copies the reference. If it is necessary for the object to be
     * completely independent of the original you must ensure that you replace the
     * reference to the contained objects.
     * <p>
     * <p>
     * You must replace node1 and node2 to do the copy independent of the original
     *
     * @param pump the pump to copy
     * @throws NullPointerException if pump is null
     */
    public Pump(@NotNull Pump pump) {
        super(Objects.requireNonNull(pump));
        LOGGER.debug("Clonning pump {}.", pump.getId());

        this.properties = new HashMap<>(pump.properties);
        this.energyPrice = pump.energyPrice;
        this.efficiencyCurve = pump.efficiencyCurve;
        this.pricePattern = pump.pricePattern;
        this.status = pump.status;
    }

    /**
     * Get the property by key. <br>
     * <br>
     * When key is {@link PumpProperty#HEAD} value has to be the id of the Curve has a String <br>
     * <br>
     * When key is {@link PumpProperty#PATTERN} value has to be the id of a Pattern has a String <br>
     * <br>
     * When key is {@link PumpProperty#SPEED} value has to be {@link Double} <br>
     * <br>
     * When key is {@link PumpProperty#POWER} value has to be {@link Double} <br>
     * <br>
     * When key is null so the property isn't setting up
     *
     * @param key the key of property
     * @return value the value (Double, Curve or Pattern or null)
     */
    public Object getProperty(PumpProperty key) {
        return this.properties.get(key);
    }

    /**
     * Set the property. <br>
     * <br>
     * When key is {@link PumpProperty#HEAD} value has to be the id of the Curve has a String <br>
     * <br>
     * When key is {@link PumpProperty#PATTERN} value has to be the id of a Pattern has a String <br>
     * <br>
     * When key is {@link PumpProperty#SPEED} value has to be {@link Double} <br>
     * <br>
     * When key is {@link PumpProperty#POWER} value has to be {@link Double}
     * <p>
     * {@link PumpProperty#POWER} and {@link PumpProperty#HEAD} cannot be configured
     * at the same time. The last addition is the one that remains.
     *
     * @param key   the key
     * @param value the value (Double, Curve or Pattern)
     * @throws IllegalArgumentException if value type is not valid to key used.
     */
    public void setProperty(@NotNull PumpProperty key, Object value) {
        Objects.requireNonNull(value);

        if (PumpProperty.HEAD == key) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("The value of the key " + key.getName() + " is not of type String");
            }
            // if the property to set is HEAD so remove Power
            this.properties.remove(PumpProperty.POWER);
        } else if (PumpProperty.PATTERN == key) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("The value of the key " + key.getName() + "is not of type String");
            }

        } else {
            if (!(value instanceof Double)) {
                throw new IllegalArgumentException("The value of the key " + key.getName() + "is not of type Double");
            }
            // if the property to set is Power so remove HEAD
            if (key == PumpProperty.POWER) {
                this.properties.remove(PumpProperty.HEAD);
            }
        }
        this.properties.put(key, value);
    }

    /**
     * Get the keys of properties configured.
     *
     * @return the keys
     */
    public @NotNull Set<PumpProperty> getPropertyKeys() {
        return this.properties.keySet();
    }

    /**
     * Get the status of this pump
     *
     * @return the status
     */
    public @NotNull PumpStatus getStatus() {
        return status;
    }

    /**
     * Set the status of this pipe
     *
     * @param status the status to set
     * @throws NullPointerException if status is null
     */
    public void setStatus(@NotNull PumpStatus status) {
        Objects.requireNonNull(status);
        this.status = status;
    }

    /**
     * Get the efficiency curve
     *
     * @return the efficiencyCurve or a empty string if not exist
     */
    public @NotNull String getEfficiencyCurve() {
        return efficiencyCurve;
    }

    /**
     * Set the efficiency curve. It receive the id of the curve
     *
     * @param efficiencyCurve the efficiencyCurve to set or a empty string if it doesn't exist
     * @throws NullPointerException if efficiencyCurve is null
     */
    public void setEfficiencyCurve(@NotNull String efficiencyCurve) {
        Objects.requireNonNull(efficiencyCurve);
        this.efficiencyCurve = efficiencyCurve;
    }

    /**
     * Get the energy price
     *
     * @return the energyPrice or null if not exist
     */
    public @Nullable Double getEnergyPrice() {
        return energyPrice;
    }

    /**
     * Set the energy price
     *
     * @param energyPrice the energyPrice to set or null if not exist
     */
    public void setEnergyPrice(@Nullable Double energyPrice) {
        this.energyPrice = energyPrice;
    }

    /**
     * Get the pattern price
     *
     * @return the patternPrice or null if not exist
     */
    public @NotNull String getPricePattern() {
        return pricePattern;
    }

    /**
     * Set the price pattern. A id to a Pattern
     *
     * @param pricePattern the price pattern id to set or a empty string if it doesn't exist
     * @throws NullPointerException if pricePattern is null
     */
    public void setPricePattern(@NotNull String pricePattern) {
        Objects.requireNonNull(pricePattern);
        this.pricePattern = pricePattern;
    }

    @Override
    @SuppressWarnings("unchecked") // the superclass also use Gson to generate the string
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, Object> map = new LinkedHashMap<String, Object>(gson.fromJson(super.toString(), LinkedHashMap.class)); //unchecked
        if (properties.isEmpty()) {
            map.put("properties", "");
        } else {
            map.put("properties", properties);
        }
        map.put("status", status);
        map.put("efficiencyCurve", efficiencyCurve);
        if (energyPrice == null) {
            map.put("energyPrice", "");
        } else {
            map.put("energyPrice", energyPrice);//
        }
        map.put("pricePattern", pricePattern);
        return gson.toJson(map);
    }

    /**
     * Copy this instance. This is a shallow copy. return the copy
     */
    @Override
    public @NotNull Pump copy() {
        return new Pump(this);
    }

}
