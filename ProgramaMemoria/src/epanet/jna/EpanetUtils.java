package epanet.jna;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class EpanetUtils {

	/**
	 * Transform the elements of buffer to char and put it in a String.
	 * @param buffer the buffer to transform
	 * @return String 
	 */
	public static String byteBufferToString(ByteBuffer buffer) {
		StringBuilder text = new StringBuilder();
		byte[] bufferArray = buffer.array();
		System.out.println("ToString");
		for (byte element : bufferArray) {
			char charElement = (char) element;
			System.out.println(element +" "+ charElement);

			text.append(charElement);
		}
		
		return text.toString().trim();
	}
	
	/**
	 * Transform IntBuffer to int
	 * @param buffer
	 * @return int
	 */
	public static int IntBufferToInt(IntBuffer buffer) {
		return buffer.get();
	}
	
	/**
	 * Transform FloatBuffer to float
	 * @param buffer
	 * @return float
	 */
	public static float FloatBufferToFloat(FloatBuffer buffer) {
		return buffer.get();
	}
	
	/**
	 * Transform a text in a ByteBuffer.
	 * @param text text to transform.
	 * @return ByteBuffer
	 */
	public static ByteBuffer StringToByteBuffer(String text) {
		ByteBuffer buffer = ByteBuffer.allocate(text.length());

		char[] charArray = text.toCharArray();
		System.out.println("Text lenght " + charArray.length );
		for (char element : charArray) {
			byte byteElement = (byte) element;
			System.out.println(element +" "+ byteElement);
			buffer.put(byteElement);
		}
		System.out.println();
		return buffer;
	}
	
	public static void main(String[] args) {
		ByteBuffer buffer = EpanetUtils.StringToByteBuffer("Hello how are you");
		System.out.println(buffer.toString());
		System.out.println(EpanetUtils.byteBufferToString(buffer));
	}
}
