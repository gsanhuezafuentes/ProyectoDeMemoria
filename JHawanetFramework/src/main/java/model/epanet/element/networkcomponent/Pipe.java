package model.epanet.element.networkcomponent;

public final class Pipe extends Link {

	static public enum PipeStatus {
		OPEN("OPEN"), CLOSED("CLOSED"), CV("CV");

		private String name;

		private PipeStatus(String name) {
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

	}

	private Node node1;
	private Node node2;
	private double length;
	private double diameter;
	private double roughness;
	private double mloss;
	private PipeStatus status;

	/**
	 * @return the node1
	 */
	public Node getNode1() {
		return node1;
	}

	/**
	 * @param node1 the node1 to set
	 */
	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	/**
	 * @return the node2
	 */
	public Node getNode2() {
		return node2;
	}

	/**
	 * @param node2 the node2 to set
	 */
	public void setNode2(Node node2) {
		this.node2 = node2;
	}

	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(double length) {
		this.length = length;
	}

	/**
	 * @return the diam
	 */
	public double getDiameter() {
		return diameter;
	}

	/**
	 * @param diam the diam to set
	 */
	public void setDiameter(double diam) {
		this.diameter = diam;
	}

	/**
	 * @return the roughness
	 */
	public double getRoughness() {
		return roughness;
	}

	/**
	 * @param roughness the roughness to set
	 */
	public void setRoughness(double roughness) {
		this.roughness = roughness;
	}

	/**
	 * @return the mloss
	 */
	public double getMloss() {
		return mloss;
	}

	/**
	 * @param mloss the mloss to set
	 */
	public void setMloss(double mloss) {
		this.mloss = mloss;
	}

	/**
	 * @return the status
	 */
	public PipeStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(PipeStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		String txt = "";
		txt += getId() + "\t";
		txt += getNode1().getId() + "\t";
		txt += getNode2().getId() + "\t";
		txt += getLength() + "\t";
		txt += getDiameter() + "\t";
		txt += getRoughness() + "\t";
		txt += getMloss() + "\t";
		txt += getStatus();
		return txt;
	}
}
