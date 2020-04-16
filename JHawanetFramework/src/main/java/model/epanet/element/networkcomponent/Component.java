package model.epanet.element.networkcomponent;

import java.util.Objects;

import model.epanet.element.networkdesign.Tag;

/**
 * This class is used as super class of the component of the network (Links and
 * nodes) and defined some field that are common for this.
 *
 */
public abstract class Component {

	private String description;
	private Tag tag;

	public Component() {
		this.description = "";
	}

	/**
	 * Copy constructor. This is a deep copy, i.e., If the field value is a
	 * reference to an object (e.g., a memory address) it copies the reference. If
	 * it is necessary for the object to be completely independent of the original
	 * you must ensure that you replace the reference to the contained objects.
	 * 
	 * @param component the component to copy
	 */
	public Component(Component component) {
		this.description = component.description;
		if (this.tag != null) {
			this.tag = component.tag.copy();			
		}
	}

	/**
	 * Get the description or a empty string if it doesn't exist
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description
	 * @param description the description to set
	 * @throws NullPointerException if description is null
	 */
	public void setDescription(String description) {
		Objects.requireNonNull(description);
		this.description = description;
	}

	/**
	 * Get the tag
	 * 
	 * @return the tag
	 */
	public Tag getTag() {
		return this.tag;
	}

	/**
	 * Set the tag
	 * 
	 * @param tag the tag
	 */
	public void setTag(Tag tag) {
		Objects.requireNonNull(tag);
		this.tag = tag;
	}

}
