package model.epanet.element.networkcomponent;

public class Valve extends Link {

	private Node node1;
	private Node node2;
	private Double diameter;
	private String type;
	private String setting;
	private Double minorLoss;

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
	 * @return the diameter
	 */
	public Double getDiameter() {
		return diameter;
	}

	/**
	 * @param diameter the diameter to set
	 */
	public void setDiameter(Double diameter) {
		this.diameter = diameter;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the setting
	 */
	public String getSetting() {
		return setting;
	}

	/**
	 * @param setting the setting to set
	 */
	public void setSetting(String setting) {
		this.setting = setting;
	}

	/**
	 * @return the minorLoss
	 */
	public Double getMinorLoss() {
		return minorLoss;
	}

	/**
	 * @param minorLoss the minorLoss to set
	 */
	public void setMinorLoss(Double minorLoss) {
		this.minorLoss = minorLoss;
	}

}
