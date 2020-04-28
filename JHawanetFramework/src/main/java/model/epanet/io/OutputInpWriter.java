package model.epanet.io;

import model.epanet.element.Network;
import model.epanet.element.utils.ParseNetworkToINPString;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Class that write in the system a inp file of the specific network object.
 *
 */
public class OutputInpWriter implements OutputWriter {

	/**
	 *
	 * @param net      the network
	 * @param filename file path to save.
	 * @throws IOException if there is a error in IO operation
	 */
	@Override
	public void write(Network net, String filename) throws IOException {
		try (BufferedWriter buffWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.ISO_8859_1))) {
			buffWriter.write(ParseNetworkToINPString.parse(net));
		}
	}

}
