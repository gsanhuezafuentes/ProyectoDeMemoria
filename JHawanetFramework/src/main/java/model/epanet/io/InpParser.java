package model.epanet.io;

import exception.InputException;
import model.epanet.element.Network;
import model.epanet.element.networkcomponent.*;
import model.epanet.element.networkcomponent.Pipe.PipeStatus;
import model.epanet.element.networkcomponent.Pump.PumpStatus;
import model.epanet.element.networkcomponent.Valve.ValveStatus;
import model.epanet.element.networkcomponent.Valve.ValveType;
import model.epanet.element.networkdesign.Backdrop;
import model.epanet.element.networkdesign.Backdrop.Unit;
import model.epanet.element.networkdesign.Label;
import model.epanet.element.networkdesign.Tag;
import model.epanet.element.optionsreport.Option;
import model.epanet.element.optionsreport.Option.FlowUnit;
import model.epanet.element.optionsreport.Option.HeadlossFormule;
import model.epanet.element.optionsreport.QualityOption;
import model.epanet.element.optionsreport.QualityOption.MassUnit;
import model.epanet.element.optionsreport.Report;
import model.epanet.element.optionsreport.Time;
import model.epanet.element.systemoperation.*;
import model.epanet.element.utils.ParseNetworkToINPString;
import model.epanet.element.utils.Point;
import model.epanet.element.waterquality.Mixing;
import model.epanet.element.waterquality.Mixing.MixingModel;
import model.epanet.element.waterquality.Quality;
import model.epanet.element.waterquality.ReactionOption;
import model.epanet.element.waterquality.Source;
import model.epanet.element.waterquality.Source.SourceType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Parse the INP file to get the coordinates and vertices of the water network.
 */
public class InpParser implements InputParser {

    private @Nullable Junction lastJunctionWithDemandAdded;

    /**
     * {@inheritDoc}
     *
     * @throws InputException if there is a error to parse the inp file
     * @throws IOException if file doesn't exist or there is a error in IO operation
     * @throws NullPointerException if net or filename are null
     */
    @Override
    public @NotNull Network parse(@NotNull Network net, @NotNull String filename) throws IOException, InputException {
        Objects.requireNonNull(net);
        Objects.requireNonNull(filename);

        parsePatternAndCurve(net, filename);
        try (BufferedReader buffReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.ISO_8859_1))) { // ISO-8859-1

            String line;
            String sectionType = "";
            while ((line = buffReader.readLine()) != null) {

                // remove the blank space at the beginning and the end
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }

                String comment = "";
                int semicolonIndex = line.indexOf(";");
                // if not exist ";" in line
                if (semicolonIndex != -1) {
                    // if exists and isn't is at the beginning
                    if (semicolonIndex > 0) {
                        comment = line.substring(semicolonIndex + 1).trim();
                        line = line.substring(0, semicolonIndex);
                    } else {
                        continue;
                    }
                }

                // split the line in token
                String[] tokens = line.split("[ \t]+");
                if (tokens.length == 0)
                    continue;

                if (tokens[0].contains("[")) {
                    sectionType = identifySectionType(tokens[0]);
                } else {
                    switch (sectionType) {
                        case "TITLE":
                            net.addLineToTitle(line + "\n");
                            break;
                        case "JUNCTIONS":
                            parseJunction(net, tokens, comment);
                            break;
                        case "RESERVOIRS":
                            parseReservoir(net, tokens, comment);
                            break;
                        case "TANKS":
                            parseTanks(net, tokens, comment);
                            break;
                        case "PIPES":
                            parsePipe(net, tokens, comment);
                            break;
                        case "PUMPS":
                            parsePump(net, tokens, comment);
                            break;
                        case "VALVES":
                            parseValve(net, tokens, comment);
                            break;
                        case "EMITTERS":
                            parseEmmiter(net, tokens);
                            break;
                        case "ENERGY":
                            parseEnergy(net, tokens);
                            break;
                        case "STATUS":
                            parseStatus(net, tokens);
                            break;
                        case "CONTROLS":
                            parseControl(net, line);
                            break;
                        case "RULES":
                            parseRule(net, line);
                            break;
                        case "DEMANDS":
                            parseDemand(net, tokens, comment);
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
                            parseReport(net, line);
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

            }
        }

        return net;
    }

    /**
     * Parse the PATTERN and CURVE section.
     *
     * @throws IOException           If an I/O error occurs
     * @throws FileNotFoundException If file don't exist
     */
    private void parsePatternAndCurve(@NotNull Network net, @NotNull String filename) throws FileNotFoundException, IOException {
        try (BufferedReader buffReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.ISO_8859_1))) {
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

    }

    //*********************************************************
    //Network Components
    //*********************************************************

    /**
     * Parse the JUNCTIONS section of inp file
     *
     * @param net         network
     * @param tokens      token of the line being read
     * @param description the description of element
     * @throws InputException if there is an error to parse the line tokens
     */
    private void parseJunction(@NotNull Network net, String @NotNull [] tokens, @NotNull String description) throws InputException {
        if (net.getNode(tokens[0]) != null)
            throw new InputException("Junction " + tokens[0] + " is duplicated");
        Junction node = new Junction();
        node.setId(tokens[0]);

        if (description.length() != 0) {
            node.setDescription(description);
        }

        node.setElevation(Double.parseDouble(tokens[1]));
        if (tokens.length == 2) {
            node.setBaseDemand(0);
        }
        if (tokens.length == 3) {
            node.setBaseDemand(Double.parseDouble(tokens[2]));
        }
        if (tokens.length == 4) {
            Pattern pattern = net.getPattern(tokens[3]);
            if (pattern == null) {
                throw new InputException("Don't exist the pattern " + tokens[3] + " for this junction");
            }
            node.setDemandPattern(pattern.getId());
        }
        net.addNode(tokens[0], node);
    }

    /**
     * Parse the RESERVOIRS section of inp file
     *
     * @param net         network
     * @param tokens      token of the line being read
     * @param description the description of element
     * @throws InputException if there is an error to parse the line tokens
     */
    private void parseReservoir(@NotNull Network net, String @NotNull [] tokens, @NotNull String description) throws InputException {
        if (net.getNode(tokens[0]) != null)
            throw new InputException("Reservoir " + tokens[0] + " is duplicated");
        Reservoir node = new Reservoir();
        node.setId(tokens[0]);
        node.setTotalHead(Double.parseDouble(tokens[1]));

        if (description.length() != 0) {
            node.setDescription(description);
        }

        if (tokens.length == 3) {
            Pattern pattern = net.getPattern(tokens[2]);
            if (pattern == null) {
                throw new InputException("Don't exist the pattern " + tokens[2] + " for this reservoir");
            }
            node.setHeadPattern(pattern.getId());
        }
        net.addNode(tokens[0], node);
    }

    /**
     * Parse the TANKS section of inp file
     *
     * @param net         network
     * @param tokens      token of the line being read
     * @param description the description of element
     * @throws InputException if there is an error to parse the line tokens
     */
    private void parseTanks(@NotNull Network net, String @NotNull [] tokens, @NotNull String description) throws InputException {
        if (net.getNode(tokens[0]) != null)
            throw new InputException("Tank " + tokens[0] + " is duplicated");
        Tank node = new Tank();
        node.setId(tokens[0]);

        if (description.length() != 0) {
            node.setDescription(description);
        }

        node.setElevation(Double.parseDouble(tokens[1]));
        node.setInitialLevel(Double.parseDouble(tokens[2]));
        node.setMinimumLevel(Double.parseDouble(tokens[3]));
        node.setMaximumLevel(Double.parseDouble(tokens[4]));
        node.setDiameter(Double.parseDouble(tokens[5]));
        node.setMinimumVolume(Double.parseDouble(tokens[6]));
        //noinspection ConstantConditions
        if (tokens.length == 3) {
            Curve curve = net.getCurve(tokens[2]);
            if (curve == null) {
                throw new InputException("Don't exist the curve " + tokens[7] + " for this reservoir");
            }
            node.setVolumeCurve(curve.getId());
        }
        net.addNode(tokens[0], node);
    }

    /**
     * Parse the PIPES section of inp file
     *
     * @param net         network
     * @param tokens      token of the line being read
     * @param description the description of element
     * @throws InputException if there is an error to parse the line tokens
     */
    private void parsePipe(@NotNull Network net, String @NotNull [] tokens, @NotNull String description) throws InputException {
        if (net.getLink(tokens[0]) != null)
            throw new InputException("Pipe " + tokens[0] + " is duplicated");
        Pipe link = new Pipe();
        link.setId(tokens[0]);

        if (description.length() != 0) {
            link.setDescription(description);
        }

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
            link.setLossCoefficient(Double.parseDouble(tokens[6]));
            try{
                link.setStatus(Pipe.PipeStatus.parse(tokens[7]));
            }catch (IllegalArgumentException e){
                throw new InputException("Don't exist the " + tokens[7] + " status");
            }
        } else {
            // Set the value by defect if it isn't in inp file
            link.setLossCoefficient(0.0);
            link.setStatus(Pipe.PipeStatus.OPEN);

        }
        net.addLink(tokens[0], link);
    }

    /**
     * Parse the PUMPS section of inp file
     *
     * @param net         network
     * @param tokens      token of the line being read
     * @param description the description of element
     * @throws InputException if there is an error to parse the line tokens
     */
    private void parsePump(@NotNull Network net, String @NotNull [] tokens, @NotNull String description) throws InputException {
        String id = tokens[0];
        if (net.getLink(tokens[0]) != null)
            throw new InputException("Pump " + tokens[0] + " is duplicated");
        if (tokens.length < 4) {
            throw new InputException("A value is missing in the pump " + id + " configuration line");
        }
        Pump link = new Pump();
        link.setId(id);

        if (description.length() != 0) {
            link.setDescription(description);
        }

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
                    throw new InputException("Don't exist the curve " + tokens[i + 1] + " that is used in pump " + id);

                link.setProperty(Pump.PumpProperty.HEAD, curve.getId());
            } else if (tokens[i].equalsIgnoreCase("PATTERN")) {
                Pattern pattern = net.getPattern(tokens[i + 1]);
                if (pattern == null)
                    throw new InputException("Don't exist the pattern " + tokens[i + 1] + " that is used in pump " + id);

                link.setProperty(Pump.PumpProperty.PATTERN, pattern.getId());

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
     * @param net         network
     * @param tokens      token of the line being read
     * @param description the description of element
     * @throws InputException if there is an error to parse the line tokens
     */
    private void parseValve(@NotNull Network net, @NotNull String[] tokens, @NotNull String description) throws InputException {
        if (net.getLink(tokens[0]) != null)
            throw new InputException("Valve " + tokens[0] + " is duplicated");
        Valve link = new Valve();
        if (description.length() != 0) {
            link.setDescription(description);
        }
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
        ValveType type;
        try {
            type = ValveType.parse(tokens[4]);
        } catch (IllegalArgumentException e) {
            throw new InputException("There is no ValveType " + tokens[4], e);
        }
        link.setType(type);
        link.setSetting(tokens[5]);
        link.setLossCoefficient(Double.parseDouble(tokens[6]));
        net.addLink(tokens[0], link);
    }

    /**
     * Parse the EMITTER section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException if there is an error to parse the line tokens
     */
    private void parseEmmiter(@NotNull Network net, @NotNull String[] tokens) throws InputException {
        Emitter emitter = new Emitter();
        emitter.setCoefficient(Double.parseDouble(tokens[1]));
        Junction junction = net.getJunction(tokens[0]);
        if (junction != null) {
            junction.setEmitter(emitter);
            return;
        }
        throw new InputException("There is not junction " + tokens[0]);
    }

    //*********************************************************
    // System Operation
    //*********************************************************

    /**
     * Parse the CURVES section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     */
    private void parseCurve(@NotNull Network net, @NotNull String[] tokens) {
        String id = tokens[0];
        Curve curve;
        if (net.getCurves().size() == 0) {
            curve = new Curve();
            curve.setId(id);
            net.addCurve(id, curve);
        } else {
            curve = net.getCurve(id);

            if (curve == null) {
                curve = new Curve();
                curve.setId(id);
                net.addCurve(id, curve);
            }
        }

        curve.addPointToCurve(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
    }

    /**
     * Parse the PATTERNS section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     */
    private void parsePattern(@NotNull Network net, String @NotNull [] tokens) {
        String id = tokens[0];
        Pattern pattern;
        if (net.getPatterns().size() == 0) {
            pattern = new Pattern();
            pattern.setId(id);
            net.addPattern(id, pattern);

        } else {
            pattern = net.getPattern(id);

            if (pattern == null) {
                pattern = new Pattern();
                pattern.setId(id);
                net.addPattern(id, pattern);
            }
        }

        for (int i = 1; i < tokens.length; i++) {
            pattern.addMultipliers(Double.parseDouble(tokens[i]));
        }
    }

    /**
     * Parse the ENERGY section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException if there is no pump or a WORD is not valid
     */
    private void parseEnergy(@NotNull Network net, @NotNull String[] tokens) throws InputException {
        EnergyOption energy = net.getEnergyOption();
        if (energy == null) {
            energy = new EnergyOption();
            net.setEnergyOption(energy);
        }
        if (tokens[0].equalsIgnoreCase("Global")) {
            if (tokens[1].equalsIgnoreCase("Efficiency")) {
                energy.setGlobalEfficiency(Double.parseDouble(tokens[2]));
            }
            if (tokens[1].equalsIgnoreCase("Price")) {
                energy.setGlobalPrice(Double.parseDouble(tokens[2]));
            }
            if (tokens[1].equalsIgnoreCase("Pattern")) {
                energy.setGlobalPattern(tokens[2]);
            }
        }
        if (tokens[0].equalsIgnoreCase("Demand")) {
            energy.setDemandCharge(Double.parseDouble(tokens[2]));
        }
        if (tokens[0].equalsIgnoreCase("Pump")) {
            Pump pump = net.getPump(tokens[1]);
            if (pump == null) {
                throw new InputException("There is not pump " + tokens[1]);
            }
            if (tokens[2].equalsIgnoreCase("Efficiency")) {
                pump.setEfficiencyCurve(tokens[3]);
            } else if (tokens[2].equalsIgnoreCase("Price")) {
                pump.setEnergyPrice(Double.parseDouble(tokens[3]));
            } else if (tokens[2].equalsIgnoreCase("Pattern")) {
                pump.setPricePattern(tokens[3]);
            } else {
                throw new InputException("There isn't exist " + tokens[2]);
            }
        }
    }

    /**
     * Parse the Status section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException if the status in unknown
     */
    private void parseStatus(@NotNull Network net, @NotNull String[] tokens) throws InputException {
        String linkId = tokens[0];
        Link link = net.getLink(linkId);
        if (link instanceof Pipe) {
            Pipe pipe = (Pipe) link;
            PipeStatus status;
            try {
                status = PipeStatus.parse(tokens[1]);
            } catch (IllegalArgumentException e) {
                throw new InputException("There is no PipeStatus " + tokens[1], e);
            }
            pipe.setStatus(status);
        } else if (link instanceof Pump) {
            //The status for a Pump can be a OPEN, CLOSED or a double value
            Pump pump = (Pump) link;
            /*
             * check if token is a double
             */
            try {
                double speed = Double.parseDouble(tokens[1]);
                pump.setProperty(Pump.PumpProperty.SPEED, speed);
                //if it isn't double
            } catch (NumberFormatException e) {
                PumpStatus status;
                try {
                    status = PumpStatus.parse(tokens[1]);
                } catch (IllegalArgumentException e2) {
                    throw new InputException("There is no PumpStatus " + tokens[1], e2);
                }
                pump.setStatus(status);
            }
        } else if (link instanceof Valve) {
            Valve valve = (Valve) link;
            ValveStatus status;
            try {
                status = ValveStatus.parse(tokens[1]);
            } catch (IllegalArgumentException e) {
                throw new InputException("There is no ValveStatus " + tokens[1], e);
            }
            valve.setFixedStatus(status);
        }
    }

    /**
     * Parse the CONTROLS section of inp file
     *
     * @param net the network
     * @param line the line
     */
    private void parseControl(@NotNull Network net, @NotNull String line) {
        Control control = net.getControl();
        if (control == null) {
            control = new Control();
            net.setControl(control);
        }
        String code = control.getCode();
        if (code.isEmpty()) {
            code = line;
        } else {
            code = "\n" + line;
        }
        control.setCode(code);

    }

    /**
     * Parse the RULES section of inp file
     *
     * @param net the network
     * @param line the line
     */
    private void parseRule(@NotNull Network net, @NotNull String line) {
        Rule rule = net.getRule();
        if (rule == null) {
            rule = new Rule();
            net.setRule(rule);

        }

        String code = rule.getCode();
        if (code.isEmpty()) {
            code = line;
        } else {
            code += "\n" + line;
        }
        rule.setCode(code);

    }

    /**
     * Parse the DEMANDS section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException if there is no junction
     */
    private void parseDemand(@NotNull Network net, String @NotNull [] tokens, @NotNull String category) throws InputException {
        Demand demand = new Demand();
        demand.setDemand(Double.parseDouble(tokens[1]));
        if (tokens.length == 3) {
            demand.setDemandPattern(tokens[2]);
        }
        if (!category.isEmpty()) {
            demand.setDemandCategory(category);
        }

        // if no junction has added the demand or the current junction is not the same that the last
        if (lastJunctionWithDemandAdded == null || !lastJunctionWithDemandAdded.getId().equals(tokens[0])) {
            lastJunctionWithDemandAdded = net.getJunction(tokens[0]);
            List<Demand> demandList;
            if (lastJunctionWithDemandAdded != null) {
                demandList = lastJunctionWithDemandAdded.getDemandCategories();
                demandList.clear();
                demandList.add(demand);
            } else{
                throw new InputException("There is no junction " + tokens[0]);
            }
        } else {
            lastJunctionWithDemandAdded.getDemandCategories().add(demand);
        }

    }

    //*********************************************************
    // Water Quality
    //*********************************************************/

    /**
     * Parse the QUALITY section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException if there is no node with the id in tokens
     */
    private void parseQuality(@NotNull Network net, @NotNull String[] tokens) throws InputException {
        Node node = net.getNode(tokens[0]);
        if (node == null) {
            throw new InputException("There is no node with id " + tokens[0]);
        }
        Quality quality = new Quality();
        quality.setInitialQuality(Double.parseDouble(tokens[1]));
        node.setInitialQuality(quality);

    }

    /**
     * Parse the REACTION section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException if there is no a pipe or tank with the id or there is
     *                        a bad key word.
     */
    private void parseReaction(@NotNull Network net, @NotNull String[] tokens) throws InputException {
        if (tokens[0].equalsIgnoreCase("Bulk")) {
            Pipe pipe = net.getPipe(tokens[1]);
            if (pipe == null) {
                throw new InputException("There is no a pipe with id " + tokens[1]);
            }
            pipe.setBulkCoefficient(Double.parseDouble(tokens[2]));
            return;
        } else if (tokens[0].equalsIgnoreCase("Wall")) {
            Pipe pipe = net.getPipe(tokens[1]);
            if (pipe == null) {
                throw new InputException("There is no a pipe with id " + tokens[1]);
            }
            pipe.setWallCoefficient(Double.parseDouble(tokens[2]));
            return;
        } else if (tokens[0].equalsIgnoreCase("Tank")) {
            Tank tank = net.getTank(tokens[1]);
            if (tank == null) {
                throw new InputException("There is no a tank with id " + tokens[1]);
            }
            tank.setReactionCoefficient(Double.parseDouble(tokens[2]));
            return;
        }

        ReactionOption reaction = net.getReactionOption();

        if (reaction == null) {
            reaction = new ReactionOption();
            net.setReactionOption(reaction);
        }

        if (tokens[0].equalsIgnoreCase("Order")) {
            if (tokens[1].equalsIgnoreCase("Bulk")) {
                reaction.setOrderBulk(Double.parseDouble(tokens[2]));
            } else if (tokens[1].equalsIgnoreCase("Tank")) {
                reaction.setOrderTank(Double.parseDouble(tokens[2]));
            } else if (tokens[1].equalsIgnoreCase("Wall")) {
                reaction.setOrderWall(Integer.parseInt(tokens[2]));
            } else {
                throw new InputException("There is no a key word " + tokens[1]);
            }
        } else if (tokens[0].equalsIgnoreCase("Global")) {
            if (tokens[1].equalsIgnoreCase("Bulk")) {
                reaction.setGlobalBulk(Double.parseDouble(tokens[2]));
            } else if (tokens[1].equalsIgnoreCase("Wall")) {
                reaction.setGlobalWall(Double.parseDouble(tokens[2]));
            } else {
                throw new InputException("There is no a key word " + tokens[1]);
            }
        } else if (tokens[0].equalsIgnoreCase("Limiting")) {
            reaction.setLimitingPotential(Double.parseDouble(tokens[2]));
        } else if (tokens[0].equalsIgnoreCase("Roughness")) {
            reaction.setRoughnessCorrelation(Double.parseDouble(tokens[2]));
        } else {
            throw new InputException("There is no a key word " + tokens[0]);
        }


    }

    /**
     * Parse the SOURCES section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException if there is a error in SOURCE SECTION
     */
    private void parseSource(@NotNull Network net, String @NotNull [] tokens) throws InputException {
        Node node = net.getNode(tokens[0]);
        if (node == null) {
            throw new InputException("There is no node with id " + tokens[0]);
        }

        Source source = new Source();
        SourceType sourceType;
        try {
            sourceType = Source.SourceType.parse(tokens[1]);
        } catch (IllegalArgumentException e) {
            throw new InputException("There is no a source type " + tokens[1], e);
        }
        source.setSourceType(sourceType);

        source.setSourceQuality(Double.parseDouble(tokens[2]));
        if (tokens.length == 4) {
            if (net.getPattern(tokens[3]) == null) {
                throw new InputException("Don't exist the pattern " + tokens[3] + " for this source");
            }
            source.setTimePattern(net.getPattern(tokens[3]).getId());
        }

        node.setSourceQuality(source);
    }

    /**
     * Parse the MIXINGS section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException If there is a error in MIXING section
     */
    private void parseMixing(@NotNull Network net, String @NotNull [] tokens) throws InputException {
        Tank tank = net.getTank(tokens[0]);
        if (tank == null) {
            throw new InputException("There is no a tank with the id " + tokens[0]);
        }

        Mixing mixing = new Mixing();
        MixingModel mixingModel = Mixing.MixingModel.parse(tokens[1]);

        if (tokens.length == 3) {
            mixing.setMixingFraction(Double.parseDouble(tokens[2]));
        }

        mixing.setModel(mixingModel);
    }

    //*********************************************************
    //Options and Reports
    //*********************************************************

    /**
     * Parse the OPTIONS section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException if there is a error in a key word
     */
    private void parseOption(@NotNull Network net, String @NotNull [] tokens) throws InputException {
        Option option = net.getOption();
        if (option == null) {
            option = new Option();
            net.setOption(option);
        }
        if (tokens[0].equalsIgnoreCase("Units")) {
            FlowUnit flowUnit;
            try {
                flowUnit = FlowUnit.parse(tokens[1]);
            } catch (IllegalArgumentException e) {
                throw new InputException("There is no a flow unit called " + tokens[1], e);
            }
            option.setFlowUnit(flowUnit);
        } else if (tokens[0].equalsIgnoreCase("Headloss")) {
            HeadlossFormule headlossFormule = HeadlossFormule.parse(tokens[1]);
            if (headlossFormule == null) {
                throw new InputException("There is no a headloss formule called " + tokens[1]);
            }
            option.setHeadlossFormule(headlossFormule);
        } else if (tokens[0].equalsIgnoreCase("Specific")) {
            option.setSpecificGravity(Double.parseDouble(tokens[2]));
        } else if (tokens[0].equalsIgnoreCase("Viscosity")) {
            option.setViscosity(Double.parseDouble(tokens[1]));

        } else if (tokens[0].equalsIgnoreCase("Trials")) {
            option.setTrials(Double.parseDouble(tokens[1]));

        } else if (tokens[0].equalsIgnoreCase("Accuracy")) {
            option.setAccuracy(Double.parseDouble(tokens[1]));
        } else if (tokens[0].equalsIgnoreCase("CHECKFREQ")) {
            option.setCheckfreq(Double.parseDouble(tokens[1]));

        } else if (tokens[0].equalsIgnoreCase("MAXCHECK")) {
            option.setMaxcheck(Double.parseDouble(tokens[1]));

        } else if (tokens[0].equalsIgnoreCase("DAMPLIMIT")) {
            option.setDamplimit(Double.parseDouble(tokens[1]));

        } else if (tokens[0].equalsIgnoreCase("Unbalanced")) {
            if (tokens.length == 3) {
                option.setUnbalanced(String.join(" ", tokens[1], tokens[2]));
            } else if (tokens.length == 2) {
                option.setUnbalanced(tokens[1]);
            }
        } else if (tokens[0].equalsIgnoreCase("Pattern")) {
            option.setPattern(tokens[1]);
        } else if (tokens[0].equalsIgnoreCase("Demand")) {
            option.setDemandMultiplier(Double.parseDouble(tokens[2]));
        } else if (tokens[0].equalsIgnoreCase("Emitter")) {
            option.setEmitterExponent(Double.parseDouble(tokens[2]));
        } else if (tokens[0].equalsIgnoreCase("Hydraulics")) {
            option.setHydraulic(String.join(" ", tokens[1], tokens[2]));

        } else if (tokens[0].equalsIgnoreCase("Map")) {
            option.setMap(tokens[1]);
        }

        QualityOption qualityOption = net.getQualityOption();
        if (qualityOption == null) {
            qualityOption = new QualityOption();
            net.setQualityOption(qualityOption);
        }
        if (tokens[0].equalsIgnoreCase("Quality")) {
            qualityOption.setParameter(tokens[1]);
            if (tokens[1].equalsIgnoreCase("Trace")) {
                if (tokens.length == 3) {
                    qualityOption.setTraceNodeID(tokens[2]);
                }
            } else {
                MassUnit massUnit;
                try {
                    massUnit = MassUnit.parse(tokens[2]);
                } catch (IllegalArgumentException e) {
                    throw new InputException("There is no a mass unit called " + tokens[2]);
                }
                qualityOption.setMassUnit(massUnit);
            }
        } else if (tokens[0].equalsIgnoreCase("Diffusivity")) {
            qualityOption.setRelativeDiffusivity(Double.parseDouble(tokens[1]));
        } else if (tokens[0].equalsIgnoreCase("Tolerance")) {
            qualityOption.setQualityTolerance(Double.parseDouble(tokens[1]));
        }

    }

    /**
     * Parse the TIMES section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     */
    private void parseTime(@NotNull Network net, String @NotNull [] tokens) {
        Time time = net.getTime();
        if (time == null) {
            time = new Time();
            net.setTime(time);
        }
        if (tokens[0].equalsIgnoreCase("Duration")) {
            time.setDuration(StringUtils.join(tokens, ' ', 1, tokens.length));
        } else if (tokens[0].equalsIgnoreCase("Hydraulic")) {
            time.setHydraulicTimestep(StringUtils.join(tokens, ' ', 2, tokens.length));
        } else if (tokens[0].equalsIgnoreCase("Quality")) {
            time.setQualityTimestep(StringUtils.join(tokens, ' ', 2, tokens.length));
        } else if (tokens[0].equalsIgnoreCase("Pattern")) {
            if (tokens[1].equalsIgnoreCase("Timestep")) {
                time.setPatternTimestep(StringUtils.join(tokens, ' ', 2, tokens.length));
            } else if (tokens[1].equalsIgnoreCase("Start")) {
                time.setPatternStart(StringUtils.join(tokens, ' ', 2, tokens.length));
            }
        } else if (tokens[0].equalsIgnoreCase("Report")) {
            if (tokens[1].equalsIgnoreCase("Timestep")) {
                time.setReportTimestep(StringUtils.join(tokens, ' ', 2, tokens.length));
            } else if (tokens[1].equalsIgnoreCase("Start")) {
                time.setReportStart(StringUtils.join(tokens, ' ', 2, tokens.length));
            }
        } else if (tokens[0].equalsIgnoreCase("Start")) {
            time.setStartClockTime(StringUtils.join(tokens, ' ', 2, tokens.length));
        } else if (tokens[0].equalsIgnoreCase("Statistic")) {
            time.setStatistic(Time.Statistic.parse(tokens[1]));
        }
    }

    /**
     * Parse the REPORT section of inp file
     *
     * @param net the network
     * @param line the line
     */
    private void parseReport(@NotNull Network net, @NotNull String line) {
        Report report;
        if (net.getReport() == null) {
            report = new Report();
            net.setReport(report);
        } else {
            report = net.getReport();
        }
        // Add lines with configuration
        String code = report.getCode();

        if (code.isEmpty()) {
            report.setCode(line);
        } else {
            report.setCode(code + "\n" + line);
        }
    }

    //*********************************************************
    //Network Design
    //*********************************************************

    /**
     * Parse the COORDINATES section of inp file
     *
     * @param net @param tokens @throws
     */
    private void parseCoordinate(@NotNull Network net, @NotNull String[] tokens) throws InputException {
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
     * @param net the network
     * @param tokens the tokens
     */
    private void parseVertice(@NotNull Network net, String[] tokens) throws InputException {
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
     * @param net the network
     * @param tokens the tokens
     * @throws InputException If there is a error in LABELS section
     */
    private void parseLabel(@NotNull Network net, String @NotNull [] tokens) throws InputException {
        Label label = new Label();
        double x = Double.parseDouble(tokens[0]);
        double y = Double.parseDouble(tokens[1]);
        label.setPosition(new Point(x, y));
        label.setLabel(String.format("%s", tokens[2]).replace("\"", ""));
        if (tokens.length == 4) {
            Node node = net.getNode(tokens[3]);
            if (node == null) {
                throw new InputException("The node " + tokens[3] + " don't exist");
            }
            label.setAnchorNode(node.getId());
        }

        net.addLabel(label);
    }

    /**
     * Parse the Backdrop section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException if the key work isn't valid
     */
    private void parseBackdrop(@NotNull Network net, String @NotNull [] tokens) throws InputException {
        Backdrop backdrop = net.getBackdrop();
        if (backdrop == null) {
            backdrop = new Backdrop();
            net.setBackdrop(backdrop);
        }
        if (tokens[0].equalsIgnoreCase("DIMENSIONS")) {
            double xBottomLeft = Double.parseDouble(tokens[1]);
            double yBottomLeft = Double.parseDouble(tokens[2]);
            double xUpperRight = Double.parseDouble(tokens[3]);
            double yUpperRight = Double.parseDouble(tokens[4]);
            backdrop.setDimension(xBottomLeft, yBottomLeft, xUpperRight, yUpperRight);
        } else if (tokens[0].equalsIgnoreCase("UNITS")) {
            Unit unit = Unit.parse(tokens[1]);
            if (unit == null) {
                unit = Unit.NONE;
            }
            backdrop.setUnit(unit);
        } else if (tokens[0].equalsIgnoreCase("FILE")) {
            if (tokens.length == 2) {
                backdrop.setFile(tokens[1]);
            }
        } else if (tokens[0].equalsIgnoreCase("OFFSET")) {
            double xOffset = Double.parseDouble(tokens[1]);
            double yOffset = Double.parseDouble(tokens[2]);
            backdrop.setOffset(xOffset, yOffset);
        } else {
            throw new InputException("No valid key work " + tokens[0]);
        }
    }

    /**
     * Parse the TAGS section of inp file
     *
     * @param net the network
     * @param tokens the tokens
     * @throws InputException if there is a error in TAGS section
     */
    private void parseTag(@NotNull Network net, String @NotNull [] tokens) throws InputException {
        Component component;
        Tag tag = new Tag();
        if (tokens.length != 3) {
            throw new InputException("There are a error in TAGS section associated with the number of parameters");
        }
        if (tokens[0].equalsIgnoreCase("NODE")) {
            component = net.getNode(tokens[1]);
            tag.setType(Tag.TagType.NODE);
        } else if (tokens[0].equalsIgnoreCase("LINK")) {
            component = net.getLink(tokens[1]);
            tag.setType(Tag.TagType.LINK);
        } else {
            throw new InputException(tokens[0] + " isn't a valid token for TAGS section");
        }

        if (component == null) {
            throw new InputException("There is no a node or link with id " + tokens[1]);
        }
        tag.setLabel(tokens[2]);
        component.setTag(tag);
    }

    private @NotNull String identifySectionType(@NotNull String token) {
        int endIndex = token.indexOf("]");
        return token.substring(1, endIndex).toUpperCase();
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
            network = parse.parse(network, "inp/vanzylOriginal.inp");
            System.out.println(ParseNetworkToINPString.parse(network));
            OutputInpWriter writer = new OutputInpWriter();
            writer.write(network, "red1writer.inp");
        } catch (IOException | InputException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
