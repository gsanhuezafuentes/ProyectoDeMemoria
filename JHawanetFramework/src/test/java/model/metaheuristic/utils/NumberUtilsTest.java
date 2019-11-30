package model.metaheuristic.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NumberUtilsTest {

	@ParameterizedTest
	@CsvSource({ "0.00, 0, 2 ", // Expected, Number, NumberOfDecimals
			"0.33, 0.33333, 2", //
			"13.33, 13.3333, 2", //
			"13.57, 13.5678, 2",//
			"13.568, 13.5678, 3",//
			"14, 13.5678, 0"
	})
	void roundDoubleToReduceTheNumberOfDecimals(double expected, double number, int ndecimal) {
		assertEquals(expected, NumberUtils.roundTo(number, ndecimal));
	}
	
	@ParameterizedTest
	@CsvSource({
		"13.3333, -2"
	})
	void roundDoubleToReduceTheNumberOfDecimalsWithANegativeNumberOfDecimals(double number, int ndecimal) {
		assertThrows(IllegalArgumentException.class, () -> NumberUtils.roundTo(number, ndecimal));
	}

}
