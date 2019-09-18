package epanet.uielements;

import java.util.Collection;
import java.util.Map;

public class Network {
	private Map<String, Link> links;
	private Map<String, Node> nodes;
	//Nodes
	private Map<String, Junction> junctions;
	private Map<String, Reservoir> reservoirs;
	private Map<String, Tank> tanks;
	//Links
	private Map<String, Pipe> pipes;
	private Map<String, Pump> pumps;
	private Map<String, Valves> valves;
	
	
	public void addLink(Link link) {
		
	}
	
	
	/**
	 * @return the links
	 */
	public Collection<Link> getLinks() {
		return links.values();
	}
	/**
	 * @return the nodes
	 */
	public Collection<Node> getNodes() {
		return nodes.values();
	}
	/**
	 * @return the junctions
	 */
	public Collection<Junction> getJunctions() {
		return junctions.values();
	}
	/**
	 * @return the reservoirs
	 */
	public Collection<Reservoir> getReservoirs() {
		return reservoirs.values();
	}
	/**
	 * @return the tanks
	 */
	public Collection<Tank> getTanks() {
		return tanks.values();
	}
	/**
	 * @return the pipes
	 */
	public Collection<Pipe> getPipes() {
		return pipes.values();
	}
	/**
	 * @return the pumps
	 */
	public Collection<Pump> getPumps() {
		return pumps.values();
	}
	/**
	 * @return the valves
	 */
	public Collection<Valves> getValves() {
		return valves.values();
	}
	
	
	
}
