package model.epanet.element.systemoperation;

public final class Status {
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

	public Status() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Copy constructor.
	 * @param status the object to copy
	 */
	public Status(Status status) {
		this.linkId = status.linkId;
		this.status = status.status;
	}

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
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getLinkId()));
		txt.append(String.format("%-10s\t", getStatus()));
		return txt.toString();
	}
	
	/**
	 * Create a copy of this object.
	 * @return the copy
	 */
	public Status copy() {
		return new Status(this);
	}

}
