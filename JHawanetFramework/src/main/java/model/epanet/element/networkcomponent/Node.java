package model.epanet.element.networkcomponent;

import java.util.Objects;

import model.epanet.element.utils.Point;
import model.epanet.element.waterquality.Quality;
import model.epanet.element.waterquality.Source;

public abstract class Node extends Component {
	private String id;
	private Point position;
	private Quality initialQuality;
	private Source sourceQuality;;

	public Node() {
		this.id = "";
	}

	/**
	 * Copy constructor. This is a deep copy, i.e., If the field value is a
	 * reference to an object (e.g., a memory address) it copies the reference. If
	 * it is necessary for the object to be completely independent of the original
	 * you must ensure that you replace the reference to the contained objects.
	 * 
	 * @param node
	 */
	public Node(Node node) {
		super(node);
		this.id = node.id;
		this.position = node.position;
		if (node.initialQuality != null) {
			this.initialQuality = node.initialQuality.copy();

		}
		if (node.sourceQuality != null) {
			this.sourceQuality = node.sourceQuality.copy();			
		}
	}

	public final Point getPosition() {
		return position;
	}

	public final void setPosition(Point position) {
		this.position = position;
	}

	/**
	 * Get the id
	 * @return the id or a empty string if it doesn't exist
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id
	 * @param id the id to set
	 * @throws NullPointerException if id is null
	 */
	public void setId(String id) {
		Objects.requireNonNull(id);
		this.id = id;
	}

	/**
	 * Get initial quality
	 * @return the initialQuality if exist or null
	 */
	public Quality getInitialQuality() {
		return initialQuality;
	}

	/**
	 * @param initialQuality the initialQuality to set or null if it does not exist
	 */
	public void setInitialQuality(Quality initialQuality) {
		this.initialQuality = initialQuality;
	}

	/**
	 * Get the source quality
	 * @return the sourceQuality if exist or null
	 */
	public Source getSourceQuality() {
		return sourceQuality;
	}

	/**
	 * Set the source quality
	 * @param sourceQuality the sourceQuality to set or null if it does not exist
	 */
	public void setSourceQuality(Source sourceQuality) {
		this.sourceQuality = sourceQuality;
	}

	@Override
	public String toString() {
		String text = "id " + id + " " + position.toString();
		return text;
	}

	/**
	 * Copy this node. This is a shallow copy.
	 * 
	 * @return the copy
	 */
	public abstract Node copy();

}
