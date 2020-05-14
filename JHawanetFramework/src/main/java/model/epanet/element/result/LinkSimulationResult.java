package model.epanet.element.result;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This class is used to store the result value of a simulation for the links.
 * Not all links need all values. The values needed by each link type are:
 * <p>
 * The Pipe: flow, velocity, unitHeadloss, frictionFactor, reactionRate, quality, status.
 * The Valve: flow, velocity, headloss, quality, status.
 * The Pump: flow, headloss, quality, status.
 * <p>
 * There are a constructor for each type. The no used parameters are filled with a default value 0.
 */
public final class LinkSimulationResult extends ResultSimulation {
    public enum Status {
        OPEN("OPEN"), CLOSE("CLOSE");

        private final String name;

        Status(String name) {
            this.name = name;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Parse the name to a object of the enum class if exist. if name no exist in enum class so return null;
         *
         * @param name the name of object
         * @return the object of enum class or null if no exist
         * @throws IllegalArgumentException if name is not valid
         */
        public static @NotNull Status parse(String name) {
            for (Status object : Status.values()) {
                if (object.getName().equalsIgnoreCase(name)) {
                    return object;
                }
            }
            throw new IllegalArgumentException("There are not a valid element with the name " + name);
        }
    }

    private final double flow;
    private final double velocity;
    private final double headloss;
    // parameter no implemented because i don't know how calculate its =P
    private final double frictionFactor; //no implemented eliminar
    private final double reactionRate; // no implemented eliminar
    private final double quality; //no implemented eliminar
    @NotNull
    private final Status status;

    /**
     * Use this constructor for result of Pump
     *
     * @param timeInSeconds the simulation time in seconds
     * @param flow          the flow
     * @param headloss      the headloss
     * @param quality       the quality
     * @param status        the status
     * @throws NullPointerException if status is null
     */
    public LinkSimulationResult(long timeInSeconds, double flow, double headloss, double quality, @NotNull Status status) {
        this(timeInSeconds, flow, 0, headloss, 0, 0, quality, status);
    }

    /**
     * Use this constructor for result of Valve
     *
     * @param timeInSeconds the simulation time in seconds
     * @param flow          the flow
     * @param velocity      the velocity
     * @param headloss      the headloss
     * @param quality       the quality
     * @param status        the status
     * @throws NullPointerException if status is null
     */
    public LinkSimulationResult(long timeInSeconds, double flow, double velocity, double headloss, double quality, @NotNull Status status) {
        this(timeInSeconds, flow, velocity, headloss, 0, 0, quality, status);
    }

    /**
     * Use this constructor for result of Pipe
     *
     * @param timeInSeconds  the simulation time in seconds
     * @param flow           the flow
     * @param velocity       the velocity
     * @param unitHeadloss   the unit headloss
     * @param frictionFactor the friction factor
     * @param reactionRate   the reaction rate
     * @param quality        the quality status
     * @param status         the status
     * @throws NullPointerException if status is null
     */
    public LinkSimulationResult(long timeInSeconds, double flow, double velocity, double unitHeadloss, double frictionFactor, double reactionRate, double quality, @NotNull Status status) {
        super(timeInSeconds);
        Objects.requireNonNull(status);
        this.flow = flow;
        this.velocity = velocity;
        this.headloss = unitHeadloss;
        this.frictionFactor = frictionFactor;
        this.reactionRate = reactionRate;
        this.quality = quality;
        this.status = status;
    }

    /**
     * Get the flow
     *
     * @return the flow
     */
    public double getFlow() {
        return flow;
    }

    /**
     * Get the velocity
     *
     * @return the velocity
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * Get the headloss for Valve and Pump and the unit headloss for pipe
     *
     * @return the headloss
     */
    public double getHeadloss() {
        return headloss;
    }

    /**
     * Get friction factor
     *
     * @return the friction factor
     */
    public double getFrictionFactor() {
        return frictionFactor;
    }

    /**
     * Get the reaction rate
     *
     * @return the reaction rate
     */
    public double getReactionRate() {
        return reactionRate;
    }

    /**
     * Get the quality
     *
     * @return the quality
     */
    public double getQuality() {
        return quality;
    }

    /**
     * Get the status
     *
     * @return the status
     */
    public @NotNull Status getStatus() {
        return status;
    }
}
