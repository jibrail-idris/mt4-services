package com.mt4agents.exceptions;

public class AgentException extends Exception {

	private static final long serialVersionUID = -2097573194356776312L;
	
	public AgentException(String message) {
		super(message);
	}
	
	public AgentException(Throwable t) {
		super(t);
	}
	
	public AgentException(String message, Throwable t) {
		super(message, t);
	}

}
