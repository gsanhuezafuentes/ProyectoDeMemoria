package view.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RegexUtilsTest {

	@ParameterizedTest
	@CsvSource({ "13.3333", "-13.333", "13", "0"})
	void DecimalNumberRegex_VALIDNUMBER_MATCH(String input) {
		assertTrue(input.matches(RegexUtils.DECIMAL_NUMBER_REGEX));
	}

	@ParameterizedTest
	@CsvSource({ "13.3333.333", "--13", "a", "13a3" })
	void DecimalNumberRegex_INVALIDNUMBER_NOMATCH(String input) {
		assertFalse(input.matches(RegexUtils.DECIMAL_NUMBER_REGEX));
	}

	@ParameterizedTest
	@CsvSource({ "0", "-1", "1" })
	void WholeNumberRegex_VALIDNUMBER_MATCH(String input) {
		assertTrue(input.matches(RegexUtils.WHOLE_NUMBER_REGEX));
	}

	@ParameterizedTest
	@CsvSource({ "13.3333.333", "--13", "13.3333", "-13.333", "a" })
	void WholeNumberRegex_INVALIDNUMBER_NOMATCH(String input) {
		assertFalse(input.matches(RegexUtils.WHOLE_NUMBER_REGEX));
	}

}
