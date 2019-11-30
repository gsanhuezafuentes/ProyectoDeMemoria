package model.epanet.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import epanet.core.EpanetException;
import exception.InputException;
import model.epanet.element.Network;
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

/**
 * Parse the INP file to get the coordinates and vertices of the water network.
 * 
 *
 */
public class InpParser implements InputParser {

	private Rule currentRule;

	/** {@inheritDoc} */
	@Override
	public Network parse(Network net, String filename) throws FileNotFoundException, IOException, InputException {
		parsePatternAndCurve(net, filename);
		try (BufferedReader buffReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filename), "ISO-8859-1"))) { // ISO-8859-1

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
					case "ENERGY":
						parseEnergy(net, tokens, line);
						break;
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
						parseReaction(net, tokens, line);
						break;
					case "SOURCES":
						parseSource(net, tokens);
						break;
					case "MIXING":
						parseMixing(net, tokens);
						break;
					case "OPTIONS":
						parseOption(net, tokens, line);
						break;
					case "TIMES":
						parseTime(net, tokens, line);
						break;
					case "REPORT":
						parseReport(net, tokens, line);
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
						parseBackdrop(net, tokens, line);
						break;
					case "TAGS":
						parseTag(net, tokens);
						break;

					}

				}

			}
		} catch (UnsupportedEncodingException e) {
			throw new InputException("Error in encoding file", e);
		}
		return net;
	}

	private Network parsePatternAndCurve(Network net, String filename)
			throws FileNotFoundException, IOException, InputException {
		try (BufferedReader buffReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filename), "ISO-8859-1"))) {
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
					case "PATTERNS":
						parsePattern(net, tokens);
						break;
					case "CURVES":
						parseCurve(net, tokens);
						break;
					}
				}
			}
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
	private void parseJunction(Network net, String[] tokens) throws InputException {
		if (net.getNode(tokens[0]) != null)
			throw new InputException("Junction " + tokens[0] + " is duplicated");
		Junction node = new Junction();
		node.setId(tokens[0]);
		node.setElev(Double.parseDouble(tokens[1]));
		if (tokens.length == 2) {
			node.setDemand(0);
		}
		if (tokens.length == 3) {
			node.setDemand(Double.parseDouble(tokens[2]));
		}
		if (tokens.length == 4) {
			Pattern pattern = net.getPattern(tokens[3]);
			if (pattern == null) {
				throw new InputException("Don't exist the pattern " + tokens[3] + " for this junction");
			}
			node.setPattern(pattern);
		}
		net.addNode(tokens[0], node);
	}

	/**
	 * Parse the RESERVOIRS section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parseReservoir(Network net, String[] tokens) throws InputException {
		if (net.getNode(tokens[0]) != null)
			throw new InputException("Reservoir " + tokens[0] + " is duplicated");
		Reservoir node = new Reservoir();
		node.setId(tokens[0]);
		node.setHead(Double.parseDouble(tokens[1]));

		if (tokens.length == 3) {
			Pattern pattern = net.getPattern(tokens[2]);
			if (pattern == null) {
				throw new InputException("Don't exist the pattern " + tokens[2] + " for this reservoir");
			}
			node.setPattern(pattern);
		}
		net.addNode(tokens[0], node);
	}

	/**
	 * Parse the TANKS section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parseTanks(Network net, String[] tokens) throws InputException {
		if (net.getNode(tokens[0]) != null)
			throw new InputException("Tank " + tokens[0] + " is duplicated");
		Tank node = new Tank();
		node.setId(tokens[0]);
		node.setElev(Double.parseDouble(tokens[1]));
		node.setInitLvl(Double.parseDouble(tokens[2]));
		node.setMinLvl(Double.parseDouble(tokens[3]));
		node.setMaxLvl(Double.parseDouble(tokens[4]));
		node.setDiameter(Double.parseDouble(tokens[5]));
		node.setMinVol(Double.parseDouble(tokens[6]));
		if (tokens.length == 3) {
			Curve curve = net.getCurve(tokens[2]);
			if (curve == null) {
				throw new InputException("Don't exist the curve " + tokens[7] + " for this reservoir");
			}
			node.setVolCurve(curve);
		}
		net.addNode(tokens[0], node);
	}

	/**
	 * Parse the PIPES section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parsePipe(Network net, String[] tokens) throws InputException {
		if (net.getLink(tokens[0]) != null)
			throw new InputException("Pipe " + tokens[0] + " is duplicated");
		Pipe link = new Pipe();
		link.setId(tokens[0]);

		Node to = net.getNode(tokens[1]);
		if (to == null) {
			throw new InputException("Don't exist the node with id " + tokens[1]);
		}
		Node from = net.getNode(tokens[2]);
		if (from == null) {
			throw new InputException("Don't exist the node with id " + tokens[2]);
		}
		link.setNode1(to);
		link.setNode2(from);

		link.setLength(Double.parseDouble(tokens[3]));
		link.setDiameter(Double.parseDouble(tokens[4]));
		link.setRoughness(Double.parseDouble(tokens[5]));

		if (tokens.length == 8) {
			link.setMloss(Double.parseDouble(tokens[6]));

			if (tokens[7].equalsIgnoreCase("OPEN")) {
				link.setStatus(Pipe.PipeStatus.OPEN);

			} else if (tokens[7].equalsIgnoreCase("CLOSED")) {
				link.setStatus(Pipe.PipeStatus.CLOSED);

			} else if (tokens[7].equalsIgnoreCase("CV")) {
				link.setStatus(Pipe.PipeStatus.CV);
			} else {
				throw new InputException("Don't exist the " + tokens[7] + " status");
			}
		} else {
			// Set the value by defect if it isn't in inp file
			link.setMloss(0.0);
			link.setStatus(Pipe.PipeStatus.OPEN);

		}
		net.addLink(tokens[0], link);
	}

	/**
	 * Parse the PUMPS section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parsePump(Network net, String[] tokens) throws InputException {
		String id = tokens[0];
		if (net.getLink(tokens[0]) != null)
			throw new InputException("Pump " + tokens[0] + " is duplicated");
		if (tokens.length < 4) {
			throw new InputException("A value is missing in the pump " + id + " configuration line");
		}
		Pump link = new Pump();
		link.setId(id);

		Node to = net.getNode(tokens[1]);
		if (to == null) {
			throw new InputException("Don't exist the node with id " + tokens[1]);
		}
		Node from = net.getNode(tokens[2]);
		if (from == null) {
			throw new InputException("Don't exist the node with id " + tokens[2]);
		}

		int propertySize = tokens.length - 3; // Length of tokens without the id, node1 and node2.
		if (propertySize % 2 != 0) { // Properties are key and value
			throw new InputException("Properties of pump " + id + " are bad defined. Is missing a key or a value");
		}

		for (int i = 3; i < tokens.length; i += 2) {
			if (tokens[i].equalsIgnoreCase("HEAD")) {
				Curve curve = net.getCurve(tokens[i + 1]);
				if (curve == null)
					throw new InputException("Don't exist the curve " + tokens[i + 1] + "that is used in pump " + id);

				link.setProperty(Pump.PumpProperty.HEAD, curve);
			} else if (tokens[i].equalsIgnoreCase("PATTERN")) {
				Pattern pattern = net.getPattern(tokens[i + 1]);
				if (pattern == null)
					throw new InputException("Don't exist the pattern " + tokens[i + 1] + "that is used in pump " + id);

				link.setProperty(Pump.PumpProperty.PATTERN, pattern);

			} else if (tokens[i].equalsIgnoreCase("SPEED")) {
				link.setProperty(Pump.PumpProperty.SPEED, Double.parseDouble(tokens[i + 1]));

			} else if (tokens[i].equalsIgnoreCase("POWER")) {
				link.setProperty(Pump.PumpProperty.POWER, Double.parseDouble(tokens[i + 1]));
			} else {
				throw new InputException("Don't exist the property " + tokens[i] + " for pumps");
			}
		}

		link.setNode1(to);
		link.setNode2(from);

		net.addLink(id, link);
	}

	/**
	 * Parse the VALVES section of inp file
	 * 
	 * @param net    network
	 * @param tokens token of the line being read
	 * @throws EpanetException
	 */
	private void parseValve(Network net, String[] tokens) throws InputException {
		if (net.getLink(tokens[0]) != null)
			throw new InputException("Valve " + tokens[0] + " is duplicated");
		Valve link = new Valve();
		link.setId(tokens[0]);

		Node to = net.getNode(tokens[1]);
		if (to == null) {
			throw new InputException("Don't exist the node with id " + tokens[1]);
		}
		Node from = net.getNode(tokens[2]);
		if (from == null) {
			throw new InputException("Don't exist the node with id " + tokens[2]);
		}
		link.setNode1(to);
		link.setNode2(from);

		link.setDiameter(Double.parseDouble(tokens[3]));
		link.setType(tokens[4]);
		link.setSetting(tokens[5]);
		link.setMinorLoss(Double.parseDouble(tokens[6]));
		net.addLink(tokens[0], link);
	}

	/**
	 * Parse the EMITTER section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseEmmiter(Network net, String[] tokens) {
		Emitter emitter = new Emitter();
		emitter.setJunctionID(tokens[0]);
		emitter.setCoefficient(Double.parseDouble(tokens[1]));
		net.addEmiter(emitter);
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
		String id = tokens[0];
		Curve curve;
		if (net.getCurveList().size() == 0) {
			curve = new Curve();
			curve.setId(id);
			net.addCurve(id, curve);
		} else {
			curve = net.getCurve(id);
		}
		List<Double> x = curve.getX();
		List<Double> y = curve.getY();

		x.add(Double.parseDouble(tokens[1]));
		y.add(Double.parseDouble(tokens[2]));
	}

	/**
	 * Parse the PATTERNS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parsePattern(Network net, String[] tokens) {
		String id = tokens[0];
		Pattern pattern;
		if (net.getPatternList().size() == 0) {
			pattern = new Pattern();
			pattern.setId(id);
		} else {
			pattern = net.getPattern(id);
		}

		for (int i = 1; i < tokens.length; i++) {
			pattern.addMultipliers(Double.parseDouble(tokens[i]));
		}
		net.addPattern(id, pattern);
	}

	/**
	 * Parse the ENERGY section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseEnergy(Network net, String[] tokens, String line) {
		Energy energy = new Energy();
		energy.setCode(line);
		net.addEnergy(energy);
	}

	/**
	 * Parse the Status section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseStatus(Network net, String[] tokens) {
		Status status = new Status();
		status.setLinkId(tokens[0]);
		status.setStatus(tokens[1]);

		net.addStatus(status);
	}

	/**
	 * Parse the CONTROLS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 * @throws InputException if there is a error in controls section
	 */
	private void parseControl(Network net, String[] tokens) throws InputException {
		Control control = new Control();
		control.setLinkId(tokens[1]);
		if (tokens[2].equalsIgnoreCase("OPEN")) {
			control.setStatType(Control.StatType.OPEN);
		} else if (tokens[2].equalsIgnoreCase("CLOSED")) {
			control.setStatType(Control.StatType.CLOSE);

		} else {
			control.setStatType(Control.StatType.VALUE);
			control.setStatValue(Double.parseDouble(tokens[2]));
		}

		if (tokens[3].equalsIgnoreCase("IF")) { // LINK linkID status IF NODE nodeID ABOVE/BELOW value
			if (tokens[4].equalsIgnoreCase("NODE")) {
				control.setNodeId(tokens[5]);
				if (tokens[6].equalsIgnoreCase("ABOVE")) {
					control.setControlType(Control.ControlType.IF_ABOVE);
				} else if (tokens[6].equalsIgnoreCase("BELOW")) {
					control.setControlType(Control.ControlType.IF_BELOW);
				} else {
					throw new InputException(tokens[6] + "isn't a valid token for CONTROL section");
				}
				control.setValue(Double.parseDouble(tokens[7]));
			} else {
				throw new InputException(tokens[4] + "isn't a valid token for CONTROL section");
			}

		} else if (tokens[3].equalsIgnoreCase("AT")) {
			if (tokens[4].equalsIgnoreCase("TIME")) { // LINK linkID status AT TIME time
				control.setTime(Integer.parseInt(tokens[5]));
				control.setControlType(Control.ControlType.AT_TIME);
			} else if (tokens[4].equalsIgnoreCase("CLOCKTIME")) { // LINK linkID status AT CLOCKTIME clocktime AM/PM
				control.setClocktime(tokens[5] + " " + tokens[6]); // Example 12:32 AM
				control.setControlType(Control.ControlType.AT_CLOCKTIME);
			} else {
				throw new InputException(tokens[4] + "isn't a valid token for CONTROL section");
			}
		} else {
			throw new InputException(tokens[3] + "isn't a valid token for CONTROL section");
		}
		net.addControl(control);
	}

	/**
	 * Parse the RULES section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseRule(Network net, String[] tokens, String line) {
		if (tokens[0].equalsIgnoreCase("RULE")) {
			currentRule = new Rule();
			currentRule.setRuleId(tokens[1]);
			net.addRule(currentRule);

		} else if (currentRule != null) {
			String code = currentRule.getCode();
			if (code == null) {
				code = line;
			} else {
				code = "\n" + line;
			}
			currentRule.setCode(code);
		}

	}

	/**
	 * Parse the DEMANDS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseDemand(Network net, String[] tokens) {
		Demand demand = new Demand();
		demand.setId(tokens[0]);
		demand.setDemand(Double.parseDouble(tokens[1]));
		if (tokens.length > 3) {
			if (net.getPattern(tokens[2]) != null) {
				demand.setDemandPatern(net.getPattern(tokens[2]));
			}
		}
		if (tokens.length == 4) {
			demand.setDemandCategory(";MISS");
		}

		net.addDemand(demand);
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
		Quality quality = new Quality();
		quality.setNodeId(tokens[0]);
		quality.setInitialQuality(Double.parseDouble(tokens[1]));

		net.addQuality(quality);
	}

	/**
	 * Parse the REACTION section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseReaction(Network net, String[] tokens, String line) {
		Reaction reaction;
		if (net.getReaction() == null) {
			reaction = new Reaction();
			net.setReaction(reaction);
		} else {
			reaction = net.getReaction();
		}
		// Add lines with configuration
		String code = reaction.getCode();

		if (code == null) {
			reaction.setCode(line);
		} else {
			reaction.setCode(code + "\n" + line);
		}

	}

	/**
	 * Parse the SOURCES section of inp file
	 * 
	 * @param net
	 * @param tokens
	 * @throws InputException if there is a error in SOURCE SECTION
	 */
	private void parseSource(Network net, String[] tokens) throws InputException {
		Source source = new Source();
		source.setNodeId(tokens[0]);
		if (tokens[1].equalsIgnoreCase("CONCEN")) {
			source.setSourceType(Source.SourceType.CONCEN);
		} else if (tokens[1].equalsIgnoreCase("MASS")) {
			source.setSourceType(Source.SourceType.MASS);
		} else if (tokens[1].equalsIgnoreCase("FLOWPACED")) {
			source.setSourceType(Source.SourceType.FLOWPACED);
		} else if (tokens[1].equalsIgnoreCase("SETPOINT")) {
			source.setSourceType(Source.SourceType.SETPOINT);
		} else {
			throw new InputException(tokens[1] + " isn't a valid token for SOURCE section");
		}

		source.setBaselineStrenth(Double.parseDouble(tokens[2]));

		if (tokens.length == 4) {
			if (net.getPattern(tokens[4]) == null) {
				throw new InputException("Don't exist the pattern " + tokens[4] + " for this source");
			}
			source.setTimePattern(net.getPattern(tokens[4]));
		}

		net.addSource(source);
	}

	/**
	 * Parse the MIXINGS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 * @throws InputException If there is a error in MIXING section
	 */
	private void parseMixing(Network net, String[] tokens) throws InputException {
		Mixing mixing = new Mixing();
		mixing.setTankId(tokens[0]);

		if (tokens[1].equalsIgnoreCase("MIXED")) {
			mixing.setModel(Mixing.MixingModel.MIXED);
		} else if (tokens[1].equalsIgnoreCase("2COMP")) {
			mixing.setModel(Mixing.MixingModel.TWOCOMP);
			mixing.setCompartmentVolume(Double.parseDouble(tokens[2]));
		} else if (tokens[1].equalsIgnoreCase("FIFO")) {
			mixing.setModel(Mixing.MixingModel.FIFO);

		} else if (tokens[1].equalsIgnoreCase("LIFO")) {
			mixing.setModel(Mixing.MixingModel.LIFO);

		} else {
			throw new InputException(tokens[1] + "isn't a valid token in MIXING section");
		}

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
	private void parseOption(Network net, String[] tokens, String line) {
		Option option;
		if (net.getOption() == null) {
			option = new Option();
			net.setOption(option);
		} else {
			option = net.getOption();
		}
		// Add lines with configuration
		String code = option.getCode();

		if (code == null) {
			option.setCode(line);
		} else {
			option.setCode(code + "\n" + line);
		}
	}

	/**
	 * Parse the TIMES section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseTime(Network net, String[] tokens, String line) {
		Time time;
		if (net.getTime() == null) {
			time = new Time();
			net.setTime(time);
		} else {
			time = net.getTime();
		}
		// Add lines with configuration
		String code = time.getCode();

		if (code == null) {
			time.setCode(line);
		} else {
			time.setCode(code + "\n" + line);
		}
	}

	/**
	 * Parse the REPORT section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseReport(Network net, String[] tokens, String line) {
		Report report;
		if (net.getReport() == null) {
			report = new Report();
			net.setReport(report);
		} else {
			report = net.getReport();
		}
		// Add lines with configuration
		String code = report.getCode();

		if (code == null) {
			report.setCode(line);
		} else {
			report.setCode(code + "\n" + line);
		}

	}

	/*********************************************************
	 * Network Design
	 *********************************************************/

	/**
	 * Parse the COORDINATES section of inp file
	 * 
	 * @param net @param tokens @throws
	 */
	private void parseCoordinate(Network net, String[] tokens) throws InputException {
		Node node = net.getNode(tokens[0]);
		if (node == null)
			throw new InputException("Node " + tokens[0] + " don't exist");

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
	private void parseVertice(Network net, String[] tokens) throws InputException {
		Link link = net.getLink(tokens[0]);
		if (link == null)
			throw new InputException("Link " + tokens[0] + " don't exist");
		double x = Double.parseDouble(tokens[1].replace(",", "."));
		double y = Double.parseDouble(tokens[2].replace(",", "."));
		link.getVertices().add(new Point(x, y));
	}

	/**
	 * Parse the LABELS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 * @throws InputException If there is a error in LABELS section
	 */
	private void parseLabel(Network net, String[] tokens) throws InputException {
		Label label = new Label();
		double x = Double.parseDouble(tokens[0]);
		double y = Double.parseDouble(tokens[1]);
		label.setPosition(new Point(x, y));
		label.setLabel(String.format("%s", tokens[2]));
		if (tokens.length == 4) {
			Node node = net.getNode(tokens[4]);
			if (node == null) {
				throw new InputException("The node "+tokens[3]+ " don't exist");
			}
			label.setAnchorNode(node);
		}
		
		net.addLabel(label);
	}

	/**
	 * Parse the Backdrop section of inp file
	 * 
	 * @param net
	 * @param tokens
	 */
	private void parseBackdrop(Network net, String[] tokens, String line) {
		Backdrop backdrop;
		if (net.getBackdrop() == null) {
			backdrop = new Backdrop();
			net.setBackdrop(backdrop);
		} else {
			backdrop = net.getBackdrop();
		}
		// Add lines with configuration
		String code = backdrop.getCode();

		if (code == null) {
			backdrop.setCode(line);
		} else {
			backdrop.setCode(code + "\n" + line);
		}
	}

	/**
	 * Parse the TAGS section of inp file
	 * 
	 * @param net
	 * @param tokens
	 * @throws InputException if there is a error in TAGS section
	 */
	private void parseTag(Network net, String[] tokens) throws InputException {
		Tag tag = new Tag();
		if (tokens.length != 3) {
			throw new InputException("There are a error in TAGS section associed with the number of parameters");
		}
		if (tokens[0].equalsIgnoreCase("NODE")) {
			tag.setType(Tag.TagType.NODE);
		}
		if (tokens[0].equalsIgnoreCase("LINK")) {
			tag.setType(Tag.TagType.LINK);
		}
		else {
			throw new InputException(tokens[0] + " isn't a valid token for TAGS section");
		}
		tag.setId(tokens[1]);
		tag.setLabel(tokens[2]);
		net.addTag(tag);
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
		try {
			System.out.println(parse.parse(network, "inp/red1.inp"));
			InpWriter writer = new InpWriter();
			writer.write(network, "red1writer.inp");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
