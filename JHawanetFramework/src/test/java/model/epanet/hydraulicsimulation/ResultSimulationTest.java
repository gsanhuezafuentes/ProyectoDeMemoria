package model.epanet.hydraulicsimulation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ResultSimulationTest {
    private static class ResultStub extends ResultSimulation {
        public ResultStub(String id,long timeInSeconds) {
            super(id, timeInSeconds);
        }
    }

    @Test
    void getTimeInSeconds() {
        ResultStub object = new ResultStub("someid",72000);
        assertEquals(72000, object.getTimeInSeconds());
    }

    @ParameterizedTest
    @CsvSource({
            "72000, 20:00:00",//
            "0, 00:00:00",//
            "1, 00:00:01",//
            "1800, 00:30:00",//
            "86159, 23:55:59",//
            "86399, 23:59:59",//
    })
    void getTimeString(long time, String expected) {
        ResultStub object = new ResultStub("someid", time);
        assertEquals(expected, object.getTimeString());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 86400, 86401})
    void ResultSimulation_OutOfValidRange_IllegalArgumentException(long time) {
        assertThrows(IllegalArgumentException.class, () -> new ResultStub("someid", time));
    }
}