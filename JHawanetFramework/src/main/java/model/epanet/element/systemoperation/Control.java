package model.epanet.element.systemoperation;

import exception.ApplicationException;

public final class Control {
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

	private String linkId;
	private ControlType controlType;
	private StatType statType;
	private double statValue;
	private String nodeId;
	private double value;
	private int time;
	private String clocktime;

	/**
	 * @return the link id
	 */
	public String getLinkId() {
		return linkId;
	}

	/**
	 * @param linkId the link id to set
	 */
	public void setLinkId(String linkId) {
		this.linkId = linkId;
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
	 * 
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
	 * Get the id of node. Use only when ControlType is {@link ControlType#IF_ABOVE}
	 * or {@link ControlType#IF_BELOW}
	 * 
	 * @return the node id
	 */
	public String getNodeId() {
		return nodeId;
	}

	/**
	 * Set the id of node. Use only when ControlType is {@link ControlType#IF_ABOVE}
	 * or {@link ControlType#IF_BELOW}
	 * 
	 * @param NodeId the idNodo to set
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	/**
	 * Get the value. Use only when ControlType is {@link ControlType#IF_ABOVE} or
	 * {@link ControlType#IF_BELOW}
	 * 
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Set the value. Use only when ControlType is {@link ControlType#IF_ABOVE} or
	 * {@link ControlType#IF_BELOW}
	 * 
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Get the time assigned.
	 * 
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * 
	 * Set the time since the start of simulation in hours
	 * 
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * Get the clocktime in (hrs:min) format.
	 * 
	 * @return the clocktime
	 */
	public String getClocktime() {
		return clocktime;
	}

	/**
	 * Set the clocktime in (hrs:min AM/PM) format. Examples: <br>
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

	@Override
	public String toString() {
		String txt = "LINK ";
		txt += getLinkId() + " ";
		if (!(getStatType() == StatType.VALUE)) {
			txt += getStatType().getName() + " ";
		} else {
			txt += getStatValue() + " ";
		}
		if (getControlType() == ControlType.IF_ABOVE) {
			txt += "IF NODE ";
			txt += getNodeId() + " ";
			txt += "ABOVE ";
			txt += getValue();
		}
		if (getControlType() == ControlType.IF_BELOW) {
			txt += "IF NODE ";
			txt += getNodeId() + " ";
			txt += "BELOW ";
			txt += getValue();
		}
		if (getControlType() == ControlType.AT_TIME) {
			txt += "AT TIME ";
			txt += getTime();
		}
		if (getControlType() == ControlType.AT_CLOCKTIME) {
			txt += "AT CLOCKTIME ";
			txt += getClocktime();
		}
		return txt;
	}

}
