package model.epanet.element.waterquality;

public class Mixing {

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
		String txt = "";
		txt = getTankId() + "\t";
		txt = getModel().getName() + "\t";
		if (getModel() == MixingModel.TWOCOMP) {
			txt += getCompartmentVolume();
		}
		return txt;
	}

}
