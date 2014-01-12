package com.mt4agents.exceptions;

public class ClientTradesServiceException extends Exception {

	private static final long serialVersionUID = -8089389425266971123L;

	public ClientTradesServiceException(String message) {
		super(message);
	}
	
	public ClientTradesServiceException(Throwable t) {
		super(t);
	}
	
	public ClientTradesServiceException(String message, Throwable t) {
		super(message, t);
	}
}
