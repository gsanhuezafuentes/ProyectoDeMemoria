package model.epanet.io;

import exception.InputException;
import model.epanet.element.Network;

import java.io.IOException;

/**
 * Interface to classes that read a network file.
 *
 */
public interface InputParser {

	/**
	 * Read the file contain the network description.
	 * @param net Network object that will be filled.
	 * @param filename filepath to file.
	 * @return Network net filled.
	 * @throws InputException If there is a error in input file
	 * @throws IOException  If an I/O error occurs or file doesn't exist
	 */
	Network parse(Network net, String filename) throws IOException, InputException;

}