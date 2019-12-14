package model.epanet.element.waterquality;

public final class Quality {
	private String nodeId;
	private double initialQuality;
	
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
	 * @return the initialQuality
	 */
	public double getInitialQuality() {
		return initialQuality;
	}
	
	/**
	 * @param initialQuality the initialQuality to set
	 */
	public void setInitialQuality(double initialQuality) {
		this.initialQuality = initialQuality;
	}
	
	@Override
	public String toString() {
		String txt = "";
		txt += getNodeId() + "\t";
		txt += getInitialQuality() + "\t";

		return txt;
	}
}
