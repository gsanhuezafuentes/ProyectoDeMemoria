package model.epanet.element.networkdesign;

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

public final class Tag {
	private static final Logger LOGGER = LoggerFactory.getLogger(Tag.class);

	public enum TagType {
		NODE("NODE"), LINK("LINK");

		private final String name;

		TagType(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Parse the string to the enum
		 * @param name the name
		 * @return the associated enum
		 * @throws IllegalArgumentException if name is not valid
		 */
		public static @NotNull TagType parse(String name) {
			for (TagType object : TagType.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			throw new IllegalArgumentException("There are not a valid element with the name " + name);
		}

	}

	@Nullable
	private TagType type;
	@NotNull private String label;

	public Tag() {
		this.label = "";
	}

	/**
	 * Copy constructor
	 * @param tag the tag to copy
	 * @throws NullPointerException if tag is null
	 */
	public Tag(@NotNull Tag tag) {
		Objects.requireNonNull(tag);
		LOGGER.debug("Clonning tag.");

		this.type = tag.type;
		this.label = tag.label;
	}

	/**
	 * Get the type
	 * @return the type. It can be null if hasn't been initialized
	 */
	public @Nullable TagType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 * @throws NullPointerException if type is null 
	 */
	public void setType(@NotNull TagType type) {
		Objects.requireNonNull(type);
		this.type = type;
	}

	/**
	 * @return the label
	 */
	public @NotNull String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 * @throws NullPointerException if label is null
	 */
	public void setLabel(@NotNull String label) {
		Objects.requireNonNull(label);
		this.label = label;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new LinkedHashMap<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		if (type == null){
			map.put("type", "");
		}
		map.put("type", type);
		map.put("label", label);
		return gson.toJson(map);
	}

	/**
	 * Copy this object.
	 * 
	 * @return the copy.
	 */
	public @NotNull Tag copy() {
		return new Tag(this);
	}
}
