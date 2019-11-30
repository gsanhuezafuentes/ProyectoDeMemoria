package model.epanet.element.systemoperation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import exception.ApplicationException;

class ControlTest {

	@ParameterizedTest
	@ValueSource(strings = { //
			"0", "5", "13", //
			"0 AM", "13 AM", //
			"1:63 AM", "12:63 PM", "13:63 AM",//
			"1AM", "1:60 AM", "12:60 AM"})
	void setClocktime_exception_invalidformat(String clocktime) {
		Control control = new Control();

		assertThrows(ApplicationException.class, () -> control.setClocktime(clocktime));
	}

	@ParameterizedTest @ValueSource(strings={ //
	"1 AM","1 PM","5 AM","5 PM","12 AM","12 PM",//
	"1:00 AM","1:00 PM","5:00 AM","5:00 PM","12:00 AM","12:00 PM",//
	"1:59 AM","1:59 PM","5:59 AM","5:59 PM","12:59 AM","12:59 PM",//
	"1:32 AM","1:32 PM","5:32 AM","5:32 PM","12:32 AM","12:32 PM"
	})
	void setClocktime_nothing_validformat(String clocktime) {
		Control control = new Control();

		assertAll(() -> control.setClocktime(clocktime));
	}

}
