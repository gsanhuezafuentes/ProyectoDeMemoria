package model.epanet.element.waterquality;

public class Quality {
	private String nodeId;
	private double initialQuality;
	
	/**
	 * @param nodeId the nodeId to set
	 */
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	/**
	 * @param initialQuality the initialQuality to set
	 */
	public void setInitialQuality(double initialQuality) {
		this.initialQuality = initialQuality;
	}
}
