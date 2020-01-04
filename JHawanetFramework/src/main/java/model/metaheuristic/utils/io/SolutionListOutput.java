package model.metaheuristic.utils.io;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

import exception.ApplicationException;
import model.metaheuristic.solution.Solution;

public class SolutionListOutput {

	/**
	 * Write a FUN and VAR file. The FUN has the objectives. The VAR file has the
	 * decision variables.
	 * 
	 * @param solutionList the solution list to write.
	 * @param filename     the name of file
	 * @throws IOException           If an I/O error occurs
	 * @throws FileNotFoundException if the file exists but is a directory rather
	 *                               than a regular file, does not exist but cannot
	 *                               be created, or cannot be opened for any other
	 *                               reason.
	 */
	public void write(List<? extends Solution<?>> solutionList, String filename) throws FileNotFoundException, IOException {
		Objects.requireNonNull(solutionList);
		try (BufferedWriter buffFunWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(filename + ".FUN"), "ISO-8859-1"));
				BufferedWriter buffVarWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(filename + ".VAR"), "ISO-8859-1"))) {
			for (Solution<?> solution : solutionList) {
				buffFunWriter.write(formatFUN(solution));
				buffFunWriter.write("\n");
				buffVarWriter.write(formatVAR(solution));
				buffVarWriter.write("\n");
			}
		} catch (UnsupportedEncodingException e) {
			throw new ApplicationException("Error in encoding file", e);
		}
	}

	private String formatVAR(Solution<?> solution) {
		StringBuilder text = new StringBuilder();
		for (Object object : solution.getVariables()) {
			text.append(object + " ");
		}
		return text.toString();
	}

	private String formatFUN(Solution<?> solution) {
		StringBuilder text = new StringBuilder();
		for (double objective : solution.getObjectives()) {
			text.append(objective + " ");
		}
		return text.toString();
	}
}
