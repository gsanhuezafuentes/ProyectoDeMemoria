package model.epanet.element.systemoperation;

public class Status {
	public static enum Setting {
		OPEN("OPEN"), CLOSE("OPEN");

		private String name;

		private Setting(String name) {
			this.name = name;
		}
	}
	
	private String linkId;
	private Setting status;
	/**
	 * @return the linkId
	 */
	public String getLinkId() {
		return linkId;
	}
	/**
	 * @param linkId the linkId to set
	 */
	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}
	/**
	 * @return the status
	 */
	public Setting getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Setting status) {
		this.status = status;
	}
	
	
}
