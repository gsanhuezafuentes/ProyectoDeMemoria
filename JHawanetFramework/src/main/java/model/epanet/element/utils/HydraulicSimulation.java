package model.epanet.element.utils;

import epanet.core.*;
import exception.ApplicationException;
import model.epanet.element.Network;
import model.epanet.element.networkcomponent.Link;
import model.epanet.element.networkcomponent.Node;
import model.epanet.element.result.LinkSimulationResult;
import model.epanet.element.result.NodeSimulationResult;
import model.epanet.io.InpParser;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class realize a total simulation with the predefined configuration of inp file indicated
 * in inpPath parameter of {@link #run} method. When the simulation is finished, the results will
 * be added to the network object passed to the method.
 */
public final class HydraulicSimulation {

    private HydraulicSimulation() {
    }

    /**
     * Run the total simulation and save result in the network object
     *
     * @param inpPath the inp file
     * @param network The network object corresponding to the inp file
     * @throws ApplicationException if there is a error when DLL is loaded
     * @throws EpanetException      if there is an error in simulation
     */
    public static void run(String inpPath, Network network) throws ApplicationException, EpanetException {
        try {
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

            int nodeCount = epanet.ENgetcount(Components.EN_NODECOUNT);
            int linkCount = epanet.ENgetcount(Components.EN_LINKCOUNT);

            //long t, tstep;
            long[] tstep = {1};
            long[] t = {0};

            epanet.ENopenH();
            epanet.ENinitH(0);
            do {
                epanet.ENrunH(t);
                if (t[0] % rtstep == 0 && t[0] >= rtstart) {
                    long hour = t[0] / (3600);
                    long minute = (t[0] - (3600 * hour)) / 60;
                    long second = t[0] - ((3600 * hour) + (minute * 60));
//                    System.out.printf("%02d:%02d:%02d\n", hour, minute, second);

                    // performance?
                    for (int i = 1; i <= nodeCount; i++) {
                        String nodeId = epanet.ENgetnodeid(i);
                        Node node = network.getNode(nodeId);
                        assert node != null;
                        List<NodeSimulationResult> simulationResults = node.getSimulationResults();
                        if (simulationResults.isEmpty()) {
                            simulationResults = new ArrayList<>();
                            node.setSimulationResults(simulationResults);
                        }
                        simulationResults.add(getNodeResult(t[0], i, epanet));
                    }
                    for (int i = 1; i <= linkCount; i++) {
                        String linkId = epanet.ENgetlinkid(i);
                        Link link = network.getLink(linkId);
                        assert link != null;
                        List<LinkSimulationResult> simulationResults = link.getSimulationResults();
                        if (simulationResults.isEmpty()) {
                            simulationResults = new ArrayList<>();
                            link.setSimulationResults(simulationResults);
                        }
                        simulationResults.add(getLinkResult(t[0], i, epanet));
                    }
                }
                epanet.ENnextH(tstep);
            } while (tstep[0] > 0);

            epanet.ENcloseH();
//            System.out.println("n element " + numberOfElement);
            epanet.ENclose();
        } catch (URISyntaxException e) {
            throw new ApplicationException(e);
        }
    }

    /**
     * Get the node result of the simulation
     *
     * @param timeInSeconds the time in second
     * @param index         the index of node
     * @param epanet        the simulation engine
     * @return the result of the simulation
     */
    private static NodeSimulationResult getNodeResult(long timeInSeconds, int index, EpanetAPI epanet) throws EpanetException {
        final double demand = epanet.ENgetnodevalue(index, NodeParameters.EN_DEMAND);
        final double head = epanet.ENgetnodevalue(index, NodeParameters.EN_HEAD);
        final double pressure = epanet.ENgetnodevalue(index, NodeParameters.EN_PRESSURE);
        final double quality = epanet.ENgetnodevalue(index, NodeParameters.EN_QUALITY);
        return new NodeSimulationResult(timeInSeconds, demand, head, pressure, quality);
    }

    /**
     * Get the link result of the simulation
     *
     * @param timeInSeconds the time in second
     * @param index         the index of link
     * @param epanet        the simulation engine
     * @return the result of the simulation
     */
    private static LinkSimulationResult getLinkResult(long timeInSeconds, int index, EpanetAPI epanet) throws EpanetException {
        final float flow = epanet.ENgetlinkvalue(index, LinkParameters.EN_FLOW)[0];
        final float velocity = epanet.ENgetlinkvalue(index, LinkParameters.EN_VELOCITY)[0];
        final float headloss = epanet.ENgetlinkvalue(index, LinkParameters.EN_HEADLOSS)[0];
        final String status = epanet.ENgetlinkvalue(index, LinkParameters.EN_STATUS)[0] == 1 ? "OPEN" : "CLOSE";
        System.out.println(epanet.ENgetlinkvalue(index, LinkParameters.EN_MINORLOSS)[0]);
        return new LinkSimulationResult(timeInSeconds, flow, velocity, headloss, 0, 0, 0, LinkSimulationResult.Status.parse(status));
    }

    /**
     * A method to test the time of a timestep
     *
     * @param time the time in seconds
     * @return the time as string
     */
    private static String timeToStringTime(long time) {
        // Transform the seconds in a time in formar HH:mm:ss
        double hour = time / (60.0 * 60.0); //transform to double
        // the % 1 (mod 1) extract the decimal part of the hour and multiply it by 60 to get the minutes
        double minute = (hour % 1) * 60.0;
        // the % 1 (mod 1) extract the decimal part of the minute and multiply it by 60 to get the minutes
        long second = Math.round((minute % 1) * 60);
        return String.format("%02.0f:%02.0f:%d", Math.floor(hour), Math.floor(minute), second);
    }

    public static void main(String[] args) {
        String inpPath = "inp/vanzylOriginal.inp";
//        String inpPath = "inp/hanoi-Frankenstein.INP";
        Network network = new Network();
        try {
            InpParser inpParser = new InpParser();
            inpParser.parse(network, inpPath);
            HydraulicSimulation.run(inpPath, network);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
