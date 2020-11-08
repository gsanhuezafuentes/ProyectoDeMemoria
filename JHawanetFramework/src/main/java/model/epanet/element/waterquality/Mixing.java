package model.epanet.element.waterquality;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.epanet.element.Network;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class Mixing {
	private static final Logger LOGGER = LoggerFactory.getLogger(Mixing.class);

	public enum MixingModel {
		MIXED("MIXED"), TWOCOMP("2COMP"), FIFO("FIFO"), LIFO("LIFO");

		private final String name;

		MixingModel(String name) {
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
		 * @throws IllegalArgumentException if name is not valid
		 * @return the mixing model
		 */
		public static @NotNull MixingModel parse(String name) {
			for (MixingModel object : MixingModel.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			throw new IllegalArgumentException("There are not a valid element with the name " + name);

		}
	}

	@NotNull private MixingModel model;
	@Nullable private Double mixingFraction;
	
	public Mixing() {
		this.model = MixingModel.MIXED;
	}
	
	/**
	 * Copy constructor
	 * @param mixing the object to copy
	 * @throws NullPointerException if mixing is null.
	 */
	public Mixing(@NotNull Mixing mixing) {
		Objects.requireNonNull(mixing);
		LOGGER.debug("Clonning Mixing.");

		this.model = mixing.model;
		this.mixingFraction = mixing.mixingFraction;
	}

	/**
	 * @return the model
	 */
	public @NotNull MixingModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 * @throws NullPointerException if model is null
	 */
	public void setModel(@NotNull MixingModel model) {
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
	public @Nullable Double getMixingFraction() {
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
	public void setMixingFraction(@Nullable Double mixingFraction) {
		this.mixingFraction = mixingFraction;
	}
	
	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		map.put("model", model);
		if (mixingFraction == null) {
			map.put("mixingFraction", "");
		} else {
			map.put("mixingFraction", mixingFraction);//
		}
		return gson.toJson(map);
	}

	/**
	 * Create a copy of this object.
	 * 
	 * @return the copy
	 */
	public @NotNull Mixing copy() {
		return new Mixing(this);
	}

}
