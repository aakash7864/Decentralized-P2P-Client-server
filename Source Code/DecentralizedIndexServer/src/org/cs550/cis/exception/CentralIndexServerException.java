package org.cs550.cis.exception;

/**
 * Custom exception class for Central Index server.
 * 
 */
public class CentralIndexServerException extends Exception {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 4578396135088470965L;

	public CentralIndexServerException(String message) {
		super(message);
	}

	public CentralIndexServerException(Throwable cause) {
		super(cause);
	}

	public CentralIndexServerException(String message, Throwable cause) {
		super(message, cause);
	}

}
