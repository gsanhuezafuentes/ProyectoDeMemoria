package model.epanet.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import exception.InputException;
import model.epanet.element.Network;

public interface OutputWriter {

	/**
	 * Create a file based on Network
	 * 
	 * @param net  the network
	 * @param file path to the file
	 * @throws InputException        If there is a error in the encoding
	 * @throws IOException           If an I/O error occurs
	 * @throws FileNotFoundException if the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
	 */
	void write(Network net, String filename) throws FileNotFoundException, IOException, InputException;
}
