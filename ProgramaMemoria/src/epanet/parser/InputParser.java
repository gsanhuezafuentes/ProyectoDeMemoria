package epanet.parser;

import java.io.File;

import epanet.EpanetException;
import epanet.uielements.Network;

public interface InputParser {

	/**
	 * Read the file contain the network description.
	 * @param net Network object that will be filled.
	 * @param file File that contain network description.
	 * @return Network net filled.
	 * @throws EpanetException Is throw when file can't be parser.
	 */
	Network parse(Network net, File file) throws EpanetException;

}