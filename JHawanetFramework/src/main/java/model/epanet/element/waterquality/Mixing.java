package model.epanet.element.waterquality;

public final class Mixing {

	public static enum MixingModel {
		MIXED("MIXED"), TWOCOMP("2COM"), FIFO("FIFO"), LIFO("LIFO");

		private String name;

		private MixingModel(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
		
	}

	private String tankId;
	private MixingModel model;
	private double compartmentVolume;
	
	public Mixing() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Copy constructor
	 * @param mixing the object to copy
	 */
	public Mixing(Mixing mixing) {
		this.tankId = mixing.tankId;
		this.model = mixing.model;
		this.compartmentVolume = mixing.compartmentVolume;
	}

	/**
	 * @return the tankId
	 */
	public String getTankId() {
		return tankId;
	}

	/**
	 * @param tankId the tankId to set
	 */
	public void setTankId(String tankId) {
		this.tankId = tankId;
	}

	/**
	 * @return the model
	 */
	public MixingModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(MixingModel model) {
		this.model = model;
	}

	/**
	 * Get Compartment volume value. <br>
	 * <br>
	 * This value only should have assigned a value when the mixing model is
	 * {@link MixingModel#TWOCOMP}
	 * 
	 * @return the compartmentVolume
	 */
	public double getCompartmentVolume() {
		return compartmentVolume;
	}

	/**
	 * Set Compartment volume value. <br>
	 * <br>
	 * Only should assigned a value when the mixing model is
	 * {@link MixingModel#TWOCOMP}
	 * 
	 * @param compartmentVolume the compartmentVolume to set
	 */
	public void setCompartmentVolume(double compartmentVolume) {
		this.compartmentVolume = compartmentVolume;
	}
	
	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getTankId()));
		txt.append(String.format("%-10s\t", getModel().getName()));
		if (getModel() == MixingModel.TWOCOMP) {
			txt.append(String.format("%-10f", getCompartmentVolume()));
		}
		return txt.toString();
	}
	
	/**
	 * Create a copy of this object.
	 * 
	 * @return the copy
	 */
	public Mixing copy() {
		return new Mixing(this);
	}

}
