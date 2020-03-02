package model.epanet.element.waterquality;

import java.util.Objects;

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
		
		/**
		 * Parse the name to a object of the enum class if exist. if name no exist in enum class so return null;
		 * @param name the name of object
		 * @return the object of enum class or null if no exist
		 */
		public static MixingModel parse(String name) {
			for (MixingModel object : MixingModel.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			return null;
		}
	}

	private MixingModel model;
	private Double mixingFraction;
	
	public Mixing() {
		this.model = MixingModel.MIXED;
	}
	
	/**
	 * Copy constructor
	 * @param mixing the object to copy
	 */
	public Mixing(Mixing mixing) {
		this.model = mixing.model;
		this.mixingFraction = mixing.mixingFraction;
	}

	/**
	 * @return the model
	 */
	public MixingModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 * @throws NullPointerException if model is null
	 */
	public void setModel(MixingModel model) {
		Objects.requireNonNull(model);
		this.model = model;
	}

	/**
	 * Get mixing fraction value. <br>
	 * <br>
	 * This value should be assigned when the mixing model is not
	 * {@link MixingModel#MIXED}
	 * 
	 * @return the compartmentVolume or null if it is not assigned
	 */
	public Double getMixingFraction() {
		return mixingFraction;
	}

	/**
	 * Set mixing fraction value. <br>
	 * <br>
	 * This value should be assigned when the mixing model is not
	 * {@link MixingModel#MIXED}
	 * 
	 * @param mixingFraction the mixing fraction or null if it is not assigned
	 */
	public void setMixingFraction(Double mixingFraction) {
		this.mixingFraction = mixingFraction;
	}
	
	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getModel().getName()));
		if (getModel() != MixingModel.MIXED) {
			txt.append(String.format("%-10f", getMixingFraction()));
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
