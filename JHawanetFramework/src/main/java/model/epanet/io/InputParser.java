package model.epanet.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import exception.InputException;
import model.epanet.element.Network;

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
	 * @throws IOException  If an I/O error occurs
	 * @throws FileNotFoundException If file don't exist
	 */
	Network parse(Network net, String filename) throws FileNotFoundException, IOException, InputException;

}