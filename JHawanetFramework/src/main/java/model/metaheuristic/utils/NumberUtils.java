package model.metaheuristic.utils;

// TODO remover
public class NumberUtils {

	/**
	 * Round the number to numberOfDecimals digits
	 * @param number the number to round
	 * @param numberOfDecimals the number of decimals
	 * @return the number 
	 * @exception IllegalArgumentException When the number of decimals is negative
	 */
	public static double roundTo(double number, int numberOfDecimals) throws IllegalArgumentException{
		if (numberOfDecimals < 0) {
			throw new IllegalArgumentException(
					"The number of decimals is " + numberOfDecimals + " but it can't be negative");
		}
		double wholePart = Math.floor(number);
		double decimalPart = number - wholePart;
		double decimalPartToWholePart = decimalPart * Math.pow(10, numberOfDecimals);
		decimalPart = Math.round(decimalPartToWholePart) / Math.pow(10, numberOfDecimals);
		return wholePart + decimalPart;
	}
}
