package model.epanet.jna;

/**
 * Class to denote code statistic to use join with
 * {@link EpanetTimeParameterCodes#EN_STATISTIC}.
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
public enum EpanetTimeParameterCodesStatistic {
	EN_NONE(0, "none"), //
	EN_AVERAGE(1, "averaged"), //
	EN_MINIMUM(2, "minimums"), //
	EN_MAXIMUM(3, "maximums"), //
	EN_RANGE(4, "ranges");

	private int code;
	private String description;

	private EpanetTimeParameterCodesStatistic(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetTimeParameterCodesStatistic.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetTimeParameterCodesStatistic convert(int code) {
		EpanetTimeParameterCodesStatistic[] types = EpanetTimeParameterCodesStatistic.values();
		for (EpanetTimeParameterCodesStatistic type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException("Don't exist a type for code " + code + "in "
				+ EpanetTimeParameterCodesStatistic.class.getSimpleName());
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
