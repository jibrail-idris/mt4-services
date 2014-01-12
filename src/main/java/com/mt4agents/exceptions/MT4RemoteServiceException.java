package com.mt4agents.exceptions;

public class MT4RemoteServiceException extends Exception {

	private static final long serialVersionUID = 7584712186390225565L;

	public MT4RemoteServiceException(String message) {
		super(message);
	}

	public MT4RemoteServiceException(Throwable t) {
		super(t);
	}

	public MT4RemoteServiceException(String message, Throwable t) {
		super(message, t);
	}
}
