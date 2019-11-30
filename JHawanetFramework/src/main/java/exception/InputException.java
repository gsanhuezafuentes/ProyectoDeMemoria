package exception;

public class InputException extends Exception {
	private static final long serialVersionUID = 1L;

	public InputException(String message) {
		super(message);
	}
	
	public InputException(String message, Throwable cause) {
		super(message, cause);
	}
}
