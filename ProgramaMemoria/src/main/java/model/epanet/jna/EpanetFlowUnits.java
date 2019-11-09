package model.epanet.jna;

/**
 * Class with Flow units using in epanet.
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
public enum EpanetFlowUnits {
	EN_CFS(0, "cubic feet per second"), //
	EN_GPM(1, "gallons per minute"), //
	EN_MGD(2, "million gallons per day"), //
	EN_IMGD(3, "Imperial mgd"), //
	EN_AFD(4, "acre-feet per day"), //
	EN_LPS(5, "liters per second"), //
	EN_LPM(6, "liters per minute"), //
	EN_MLD(7, "million liters per day"), //
	EN_CMH(8, "cubic meters per hour"), //
	EN_CMD(9, "cubic meters per day");

	private int code;
	private String description;

	private EpanetFlowUnits(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetQualType.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetFlowUnits convert(int code) {
		EpanetFlowUnits[] types = EpanetFlowUnits.values();
		for (EpanetFlowUnits type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException(
				"Don't exist a type for code " + code + "in " + EpanetFlowUnits.class.getSimpleName());
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
