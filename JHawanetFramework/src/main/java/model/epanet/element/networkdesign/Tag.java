package model.epanet.element.networkdesign;

import java.util.Objects;

public final class Tag {
	public static enum TagType {
		NODE("NODE"), LINK("LINK");

		private String name;

		private TagType(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		
		public static TagType parse(String name) {
			for (TagType object : TagType.values()) {
				if (object.getName().equalsIgnoreCase(name)) {
					return object;
				}
			}
			throw new IllegalArgumentException("There are not a valid element with the name " + name);
		}

	}

	private TagType type;
	private String label;

	public Tag() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Copy constructor
	 * @param tag the tag to cop 
	 */
	public Tag(Tag tag) {
		this.type = tag.type;
		this.label = tag.label;
	}

	/**
	 * Get the type
	 * @return the type
	 */
	public TagType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 * @throws NullPointerException if type is null 
	 */
	public void setType(TagType type) {
		Objects.requireNonNull(type);
		this.type = type;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 * @throws NullPointerException if label is null
	 */
	public void setLabel(String label) {
		Objects.requireNonNull(label);
		this.label = label;
	}

	@Override
	public String toString() {
		StringBuilder txt = new StringBuilder();
		txt.append(String.format("%-10s\t", getType().getName()));
		txt.append(String.format("%-10s", getLabel()));
		return txt.toString();
	}

	/**
	 * Copy this object.
	 * 
	 * @return the copy.
	 */
	public Tag copy() {
		return new Tag(this);
	}
}
