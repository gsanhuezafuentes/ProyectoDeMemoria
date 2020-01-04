package model.epanet.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.epanet.element.networkcomponent.Emitter;
import model.epanet.element.networkcomponent.Junction;
import model.epanet.element.networkcomponent.Link;
import model.epanet.element.networkcomponent.Node;
import model.epanet.element.networkcomponent.Pipe;
import model.epanet.element.networkcomponent.Point;
import model.epanet.element.networkcomponent.Pump;
import model.epanet.element.networkcomponent.Reservoir;
import model.epanet.element.networkcomponent.Tank;
import model.epanet.element.networkcomponent.Valve;
import model.epanet.element.networkdesign.Backdrop;
import model.epanet.element.networkdesign.Label;
import model.epanet.element.networkdesign.Tag;
import model.epanet.element.optionsreport.Option;
import model.epanet.element.optionsreport.Report;
import model.epanet.element.optionsreport.Time;
import model.epanet.element.systemoperation.Control;
import model.epanet.element.systemoperation.Curve;
import model.epanet.element.systemoperation.Demand;
import model.epanet.element.systemoperation.Energy;
import model.epanet.element.systemoperation.Pattern;
import model.epanet.element.systemoperation.Rule;
import model.epanet.element.systemoperation.Status;
import model.epanet.element.waterquality.Mixing;
import model.epanet.element.waterquality.Quality;
import model.epanet.element.waterquality.Reaction;
import model.epanet.element.waterquality.Source;

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
	private List<Emitter> emitterList;

	/*********************************************************
	 * System Operation
	 *********************************************************/
	private Map<String, Curve> curveMap;
	private Map<String, Pattern> patternMap;

	private List<Control> controlList;
	private List<Curve> curveList;
	private List<Demand> demandList;
	private List<Energy> energyList;
	private List<Pattern> patternList;
	private List<Rule> ruleList;
	private List<Status> statusList;

	/*********************************************************
	 * Water Quality
	 *********************************************************/
	private List<Mixing> mixingList;
	private List<Quality> qualityList;
	private List<Source> sourceList;
	private Reaction reaction;

	/*********************************************************
	 * Options and Reports
	 *********************************************************/
	private Option option;
	private Time time;
	private Report report;
	/*********************************************************
	 * Network Design
	 *********************************************************/
	private Backdrop backdrop;
	private List<Label> labels;
	private List<Tag> tags;

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
			if (node instanceof Junction) {
				Junction junction = (Junction) node;
				// the copy is shallow for this the pattern is replaced by a new created to this
				// object.
				if (junction.getPattern() != null) {
					Pattern pattern = getPattern(junction.getPattern().getId());
					junction.setPattern(pattern);
				}
			} else if (node instanceof Tank) {
				Tank tank = (Tank) node;
				// the copy is shallow for this the pattern is replaced by a new created to this
				// object.
				if (tank.getVolCurve() != null) {
					Curve volCurve = getCurve(tank.getVolCurve().getId());
					tank.setVolCurve(volCurve);
				}
			} else {
				Reservoir reservoir = (Reservoir) node;
				// the copy is shallow for this the pattern is replaced by a new created to this
				// object.
				if (reservoir.getPattern() != null) {
					Pattern pattern = getPattern(reservoir.getPattern().getId());
					reservoir.setPattern(pattern);
				}
			}
			addNode(nodeKey, node);
		}

		// copy the links and replace the references
		for (String linkKey : network.linkMap.keySet()) {
			Link link = network.linkMap.get(linkKey).copy();
			addLink(linkKey, link);
			// Replace the nodes in link for the nodes created for this copy of network
			link.setNode1(getNode(link.getNode1().getId()));
			link.setNode2(getNode(link.getNode2().getId()));

			if (link instanceof Pump) {
				Pump pump = (Pump) link;
				/**
				 * If link is pump it can have object references in his property. So is needed
				 * replace the reference to the created for this network.
				 */
				for (Pump.PumpProperty key : pump.getPropertyKeys()) {
					if (key == Pump.PumpProperty.HEAD) {
						Curve curve = (Curve) pump.getProperty(key);
						// replace the curve used in original object for the copy of the curve
						pump.setProperty(key, getCurve(curve.getId()));
					} else if (key == Pump.PumpProperty.PATTERN) {
						Pattern pattern = (Pattern) pump.getProperty(key);
						// replace the pattern used in original object for the copy of the pattern
						pump.setProperty(key, getPattern(pattern.getId()));
					}
				}
			}
		}

		// copy emitter
		for (Emitter emitter : network.getEmitterList()) {
			addEmiter(emitter.copy());
		}

		// copy backdrop
		Backdrop backdrop = network.getBackdrop();
		if (backdrop != null) {
			setBackdrop(backdrop.copy());
		}

		// copy label
		for (Label label : network.getLabels()) {
			Label copy = label.copy();
			if (label.getAnchorNode() != null) {
				copy.setAnchorNode(getNode(label.getAnchorNode().getId()));
			}
			addLabel(copy);
		}

		// copy tag
		for (Tag tag : network.getTags()) {
			addTag(tag.copy());
		}

		// copy option
		Option option = network.getOption();
		if (option != null) {
			setOption(option.copy());
		}

		// copy report
		Report report = network.getReport();
		if (report != null) {
			setReport(report.copy());
		}

		// copy time
		Time time = network.getTime();
		if (time != null) {
			setTime(network.getTime().copy());
		}

		// copy control
		for (Control control : network.getControlList()) {
			addControl(control);
		}

		// copy demand
		for (Demand demand : network.getDemandList()) {
			Demand copy = demand.copy();
			if (demand.getDemandPatern() != null) {
				copy.setDemandPatern(getPattern(demand.getDemandPatern().getId()));
			}
			addDemand(copy);
		}

		// copy energy
		for (Energy energy : network.getEnergyList()) {
			addEnergy(energy);
		}

		// copy rule
		for (Rule rule : network.getRuleList()) {
			addRule(rule.copy());
		}

		// copy status
		for (Status status : network.getStatusList()) {
			addStatus(status.copy());
		}

		// copy mixing
		for (Mixing mixing : network.getMixingList()) {
			addMixing(mixing.copy());
		}

		// copy quality
		for (Quality quality : network.getQualityList()) {
			addQuality(quality.copy());
		}

		// copy reaction
		Reaction reaction = network.getReaction();
		if (reaction != null) {
			setReaction(reaction.copy());
		}

		// copy source
		for (Source source : network.getSourceList()) {
			Source copy = source.copy();
			if (source.getTimePattern() != null) {
				copy.setTimePattern(getPattern(source.getTimePattern().getId()));
			}
			addSource(copy);
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

	/**
	 * Get a unmodifiable list only with emitter.
	 * 
	 * @return the emitterList
	 */
	public List<Emitter> getEmitterList() {
		if (this.emitterList == null) {
			this.emitterList = new ArrayList<Emitter>();
		}
		return Collections.unmodifiableList(emitterList);
	}

	/**
	 * Add a emitter to network
	 * 
	 * @param emitter a emitter
	 */
	public void addEmiter(Emitter emitter) {
		if (this.emitterList == null) {
			this.emitterList = new ArrayList<Emitter>();
		}
		this.emitterList.add(emitter);
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
	 * Get a unmodifiable list with the controls
	 * 
	 * @return the controlList
	 */
	public List<Control> getControlList() {
		if (this.controlList == null) {
			this.controlList = new ArrayList<>();
		}
		return Collections.unmodifiableList(controlList);
	}

	/**
	 * Add a control to the network.
	 * 
	 * @param control the control to add
	 */
	public void addControl(Control control) {
		if (this.controlList == null) {
			this.controlList = new ArrayList<>();
		}
		this.controlList.add(control);
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
	 * Get a unmodifiable list with the demands.
	 * 
	 * @return the demandList
	 */
	public List<Demand> getDemandList() {
		if (this.demandList == null) {
			this.demandList = new ArrayList<>();
		}
		return Collections.unmodifiableList(demandList);
	}

	/**
	 * Add a demand to the network.
	 * 
	 * @param demand the demand to add
	 */
	public void addDemand(Demand demand) {
		if (this.demandList == null) {
			this.demandList = new ArrayList<>();
		}
		this.demandList.add(demand);
	}

	/**
	 * Get a unmodifiable list with the energy
	 * 
	 * @return the energyList
	 */
	public List<Energy> getEnergyList() {
		if (this.energyList == null) {
			this.energyList = new ArrayList<>();
		}
		return Collections.unmodifiableList(energyList);
	}

	/**
	 * Add a energy to the network.
	 * 
	 * @param energy the energy to add
	 */
	public void addEnergy(Energy energy) {
		if (this.energyList == null) {
			this.energyList = new ArrayList<>();
		}
		this.energyList.add(energy);
	}

	/**
	 * Get a unmodifiable list with patterns
	 * 
	 * @return the patternList
	 */
	public List<Pattern> getPatternList() {
		if (this.patternList == null) {
			this.patternList = new ArrayList<Pattern>();
		}
		return Collections.unmodifiableList(patternList);
	}

	/**
	 * Get a unmodifiable list with rules.
	 * 
	 * @return the ruleList
	 */
	public List<Rule> getRuleList() {
		if (this.ruleList == null) {
			this.ruleList = new ArrayList<>();
		}
		return Collections.unmodifiableList(ruleList);
	}

	/**
	 * Add a rule to the network.
	 * 
	 * @param rule the rule to add
	 */
	public void addRule(Rule rule) {
		if (this.ruleList == null) {
			this.ruleList = new ArrayList<>();
		}
		this.ruleList.add(rule);
	}

	/**
	 * Get a unmodifiable list with status.
	 * 
	 * @return the statusList
	 */
	public List<Status> getStatusList() {
		if (this.statusList == null) {
			this.statusList = new ArrayList<>();
		}
		return Collections.unmodifiableList(statusList);
	}

	/**
	 * Add a status to the network.
	 * 
	 * @param status the status to add
	 */
	public void addStatus(Status status) {
		if (this.statusList == null) {
			this.statusList = new ArrayList<>();
		}
		this.statusList.add(status);
	}

	/*********************************************************
	 * Water Quality
	 *********************************************************/

	/**
	 * Get a unmodifiable list with mixing configurations.
	 * 
	 * @return the mixingList
	 */
	public List<Mixing> getMixingList() {
		if (this.mixingList == null) {
			this.mixingList = new ArrayList<>();
		}
		return Collections.unmodifiableList(mixingList);
	}

	/**
	 * Add a mixing configuration.
	 * 
	 * @param mixing the mixing to add
	 */
	public void addMixing(Mixing mixing) {
		if (this.mixingList == null) {
			this.mixingList = new ArrayList<>();
		}
		this.mixingList.add(mixing);
	}

	/**
	 * Get a unmodifiable list with quality configurations.
	 * 
	 * @return the qualityList
	 */
	public List<Quality> getQualityList() {
		if (this.qualityList == null) {
			this.qualityList = new ArrayList<>();
		}
		return Collections.unmodifiableList(qualityList);
	}

	/**
	 * Add a quality configuration.
	 * 
	 * @param quality the quality to add
	 */
	public void addQuality(Quality quality) {
		if (this.qualityList == null) {
			this.qualityList = new ArrayList<>();
		}
		this.qualityList.add(quality);
	}

	/**
	 * Get a unmodifiable list with sources configuration.
	 * 
	 * @return the sourceList
	 */
	public List<Source> getSourceList() {
		if (this.sourceList == null) {
			this.sourceList = new ArrayList<>();
		}
		return Collections.unmodifiableList(sourceList);
	}

	/**
	 * Add a source configuration.
	 * 
	 * @param source the source to add
	 */
	public void addSource(Source source) {
		if (this.sourceList == null) {
			this.sourceList = new ArrayList<>();
		}
		this.sourceList.add(source);
	}

	/**
	 * Get the reaction configurations.
	 * 
	 * @return the reaction or null if not exist
	 */
	public Reaction getReaction() {
		return reaction;
	}

	/**
	 * Set the reaction configurations.
	 * 
	 * @param reaction the reaction to set
	 */
	public void setReaction(Reaction reaction) {
		this.reaction = reaction;
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
		if (this.labels == null) {
			this.labels = new ArrayList<>();
		}
		return Collections.unmodifiableList(labels);
	}

	/**
	 * Add a label to network
	 * 
	 * @param label the label to add
	 */
	public void addLabel(Label label) {
		if (this.labels == null) {
			this.labels = new ArrayList<>();
		}
		this.labels.add(label);
	}

	/**
	 * Get a unmodifiable list with tags
	 * 
	 * @return the tags
	 */
	public List<Tag> getTags() {
		if (this.tags == null) {
			this.tags = new ArrayList<>();
		}
		return Collections.unmodifiableList(tags);
	}

	/**
	 * Add a tag to network
	 * 
	 * @param tag the tag to add
	 */
	public void addTag(Tag tag) {
		if (this.tags == null) {
			this.tags = new ArrayList<>();
		}
		this.tags.add(tag);
	}

	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append("[TITLE]\n");
		text.append(getTitle() + "\n");

		text.append("[JUNCTION]\n");
		text.append(";ID\tElev\tDemand\tPattern\n");
		for (Junction junction : getJunctions()) {
			text.append(junction.toString() + "\n");
		}
		text.append("\n");

		text.append("[RESERVOIR]\n");
		text.append(";ID\tHead\tPattern\n");
		for (Reservoir reservoir : getReservoirs()) {
			text.append(reservoir.toString() + "\n");
		}
		text.append("\n");

		text.append("[TANK]\n");
		text.append(";ID\tElevation\tInitLevel\tMinLevel\tMaxLevel\tDiameter\tMinVol\tVolCurve\n");
		for (Tank tank : getTanks()) {
			text.append(tank.toString() + "\n");
		}
		text.append("\n");

		text.append("[PIPE]\n");
		text.append(";ID\tNode1\tNode2\tLength\tDiameter\tRoughness\tMinorLoss\tStatus\n");
		for (Pipe pipe : getPipes()) {
			text.append(pipe.toString() + "\n");
		}
		text.append("\n");

		text.append("[PUMP]\n");
		text.append(";ID\tNode1\tNode2\tParameters\n");
		for (Pump pump : getPumps()) {
			text.append(pump.toString() + "\n");
		}
		text.append("\n");

		text.append("[VALVE]\n");
		text.append(";ID\tNode2\tDiameter\tType\tSetting\tMinorLoss\n");
		for (Valve valve : getValves()) {
			text.append(valve.toString() + "\n");
		}
		text.append("\n");

		text.append("[EMITTER]\n");
		text.append(";Junction\tCoefficient\n");
		for (Emitter emitter : getEmitterList()) {
			text.append(emitter.toString() + "\n");
		}
		text.append("\n");

		text.append("[CONTROL]\n");
		for (Control control : getControlList()) {
			text.append(control.toString() + "\n");
		}
		text.append("\n");

		text.append("[CURVE]\n");
		text.append(";ID\tX-Value\tY-Value\n");
		for (Curve curve : getCurveList()) {
			text.append(curve.toString() + "\n");
		}
		text.append("\n");

		text.append("[DEMAND]\n");
		text.append(";Junction\tDemand\tPattern\tCategory\n");
		for (Demand demand : getDemandList()) {
			text.append(demand.toString() + "\n");
		}
		text.append("\n");

		text.append("[ENERGY]\n");
		for (Energy energy : getEnergyList()) {
			text.append(energy.toString() + "\n");
		}
		text.append("\n");

		text.append("[PATTERN]\n");
		text.append(";ID\tMultipliers\n");
		for (Pattern pattern : getPatternList()) {
			text.append(pattern.toString() + "\n");
		}
		text.append("\n");

		text.append("[RULE]\n");
		for (Rule rule : getRuleList()) {
			text.append(rule.toString() + "\n");
		}
		text.append("\n");

		text.append("[STATUS]\n");
		text.append(";ID\tStatus/Setting\n");
		for (Status status : getStatusList()) {
			text.append(status.toString() + "\n");
		}
		text.append("\n");

		text.append("[MIXING]\n");
		text.append(";Tank\tModel\n");
		for (Mixing mixing : getMixingList()) {
			text.append(mixing.toString() + "\n");
		}
		text.append("\n");

		text.append("[QUALITY]\n");
		text.append(";Node\tInitQual\n");
		for (Quality quality : getQualityList()) {
			text.append(quality.toString() + "\n");
		}
		text.append("\n");

		text.append("[REACTION]\n");
		if (getReaction() != null) {
			text.append(getReaction() + "\n");
		}
		text.append("\n");

		text.append("[SOURCE]\n");
		text.append(";Node\tType\tQuality\tPattern\n");
		for (Source source : this.getSourceList()) {
			text.append(source.toString() + "\n");
		}
		text.append("\n");

		text.append("[OPTION]\n");
		if (getOption() != null) {
			text.append(getOption().toString() + "\n");
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
		text.append(";X-Coord\tY-Coord\tLabel & Anchor Node\n");
		for (Label label : this.getLabels()) {
			text.append(label.toString() + "\n");
		}
		text.append("\n");

		text.append("[TAGS]\n");
		text.append(";Object\tID\tLabel\n");
		for (Tag tag : this.getTags()) {
			text.append(tag.toString() + "\n");
		}
		text.append("\n");

		text.append("[COORDINATES]\n");
		text.append(";Node\tX-Coord\tY-Coord\n");
		for (Junction junction : getJunctions()) {
			text.append(junction.getId() + "\t");
			text.append(junction.getPosition() + "\n");
		}
		for (Reservoir reservoir : getReservoirs()) {
			text.append(reservoir.getId() + "\t");
			text.append(reservoir.getPosition() + "\n");
		}
		for (Tank tank : getTanks()) {
			text.append(tank.getId() + "\t");
			text.append(tank.getPosition() + "\n");
		}
		text.append("\n");

		text.append("[VERTICES]\n");
		text.append(";Node\tX-Coord\tY-Coord\n");
		for (Pipe pipe : getPipes()) {
			for (Point point : pipe.getVertices()) {
				text.append(pipe.getId() + "\t");
				text.append(point + "\n");
			}
		}
		for (Pump pump : getPumps()) {
			for (Point point : pump.getVertices()) {
				text.append(pump.getId() + "\t");
				text.append(point + "\n");
			}
		}
		for (Valve valve : getValves()) {
			for (Point point : valve.getVertices()) {
				text.append(valve.getId() + "\t");
				text.append(point + "\n");
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
