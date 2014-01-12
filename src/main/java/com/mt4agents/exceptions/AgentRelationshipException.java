package com.mt4agents.exceptions;

/**
 * 
 * @author Jibrail Idris
 *
 */
public class AgentRelationshipException extends Exception {
	
	private static final long serialVersionUID = -8210544950694557726L;
	
	public AgentRelationshipException(String message) {
		super(message);
	}
	
	public AgentRelationshipException(Throwable t) {
		super(t);
	}
	
	public AgentRelationshipException(String message, Throwable t) {
		super(message, t);
	}

}
