package model.epanet.jna;

/**
 * 
 * 
 * Class that contain control type code.
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
 * 
 */
public enum EpanetControlType {
	LOW_LEVEL(0, "Applies when tank level or node pressure drops below specified level"), //
	HIGH_LEVEL(1, "Applies when tank level or node pressure rises above specified level"), //
	TIMER(2, "Applies at specific time into simulation"), //
	TIME_OF_DAY(3, "Applies at specific time of day");

	private int code;
	private String description;

	private EpanetControlType(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetControlType.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetControlType convert(int code) {
		EpanetControlType[] types = EpanetControlType.values();
		for (EpanetControlType type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException(
				"Don't exist a type for code " + code + "in " + EpanetControlType.class.getSimpleName());
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
