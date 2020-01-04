package model.epanet.io;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import exception.ApplicationException;
import exception.InputException;
import model.epanet.element.Network;

/**
 * Class that write in the system a inp file of the specific network object.
 *
 */
public class OutputInpWriter implements OutputWriter {

	@Override
	public void write(Network net, String filename) throws FileNotFoundException, IOException {
		try (BufferedWriter buffWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(filename), "ISO-8859-1"))) {
			buffWriter.write(net.toString());
		} catch (UnsupportedEncodingException e) {
			throw new ApplicationException("Error in encoding file", e);
		}
	}

}
