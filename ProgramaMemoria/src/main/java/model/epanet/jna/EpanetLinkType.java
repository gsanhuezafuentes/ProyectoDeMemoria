package model.epanet.jna;

/**
 * This class denote the link type of the link.
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
public enum EpanetLinkType {
	EN_CVPIPE(0, "Pipe with Check Valve"), //
	EN_PIPE(1, "Pipe"), EN_PUMP(2, "Pump"), //
	EN_PRV(3, "Pressure Reducing Valve"), //
	EN_PSV(4, "Pressure Sustaining Valve"), //
	EN_PBV(5, "Pressure Breaker Valve"), //
	EN_FCV(6, "Flow Control Valve"), //
	EN_TCV(7, "Throttle Control Valve"), //
	EN_GPV(8, "General Purpose Valve");

	private int code;
	private String description;

	private EpanetLinkType(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetLinkType.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetLinkType convert(int code) {
		EpanetLinkType[] types = EpanetLinkType.values();
		for (EpanetLinkType type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException(
				"Don't exist a type for code " + code + "in " + EpanetLinkType.class.getSimpleName());
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
