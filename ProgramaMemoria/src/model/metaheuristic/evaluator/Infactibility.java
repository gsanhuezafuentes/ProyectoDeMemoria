package model.metaheuristic.evaluator;

public class Infactibility {
	private int numberOfInfactibility;
	private double gradeOfInfactibility;
	public Infactibility(int numberOfInfactibility, double gradeOfInfactibility) {
		this.numberOfInfactibility = numberOfInfactibility;
		this.gradeOfInfactibility = gradeOfInfactibility;
	}
	/**
	 * @return the numberOfInfactibility
	 */
	public int getNumberOfInfactibility() {
		return numberOfInfactibility;
	}
	/**
	 * @param numberOfInfactibility the numberOfInfactibility to set
	 */
	public void setNumberOfInfactibility(int numberOfInfactibility) {
		this.numberOfInfactibility = numberOfInfactibility;
	}
	/**
	 * @return the gradeOfInfactibility
	 */
	public double getGradeOfInfactibility() {
		return gradeOfInfactibility;
	}
	/**
	 * @param gradeOfInfactibility the gradeOfInfactibility to set
	 */
	public void setGradeOfInfactibility(double gradeOfInfactibility) {
		this.gradeOfInfactibility = gradeOfInfactibility;
	}
	
	@Override
	public String toString() {
		String text = "Number of infactibility = " + this.numberOfInfactibility + " Grade of infactibility = " + this.gradeOfInfactibility;
		return text;
	}
	
}
