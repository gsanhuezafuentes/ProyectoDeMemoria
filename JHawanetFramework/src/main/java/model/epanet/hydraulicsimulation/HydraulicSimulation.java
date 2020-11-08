package model.epanet.hydraulicsimulation;

import controller.HydraulicSimulationResultWindowController;
import epanet.core.*;
import exception.ApplicationException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.*;

/**
 * This class realize a total simulation with the predefined configuration of inp file indicated
 * in inpPath parameter of {@link #run} method. When the simulation is finished, the results will
 * be added to the network object passed to the method.
 */
public final class HydraulicSimulation {
    private static final Logger LOGGER = LoggerFactory.getLogger(HydraulicSimulation.class);

    private final String inpPath;
    private List<String> times;
    private List<List<NodeSimulationResult>> nodeResults; // row: nodos, column: time
    private List<List<LinkSimulationResult>> linkResults; // row: nodos, column: time
    private Map<String, Integer> nodeIndex; // map id of node to index in nodeResult
    private Map<String, Integer> linkIndex; // map id of link to index in linkResult


    private HydraulicSimulation(String inpPath) {
        this.inpPath = inpPath;
    }

    /**
     * Run the total simulation and save result in the network object.
     *
     * @param inpPath the inp file.
     * @throws NullPointerException     if inpPath is null.
     * @throws IllegalArgumentException if inpPath is a empty string.
     * @throws ApplicationException     if there is a error when DLL is loaded.
     * @throws EpanetException          if there is an error in simulation.
     *
     * @return the hydralucsimulation instance with that store the result of simulation.
     */
    public static @NotNull HydraulicSimulation run(@NotNull String inpPath) throws ApplicationException, EpanetException {
        Objects.requireNonNull(inpPath);
        if (inpPath.isEmpty()) throw new IllegalArgumentException("inpPath can not be an empty string");

        HydraulicSimulation instance = new HydraulicSimulation(inpPath);
        instance.run();
        return instance;
    }

    /**
     * Initialize the list of nodeResult.
     *
     * @param nelement number of elements in list.
     */
    private void initializeNodeList(int nelement) {
        this.nodeIndex = new HashMap<>(nelement);
        this.nodeResults = new ArrayList<>(nelement);
        for (int i = 0; i < nelement; i++) {
            this.nodeResults.add(new ArrayList<>());
        }
    }

    /**
     * Initialize the list of linkResult.
     *
     * @param nelement number of element in list.
     */
    private void initializeLinkList(int nelement) {
        this.linkIndex = new HashMap<>(nelement);
        this.linkResults = new ArrayList<>(nelement);
        for (int i = 0; i < nelement; i++) {
            this.linkResults.add(new ArrayList<>());
        }
    }

    /**
     * Initialize the hour list
     *
     * @param nelement number of elements in list
     */
    private void initializeTimesList(int nelement) {
        this.times = new ArrayList<>(nelement);
    }

    /**
     * Run the simulation.
     * @throws ApplicationException If there is a error with URI Syntax.
     * @throws EpanetException if there is a error in simulation.
     */
    private void run() throws ApplicationException, EpanetException {
        try {
            LOGGER.debug("Open epanet with {} network", inpPath);
            EpanetAPI epanet = new EpanetAPI();
            epanet.ENopen(inpPath, "defaultSimulation.rpt", "");

            long duration = epanet.ENgettimeparam(TimeParameterCodes.DURATION);
            long rtstep = epanet.ENgettimeparam(TimeParameterCodes.REPORTSTEP);
            long rtstart = epanet.ENgettimeparam(TimeParameterCodes.REPORTSTART);
//            System.out.println("Duration " + duration);
//            System.out.println("Report Step " + rtstep);
//            System.out.println("Report start " + rtstart);

            if (rtstart > duration) {
                // if report start time is greater than duration set report start time to 0
                rtstart = 0;
            }
            long numberOfElement = (duration - rtstart) / rtstep + 1; //the number of element that have to be retrieved
            LOGGER.debug("The hydraulic simulation will have {} periods.", numberOfElement);

            int nodeCount = epanet.ENgetcount(Components.EN_NODECOUNT);
            int linkCount = epanet.ENgetcount(Components.EN_LINKCOUNT);

            // initialize the list where result are saved
            initializeTimesList((int) numberOfElement);
            initializeNodeList(nodeCount);
            initializeLinkList(linkCount);

            //long t, tstep;
            long[] tstep = {1};
            long[] t = {0};

            epanet.ENopenH();
            epanet.ENinitH(0);
            do {
                epanet.ENrunH(t);
                if (t[0] % rtstep == 0 && t[0] >= rtstart) {
//                    System.out.printf(timeToStringTime(t[0]));
                    times.add(timeToStringTime(t[0]));

                    for (int i = 1; i <= nodeCount; i++) {
                        NodeSimulationResult result = getNodeResult(t[0], i, epanet);
                        // add to the node list the result in a specific time
                        this.nodeResults.get(i - 1).add(result);
                        // save in map the index correspondent to result of a specific node.
                        if (!nodeIndex.containsKey(result.getId())) {
                            nodeIndex.put(result.getId(), i - 1);
                        }
                    }
                    for (int i = 1; i <= linkCount; i++) {
                        LinkSimulationResult result = getLinkResult(t[0], i, epanet);
                        this.linkResults.get(i - 1).add(result);
                        if (!linkIndex.containsKey(result.getId())) {
                            linkIndex.put(result.getId(), i - 1);
                        }
                    }
                }
                epanet.ENnextH(tstep);
            } while (tstep[0] > 0);

            epanet.ENcloseH();
//            System.out.println("n element " + numberOfElement);
            epanet.ENclose();
            LOGGER.debug("Closing epanet.", inpPath);

        } catch (URISyntaxException e) {
            throw new ApplicationException("There is a error with EpanetToolkit.",e);
        }
    }

    /**
     * Get the node result of the simulation
     *
     * @param timeInSeconds the time in second
     * @param index         the index of node
     * @param epanet        the simulation engine
     * @return the result of the simulation
     * @throws NullPointerException if epanet is null
     */
    private @NotNull NodeSimulationResult getNodeResult(long timeInSeconds, int index, @NotNull EpanetAPI epanet) throws EpanetException {
        Objects.requireNonNull(epanet);
        String nodeId = epanet.ENgetnodeid(index);
        final double demand = epanet.ENgetnodevalue(index, NodeParameters.EN_DEMAND);
        final double head = epanet.ENgetnodevalue(index, NodeParameters.EN_HEAD);
        final double pressure = epanet.ENgetnodevalue(index, NodeParameters.EN_PRESSURE);
        final double quality = epanet.ENgetnodevalue(index, NodeParameters.EN_QUALITY);
        return new NodeSimulationResult(nodeId, timeInSeconds, demand, head, pressure, quality);
    }

    /**
     * Get the link result of the simulation.
     *
     * @param timeInSeconds the time in second.
     * @param index         the index of link.
     * @param epanet        the simulation engine.
     * @return the result of the simulation.
     * @throws NullPointerException if epanet is null.
     */
    private @NotNull LinkSimulationResult getLinkResult(long timeInSeconds, int index, @NotNull EpanetAPI epanet) throws EpanetException {
        String linkId = epanet.ENgetlinkid(index);
        final float flow = epanet.ENgetlinkvalue(index, LinkParameters.EN_FLOW)[0];
        final float velocity = epanet.ENgetlinkvalue(index, LinkParameters.EN_VELOCITY)[0];
        final float headloss = epanet.ENgetlinkvalue(index, LinkParameters.EN_HEADLOSS)[0];
        final String status = epanet.ENgetlinkvalue(index, LinkParameters.EN_STATUS)[0] == 1 ? "OPEN" : "CLOSE";
        return new LinkSimulationResult(linkId, timeInSeconds, flow, velocity, headloss, LinkSimulationResult.Status.parse(status));
    }

    /**
     * A method to test the time of a timestep.
     *
     * @param time the time in seconds.
     * @return the time as string.
     */
    private @NotNull String timeToStringTime(long time) {
        // Transform the seconds in a time in formar HH:mm:ss
        double hour = time / (60.0 * 60.0); //transform to double
        // the % 1 (mod 1) extract the decimal part of the hour and multiply it by 60 to get the minutes
        double minute = (hour % 1) * 60.0;
        // the % 1 (mod 1) extract the decimal part of the minute and multiply it by 60 to get the minutes
        long second = Math.round((minute % 1) * 60);
        return String.format("%02.0f:%02.0f:%d", Math.floor(hour), Math.floor(minute), second);
    }

    /**
     * Return the node result of a specific time indicated by parameter time.
     * <p>
     * Available times can be retrieved using the {@link #getTimes} method.
     *
     * @param time the time as string.
     * @return the node results in a specific time or empty list if time is not valid.
     * @throws NullPointerException if time is null.
     */
    public @NotNull List<NodeSimulationResult> getNodeResultsInTime(String time) {
        Objects.requireNonNull(time);
        int indexTime = times.indexOf(time);
        if (indexTime == -1) return Collections.emptyList();

        int numberOfNodes = nodeResults.size();
        List<NodeSimulationResult> result = new ArrayList<>(numberOfNodes);
        for (int i = 0; i < numberOfNodes; i++) {
            result.add(nodeResults.get(i).get(indexTime));
        }
        return result;
    }

    /**
     * Return the link result of a specific time indicated by parameter time.
     * <p>
     * Available times can be retrieved using the {@link #getTimes} method.
     *
     * @param time the time as string.
     * @return the links result in a specific time or empty list if time is not valid.
     * @throws NullPointerException if time is null.
     */
    public @NotNull List<LinkSimulationResult> getLinkResultInTime(String time) {
        Objects.requireNonNull(time);
        int indexTime = times.indexOf(time);
        if (indexTime == -1) return Collections.emptyList();

        int numberOfLink = linkResults.size();

        List<LinkSimulationResult> result = new ArrayList<>(numberOfLink);
        for (int i = 0; i < numberOfLink; i++) {
            result.add(linkResults.get(i).get(indexTime));
        }
        return result;
    }

    /**
     * Get the available times of simulation
     *
     * @return the list of times.
     */
    public @NotNull List<String> getTimes() {
        return times;
    }

    /**
     * Return for a node the result of execution in all times.
     *
     * @param nodeId the id of a node.
     * @return the result of execution for that node in all time.
     * @throws NullPointerException if nodeId is null.
     */
    public @NotNull List<NodeSimulationResult> getTimeSeriesForNode(@NotNull String nodeId) {
        Objects.requireNonNull(nodeId);
        Integer index = this.nodeIndex.get(nodeId);
        if (index != null){
            return this.nodeResults.get(index);
        }
        return Collections.emptyList();
    }

    /**
     * Return for a link the result of execution in all times.
     *
     * @param linkId the id of a link.
     * @return the result of execution for that link in all time.
     * @throws NullPointerException if linkId is null.
     */
    public @NotNull List<LinkSimulationResult> getTimeSeriesForLink(@NotNull String linkId) {
        Objects.requireNonNull(linkId);
        Integer index = this.linkIndex.get(linkId);
        if (index != null) {
            return this.linkResults.get(index);
        }
        return Collections.emptyList();
    }


    /**
     * To test.
     * @param args list of string.
     */
    public static void main(String[] args) {
        String inpPath = "inp/vanzylOriginal.inp";
//        String inpPath = "inp/hanoi-Frankenstein.INP";
        try {
            HydraulicSimulation simulation = HydraulicSimulation.run(inpPath);
            System.out.println(Arrays.toString(simulation.getTimes().toArray()));
//            for (LinkSimulationResult result : simulation.getLinkResultInTime(simulation.getTimes().get(17))) {
//                System.out.println(String.format("%s %f %f %s %f, %s \n",
//                        result.getId(), result.getFlow(), result.getHeadloss(), result.getStatus(),
//                        result.getVelocity(), result.getTimeString()));
//            }

//            for (NodeSimulationResult result : simulation.getNodeResultInTime(simulation.getTimes().get(17))) {
//                System.out.println(String.format("%s %f %f %f %f, %s \n",
//                        result.getId(), result.getDemand(), result.getHead(), result.getPressure(),
//                        result.getQuality(), result.getTimeString()));
//            }
//            for (LinkSimulationResult result : simulation.getTimeSeriesForLink("p10")) {
//                System.out.println(String.format("%s %f %f %s %f, %s \n",
//                        result.getId(), result.getFlow(), result.getHeadloss(), result.getStatus(),
//                        result.getVelocity(), result.getTimeString()));
//            }

//            for (NodeSimulationResult result : simulation.getTimeSeriesForNode("n10")) {
//                System.out.println(String.format("%s %f %f %f %f, %s \n",
//                        result.getId(), result.getDemand(), result.getHead(), result.getPressure(),
//                        result.getQuality(), result.getTimeString()));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
