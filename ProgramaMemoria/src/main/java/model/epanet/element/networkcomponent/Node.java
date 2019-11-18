package model.epanet.element.networkcomponent;

public abstract class Node {
	private String id;
	private Point position;

	public final Point getPosition() {
		return position;
	}

	public final void setPosition(Point position) {
		this.position = position;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		String text = "id " + id + " " + position.toString();
		return text;
	}

}
