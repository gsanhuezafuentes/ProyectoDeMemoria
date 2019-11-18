package model.epanet.element.systemoperation;

public class Status {
//	public static enum Setting {
//		OPEN("OPEN"), CLOSE("OPEN");
//
//		private String name;
//
//		private Setting(String name) {
//			this.name = name;
//		}
//	}

	private String linkId;
	private String status;

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
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		String txt = "";
		txt += getLinkId() + "\t";
		txt += getStatus();
		return txt;
	}

}
