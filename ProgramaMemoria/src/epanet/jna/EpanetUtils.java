package epanet.jna;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AllPermission;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class EpanetUtils {

	/**
	 * Transform the elements of buffer to char and put it in a String.
	 * 
	 * @param buffer the buffer to transform
	 * @return String
	 */
	public static String byteBufferToString(ByteBuffer buffer) {
		StringBuilder text = new StringBuilder();
		byte[] bufferArray = buffer.array();
		for (byte element : bufferArray) {
			char charElement = (char) element;
			text.append(charElement);
		}

		return text.toString().trim();
	}

	/**
	 * Transform IntBuffer to int
	 * 
	 * @param buffer
	 * @return int
	 */
	public static int IntBufferToInt(IntBuffer buffer) {
		return buffer.get();
	}

	/**
	 * Transform FloatBuffer to float
	 * 
	 * @param buffer
	 * @return float
	 */
	public static float FloatBufferToFloat(FloatBuffer buffer) {
		return buffer.get();
	}
	
	/**
	 * Transform a floatArray in a ByteBuffer.
	 * 
	 * @param text text to transform.
	 * @return ByteBuffer
	 */
	public static FloatBuffer floatToByteBuffer(float[] array) {
		int sizeOfMemory = array.length*Float.BYTES;
		Pointer pointer = new Memory(sizeOfMemory);
		for (int i = 0; i < array.length; i++) {
			pointer.setFloat(i*Float.BYTES, array[i]);
		}
		FloatBuffer buffer = pointer.getByteBuffer(0, sizeOfMemory).asFloatBuffer();
		return buffer;
	}

	/**
	 * Transform a text in a ByteBuffer.
	 * 
	 * @param text text to transform.
	 * @return ByteBuffer
	 */
	public static ByteBuffer stringToByteBuffer(String text) {
		Pointer pointer = new Memory(text.length()+1);
		pointer.setString(0, text);
		return pointer.getByteBuffer(0, text.length()+1);
	}
}
