package model.epanet.element.systemoperation;

import exception.ApplicationException;

public class Control {
	public static enum ControlType {
		IF_ABOVE("IF_ABOVE"), IF_BELOW("IF_BELOW"), AT_TIME("AT_TIME"), AT_CLOCKTIME("AT_CLOCKTIME");

		String name;

		private ControlType(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

	}

	public static enum StatType {
		OPEN("OPEN"), CLOSE("CLOSE"), VALUE("VALUE");

		String name;

		private StatType(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

	}

	private String idLink;
	private ControlType controlType;
	private StatType statType;
	private double statValue;
	private String idNodo;
	private int value;
	private int time;
	private String clocktime;
	/**
	 * @return the idLink
	 */
	public String getIdLink() {
		return idLink;
	}
	/**
	 * @param idLink the idLink to set
	 */
	public void setIdLink(String idLink) {
		this.idLink = idLink;
	}
	/**
	 * @return the controlType
	 */
	public ControlType getControlType() {
		return controlType;
	}
	/**
	 * 
	 * @param controlType the controlType to set
	 */
	public void setControlType(ControlType controlType) {
		this.controlType = controlType;
	}
	/**
	 * @return the statType
	 */
	public StatType getStatType() {
		return statType;
	}
	/**
	 * @param statType the statType to set
	 */
	public void setStatType(StatType statType) {
		this.statType = statType;
	}
	/**
	 * Only have to be assigned when StatType contains {@link StatType#VALUE}.
	 * @return the statValue
	 */
	public double getStatValue() {
		return statValue;
	}
	/**
	 * @param statValue the statValue to set
	 */
	public void setStatValue(double statValue) {
		this.statValue = statValue;
	}
	/**
	 * Get the id of nodo. Use only when ControlType is {@link ControlType#IF_ABOVE} or {@link ControlType#IF_BELOW}
	 * @return the idNodo
	 */
	public String getIdNodo() {
		return idNodo;
	}
	/**
	 * Set the id of nodo. Use only when ControlType is {@link ControlType#IF_ABOVE} or {@link ControlType#IF_BELOW}
	 * @param idNodo the idNodo to set
	 */
	public void setIdNodo(String idNodo) {
		this.idNodo = idNodo;
	}
	/**
	 * Get the value. Use only when ControlType is {@link ControlType#IF_ABOVE} or {@link ControlType#IF_BELOW}
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	/**
	 * Set the value. Use only when ControlType is {@link ControlType#IF_ABOVE} or {@link ControlType#IF_BELOW}
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	/**
	 * Get the time assigned. 
	 * @return the time
	 */
	public int getTime() {
		return time;
	}
	/**
	 * 
	 * Set the time since the start of simulation in hours
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}
	/**
	 * Get the clocktime in (hrs:min) format.
	 * @return the clocktime
	 */
	public String getClocktime() {
		return clocktime;
	}
	/**
	 * Set the clocktime in (hrs:min AM/PM) format.
	 * Examples: <br>
	 * 1 AM <br>
	 * 1:32 AM <br>
	 * 12 PM <br>
	 * 12:00
	 * 
	 * @param clocktime the clocktime to set in hrs:min format
	 */
	public void setClocktime(String clocktime) {
		if (!clocktime.matches("([1-9]|1?[1-2])(:[0-5][0-9])? (AM|PM)")) {
			throw new ApplicationException("clocktime " + clocktime + " is not a valid format");
		}
		this.clocktime = clocktime;
	}


}
