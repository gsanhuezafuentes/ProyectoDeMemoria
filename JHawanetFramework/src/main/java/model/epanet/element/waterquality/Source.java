package model.epanet.element.waterquality;

import model.epanet.element.systemoperation.Pattern;

public final class Source {

	public static enum SourceType {
		CONCEN("CONCEN"), MASS("MASS"), FLOWPACED("FLOWPACED"), SETPOINT("SETPOINT");

		private String name;

		private SourceType(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
	}

	private String nodeId;
	private SourceType sourceType;
	private double baselineStrenth;
	private Pattern timePattern;
	/**
	 * @return the nodeId
	 */
	public String getNodeId() {
		return nodeId;
	}
	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	/**
	 * @return the sourceType
	 */
	public SourceType getSourceType() {
		return sourceType;
	}
	/**
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}
	/**
	 * @return the baselineStrenth
	 */
	public double getBaselineStrenth() {
		return baselineStrenth;
	}
	/**
	 * @param baselineStrenth the baselineStrenth to set
	 */
	public void setBaselineStrenth(double baselineStrenth) {
		this.baselineStrenth = baselineStrenth;
	}
	/**
	 * @return the timePattern
	 */
	public Pattern getTimePattern() {
		return timePattern;
	}
	/**
	 * @param timePattern the timePattern to set
	 */
	public void setTimePattern(Pattern timePattern) {
		this.timePattern = timePattern;
	}
	
	@Override
	public String toString() {
		String txt = "";
		txt = getNodeId() + "\t";
		txt = getSourceType().getName() + "\t";
		txt = getBaselineStrenth() + "\t";
		if (getTimePattern() != null) {
			txt = getTimePattern().getId();
		}

		return txt;
	}
	
}
