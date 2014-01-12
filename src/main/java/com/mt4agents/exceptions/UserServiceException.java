package com.mt4agents.exceptions;

public class UserServiceException extends Exception {
	
	private static final long serialVersionUID = -9000558067488475435L;

	public UserServiceException(String message) {
		super(message);
	}
	
	public UserServiceException(Throwable t) {
		super(t);
	}
	
	public UserServiceException(String message, Throwable t) {
		super(message, t);
	}
}
