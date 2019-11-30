package model.epanet.element.waterquality;

public class Reaction {
	private String Code;

	/**
	 * @return the reactionCode
	 */
	public String getCode() {
		return Code;
	}

	/**
	 * @param reactionCode the reactionCode to set
	 */
	public void setCode(String reactionCode) {
		this.Code = reactionCode;
	}
	
	@Override
	public String toString() {
		return getCode();
	}
}
