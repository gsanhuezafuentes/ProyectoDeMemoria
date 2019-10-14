package model.epanet.jna;

/**
 * This class indicate the time parameter codes.
 *
 *
 * Take and modified from https://github.com/TheHortonMachine/hortonmachine
 * 
 * JGrass - Free Open Source Java GIS http://www.jgrass.org (C) HydroloGIS -
 * www.hydrologis.com
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
public enum EpanetTimeParameterCodes {
	EN_DURATION(0, "Simulation duration"), //
	EN_HYDSTEP(1, "Hydraulic time step"), //
	EN_QUALSTEP(2, "Water quality time step"), //
	EN_PATTERNSTEP(3, "Time pattern time step"), //
	EN_PATTERNSTART(4, "Time pattern start time"), //
	EN_REPORTSTEP(5, "Reporting time step"), //
	EN_REPORTSTART(6, "Report starting time"), //
	EN_RULESTEP(7, "Time step for evaluating rule-based controls"), //
	/**
	 * {@link #EN_STATISTIC} can take the values indicated in
	 * {@link EpanetTimeParameterCodesStatistic}.
	 */
	EN_STATISTIC(8, "Type of time series post-processing to use");
	// EN_PERIODS(9, "Number of reporting periods saved to binary output file");

	private int code;
	private String description;

	private EpanetTimeParameterCodes(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetTimeParameterCodes.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetTimeParameterCodes convert(int code) {
		EpanetTimeParameterCodes[] types = EpanetTimeParameterCodes.values();
		for (EpanetTimeParameterCodes type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException(
				"Don't exist a type for code " + code + "in " + EpanetTimeParameterCodes.class.getSimpleName());
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
