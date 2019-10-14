package model.epanet.jna;

/**
 * Class that contain the status report
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
public enum EpanetStatusReport {
	NO_REPORTING(0, "no status reporting"), //
	NORMAL_REPORTING(1, "normal reporting"), //
	FULL_REPORTING(2, "full status reporting");

	private int code;
	private String description;

	private EpanetStatusReport(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetQualType.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetStatusReport convert(int code) {
		EpanetStatusReport[] types = EpanetStatusReport.values();
		for (EpanetStatusReport type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException(
				"Don't exist a type for code " + code + "in " + EpanetStatusReport.class.getSimpleName());
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
