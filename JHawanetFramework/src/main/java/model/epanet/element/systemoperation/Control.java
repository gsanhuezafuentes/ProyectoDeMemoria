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

	public Control() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Copy constructor.
	 * 
	 * @param control the object to copy
	 */
	public Control(Control control) {
		this.linkId = control.linkId;
		this.controlType = control.controlType;
		this.statType = control.statType;
		this.statValue = control.statValue;
		this.nodeId = control.nodeId;
		this.value = control.value;
		this.time = control.time;
		this.clocktime = control.clocktime;
	}

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
	 * @param nodeId the idNodo to set
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
	 * @throws if clocktime hasn't a valid format. The regex used is
	 *            "([1-9]|1?[1-2])(:[0-5][0-9])? (AM|PM)".
	 */
	public void setClocktime(String clocktime) {
		if (!clocktime.matches("([1-9]|1?[1-2])(:[0-5][0-9])? (AM|PM)")) {
			throw new ApplicationException("clocktime " + clocktime + " is not a valid format");
		}
		this.clocktime = clocktime;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append("LINK ");
		txt.append(getLinkId() + " ");
		if (!(getStatType() == StatType.VALUE)) {
			txt.append(getStatType().getName() + " ");
		} else {
			txt.append(getStatValue() + " ");
		}
		if (getControlType() == ControlType.IF_ABOVE) {
			txt.append("IF NODE ");
			txt.append(getNodeId() + " ");
			txt.append("ABOVE ");
			txt.append(getValue());
		}
		if (getControlType() == ControlType.IF_BELOW) {
			txt.append("IF NODE ");
			txt.append(getNodeId() + " ");
			txt.append("BELOW ");
			txt.append(getValue());
		}
		if (getControlType() == ControlType.AT_TIME) {
			txt.append("AT TIME ");
			txt.append(getTime());
		}
		if (getControlType() == ControlType.AT_CLOCKTIME) {
			txt.append("AT CLOCKTIME ");
			txt.append(getClocktime());
		}
		return txt.toString();
	}

	/**
	 * Copy the object.
	 * 
	 * @return a copy of this object
	 */
	public Control copy() {
		return new Control(this);
	}

}
