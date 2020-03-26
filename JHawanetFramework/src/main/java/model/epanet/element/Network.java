package model.epanet.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.epanet.element.networkcomponent.Junction;
import model.epanet.element.networkcomponent.Link;
import model.epanet.element.networkcomponent.Node;
import model.epanet.element.networkcomponent.Pipe;
import model.epanet.element.networkcomponent.Pump;
import model.epanet.element.networkcomponent.Reservoir;
import model.epanet.element.networkcomponent.Tank;
import model.epanet.element.networkcomponent.Valve;
import model.epanet.element.networkdesign.Backdrop;
import model.epanet.element.networkdesign.Label;
import model.epanet.element.optionsreport.Option;
import model.epanet.element.optionsreport.QualityOption;
import model.epanet.element.optionsreport.Report;
import model.epanet.element.optionsreport.Time;
import model.epanet.element.systemoperation.Control;
import model.epanet.element.systemoperation.Curve;
import model.epanet.element.systemoperation.EnergyOption;
import model.epanet.element.systemoperation.Pattern;
import model.epanet.element.systemoperation.Rule;
import model.epanet.element.utils.Point;
import model.epanet.element.waterquality.ReactionOption;

public final class Network {
	/*********************************************************
	 * Network Components
	 *********************************************************/
	private StringBuilder title;

	private Map<String, Link> linkMap;
	private Map<String, Node> nodesMap;

	private List<Junction> junctionList;
	private List<Reservoir> reservoirList;
	private List<Tank> tankList;

	private List<Pipe> pipeList;
	private List<Pump> PumpList;
	private List<Valve> valveList;

	/*********************************************************
	 * System Operation
	 *********************************************************/
	private Map<String, Curve> curveMap;
	private Map<String, Pattern> patternMap;
	
	private Control control;
	private List<Curve> curveList;
	private EnergyOption energy;
	private List<Pattern> patternList;
	private Rule rule;

	/*********************************************************
	 * Water Quality
	 *********************************************************/
	private ReactionOption reactionOption;

	/*********************************************************
	 * Options and Reports
	 *********************************************************/
	private Option option;
	private QualityOption qualityOption;
	private Time time;
	private Report report;
	/*********************************************************
	 * Network Design
	 *********************************************************/
	private Backdrop backdrop;
	private List<Label> labels;

	public Network() {
		this.title = new StringBuilder();
		this.nodesMap = new LinkedHashMap<String, Node>();
		this.linkMap = new LinkedHashMap<String, Link>();
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
	 * @param network
	 */
	public Network(Network network) {
		this();
		// copy the title
		this.title = new StringBuilder(network.getTitle());

		// copy the curves
		for (Curve curve : network.getCurveList()) {
			Curve copy = curve.copy();
			addCurve(copy.getId(), copy);
		}

		// copy the pattern
		for (Pattern pattern : network.getPatternList()) {
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
			link.setNode1(getNode(link.getNode1().getId()));
			link.setNode2(getNode(link.getNode2().getId()));
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

	/*********************************************************
	 * Network Components
	 *********************************************************/

	/**
	 * Get the title
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title.toString();
	}

	/**
	 * Set a title for this network
	 * 
	 * @param text The title for this network
	 */
	public void addLineToTitle(String text) {
		title.append(text);
	}

	/**
	 * Add a Link to network. It can be a Pipe, pump or valve.
	 * 
	 * @param id   the id link
	 * @param link link element
	 */
	public void addLink(String id, Link link) {
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
	 */
	public void addNode(String id, Node node) {
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
	 * @return The node
	 */
	public Node getNode(String id) {
		return this.nodesMap.get(id);
	}

	/**
	 * Return a link by id
	 * 
	 * @param id id of the link
	 * @return The link
	 */
	public Link getLink(String id) {
		return this.linkMap.get(id);
	}

	/**
	 * Get a junction by id.
	 * 
	 * @param id id of junction.
	 * @return Junction.
	 */
	public Junction getJuntion(String id) {
		return (Junction) this.nodesMap.get(id);
	}

	/**
	 * Get a reservoir by id.
	 * 
	 * @param id id of reservoir.
	 * @return Reservoir.
	 */
	public Reservoir getReservoir(String id) {
		return (Reservoir) this.nodesMap.get(id);
	}

	/**
	 * Get a tank by id.
	 * 
	 * @param id id of tank.
	 * @return Tank.
	 */
	public Tank getTank(String id) {
		return (Tank) this.nodesMap.get(id);
	}

	/**
	 * Get a pipe by id.
	 * 
	 * @param id id of pipe.
	 * @return Pipe.
	 */
	public Pipe getPipe(String id) {
		return (Pipe) this.linkMap.get(id);
	}

	/**
	 * Get a pump by id.
	 * 
	 * @param id id of pump.
	 * @return Pump.
	 */
	public Pump getPump(String id) {
		return (Pump) this.linkMap.get(id);
	}

	/**
	 * Get a valve by id.
	 * 
	 * @param id id of valve.
	 * @return Valve.
	 */
	public Valve getValve(String id) {
		return (Valve) this.linkMap.get(id);
	}

	/**
	 * Get all links no matter what they are. <br>
	 * The collection is unmodifiable. The elements of the collection are in the
	 * order in which they were added
	 * 
	 * @return the links
	 */
	public Collection<Link> getLinks() {
		return linkMap.values();
	}

	/**
	 * Get all nodes no matter what they are <br>
	 * The collection is unmodifiable. The elements of the collection are in the
	 * order in which they were added
	 * 
	 * @return the nodes
	 */
	public Collection<Node> getNodes() {
		return nodesMap.values();
	}

	/**
	 * Get a unmodifiable list only with junction.
	 * 
	 * @return the junctions
	 */
	public List<Junction> getJunctions() {
		return Collections.unmodifiableList(junctionList);
	}

	/**
	 * Get a unmodifiable list only with reservoir.
	 * 
	 * @return the reservoirs
	 */
	public List<Reservoir> getReservoirs() {
		return Collections.unmodifiableList(reservoirList);
	}

	/**
	 * Get a unmodifiable list only with tanks.
	 * 
	 * @return the tanks
	 */
	public List<Tank> getTanks() {
		return Collections.unmodifiableList(tankList);
	}

	/**
	 * Get a unmodifiable list only with pipes.
	 * 
	 * @return the pipes
	 */
	public List<Pipe> getPipes() {
		return Collections.unmodifiableList(pipeList);
	}

	/**
	 * Get a unmodifiable list only with pump.
	 * 
	 * @return the pumps
	 */
	public List<Pump> getPumps() {
		return Collections.unmodifiableList(PumpList);
	}

	/**
	 * Get a unmodifiable list only with valve.
	 * 
	 * @return the valves
	 */
	public List<Valve> getValves() {
		return Collections.unmodifiableList(valveList);
	}
	
	/*********************************************************
	 * System Operation
	 *********************************************************/

	/**
	 * Get a curve by id
	 * 
	 * @param id the curve id
	 * @return the curve or null if not exist
	 */
	public Curve getCurve(String id) {
		if (this.curveMap == null) {
			this.curveMap = new HashMap<String, Curve>();
		}
		if (this.curveList == null) {
			this.curveList = new ArrayList<Curve>();
		}
		return this.curveMap.get(id);
	}

	/**
	 * Add a curve to the network.
	 * 
	 * @param id    the curve id
	 * @param curve the curve to add.
	 */
	public void addCurve(String id, Curve curve) {
		if (this.curveMap == null) {
			this.curveMap = new HashMap<String, Curve>();
		}
		if (this.curveList == null) {
			this.curveList = new ArrayList<Curve>();
		}
		this.curveMap.put(id, curve);
		this.curveList.add(curve);
	}

	/**
	 * Get the pattern by id.
	 * 
	 * @param id the pattern id
	 * @return the pattern or null if not exist
	 */
	public Pattern getPattern(String id) {
		return this.patternMap.get(id);
	}

	/**
	 * Add a pattern to the network.
	 * 
	 * @param id      the id of pattern
	 * @param pattern the pattern to add
	 */
	public void addPattern(String id, Pattern pattern) {
		if (this.patternMap == null) {
			this.patternMap = new HashMap<String, Pattern>();
		}
		if (this.patternList == null) {
			this.patternList = new ArrayList<Pattern>();
		}
		this.patternMap.put(id, pattern);
		this.patternList.add(pattern);
	}

	/**
	 * Get the control
	 * @return the control
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Set the control
	 * @param control the control to set
	 */
	public void setControl(Control control) {
		this.control = control;
	}

	/**
	 * Get a unmodifiable list with curves.
	 * 
	 * @return the curveList
	 */
	public List<Curve> getCurveList() {
		if (this.curveList == null) {
			this.curveList = new ArrayList<Curve>();
		}
		return Collections.unmodifiableList(curveList);
	}


	/**
	 * Get the energy
	 * @return the energy
	 */
	public EnergyOption getEnergyOption() {
		return energy;
	}

	/**
	 * Set the energy
	 * @param energy the energy to set
	 */
	public void setEnergyOption(EnergyOption energy) {
		this.energy = energy;
	}

	/**
	 * Get a unmodifiable list with patterns
	 * 
	 * @return the patternList
	 */
	public List<Pattern> getPatternList() {
		checkPattern();
		return Collections.unmodifiableList(patternList);
	}
	
	/**
	 * Create the list of pattern if it doesn't exist
	 */
	private void checkPattern() {
		if (this.patternList == null) {
			this.patternList = new ArrayList<Pattern>();
		}
	}

	
	/**
	 * Get the rule
	 * @return the rule
	 */
	public Rule getRule() {
		return rule;
	}

	/**
	 * Set the rule
	 * @param rule the rule to set
	 */
	public void setRule(Rule rule) {
		this.rule = rule;
	}

	/*********************************************************
	 * Water Quality
	 *********************************************************/

	/**
	 * Get the reaction configurations.
	 * 
	 * @return the reaction or null if not exist
	 */
	public ReactionOption getReactionOption() {
		return reactionOption;
	}

	/**
	 * Set the reaction configurations.
	 * 
	 * @param reaction the reaction to set
	 */
	public void setReactionOption(ReactionOption reaction) {
		this.reactionOption = reaction;
	}

	/*********************************************************
	 * Options and Reports
	 *********************************************************/

	/**
	 * Get the option configuration.
	 * 
	 * @return the option or null if not exist
	 */
	public Option getOption() {
		return option;
	}

	/**
	 * Set the option configuration.
	 * 
	 * @param option the option to set
	 */
	public void setOption(Option option) {
		this.option = option;
	}
	
	/**
	 * Get the option configuration.
	 * 
	 * @return the option or null if not exist
	 */
	public QualityOption getQualityOption() {
		return this.qualityOption;
	}

	/**
	 * Set the option configuration.
	 * 
	 * @param option the option to set
	 */
	public void setQualityOption(QualityOption option) {
		this.qualityOption = option;
	}

	/**
	 * Get the time configuration or null if not exist.
	 * 
	 * @return the time
	 */
	public Time getTime() {
		return time;
	}

	/**
	 * Set the time configuration.
	 * 
	 * @param time the time to set
	 */
	public void setTime(Time time) {
		this.time = time;
	}

	/**
	 * Get the report configuration or null if not exist.
	 * 
	 * @return the report
	 */
	public Report getReport() {
		return report;
	}

	/**
	 * Set report configuration or null if not exist.
	 * 
	 * @param report the report to set
	 */
	public void setReport(Report report) {
		this.report = report;
	}

	/*********************************************************
	 * Network Design
	 *********************************************************/

	/**
	 * Get the backdrop or null if not exist.
	 * 
	 * @return the backdrop
	 */
	public Backdrop getBackdrop() {
		return backdrop;
	}

	/**
	 * Set the backdrop.
	 * 
	 * @param backdrop the backdrop to set
	 */
	public void setBackdrop(Backdrop backdrop) {
		this.backdrop = backdrop;
	}

	/**
	 * Get a unmodifiable list with labels
	 * 
	 * @return the labels
	 */
	public List<Label> getLabels() {
		checkLabel();
		return Collections.unmodifiableList(labels);
	}

	/**
	 * Add a label to network
	 * 
	 * @param label the label to add
	 */
	public void addLabel(Label label) {
		checkLabel();
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
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append("[TITLE]\n");
		text.append(getTitle() + "\n");

		text.append("[JUNCTION]\n");
		text.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\n", "ID", "Elev", "Demand", "Pattern"));
		for (Junction junction : getJunctions()) {
			text.append(junction.toString() + "\n");
		}
		text.append("\n");

		text.append("[RESERVOIR]\n");
		text.append(String.format(";%-10s\t%-10s\t%-10s\n", "ID", "Head", "Pattern"));
		for (Reservoir reservoir : getReservoirs()) {
			text.append(reservoir.toString() + "\n");
		}
		text.append("\n");

		text.append("[TANK]\n");
		text.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n", "ID", "Elevation",
				"InitLevel", "MinLevel", "MaxLevel", "Diameter", "MinVol", "VolCurve"));
		for (Tank tank : getTanks()) {
			text.append(tank.toString() + "\n");
		}
		text.append("\n");

		text.append("[PIPE]\n");
		text.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n", "ID", "Node1", "Node2",
				"Length", "Diameter", "Roughness", "MinorLoss", "Status"));
		for (Pipe pipe : getPipes()) {
			text.append(pipe.toString() + "\n");
		}
		text.append("\n");

		text.append("[PUMP]\n");
		text.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\n", "ID", "Node1", "Node2", "Parameters"));
		for (Pump pump : getPumps()) {
			text.append(pump.toString() + "\n");
		}
		text.append("\n");

		text.append("[VALVE]\n");
		text.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n", "ID", "Node1", "Node2",
				"Diameter", "Type", "Setting", "MinorLoss"));
		for (Valve valve : getValves()) {
			text.append(valve.toString() + "\n");
		}
		text.append("\n");

		text.append("[CONTROL]\n");
		Control control = getControl();
		if (control != null) {
			text.append(control.getCode());			
		}
		
		text.append("\n");

		text.append("[CURVE]\n");
		text.append(String.format(";%-10s\t%-10s\t%-10s\n", "ID", "X-Value", "Y-Value"));
		for (Curve curve : getCurveList()) {
			text.append(curve.toString() + "\n");
		}
		text.append("\n");

		text.append("[ENERGY]\n");
		if (getEnergyOption() != null) {
			text.append(getEnergyOption().toString() + "\n");
		}
		text.append("\n");

		text.append("[PATTERN]\n");
		text.append(String.format(";%-10s\t%-10s\n", "ID", "Multipliers"));
		for (Pattern pattern : getPatternList()) {
			text.append(pattern.toString() + "\n");
		}
		text.append("\n");

		text.append("[RULE]\n");
		Rule rule = getRule();
		if (rule != null) {
			text.append(rule.getCode());			
		}
		text.append("\n");

		text.append("[REACTION]\n");
		if (getReactionOption() != null) {
			text.append(getReactionOption() + "\n");
		}
		text.append("\n");

		
		text.append("[OPTION]\n");
		if (getOption() != null) {
			text.append(getOption().toString() + "\n");
		}
		text.append("\n");
		
		text.append("[QUALITYOPTION]\n");
		if (getQualityOption() != null) {
			text.append(getQualityOption().toString() + "\n");
		}
		text.append("\n");

		text.append("[REPORT]\n");
		if (getReport() != null) {
			text.append(getReport().toString() + "\n");
		}
		text.append("\n");

		text.append("[TIME]\n");
		if (getTime() != null) {
			text.append(getTime().toString() + "\n");
		}
		text.append("\n");

		text.append("[BACKDROP]\n");
		if (getBackdrop() != null) {
			text.append(getBackdrop().toString() + "\n");
		}
		text.append("\n");

		text.append("[LABELS]\n");
		text.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\n", "X-Coord", "Y-Coord", "Label", "Anchor Node"));
		for (Label label : this.getLabels()) {
			text.append(label.toString() + "\n");
		}
		text.append("\n");

		text.append("[COORDINATES]\n");
		text.append(String.format(";%-10s\t%-10s\t%-10s\n", "Node", "X-Coord", "Y-Coord"));
		for (Junction junction : getJunctions()) {
			text.append(String.format("%-10s\t%s\n", junction.getId(), junction.getPosition()));
		}
		for (Reservoir reservoir : getReservoirs()) {
			text.append(String.format("%-10s\t%s\n", reservoir.getId(), reservoir.getPosition()));

		}
		for (Tank tank : getTanks()) {
			text.append(String.format("%-10s\t%s\n", tank.getId(), tank.getPosition()));

		}
		text.append("\n");

		text.append("[VERTICES]\n");
		text.append(String.format(";%-10s\t%-10s\t%-10s\n", "Node", "X-Coord", "Y-Coord"));
		for (Pipe pipe : getPipes()) {
			for (Point point : pipe.getVertices()) {
				text.append(String.format("%-10s\t%s\n", pipe.getId(), point));
			}
		}
		for (Pump pump : getPumps()) {
			for (Point point : pump.getVertices()) {
				text.append(String.format("%-10s\t%s\n", pump.getId(), point));
			}
		}
		for (Valve valve : getValves()) {
			for (Point point : valve.getVertices()) {
				text.append(String.format("%-10s\t%s\n", valve.getId(), point));
			}
		}
		text.append("\n");

		text.append("[END]");
		return text.toString();
	}

	/**
	 * Create a independent copy of this network
	 * 
	 * @return the copy
	 */
	public Network copy() {
		return new Network(this);
	}

}
