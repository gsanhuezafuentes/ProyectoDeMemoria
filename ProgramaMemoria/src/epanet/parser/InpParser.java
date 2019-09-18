package epanet.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import epanet.EpanetErrors;
import epanet.EpanetException;
import epanet.uielements.Network;

/**
 * Parse the INP file to get the coordinates and vertices of the water network.
 * 
 *
 */
public class InpParser implements InputParser {

	@Override
	public Network parse(Network net, File file) throws EpanetException {
		try (BufferedReader buffReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "ISO-8859-1"))) {
			String line;
			while ((line = buffReader.readLine()) != null) {

			}

		} catch (IOException e) {
			EpanetErrors.throwException(302);
		}

		return net;
	}
	
	public void parseJunction(Network net, String[] tokens) {
		
	}
	
	public void parseReservoir(Network net, String[] tokens) {
		
	}
	
	public void parseTanks(Network net, String[] tokens) {
		
	}
	
	public void parsePipe(Network net, String[] tokens) {
		
	}
	
	public void parsePump(Network net, String[] tokens) {
		
	}
	
	public void parseValve(Network net, String[] tokens) {
		
	}
	public void parseCoordinate(Network net, String[] tokens) {
		
	}
	public void parseVertice(Network net, String[] tokens) {
		
	}
}
