package model.epanet.element.result;

/**
 * This class is used to store the result value of a simulation for the nodes.
 */
public final class NodeSimulationResult extends ResultSimulation {
    private final double demand;
    private final double head;
    private final double pressure;
    private final double quality;

    /**
     * Constructor to save the result of simulation for a node
     *
     * @param timeInSeconds the simulation time in seconds
     * @param demand        the demand for a Junction or the net inflow for a Reservoir or Tank
     * @param head          the total head for a Junction or the elevation for a Reservoir or Tank
     * @param pressure      the pressure value
     * @param quality       the quality value
     */
    public NodeSimulationResult(long timeInSeconds, double demand, double head, double pressure, double quality) {
        super(timeInSeconds);
        this.demand = demand;
        this.head = head;
        this.pressure = pressure;
        this.quality = quality;
    }

    /**
     * Get the actual demand for a Junction or the net inflow for a Reservoir or Tank
     *
     * @return the demand or net inflow of a node
     */
    public double getDemand() {
        return demand;
    }

    /**
     * Get the total head for a Junction or the elevation for a Reservoir or Tank
     *
     * @return the head or elevation of a node
     */
    public double getHead() {
        return head;
    }

    /**
     * Get the pressure for a node
     *
     * @return the pressure of a node
     */
    public double getPressure() {
        return pressure;
    }

    /**
     * Get the quality for a node
     *
     * @return the quality of a node
     */
    public double getQuality() {
        return quality;
    }
}
