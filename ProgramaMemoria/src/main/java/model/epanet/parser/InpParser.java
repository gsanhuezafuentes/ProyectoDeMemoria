package model.epanet.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import model.epanet.EpanetErrors;
import model.epanet.EpanetException;
import model.epanet.element.Network;
import model.epanet.element.networkcomponent.Junction;
import model.epanet.element.networkcomponent.Link;
import model.epanet.element.networkcomponent.Node;
import model.epanet.element.networkcomponent.Pipe;
import model.epanet.element.networkcomponent.Point;
import model.epanet.element.networkcomponent.Pump;
import model.epanet.element.networkcomponent.Reservoir;
import model.epanet.element.networkcomponent.Tank;
import model.epanet.element.networkcomponent.Valve;

/**
 * Parse the INP file to get the coordinates and vertices of the water network.
 * 
 *
 */
public class InpParser implements InputParser {

	@Override
	public Network parse(Network net, File file) throws EpanetException {
		try (BufferedReader buffReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "ISO-8859-1"))) {

			String line;
			String sectionType = "";
			while ((line = buffReader.readLine()) != null) {

				line = line.trim();
				if (line.length() == 0) {
					continue;
				}

				int semicolonIndex = line.indexOf(";");
				if (semicolonIndex != -1) {
					if (semicolonIndex > 0) {
						line = line.substring(0, semicolonIndex);
					} else {
						continue;
					}
				}

				String[] tokens = line.split("[ \t]+");
				if (tokens.length == 0)
					continue;

				if (tokens[0].contains("[")) {
					sectionType = identifySectionType(tokens[0]);
					continue;
				} else {
					switch (sectionType) {
					case "TITLE":
						net.addLineToTitle(line + "\n");
						break;
					case "JUNCTIONS":
						parseJunction(net, tokens);
						break;
					case "RESERVOIRS":
						parseReservoir(net, tokens);
						break;
					case "TANKS":
						parseTanks(net, tokens);
						break;
					case "PIPES":
						parsePipe(net, tokens);
						break;
					case "PUMPS":
						parsePump(net, tokens);
						break;
					case "VALVES":
						parseValve(net, tokens);
						break;
					case "EMITTERS":
						parseEmmiter(net, tokens);
						break;
					case "CURVES":
						parseCurve(net, tokens);
						break;
					case "PATTERNS":
						parsePattern(net, tokens);
						break;
					case "ENERGY":
						parseEnergy(net, tokens);
					case "STATUS":
						parseStatus(net, tokens);
						break;
					case "CONTROLS":
						parseControl(net, tokens);
						break;
					case "RULES":
						parseRule(net, tokens, line);
						break;
					case "DEMANDS":
						parseDemand(net, tokens);
						break;
					case "QUALITY":
						parseQuality(net, tokens);
						break;
					case "REACTIONS":
						parseReaction(net, tokens);
						break;
					case "SOURCES":
						parseSource(net, tokens);
						break;
					case "MIXING":
						parseMixing(net, tokens);
						break;
					case "OPTIONS":
						parseOption(net, tokens);
						break;
					case "TIMES":
						parseTime(net, tokens);
						break;
					case "REPORT":
						parseReport(net, tokens);
						break;
					case "COORDINATES":
						parseCoordinate(net, tokens);
						break;
					case "VERTICES":
						parseVertice(net, tokens);
						break;
					case "LABELS":
						parseLabel(net, tokens);
						break;
					case "BACKDROP":
						parseBackdrop(net, tokens);
						break;
					case "TAGS":
						parseTag(net, tokens);
						break;
					
				}

			}

		} catch (EpanetException e) {
			throw e;
		} catch (IOException e) {
			EpanetErrors.throwException(302);
		}

		return net;
	}

	/*********************************************************
	 * Network Components
	 *********************************************************/

	/**
	 * Parse the JUNCTIONS section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parseJunction(Network net, String[] tokens) throws EpanetException {
		if (net.getNode(tokens[0]) != null)
			EpanetErrors.throwException(200, "Junction " + tokens[0] + " is duplicated");
		Node node = new Junction();
		node.setId(tokens[0]);
		net.addNode(tokens[0], node);
	}

	/**
	 * Parse the RESERVOIRS section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parseReservoir(Network net, String[] tokens) throws EpanetException {
		if (net.getNode(tokens[0]) != null)
			EpanetErrors.throwException(200, "Reservoir " + tokens[0] + " is duplicated");
		Node node = new Reservoir();
		node.setId(tokens[0]);
		net.addNode(tokens[0], node);
	}

	/**
	 * Parse the TANKS section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parseTanks(Network net, String[] tokens) throws EpanetException {
		if (net.getNode(tokens[0]) != null)
			EpanetErrors.throwException(200, "Tank " + tokens[0] + " is duplicated");
		Node node = new Tank();
		node.setId(tokens[0]);
		net.addNode(tokens[0], node);

	}

	/**
	 * Parse the PIPES section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parsePipe(Network net, String[] tokens) throws EpanetException {
		if (net.getLink(tokens[0]) != null)
			EpanetErrors.throwException(200, "Pipe " + tokens[0] + " is duplicated");
		Link link = new Pipe();
		link.setId(tokens[0]);

		Node to = net.getNode(tokens[1]);
		Node from = net.getNode(tokens[2]);

		link.setToNode(to);
		link.setFromNode(from);

		net.addLink(tokens[0], link);
	}

	/**
	 * Parse the PUMPS section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parsePump(Network net, String[] tokens) throws EpanetException {
		if (net.getLink(tokens[0]) != null)
			EpanetErrors.throwException(200, "Pump " + tokens[0] + " is duplicated");
		Link link = new Pump();
		link.setId(tokens[0]);

		Node to = net.getNode(tokens[1]);
		Node from = net.getNode(tokens[2]);

		link.setToNode(to);
		link.setFromNode(from);

		net.addLink(tokens[0], link);
	}

	/**
	 * Parse the VALVES section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parseValve(Network net, String[] tokens) throws EpanetException {
		if (net.getLink(tokens[0]) != null)
			EpanetErrors.throwException(200, "Valve " + tokens[0] + " is duplicated");
		Link link = new Valve();
		link.setId(tokens[0]);

		Node to = net.getNode(tokens[1]);
		Node from = net.getNode(tokens[2]);

		link.setToNode(to);
		link.setFromNode(from);

		net.addLink(tokens[0], link);
	}

	/**
	 * Parse the EMITTER section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseEmmiter(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/*********************************************************
	 * System Operation
	 *********************************************************/

	/**
	 * Parse the CURVES section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseCurve(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the PATTERNS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parsePattern(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the ENERGY section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseEnergy(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the Status section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseStatus(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the CONTROLS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseControl(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the RULES section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseRule(Network net, String[] tokens, String line) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the DEMANDS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseDemand(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/*********************************************************
	 * Water Quality
	 *********************************************************/

	/**
	 * Parse the QUALITY section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseQuality(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the REACTION section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseReaction(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the SOURCES section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseSource(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the MIXINGS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseMixing(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/*********************************************************
	 * Options and Reports
	 *********************************************************/

	/**
	 * Parse the OPTIONS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseOption(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the TIMES section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseTime(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the REPORT section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	-

	private void parseReport(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/*********************************************************
	 * Network Design
	 *********************************************************/

	/**
	 * Parse the COORDINATES section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseCoordinate(Network net, String[] tokens) throws EpanetException {
		Node node = net.getNode(tokens[0]);
		if (node == null)
			EpanetErrors.throwException(200, "Node " + tokens[0] + " don't exist");

		double x = Double.parseDouble(tokens[1].replace(",", "."));
		double y = Double.parseDouble(tokens[2].replace(",", "."));
		node.setPosition(new Point(x, y));
	}

	/**
	 * Parse the VERTICES section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseVertice(Network net, String[] tokens) throws EpanetException {
		Link link = net.getLink(tokens[0]);
		if (link == null)
			EpanetErrors.throwException(200, "Link " + tokens[0] + " don't exist");
		double x = Double.parseDouble(tokens[1].replace(",", "."));
		double y = Double.parseDouble(tokens[2].replace(",", "."));
		link.getVertices().add(new Point(x, y));
	}

	/**
	 * Parse the LABELS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseLabel(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	/**
	 * Parse the Backdrop section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseBackdrop(Network net, String[] tokens) {
		Backdrop backdrop = new Backdrop();

	}

	/**
	 * Parse the TAGS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseTag(Network net, String[] tokens) {
		// TODO Auto-generated method stub

	}

	public String identifySectionType(String token) {
		int endIndex = token.indexOf("]");
		return token.substring(1, endIndex);
	}

//	/**
//	 * Print the token. Function to test
//	 * @param tokens
//	 */
//	private void printToken(String[] tokens) {
//		String text = "Token[" + tokens.length +"]: \n";
//		for (String token : tokens) {
//			text += token + " "; 
//		}
//		System.out.println(text);
//	}

	public static void main(String[] args) {
		InpParser parse = new InpParser();
		Network network = new Network();
		// File file = new File("inp/hanoi-Frankenstein2.inp");
		File file = new File("inp/red1.inp");
		try {
			System.out.println(parse.parse(network, file));
		} catch (EpanetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
