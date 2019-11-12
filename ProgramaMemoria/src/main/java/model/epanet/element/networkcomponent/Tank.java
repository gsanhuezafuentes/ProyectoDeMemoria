package model.epanet.element.networkcomponent;

import model.epanet.element.systemoperation.Curve;

public class Tank extends Node{
	private double elev;
	private double initLvl;
	private double minLvl;
	private double maxLvl;
	private double diam;
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
	public double getDiam() {
		return diam;
	}
	/**
	 * @param diam the diam to set
	 */
	public void setDiam(double diam) {
		this.diam = diam;
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
	
	
	
}
