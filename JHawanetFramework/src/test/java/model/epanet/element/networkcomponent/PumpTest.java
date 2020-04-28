package model.epanet.element.networkcomponent;

import model.epanet.element.networkcomponent.Pump.PumpProperty;
import model.epanet.element.systemoperation.Curve;
import model.epanet.element.systemoperation.Pattern;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PumpTest {

	@Test
	void testSetProperty_TestEnumHeadPassingAIllegalArgument_IllegalArgumentException() {
		Pump pump = new Pump();
		assertThrows(IllegalArgumentException.class, () -> pump.setProperty(PumpProperty.HEAD, new Curve()));

	}

	@Test
	void testSetProperty_TestEnumPatternPassingAIllegalArgument_IllegalArgumentException() {
		Pump pump = new Pump();
		assertThrows(IllegalArgumentException.class, () -> pump.setProperty(PumpProperty.PATTERN, new Pattern()));

	}

	@Test
	void testSetProperty_TestEnumSpeedPassingAIllegalArgument_IllegalArgumentException() {
		Pump pump = new Pump();
		assertThrows(IllegalArgumentException.class, () -> pump.setProperty(PumpProperty.SPEED, (int) 5));

	}

	@Test
	void testSetProperty_TestEnumPowerPassingAIllegalArgument_IllegalArgumentException() {
		Pump pump = new Pump();
		assertThrows(IllegalArgumentException.class, () -> pump.setProperty(PumpProperty.POWER, (int) 5));
	}

	@Test
	void testSetProperty_TestEnumHeadPassingAValidArgument_IllegalArgumentException() {
		Pump pump = new Pump();
		assertDoesNotThrow(() -> pump.setProperty(PumpProperty.HEAD, "curva1"));

	}

	@Test
	void testSetProperty_TestEnumPatternPassingAValidArgument_IllegalArgumentException() {
		Pump pump = new Pump();
		assertDoesNotThrow(() -> pump.setProperty(PumpProperty.PATTERN, "pattern1"));

	}

	@Test
	void testSetProperty_TestEnumSpeedPassingAValidArgument_IllegalArgumentException() {
		Pump pump = new Pump();
		assertDoesNotThrow(() -> pump.setProperty(PumpProperty.SPEED, (double) 5));

	}

	@Test
	void testSetProperty_TestEnumPowerPassingAValidArgument_IllegalArgumentException() {
		Pump pump = new Pump();
		assertDoesNotThrow(() -> pump.setProperty(PumpProperty.POWER, (double) 5));
	}
}
