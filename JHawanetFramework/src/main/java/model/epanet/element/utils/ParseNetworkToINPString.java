package model.epanet.element.utils;

import java.util.Collection;
import java.util.List;

import model.epanet.element.Network;
import model.epanet.element.networkcomponent.Emitter;
import model.epanet.element.networkcomponent.Junction;
import model.epanet.element.networkcomponent.Link;
import model.epanet.element.networkcomponent.Node;
import model.epanet.element.networkcomponent.Pipe;
import model.epanet.element.networkcomponent.Pump;
import model.epanet.element.networkcomponent.Pump.PumpProperty;
import model.epanet.element.networkcomponent.Pump.PumpStatus;
import model.epanet.element.networkcomponent.Reservoir;
import model.epanet.element.networkcomponent.Tank;
import model.epanet.element.networkcomponent.Valve;
import model.epanet.element.networkcomponent.Valve.ValveStatus;
import model.epanet.element.networkdesign.Backdrop;
import model.epanet.element.networkdesign.Label;
import model.epanet.element.networkdesign.Tag;
import model.epanet.element.optionsreport.Option;
import model.epanet.element.optionsreport.QualityOption;
import model.epanet.element.optionsreport.Report;
import model.epanet.element.optionsreport.Time;
import model.epanet.element.systemoperation.Control;
import model.epanet.element.systemoperation.Curve;
import model.epanet.element.systemoperation.Demand;
import model.epanet.element.systemoperation.EnergyOption;
import model.epanet.element.systemoperation.Pattern;
import model.epanet.element.systemoperation.Rule;
import model.epanet.element.waterquality.Mixing;
import model.epanet.element.waterquality.Mixing.MixingModel;
import model.epanet.element.waterquality.Quality;
import model.epanet.element.waterquality.ReactionOption;
import model.epanet.element.waterquality.Source;

public final class ParseNetworkToINPString {

	/**
	 * Create a string with the config of network in the format of inp file
	 * 
	 * @param network the network
	 * @return the string that represent the network
	 */
	public static String parse(Network network) {
		StringBuilder out = new StringBuilder();

		createTitle(out, network.getTitle());
		createJunction(out, network.getJunctions());
		createReservoir(out, network.getReservoirs());
		createTanks(out, network.getTanks());
		createPipe(out, network.getPipes());
		createPump(out, network.getPumps());
		createValve(out, network.getValves());
		createEmmiter(out, network.getJunctions());
		createEnergy(out, network.getEnergyOption(), network.getPumps());
		createStatus(out, network.getPipes(), network.getPumps(), network.getValves());
		createPattern(out, network.getPatternList());
		createCurves(out, network.getCurveList());
		createControl(out, network.getControl());
		createRule(out, network.getRule());
		createDemand(out, network.getJunctions());
		createQuality(out, network.getJunctions(), network.getReservoirs(), network.getTanks());
		createReaction(out, network.getReactionOption(), network.getPipes(), network.getTanks());
		createSource(out, network.getJunctions(), network.getReservoirs(), network.getTanks());
		createMixing(out, network.getTanks());
		createOption(out, network.getOption(), network.getQualityOption());
		createTime(out, network.getTime());
		createReport(out, network.getReport());
		createCoordinate(out, network.getNodes());
		createVertice(out, network.getLinks());
		createLabel(out, network.getLabels());
		createBackdrop(out, network.getBackdrop());
		createTag(out, network.getNodes(), network.getLinks());

		out.append("[END]");

		return out.toString().replace(",", ".");
	}

	private static void createTitle(StringBuilder out, String title) {
		out.append("[TITLE]\n");
		out.append(title + "\n");

	}

	private static void createJunction(StringBuilder out, List<Junction> junctions) {
		out.append("[Junction]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\n", "ID", "Elev", "Demand", "Pattern"));
		for (Junction junction : junctions) {
			out.append(String.format("%-10s\t", junction.getId()));
			out.append(String.format("%-10f\t", junction.getElevation()));
			out.append(String.format("%-10f\t", junction.getBaseDemand()));
			out.append(String.format("%-10s", junction.getDemandPattern()));
			String description = junction.getDescription();
			if (!description.isEmpty()) {
				out.append(String.format(";%s", description));
			}
			out.append("\n");

		}
		out.append("\n");
	}

	private static void createReservoir(StringBuilder out, List<Reservoir> reservoirs) {
		out.append("[RESERVOIR]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\n", "ID", "Head", "Pattern"));
		for (Reservoir reservoir : reservoirs) {
			out.append(String.format("%-10s\t", reservoir.getId()));
			out.append(String.format("%-10f\t", reservoir.getTotalHead()));
			out.append(String.format("%-10s\t", reservoir.getHeadPattern()));
			String description = reservoir.getDescription();
			if (!description.isEmpty()) {
				out.append(String.format(";%s", description));
			}
			out.append("\n");

		}
		out.append("\n");
	}

	private static void createTanks(StringBuilder out, List<Tank> tanks) {
		out.append("[TANK]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n", "ID", "Elevation",
				"InitLevel", "MinLevel", "MaxLevel", "Diameter", "MinVol", "VolCurve"));
		for (Tank tank : tanks) {
			out.append(String.format("%-10s\t", tank.getId()));
			out.append(String.format("%-10f\t", tank.getElevation()));
			out.append(String.format("%-10f\t", tank.getInitialLevel()));
			out.append(String.format("%-10f\t", tank.getMinimumLevel()));
			out.append(String.format("%-10f\t", tank.getMaximumLevel()));
			out.append(String.format("%-10f\t", tank.getDiameter()));
			out.append(String.format("%-10f\t", tank.getMinimumVolume()));
			out.append(String.format("%-10s", tank.getVolumeCurve()));
			String description = tank.getDescription();
			if (!description.isEmpty()) {
				out.append(String.format(";%s", description));
			}
			out.append("\n");

		}
		out.append("\n");
	}

	private static void createPipe(StringBuilder out, List<Pipe> pipes) {
		out.append("[PIPE]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n", "ID", "Node1", "Node2",
				"Length", "Diameter", "Roughness", "MinorLoss", "Status"));
		for (Pipe pipe : pipes) {
			out.append(String.format("%-10s\t", pipe.getId()));
			out.append(String.format("%-10s\t", pipe.getNode1().getId()));
			out.append(String.format("%-10s\t", pipe.getNode2().getId()));
			out.append(String.format("%-10f\t", pipe.getLength()));
			out.append(String.format("%-10f\t", pipe.getDiameter()));
			out.append(String.format("%-10f\t", pipe.getRoughness()));
			out.append(String.format("%-10f\t", pipe.getLossCoefficient()));
			out.append(String.format("%-10s", pipe.getStatus().getName()));
			String description = pipe.getDescription();
			if (!description.isEmpty()) {
				out.append(String.format(";%s", description));
			}
			out.append("\n");

		}
		out.append("\n");
	}

	private static void createPump(StringBuilder out, List<Pump> pumps) {
		out.append("[PUMP]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\n", "ID", "Node1", "Node2", "Parameters"));
		for (Pump pump : pumps) {
			out.append(String.format("%-10s\t", pump.getId()));
			out.append(String.format("%-10s\t", pump.getNode1().getId()));
			out.append(String.format("%-10s\t", pump.getNode2().getId()));

			for (PumpProperty key : pump.getPropertyKeys()) {
				if (PumpProperty.HEAD == key) {
					String curve = (String) pump.getProperty(key);
					out.append(String.format("HEAD %-10s\t", curve));

				} else if (PumpProperty.PATTERN == key) {
					String pattern = (String) pump.getProperty(key);
					out.append(String.format("PATTERN %-10s\t", pattern));

				} else if (PumpProperty.POWER == key) {
					double value = (Double) pump.getProperty(key);
					out.append(String.format("POWER %-10f\t", value));

				} else if (PumpProperty.SPEED == key) {
					double value = (Double) pump.getProperty(key);
					out.append(String.format("SPEED %-10f\t", value));

				}
			}
			String description = pump.getDescription();
			if (!description.isEmpty()) {
				out.append(String.format(";%s", description));
			}
			out.append("\n");

		}
		out.append("\n");
	}

	private static void createValve(StringBuilder out, List<Valve> valves) {
		out.append("[VALVE]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\t%-10s\n", "ID", "Node1", "Node2",
				"Diameter", "Type", "Setting", "MinorLoss"));
		for (Valve valve : valves) {
			out.append(String.format("%-10s\t", valve.getId()));
			out.append(String.format("%-10s\t", valve.getNode1().getId()));
			out.append(String.format("%-10s\t", valve.getNode2().getId()));
			out.append(String.format("%-10f\t", valve.getDiameter()));
			out.append(String.format("%-10s\t", valve.getType()));
			out.append(String.format("%-10s\t", valve.getSetting()));
			out.append(String.format("%-10f", valve.getLossCoefficient()));
			String description = valve.getDescription();
			if (!description.isEmpty()) {
				out.append(String.format(";%s", description));
			}
			out.append("\n");

		}
		out.append("\n");
	}

	private static void createEmmiter(StringBuilder out, List<Junction> junctions) {
		out.append("[EMITTER]\n");
		out.append(String.format(";%-10s\t%-10s\n", "Junction", "Coefficient"));
		for (Junction junction : junctions) {
			Emitter emitter = junction.getEmitter();
			if (emitter != null) {
				out.append(String.format("%-10s\t", junction.getId()));
				out.append(String.format("%-10f", emitter.getCoefficient()));
				out.append("\n");
			}
		}
		out.append("\n");
	}

	private static void createEnergy(StringBuilder out, EnergyOption energy, List<Pump> pumps) {
		out.append("[ENERGY]\n");
		if (energy != null) {
			out.append(String.format("%-20s\t%10f\n", "Global Efficiency", energy.getGlobalEfficiency()));
			out.append(String.format("%-20s\t%10f\n", "Global Price", energy.getGlobalPrice()));
			String globalPattern = energy.getGlobalPattern();
			if (!globalPattern.isEmpty()) {
				out.append(String.format("%-20s\t%10s\n", "Global Pattern", globalPattern));	
			}
			out.append(String.format("%-20s\t%10f\n", "Demand Charge", energy.getGlobalPrice()));
		}

		for (Pump pump : pumps) {
			String efficiencyCurve = pump.getEfficiencyCurve();
			if (!efficiencyCurve.isEmpty()) {
				out.append(String.format("%-10s\t%-10s\t%-10s\t%-10s\n", "Pump", pump.getId(), "Efficiency",
						efficiencyCurve));
			}
			Double energyPrice = pump.getEnergyPrice();
			if (energyPrice != null) {
				out.append(String.format("%-10s\t%-10s\t%-10s\t%-10f\n", "Pump", pump.getId(), "Price", energyPrice));
			}

			String patternPrice = pump.getPricePattern();
			if (!patternPrice.isEmpty()) {
				out.append(
						String.format("%-10s\t%-10s\t%-10s\t%-10s\n", "Pump", pump.getId(), "Pattern", patternPrice));
			}

		}
		out.append("\n");
	}

	private static void createStatus(StringBuilder out, List<Pipe> pipes, List<Pump> pumps, List<Valve> valves) {
		out.append("[STATUS]\n");
		out.append(String.format(";%-10s\t%-10s\n", "ID", "Status/Setting"));
		for (Pump pump : pumps) {
			Double speed = (Double) pump.getProperty(PumpProperty.SPEED);
			if (speed != null) {
				out.append(String.format("%-10s\t%-10f\n", pump.getId(), speed));
			}
			PumpStatus status = pump.getStatus();
			if (status == PumpStatus.CLOSED) {
				out.append(String.format("%-10s\t%-10s\n", pump.getId(), status.getName()));
			}

		}
		for (Valve valve : valves) {
			ValveStatus status = valve.getFixedStatus();
			if (status != ValveStatus.NONE) {
				out.append(String.format("%-10s\t%-10s\n", valve.getId(), status.getName()));
			}
		}
		out.append("\n");
	}

	private static void createPattern(StringBuilder out, List<Pattern> patternList) {
		out.append("[PATTERN]\n");
		out.append(String.format(";%-10s\t%-10s\n", "ID", "Multipliers"));
		for (Pattern pattern : patternList) {
			List<Double> multipliers = pattern.getMultipliers();
			int i = 1;
			out.append(String.format("%-10s\t", pattern.getId()));
			for (Double multiplier : multipliers) {
				out.append(String.format("%-10f\t", multiplier));
				if (i % 6 == 0 && i != multipliers.size()) {
					out.append("\n");
					out.append(String.format("%-10s\t", pattern.getId()));
				}
				i++;
			}
			out.append("\n");
		}
		out.append("\n");
	}

	private static void createCurves(StringBuilder out, List<Curve> curveList) {
		out.append("[CURVE]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\n", "ID", "X-Value", "Y-Value"));
		for (Curve curve : curveList) {
			int numberOfPoint = curve.getNumberOfPoint();
			for (int i = 0; i < numberOfPoint; i++) {
				Point point = curve.getPoint(i);
				out.append(String.format("%-10s\t", curve.getId()));
				out.append(String.format("%-10s\t", point.getX()));
				out.append(String.format("%-10s\t", point.getY()));
				out.append("\n");
			}
		}
		out.append("\n");
	}

	private static void createControl(StringBuilder out, Control control) {
		// TODO REVISAR esto (Deberia cambiarlo a string?)
		out.append("[CONTROL]\n");
		if (control != null) {
			out.append(control.getCode());
		}

		out.append("\n");
	}

	private static void createRule(StringBuilder out, Rule rule) {
		out.append("[RULE]\n");
		if (rule != null) {
			out.append(rule.getCode());
		}
		out.append("\n");
	}

	private static void createDemand(StringBuilder out, List<Junction> junctions) {
		out.append("[DEMAND]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\n", "Junction", "Demand", "Pattern", "Category"));
		for (Junction junction : junctions) {
			if (junction.getDemandCategories().size() > 1) {
				for (Demand demand : junction.getDemandCategories()) {
					out.append(String.format("%-10s\t", junction.getId()));
					out.append(String.format("%-10f\t", demand.getDemand()));
					out.append(String.format("%-10s\t", demand.getDemandPattern()));
					if (!demand.getDemandCategory().isEmpty()) {
						out.append(String.format(";%-10s\t", demand.getDemandCategory()));
					}
					out.append("\n");
				}
			}
		}
		out.append("\n");
	}

	private static void createQuality(StringBuilder out, List<Junction> junctions, List<Reservoir> reservoirs,
			List<Tank> tanks) {
		out.append("[QUALITY]\n");
		out.append(String.format(";%-10s\t%-10s\n", "Node", "InitQual"));
		for (Junction junction : junctions) {
			Quality quality = junction.getInitialQuality();
			if (quality != null) {
				out.append(String.format(";%-10s\t%-10s", junction.getId(), quality.getInitialQuality()));
				out.append("\n");

			}
		}
		for (Reservoir reservoir : reservoirs) {
			Quality quality = reservoir.getInitialQuality();
			if (quality != null) {
				out.append(String.format(";%-10s\t%-10s", reservoir.getId(), quality.getInitialQuality()));
				out.append("\n");

			}
		}
		for (Tank tank : tanks) {
			Quality quality = tank.getInitialQuality();
			if (quality != null) {
				out.append(String.format(";%-10s\t%-10s", tank.getId(), quality.getInitialQuality()));
				out.append("\n");

			}
		}
		out.append("\n");

	}

	private static void createReaction(StringBuilder out, ReactionOption reaction, List<Pipe> pipes, List<Tank> tanks) {
		out.append("[REACTION]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\n", "Type", "Pipe/Tank ", "Coefficient"));
		for (Pipe pipe : pipes) {
			Double reac = pipe.getBulkCoefficient();
			if (reac != null) {
				out.append(String.format("Bulk\t%10f\n", reac));
				out.append("\n");
			}
			reac = pipe.getWallCoefficient();
			if (reac != null) {
				out.append(String.format("Wall\t%10f\n", reac));
				out.append("\n");
			}
		}
		for (Tank tank : tanks) {
			Double reac = tank.getReactionCoefficient();
			if (reac != null) {
				out.append(String.format("Tank\t%10f\n", reac));
				out.append("\n");
			}
		}
		out.append("\n");

		out.append("[REACTION]\n");
		if (reaction != null) {
			// In the inp order tank has always the same value that order bulk
			out.append(String.format("%-22s\t%-10f\n", "Order Bulk", reaction.getOrderBulk()));
			out.append(String.format("%-22s\t%-10f\n", "Order Tank", reaction.getOrderBulk())); // this line isn't a
																								// mistake.
			out.append(String.format("%-22s\t%-10d\n", "Order Wall", reaction.getOrderWall()));
			out.append(String.format("%-22s\t%-10f\n", "Global Bulk", reaction.getGlobalBulk()));
			out.append(String.format("%-22s\t%-10f\n", "Global Wall", reaction.getGlobalWall()));
			out.append(String.format("%-22s\t%-10f\n", "Limiting Potential", reaction.getLimitingPotential()));
			out.append(String.format("%-22s\t%-10f\n", "Roughness Correlation", reaction.getRoughnessCorrelation()));
		}
		out.append("\n");
	}

	private static void createSource(StringBuilder out, List<Junction> junctions, List<Reservoir> reservoirs,
			List<Tank> tanks) {
		out.append("[SOURCE]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\n", "Node", "Type", "Quality", "Pattern"));
		for (Junction junction : junctions) {
			Source source = junction.getSourceQuality();
			if (source != null) {
				out.append(String.format("%-10s\t", junction.getId()));
				out.append(String.format("%-10s\t", source.getSourceType().getName()));
				out.append(String.format("%-10f\t", source.getBaselineStrenth()));
				if (!source.getTimePattern().isEmpty()) {
					out.append(String.format("%-10s", source.getTimePattern()));
				}
				out.append("\n");
			}
		}
		for (Reservoir reservoir : reservoirs) {
			Source source = reservoir.getSourceQuality();
			if (source != null) {
				out.append(String.format("%-10s\t", reservoir.getId()));
				out.append(String.format("%-10s\t", source.getSourceType().getName()));
				out.append(String.format("%-10f\t", source.getBaselineStrenth()));
				if (!source.getTimePattern().isEmpty()) {
					out.append(String.format("%-10s", source.getTimePattern()));
				}
				out.append("\n");
			}
		}
		for (Tank tank : tanks) {
			Source source = tank.getSourceQuality();
			if (source != null) {
				out.append(String.format("%-10s\t", tank.getId()));
				out.append(String.format("%-10s\t", source.getSourceType().getName()));
				out.append(String.format("%-10f\t", source.getBaselineStrenth()));
				if (!source.getTimePattern().isEmpty()) {
					out.append(String.format("%-10s", source.getTimePattern()));
				}
				out.append("\n");
			}
		}
		out.append("\n");

	}

	private static void createMixing(StringBuilder out, List<Tank> tanks) {
		out.append("[MIXING]\n");
		out.append(String.format(";%-10s\t%-10s\n", "Tank", "Model"));
		for (Tank tank : tanks) {
			Mixing mixing = tank.getMixing();
			if (mixing.getModel() != MixingModel.MIXED) {
				out.append(String.format("%-10s\t", tank.getId()));
				out.append(String.format("%-10s\t", mixing.getModel().getName()));
				if (mixing.getMixingFraction() != null) {
					out.append(String.format("%-10f", mixing.getMixingFraction()));

				}
			}
			out.append("\n");
		}
		out.append("\n");
	}

	private static void createOption(StringBuilder out, Option option, QualityOption qualityOption) {
		out.append("[OPTION]\n");
		if (option != null) {
			out.append(String.format("%-18s\t%-10s\n", "UNITS", option.getFlowUnit().getName()));
			out.append(String.format("%-18s\t%-10s\n", "HEADLOSS", option.getHeadlossFormule().getName()));
			if (!option.getHydraulic().isEmpty()) {
				out.append(String.format("%-18s\t%-10s\n", "HYDRAULICS", option.getHydraulic()));

			}
			out.append(String.format("%-18s\t%-10f\n", "VISCOSITY", option.getViscosity()));
			out.append(String.format("%-18s\t%-10f\n", "SPECIFIC GRAVITY", option.getSpecificGravity()));
			out.append(String.format("%-18s\t%-10f\n", "TRIALS", option.getTrials()));
			out.append(String.format("%-18s\t%-10f\n", "ACCURACY", option.getAccuracy()));
			out.append(String.format("%-18s\t%-10s\n", "UNBALANCED", option.getUnbalanced()));
			out.append(String.format("%-18s\t%-10s\n", "PATTERN", option.getPattern()));
			out.append(String.format("%-18s\t%-10f\n", "DEMAND MULTIPLIER", option.getDemandMultiplier()));
			out.append(String.format("%-18s\t%-10s\n", "EMITTER EXPONENT", option.getEmitterExponent()));
			if (!option.getMap().isEmpty()) {
				out.append(String.format("%-18s\t%-10s\n", "MAP", option.getMap()));
			}
			out.append(String.format("%-18s\t%-10f\n", "CHECKFREQ", option.getCheckfreq()));
			out.append(String.format("%-18s\t%-10f\n", "MAXCHECK", option.getMaxcheck()));
			out.append(String.format("%-18s\t%-10f\n", "DAMPLIMIT", option.getDamplimit()));
		}
		if (qualityOption != null) {
			out.append(String.format("%-18s\t%s ", "QUALITY", qualityOption.getParameter()));
			if (qualityOption.getParameter().equalsIgnoreCase("TRACE")) {
				if (qualityOption.getTraceNodeID() != null) {
					out.append(String.format("%-10s", qualityOption.getTraceNodeID()));
				}
				out.append("\n");
			} else {
				out.append(String.format("%-10s", qualityOption.getMassUnit().getName()));
				out.append("\n");

			}
			out.append(String.format("%-18s\t%-10f\n", "DIFFUSIVITY", qualityOption.getRelativeDiffusivity()));
			out.append(String.format("%-18s\t%-10f\n", "TOLERANCE", qualityOption.getQualityTolerance()));
		}
		out.append("\n");
	}

	private static void createTime(StringBuilder out, Time time) {
		out.append("[TIME]\n");
		if (time != null) {
			out.append(String.format("%-18s\t%-10s\n", "Duration", time.getDuration()));
			out.append(String.format("%-18s\t%-10s\n", "Hydraulic Timestep", time.getHydraulicTimestep()));
			out.append(String.format("%-18s\t%-10s\n", "Quality Timestep", time.getQualityTimestep()));
			out.append(String.format("%-18s\t%-10s\n", "Pattern Timestep", time.getPatternTimestep()));
			out.append(String.format("%-18s\t%-10s\n", "Pattern Start", time.getPatternStart()));
			out.append(String.format("%-18s\t%-10s\n", "Report Timestep", time.getReportTimestep()));
			out.append(String.format("%-18s\t%-10s\n", "Report Start", time.getReportStart()));
			out.append(String.format("%-18s\t%-10s\n", "Start ClockTime", time.getStartClockTime()));
			out.append(String.format("%-18s\t%-10s\n", "Statistic", time.getStatistic().getName()));
		}
		out.append("\n");
	}

	private static void createReport(StringBuilder out, Report report) {
		out.append("[REPORT]\n");
		if (report != null) {
			out.append(report.getCode() + "\n");
		}
		out.append("\n");
	}

	private static void createCoordinate(StringBuilder out, Collection<Node> nodes) {
		out.append("[COORDINATES]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\n", "Node", "X-Coord", "Y-Coord"));
		for (Node node : nodes) {
			out.append(String.format("%-10s\t%s\n", node.getId(), node.getPosition()));
		}

		out.append("\n");
	}

	private static void createVertice(StringBuilder out, Collection<Link> links) {
		out.append("[VERTICES]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\n", "Node", "X-Coord", "Y-Coord"));
		for (Link link : links) {
			for (Point point : link.getVertices()) {
				out.append(String.format("%-10s\t%s\n", link.getId(), point));
			}
		}
		out.append("\n");
	}

	private static void createLabel(StringBuilder out, List<Label> labels) {
		out.append("[LABELS]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\t%-10s\n", "X-Coord", "Y-Coord", "Label", "Anchor Node"));
		for (Label label : labels) {
			out.append(String.format("%s\t", label.getPosition()));
			out.append(String.format("%-10s\t", label.getLabel()));
			if (!label.getAnchorNode().isEmpty()) {
				out.append(String.format("%-10s", label.getAnchorNode()));
			}
			out.append("\n");
		}
		out.append("\n");
	}

	private static void createBackdrop(StringBuilder out, Backdrop backdrop) {
		out.append("[BACKDROP]\n");
		if (backdrop != null) {
			out.append(String.format("%-15s%-10f\t%-10f\t%-10f\t%-10f\n", "DIMENSION", backdrop.getXBottomLeft(),
					backdrop.getYBottomLeft(), backdrop.getXUpperRight(), backdrop.getYUpperRight()));
			out.append(String.format("%-15s%-10s\t\n", "UNITS", backdrop.getUnit().getName()));
			out.append(String.format("%-15s%s\n", "FILE", backdrop.getFile() != null ? backdrop.getFile() : ""));
			out.append(String.format("%-15s%-10f\t %-10f\n", "OFFSET", backdrop.getXOffset(), backdrop.getYOffset()));
		}
		out.append("\n");
	}

	private static void createTag(StringBuilder out, Collection<Node> nodes, Collection<Link> links) {
		out.append("[TAGS]\n");
		out.append(String.format(";%-10s\t%-10s\t%-10s\n", "Object", "ID", "Label"));
		for (Node node : nodes) {
			Tag tag = node.getTag();
			if (tag != null) {
				out.append(String.format("%-10s\t", tag.getType().getName()));
				out.append(String.format("%-10s\t", node.getId()));
				out.append(String.format("%-10s", tag.getLabel()));
				out.append("\n");

			}
		}
		for (Link link : links) {
			Tag tag = link.getTag();
			if (tag != null) {
				out.append(String.format("%-10s\t", tag.getType().getName()));
				out.append(String.format("%-10s\t", link.getId()));
				out.append(String.format("%-10s", tag.getLabel()));
				out.append("\n");

			}
		}
		out.append("\n");
	}

}
