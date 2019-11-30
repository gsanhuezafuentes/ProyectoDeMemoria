package model.epanet.io;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import exception.InputException;
import model.epanet.element.Network;

public class InpWriter implements OutputWriter {

	@Override
	public void write(Network net, String filename) throws FileNotFoundException, IOException, InputException {
		try (BufferedWriter buffReader = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(filename), "ISO-8859-1"))) {
			buffReader.write(net.toString());
		} catch (UnsupportedEncodingException e) {
			throw new InputException("Error in encoding file", e);
		}
	}

}
