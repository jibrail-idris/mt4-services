package com.mt4agents.exceptions;

public class AgentClientException extends Exception {
	
	private static final long serialVersionUID = 5164770565297424633L;
	
	public AgentClientException(String message) {
		super(message);
	}
	
	public AgentClientException(Throwable t) {
		super(t);
	}
	
	public AgentClientException(String message, Throwable t) {
		super(message, t);
	}
}
