package model.epanet.jna;

/**
 * This class denote the params code that can be modified or obtained of a link.
 *
 * A link can be a pipe, pump or valve.
 * 
 * The parameter {@link #EN_FLOW}, {@link #EN_VELOCITY}, {@link #EN_HEADLOSS},
 * {@link #EN_STATUS}, {@link #EN_SETTING} and {@link #EN_ENERGY} are computed
 * values.
 * 
 * 
 * 
 * Take and modified from https://github.com/TheHortonMachine/hortonmachine
 * 
 * * JGrass - Free Open Source Java GIS http://www.jgrass.org (C) HydroloGIS -
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
public enum EpanetLinkParameter {
	EN_DIAMETER(0, "Diameter"), //
	EN_LENGTH(1, "Length"), //
	EN_ROUGHNESS(2, "Roughness coeff."), //
	EN_MINORLOSS(3, "Minor loss coeff."), //
	EN_INITSTATUS(4, "Initial link status (0 = closed, 1 = open)"), //
	EN_INITSETTING(5, "Roughness for pipes, initial speed for pumps, initial setting for valves"), //
	EN_KBULK(6, "Bulk reaction coeff."), //
	EN_KWALL(7, "Wall reaction coeff."), //
	EN_FLOW(8, "Flow rate"), //
	EN_VELOCITY(9, "Flow velocity"), //
	EN_HEADLOSS(10, "Head loss"), //
	EN_STATUS(11, "Actual link status (0 = closed, 1 = open)"), //
	EN_SETTING(12, "Roughness for pipes, actual speed for pumps, actual setting for valves"), //
	EN_ENERGY(13, "Energy expended in kwatts");

	private int code;
	private String description;

	private EpanetLinkParameter(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetLinkParameter.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetLinkParameter convert(int code) {
		EpanetLinkParameter[] types = EpanetLinkParameter.values();
		for (EpanetLinkParameter type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException(
				"Don't exist a type for code " + code + "in " + EpanetLinkParameter.class.getSimpleName());
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
