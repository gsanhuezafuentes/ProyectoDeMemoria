package model.epanet.element;

import exception.InputException;
import model.epanet.element.networkcomponent.*;
import model.epanet.element.networkdesign.Backdrop;
import model.epanet.element.networkdesign.Label;
import model.epanet.element.optionsreport.Option;
import model.epanet.element.optionsreport.QualityOption;
import model.epanet.element.optionsreport.Report;
import model.epanet.element.optionsreport.Time;
import model.epanet.element.systemoperation.*;
import model.epanet.element.waterquality.ReactionOption;
import model.epanet.io.InpParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public final class Network {
    //*********************************************************
    //Network Components
    //*********************************************************/

    @NotNull
    private StringBuilder title;

    @NotNull
    private final Map<String, Link> linkMap;
    @NotNull
    private final Map<String, Node> nodesMap;

    @NotNull
    private final List<Junction> junctionList;
    @NotNull
    private final List<Reservoir> reservoirList;
    @NotNull
    private final List<Tank> tankList;

    @NotNull
    private final List<Pipe> pipeList;
    @NotNull
    private final List<Pump> PumpList;
    @NotNull
    private final List<Valve> valveList;

    //*********************************************************
    //System Operation
    //*********************************************************/
    @Nullable
    private Map<String, Curve> curveMap;
    @Nullable
    private Map<String, Pattern> patternMap;

    @Nullable
    private Control control;
    @Nullable
    private EnergyOption energy;
    @Nullable
    private Rule rule;

    //*********************************************************
    //Water Quality
    //*********************************************************/
    @Nullable
    private ReactionOption reactionOption;

    //*********************************************************
    //* Options and Reports
    //*********************************************************/

    @Nullable
    private Option option;
    @Nullable
    private QualityOption qualityOption;
    @Nullable
    private Time time;
    @Nullable
    private Report report;
    //*********************************************************
    //Network Design
    //*********************************************************/
    @Nullable
    private Backdrop backdrop;
    @Nullable
    private List<Label> labels;

    public Network() {
        this.title = new StringBuilder();
        this.nodesMap = new HashMap<>();
        this.linkMap = new HashMap<>();
        this.junctionList = new ArrayList<>();
        this.reservoirList = new ArrayList<>();
        this.tankList = new ArrayList<>();
        this.pipeList = new ArrayList<>();
        this.PumpList = new ArrayList<>();
        this.valveList = new ArrayList<>();
    }

    /**
     * Copy constructor. Create a copy of the network received. This method create a
     * copy totally independent of the original network, i.e., a deep copy.
     *
     * @param network the network object to copy
     * @throws NullPointerException if network is null
     */
    public Network(@NotNull Network network) {
        this();
        Objects.requireNonNull(network);

        // copy the title
        this.title = new StringBuilder(network.getTitle());

        // copy the curves
        for (Curve curve : network.getCurves()) {
            Curve copy = curve.copy();
            addCurve(copy.getId(), copy);
        }

        // copy the pattern
        for (Pattern pattern : network.getPatterns()) {
            Pattern copy = pattern.copy();
            addPattern(copy.getId(), copy);
        }

        // copy the nodes
        for (String nodeKey : network.nodesMap.keySet()) {
            Node node = network.nodesMap.get(nodeKey).copy();
            addNode(nodeKey, node);
        }

        // copy the links and replace the references
        for (String linkKey : network.linkMap.keySet()) {
            Link link = network.linkMap.get(linkKey).copy();
            addLink(linkKey, link);
            // Replace the nodes in link for the nodes created for this copy of network
            Node node1 = link.getNode1();
            if (node1 != null){
                Node node = getNode(node1.getId());
                /*
                 * As node already has been copy from the original object the method getNode
                 * should not return null
                 */
                assert node != null;
                link.setNode1(node);
            }
            Node node2 = link.getNode2();
            if (node2 != null) {
                Node node = getNode(node2.getId());
                /*
                 * As node already has been copy from the original object the method getNode
                 * should not return null
                 */
                assert node != null;
                link.setNode2(node);
            }
        }

        // copy backdrop
        Backdrop backdrop = network.getBackdrop();
        if (backdrop != null) {
            setBackdrop(backdrop.copy());
        }

        // copy label
        for (Label label : network.getLabels()) {
            Label copy = label.copy();
            addLabel(copy);
        }

        // copy option
        Option option = network.getOption();
        if (option != null) {
            setOption(option.copy());
        }

        // copy option
        QualityOption qualityOption = network.getQualityOption();
        if (qualityOption != null) {
            setQualityOption(qualityOption.copy());
        }

        // copy report
        Report report = network.getReport();
        if (report != null) {
            setReport(report.copy());
        }

        // copy time
        Time time = network.getTime();
        if (time != null) {
            setTime(time.copy());
        }

        // copy control
        Control control = network.getControl();
        if (control != null) {
            setControl(control.copy());
        }

        // copy energy

        EnergyOption energy = network.getEnergyOption();
        if (energy != null) {
            setEnergyOption(energy.copy());
        }

        // copy rule
        Rule rule = network.getRule();
        if (rule != null) {
            setRule(rule.copy());
        }


        // copy reaction
        ReactionOption reaction = network.getReactionOption();
        if (reaction != null) {
            setReactionOption(reaction.copy());
        }

    }

    public boolean isEmpty() {
        return linkMap.size() + nodesMap.size() == 0;
    }

    //********************************************************
    //Network Components
    //********************************************************

    /**
     * Get the title
     *
     * @return the title
     */
    public @NotNull String getTitle() {
        return title.toString();
    }

    /**
     * Set a title for this network
     *
     * @param text The title for this network
     * @throws NullPointerException if text is null
     */
    public void addLineToTitle(@NotNull String text) {
        Objects.requireNonNull(text);
        title.append(text);
    }

    /**
     * Add a Link to network. It can be a Pipe, pump or valve.
     *
     * @param id   the id link
     * @param link link element
     * @throws NullPointerException if id or link is null
     */
    public void addLink(@NotNull String id, @NotNull Link link) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(link);

        this.linkMap.put(id, link);
        if (link instanceof Pipe) {
            this.pipeList.add((Pipe) link);
        } else if (link instanceof Pump) {
            this.PumpList.add((Pump) link);
        } else {
            this.valveList.add((Valve) link);
        }
    }

    /**
     * Add a node to network. It can be a Junction, Reservoir or Tank.
     *
     * @param id   id node
     * @param node node element
     * @throws NullPointerException if id or node is null
     */
    public void addNode(@NotNull String id, @NotNull Node node) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(node);
        this.nodesMap.put(id, node);
        if (node instanceof Junction) {
            this.junctionList.add((Junction) node);
        } else if (node instanceof Reservoir) {
            this.reservoirList.add((Reservoir) node);
        } else {
            this.tankList.add((Tank) node);
        }
    }

    /**
     * Return a node by id
     *
     * @param id id of the node
     * @return The node or null if it doesn't exist
     */
    public @Nullable Node getNode(String id) {
        return this.nodesMap.get(id);
    }

    /**
     * Return a link by id
     *
     * @param id id of the link
     * @return The link or node if it doesn't exist
     */
    public @Nullable Link getLink(String id) {
        return this.linkMap.get(id);
    }

    /**
     * Get a junction by id.
     *
     * @param id id of junction.
     * @return Junction or null if it doesn't exist.
     */
    public @Nullable Junction getJunction(String id) {
        return (Junction) this.nodesMap.get(id);
    }

    /**
     * Get a reservoir by id.
     *
     * @param id id of reservoir.
     * @return Reservoir or null if it doesn't exist.
     */
    public @Nullable Reservoir getReservoir(String id) {
        return (Reservoir) this.nodesMap.get(id);
    }

    /**
     * Get a tank by id.
     *
     * @param id id of tank.
     * @return Tank or null if it doesn't exist.
     */
    public @Nullable Tank getTank(String id) {
        return (Tank) this.nodesMap.get(id);
    }

    /**
     * Get a pipe by id.
     *
     * @param id id of pipe.
     * @return Pipe or null if it doesn't exist.
     */
    public @Nullable Pipe getPipe(String id) {
        return (Pipe) this.linkMap.get(id);
    }

    /**
     * Get a pump by id.
     *
     * @param id id of pump.
     * @return Pump or null if it doesn't exist.
     */
    public @Nullable Pump getPump(String id) {
        return (Pump) this.linkMap.get(id);
    }

    /**
     * Get a valve by id.
     *
     * @param id id of valve.
     * @return Valve or null if it doesn't exist.
     */
    public @Nullable Valve getValve(String id) {
        return (Valve) this.linkMap.get(id);
    }

    /**
     * Get all links no matter what they are. <br>
     * The collection is unmodifiable. The elements of the collection are in the
     * order in which they were added
     *
     * @return the links
     */
    public @NotNull Collection<Link> getLinks() {
        return linkMap.values();
    }

    /**
     * Get all nodes no matter what they are <br>
     * The collection is unmodifiable. The elements of the collection are in the
     * order in which they were added
     *
     * @return the nodes
     */
    public @NotNull Collection<Node> getNodes() {
        return nodesMap.values();
    }

    /**
     * Get a unmodifiable list only with junction.
     *
     * @return the junctions
     */
    public @NotNull List<Junction> getJunctions() {
        return Collections.unmodifiableList(junctionList);
    }

    /**
     * Get a unmodifiable list only with reservoir.
     *
     * @return the reservoirs
     */
    public @NotNull List<Reservoir> getReservoirs() {
        return Collections.unmodifiableList(reservoirList);
    }

    /**
     * Get a unmodifiable list only with tanks.
     *
     * @return the tanks
     */
    public @NotNull List<Tank> getTanks() {
        return Collections.unmodifiableList(tankList);
    }

    /**
     * Get a unmodifiable list only with pipes.
     *
     * @return the pipes
     */
    public @NotNull List<Pipe> getPipes() {
        return Collections.unmodifiableList(pipeList);
    }

    /**
     * Get a unmodifiable list only with pump.
     *
     * @return the pumps
     */
    public @NotNull List<Pump> getPumps() {
        return Collections.unmodifiableList(PumpList);
    }

    /**
     * Get a unmodifiable list only with valve.
     *
     * @return the valves
     */
    public @NotNull List<Valve> getValves() {
        return Collections.unmodifiableList(valveList);
    }

    //********************************************************
    //System Operation
    //********************************************************

    /**
     * Get a curve by id
     *
     * @param id the curve id
     * @return the curve or null if not exist
     * @throws NullPointerException if id is null
     */
    public @Nullable Curve getCurve(@NotNull String id) {
        Objects.requireNonNull(id);
        checkIfExistOrCreateCurves();
        assert this.curveMap != null;
        return this.curveMap.get(id);
    }

    /**
     * Add a curve to the network.
     *
     * @param id    the curve id
     * @param curve the curve to add.
     * @throws NullPointerException if id or curve is null
     */
    public void addCurve(@NotNull String id, @NotNull Curve curve) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(curve);

        checkIfExistOrCreateCurves();
        assert this.curveMap != null;
        this.curveMap.put(id, curve);
    }

    /**
     * Get a unmodifiable list with curves.
     *
     * @return the curveList
     */
    public @NotNull Collection<Curve> getCurves() {
        checkIfExistOrCreateCurves();
        assert curveMap != null;
        return Collections.unmodifiableCollection(curveMap.values());
    }

    private void checkIfExistOrCreateCurves() {
        if (this.curveMap == null) {
            this.curveMap = new LinkedHashMap<>();
        }
    }

    /**
     * Get the pattern by id.
     *
     * @param id the pattern id
     * @return the pattern or null if not exist
     * @throws NullPointerException if id is null
     */
    public Pattern getPattern(@NotNull String id) {
        Objects.requireNonNull(id);
        checkIfExistOrCreatePattern();
        assert this.patternMap != null;
        return this.patternMap.get(id);
    }

    /**
     * Add a pattern to the network.
     *
     * @param id      the id of pattern
     * @param pattern the pattern to add
     * @throws NullPointerException if id or pattern is null
     */
    public void addPattern(@NotNull String id, @NotNull Pattern pattern) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(pattern);

        checkIfExistOrCreatePattern();
        assert this.patternMap != null;
        this.patternMap.put(id, pattern);
    }

    /**
     * Get a unmodifiable list with patterns
     *
     * @return the patternList
     */
    public @NotNull Collection<Pattern> getPatterns() {
        checkIfExistOrCreatePattern();
        assert patternMap != null;
        return Collections.unmodifiableCollection(patternMap.values());
    }

    private void checkIfExistOrCreatePattern() {
        if (this.patternMap == null) {
            this.patternMap = new LinkedHashMap<>();
        }
    }

    /**
     * Get the control
     *
     * @return the control or null if not exist
     */
    public @Nullable Control getControl() {
        return control;
    }

    /**
     * Set the control
     *
     * @param control the control to set or null if not exist
     */
    public void setControl(@Nullable Control control) {
        this.control = control;
    }


    /**
     * Get the energy
     *
     * @return the energy or null if not exist
     */
    public @Nullable EnergyOption getEnergyOption() {
        return energy;
    }

    /**
     * Set the energy
     *
     * @param energy the energy to set or null if not exist
     */
    public void setEnergyOption(EnergyOption energy) {
        this.energy = energy;
    }

    /**
     * Get the rule
     *
     * @return the rule or null if not exist
     */
    public @Nullable Rule getRule() {
        return rule;
    }

    /**
     * Set the rule
     *
     * @param rule the rule to set or null if not exist
     */
    public void setRule(@Nullable Rule rule) {
        this.rule = rule;
    }

    //********************************************************
    //Water Quality
    //********************************************************

    /**
     * Get the reaction configurations.
     *
     * @return the reaction or null if not exist
     */
    public @Nullable ReactionOption getReactionOption() {
        return reactionOption;
    }

    /**
     * Set the reaction configurations.
     *
     * @param reaction the reaction to set or null if not exist
     */
    public void setReactionOption(@Nullable ReactionOption reaction) {
        this.reactionOption = reaction;
    }

    //********************************************************
    // Options and Reports
    //********************************************************

    /**
     * Get the option configuration.
     *
     * @return the option or null if not exist
     */
    public @Nullable Option getOption() {
        return option;
    }

    /**
     * Set the option configuration.
     *
     * @param option the option to set or null if not exist
     */
    public void setOption(@Nullable Option option) {
        this.option = option;
    }

    /**
     * Get the option configuration.
     *
     * @return the option or null if not exist
     */
    public @Nullable QualityOption getQualityOption() {
        return this.qualityOption;
    }

    /**
     * Set the option configuration.
     *
     * @param option the option to set or null if not exist
     */
    public void setQualityOption(@Nullable QualityOption option) {
        this.qualityOption = option;
    }

    /**
     * Get the time configuration or null if not exist.
     *
     * @return the time or null if not exist
     */
    public @Nullable Time getTime() {
        return time;
    }

    /**
     * Set the time configuration.
     *
     * @param time the time to set or null if not exist
     */
    public void setTime(@Nullable Time time) {
        this.time = time;
    }

    /**
     * Get the report configuration or null if not exist.
     *
     * @return the report
     */
    public @Nullable Report getReport() {
        return report;
    }

    /**
     * Set report configuration
     *
     * @param report the report to set or null if not exist
     */
    public void setReport(@Nullable Report report) {
        this.report = report;
    }

    //********************************************************
    // Network Design
    //********************************************************

    /**
     * Get the backdrop or null if not exist.
     *
     * @return the backdrop
     */
    public @Nullable Backdrop getBackdrop() {
        return backdrop;
    }

    /**
     * Set the backdrop.
     *
     * @param backdrop the backdrop to set
     */
    public void setBackdrop(@Nullable Backdrop backdrop) {
        this.backdrop = backdrop;
    }

    /**
     * Get a unmodifiable list with labels
     *
     * @return the labels
     */
    public @NotNull List<Label> getLabels() {
        checkLabel();
        assert labels != null;
        return Collections.unmodifiableList(labels);
    }

    /**
     * Add a label to network
     *
     * @param label the label to add
     * @throws NullPointerException if label is null
     */
    public void addLabel(@NotNull Label label) {
        Objects.requireNonNull(label);
        checkLabel();
        assert this.labels != null;
        this.labels.add(label);
    }

    /**
     * Create the list of labels if it doesn't exist
     */
    private void checkLabel() {
        if (this.labels == null) {
            this.labels = new ArrayList<>();
        }
    }

    @Override
    public @NotNull String toString() {
        StringBuilder text = new StringBuilder();
        text.append("[TITLE]");
        text.append(getTitle());
        text.append("\n");

        text.append("[JUNCTION]");
        text.append("\n");
        for (Junction junction : getJunctions()) {
            text.append(junction.toString()).append("\n");
        }
        text.append("\n");

        text.append("[RESERVOIR]");
        text.append("\n");
        for (Reservoir reservoir : getReservoirs()) {
            text.append(reservoir.toString()).append("\n");
        }
        text.append("\n");

        text.append("[TANK]");
        text.append("\n");
        for (Tank tank : getTanks()) {
            text.append(tank.toString()).append("\n");
        }
        text.append("\n");

        text.append("[PIPE]");
        text.append("\n");
        for (Pipe pipe : getPipes()) {
            text.append(pipe.toString()).append("\n");
        }
        text.append("\n");

        text.append("[PUMP]");
        text.append("\n");
        for (Pump pump : getPumps()) {
            text.append(pump.toString()).append("\n");
        }
        text.append("\n");

        text.append("[VALVE]");
        text.append("\n");
        for (Valve valve : getValves()) {
            text.append(valve.toString()).append("\n");
        }
        text.append("\n");

        text.append("[CONTROL]");
        text.append("\n");
        Control control = getControl();
        if (control != null) {
            text.append(control.toString());
        }

        text.append("\n");

        text.append("[CURVE]");
        text.append("\n");

        for (Curve curve : getCurves()) {
            text.append(curve.toString()).append("\n");
        }
        text.append("\n");

        text.append("[ENERGY]");
        text.append("\n");
        if (getEnergyOption() != null) {
            text.append(getEnergyOption().toString()).append("\n");
        }
        text.append("\n");

        text.append("[PATTERN]");
        text.append("\n");
        for (Pattern pattern : getPatterns()) {
            text.append(pattern.toString()).append("\n");
        }
        text.append("\n");

        text.append("[RULE]");
        text.append("\n");
        Rule rule = getRule();
        if (rule != null) {
            text.append(rule.getCode()).append("\n");
        }
        text.append("\n");

        text.append("[REACTION]");
        text.append("\n");
        if (getReactionOption() != null) {
            text.append(getReactionOption().toString()).append("\n");
        }
        text.append("\n");


        text.append("[OPTION]");
        if (getOption() != null) {
            text.append(getOption().toString()).append("\n");
        }
        text.append("\n");

        text.append("[QUALITYOPTION]");
        text.append("\n");
        if (getQualityOption() != null) {
            text.append(getQualityOption().toString()).append("\n");
        }
        text.append("\n");

        text.append("[REPORT]");
        text.append("\n");
        if (getReport() != null) {
            text.append(getReport().getCode()).append("\n");
        }
        text.append("\n");

        text.append("[TIME]");
        text.append("\n");
        if (getTime() != null) {
            text.append(getTime().toString()).append("\n");
        }
        text.append("\n");

        text.append("[BACKDROP]");
        text.append("\n");
        if (getBackdrop() != null) {
            text.append(getBackdrop().toString()).append("\n");
        }
        text.append("\n");

        text.append("[LABELS]");
        text.append("\n");
        for (Label label : this.getLabels()) {
            text.append(label.toString()).append("\n");
        }
        text.append("\n");

        return text.toString();
    }

    /**
     * Create a independent copy of this network
     *
     * @return the copy
     */
    public @NotNull Network copy() {
        return new Network(this);
    }

    public static void main(String[] args) {
        InpParser parser = new InpParser();
        try {
            Network net = parser.parse(new Network(), "inp\\test.inp");
//            Network net = parser.parse(new Network(), "inp\\hanoi-Frankenstein.INP");
//            Network net = parser.parse(new Network(), "inp\\vanzylOriginal.inp");
            System.out.println(net.toString());
        } catch (IOException | InputException e) {
            e.printStackTrace();
        }
    }
}
