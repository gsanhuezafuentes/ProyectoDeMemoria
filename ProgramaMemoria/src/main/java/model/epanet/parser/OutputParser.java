package model.epanet.parser;

import java.io.File;

import model.epanet.EpanetException;
import model.epanet.element.Network;

public interface OutputParser {

	/**
	 * Create a file based on Network
	 * 
	 * @param net  the network
	 * @param file path to the file
	 */
	void parse(Network net, String file);
}
