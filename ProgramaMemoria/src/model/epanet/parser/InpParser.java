package model.epanet.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import model.epanet.EpanetErrors;
import model.epanet.EpanetException;
import model.epanet.element.Junction;
import model.epanet.element.Link;
import model.epanet.element.Network;
import model.epanet.element.Node;
import model.epanet.element.Pipe;
import model.epanet.element.Point;
import model.epanet.element.Pump;
import model.epanet.element.Reservoir;
import model.epanet.element.Tank;
import model.epanet.element.Valve;

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
					case "COORDINATES":
						parseCoordinate(net, tokens);
						break;
					case "VERTICES":
						parseVertice(net, tokens);
						break;
					}
				}

			}

		} catch (EpanetException e) {
			throw e;
		} catch (IOException e) {
			EpanetErrors.throwException(302);
		}

		return net;
	}

	/**
	 * Parse the Junctions section of inp file
	 * @param net network 
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
	 * Parse the Reservoirs section of inp file
	 * @param net network 
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
	 * Parse the Tanks section of inp file
	 * @param net network 
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
	 * Parse the Pipe section of inp file
	 * @param net network 
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
	 * Parse the Pump section of inp file
	 * @param net network 
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
	 * Parse the Valve section of inp file
	 * @param net network 
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

	private void parseCoordinate(Network net, String[] tokens) throws EpanetException {
		Node node = net.getNode(tokens[0]);
		if (node == null)
			EpanetErrors.throwException(200, "Node " + tokens[0] + " don't exist");
		
		double x = Double.parseDouble(tokens[1].replace(",", "."));
		double y = Double.parseDouble(tokens[2].replace(",", "."));
		node.setPosition(new Point(x, y));
	}

	private void parseVertice(Network net, String[] tokens) throws EpanetException {
		Link link = net.getLink(tokens[0]);
		if (link == null)
			EpanetErrors.throwException(200, "Link " + tokens[0] + " don't exist");
		double x = Double.parseDouble(tokens[1].replace(",", "."));
		double y = Double.parseDouble(tokens[2].replace(",", "."));
		link.getVertices().add(new Point(x, y));
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
		//File file = new File("inp/hanoi-Frankenstein2.inp");
		File file = new File("inp/red1.inp");
		try {
			System.out.println(parse.parse(network, file));
		} catch (EpanetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
