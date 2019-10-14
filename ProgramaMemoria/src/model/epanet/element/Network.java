package model.epanet.element;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Network {
	private StringBuilder title;
	
	private Map<String, Link> links;
	private Map<String, Node> nodes;
	//Nodes
	private Map<String, Junction> junctions;
	private Map<String, Reservoir> reservoirs;
	private Map<String, Tank> tanks;
	//Links
	private Map<String, Pipe> pipes;
	private Map<String, Pump> pumps;
	private Map<String, Valve> valves;
	
	public Network() {
		this.title = new StringBuilder();
		this.links = new HashMap<String, Link>();
		this.nodes = new HashMap<String, Node>();
		this.junctions = new HashMap<String, Junction>();
		this.reservoirs = new HashMap<String, Reservoir>();
		this.tanks = new HashMap<String, Tank>();
		this.pipes = new HashMap<String, Pipe>();
		this.pumps = new HashMap<String, Pump>();
		this.valves = new HashMap<String, Valve>();
		
	}
	
	public void addLineToTitle(String text) {
		title.append(text);
	}
	
	/**
	 * Add a Link to network. It can be a Pipe, pump or valve.
	 * @param id del link
	 * @param link link element
	 */
	public void addLink(String id, Link link) {
		this.links.put(id, link);
		if (link instanceof Pipe) {
			this.pipes.put(id, (Pipe) link);
		}
		else if (link instanceof Pump) {
			this.pumps.put(id, (Pump) link);
		}
		else{
			this.valves.put(id, (Valve) link);
		}
	}
	
	/**
	 * Add a node to network. It can be a Junction, Reservoir or Tank.
	 * @param id id node
	 * @param node node element
	 */
	public void addNode(String id, Node node) {
		this.nodes.put(id, node);
		if (node instanceof Junction) {
			this.junctions.put(id, (Junction) node);
		}
		else if (node instanceof Reservoir) {
			this.reservoirs.put(id, (Reservoir) node);

		}
		else {
			this.tanks.put(id,(Tank) node);
		}
	}
	
	/**
	 * Return a node by id
	 * @param id id of the node
	 * @return The node
	 */
	public Node getNode(String id) {
		return this.nodes.get(id);
	}
	
	/**
	 * Return a link by id
	 * @param id id of the link
	 * @return The link
	 */
	public Link getLink(String id) {
		return this.links.get(id);
	}
	
	/**
	 * Get a junction by id.
	 * @param id id of junction.
	 * @return Junction.
	 */
	public Junction getJuntion(String id) {
		return this.junctions.get(id);
	}
	
	/**
	 * Get a reservoir by id.
	 * @param id id of reservoir.
	 * @return Reservoir.
	 */
	public Reservoir getReservoir(String id) {
		return this.reservoirs.get(id);
	}
	
	/**
	 * Get a tank by id.
	 * @param id id of tank.
	 * @return Tank.
	 */
	public Tank getTank(String id) {
		return this.tanks.get(id);
	}
	/**
	 * Get a pipe by id.
	 * @param id id of pipe.
	 * @return Pipe.
	 */
	public Pipe getPipe(String id) {
		return this.pipes.get(id);
	}
	/**
	 * Get a pump by id.
	 * @param id id of pump.
	 * @return Pump.
	 */
	public Pump getPump(String id) {
		return this.pumps.get(id);
	}
	/**
	 * Get a valve by id.
	 * @param id id of valve.
	 * @return Valve.
	 */
	public Valve getValve(String id) {
		return this.valves.get(id);
	}
	
	/**
	 * Get all links no matter what they are
	 * @return the links
	 */
	public Collection<Link> getLinks() {
		return links.values();
	}
	/**
	 * Get all nodes no matter what they are
	 * @return the nodes
	 */
	public Collection<Node> getNodes() {
		return nodes.values();
	}
	/**
	 * Get all nodes that are junction.
	 * @return the junctions
	 */
	public Collection<Junction> getJunctions() {
		return junctions.values();
	}
	/**
	 * Get all nodes that are reservoir.
	 * @return the reservoirs
	 */
	public Collection<Reservoir> getReservoirs() {
		return reservoirs.values();
	}
	/**
	 * Get all nodes that are tank.
	 * @return the tanks
	 */
	public Collection<Tank> getTanks() {
		return tanks.values();
	}
	/**
	 * Get all link that are pipe.
	 * @return the pipes
	 */
	public Collection<Pipe> getPipes() {
		return pipes.values();
	}
	/**
	 * Get all link that are pump.
	 * @return the pumps
	 */
	public Collection<Pump> getPumps() {
		return pumps.values();
	}
	/**
	 * Get all link that are valve.
	 * @return the valves
	 */
	public Collection<Valve> getValves() {
		return valves.values();
	}
	
	@Override
	public String toString() {
		String text;
		text = "Network:\n";
		text += "Title: " + this.title + "\n";
		text += "Node: \n";
		for (Node node : this.getNodes()) {
			text += node.toString();
		}
		text += "Link: \n";
		for (Link link : this.getLinks()) {
			text += link.toString();
		}
		return text;
	}
	
}
