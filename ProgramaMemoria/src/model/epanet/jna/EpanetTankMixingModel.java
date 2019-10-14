package model.epanet.jna;

/**
 * This class denote the tank mixing model taken when
 * {@link EpanetNodeParameter#EN_MIXMODEL} is used.
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
public enum EpanetTankMixingModel {
	EN_MIX1(0, "Single compartment, complete mix model"), //
	EN_MIX2(1, "Two-compartment, complete mix model"), //
	EN_FIFO(2, "Plug flow, first in, first out model"), //
	EN_LIFO(3, "Stacked plug flow, last in, first out model");

	private int code;
	private String description;

	private EpanetTankMixingModel(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Convert a given code in a EpanetTankMixingModel.
	 * 
	 * @param code The code of type.
	 * @return EpanetLinkType object
	 */
	public static EpanetTankMixingModel convert(int code) {
		EpanetTankMixingModel[] types = EpanetTankMixingModel.values();
		for (EpanetTankMixingModel type : types) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException(
				"Don't exist a type for code " + code + "in " + EpanetTankMixingModel.class.getSimpleName());
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
