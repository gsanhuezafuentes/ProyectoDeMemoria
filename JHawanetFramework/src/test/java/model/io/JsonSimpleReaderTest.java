package model.io;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonSimpleReaderTest {
    static String json;
    JsonSimpleReader jreader;

    @BeforeAll
    static void beforeAll() {
        json = "{\"int\"=5, \"double\" = 2.5, \"ints\" = [0,1,2,3,4,5], \"doubles\" = [0.1, 0.2, 2.3, 2.5, 2.5]" +
                ", \"doubleMatrix\"=[[0.2,0.3,0.4],[1.2,1.3,1.5]], \"intMatrix\" = [[0,1,2], [3,4,5]]" +
                ", \"string\" = \"A simple string\", \"boolean\"= true" +
                "}";
    }
    @BeforeEach
    void setUp() {
        jreader = JsonSimpleReader.readJsonString(json);
    }

    @Test
    void shouldGetIntReturnAInt() {
        assertEquals(5, jreader.getInt("int"));
    }

    @Test
    void shouldGetDoubleReturnADouble() {
        assertEquals(2.5, jreader.getDouble("double"));
    }

    @Test
    void shouldGetBooleanReturnABoolean() {
        assertEquals(true, jreader.getBoolean("boolean"));
    }

    @Test
    void shouldGetIntegerArrayReturnAIntegerArray() {
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5}, jreader.getIntegerArray("ints"));
    }

    @Test
    void shouldGetDoubleArrayReturnADoubleArray() {
        assertArrayEquals(new double[]{0.1, 0.2, 2.3, 2.5, 2.5}, jreader.getDoubleArray("doubles"));
    }

    @Test
    void shouldGetIntegerMatrixReturnAIntegerMatrix() {
        assertArrayEquals(new int[][]{{0, 1, 2}, {3, 4, 5}}, jreader.getIntegerMatrix("intMatrix"));
    }

    @Test
    void shouldGetDoublerMatrixReturnADoublerMatrix() {
        assertArrayEquals(new double[][]{{0.2, 0.3, 0.4}, {1.2, 1.3, 1.5}}, jreader.getDoublerMatrix("doubleMatrix"));
    }

    @Test
    void shouldGetStringReturnAString() {
        assertEquals("A simple string", jreader.getString("string"));
    }

}