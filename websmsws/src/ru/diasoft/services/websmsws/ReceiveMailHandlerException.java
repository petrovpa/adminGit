package ru.diasoft.services.websmsws;

public class ReceiveMailHandlerException extends Exception {
	
	public static final long serialVersionUID = 1L;

	public ReceiveMailHandlerException() {
		super();
	}

	public ReceiveMailHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReceiveMailHandlerException(String message) {
		super(message);
	}

	public ReceiveMailHandlerException(Throwable cause) {
		super(cause);
	}

}
