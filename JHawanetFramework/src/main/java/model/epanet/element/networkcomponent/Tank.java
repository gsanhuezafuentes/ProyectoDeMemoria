package model.epanet.element.networkcomponent;

import model.epanet.element.systemoperation.Curve;

public final class Tank extends Node {
	private double elev;
	private double initLvl;
	private double minLvl;
	private double maxLvl;
	private double diameter;
	private double minVol;
	private Curve volCurve;

	/**
	 * @return the elev
	 */
	public double getElev() {
		return elev;
	}

	/**
	 * @param elev the elev to set
	 */
	public void setElev(double elev) {
		this.elev = elev;
	}

	/**
	 * @return the initLvl
	 */
	public double getInitLvl() {
		return initLvl;
	}

	/**
	 * @param initLvl the initLvl to set
	 */
	public void setInitLvl(double initLvl) {
		this.initLvl = initLvl;
	}

	/**
	 * @return the minLvl
	 */
	public double getMinLvl() {
		return minLvl;
	}

	/**
	 * @param minLvl the minLvl to set
	 */
	public void setMinLvl(double minLvl) {
		this.minLvl = minLvl;
	}

	/**
	 * @return the maxLvl
	 */
	public double getMaxLvl() {
		return maxLvl;
	}

	/**
	 * @param maxLvl the maxLvl to set
	 */
	public void setMaxLvl(double maxLvl) {
		this.maxLvl = maxLvl;
	}

	/**
	 * @return the diam
	 */
	public double getDiameter() {
		return diameter;
	}

	/**
	 * @param diam the diam to set
	 */
	public void setDiameter(double diam) {
		this.diameter = diam;
	}

	/**
	 * @return the minVol
	 */
	public double getMinVol() {
		return minVol;
	}

	/**
	 * @param minVol the minVol to set
	 */
	public void setMinVol(double minVol) {
		this.minVol = minVol;
	}

	/**
	 * @return the volCurve
	 */
	public Curve getVolCurve() {
		return volCurve;
	}

	/**
	 * @param volCurve the volCurve to set
	 */
	public void setVolCurve(Curve volCurve) {
		this.volCurve = volCurve;
	}

	@Override
	public String toString() {
		String txt = "";
		txt += getId() + "\t";
		txt += getElev() + "\t";
		txt += getInitLvl() + "\t";
		txt += getMinLvl() + "\t";
		txt += getMaxLvl() + "\t";
		txt += getDiameter() + "\t";
		txt += getMinVol() + "\t";
		if (getVolCurve() != null) {
			txt += getVolCurve().getId();			
		}
		return txt;
	}
}
