package model.epanet.element.waterquality;

public final class Reaction {
	private String code;
	
	public Reaction() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Copy constructor
	 * @param reaction the object to copy
	 */
	public Reaction(Reaction reaction) {
		this.code = reaction.code;
	}

	/**
	 * @return the reactionCode
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param reactionCode the reactionCode to set
	 */
	public void setCode(String reactionCode) {
		this.code = reactionCode;
	}
	
	@Override
	public String toString() {
		return getCode();
	}
	
	/**
	 * Create a copy of this object
	 * @return the copy
	 */
	public Reaction copy() {
		return new Reaction(this);
	}
}
