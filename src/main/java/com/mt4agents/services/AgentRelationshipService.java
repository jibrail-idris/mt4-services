package com.mt4agents.services;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mt4agents.dao.AgentRelationshipDAO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentRelationship;
import com.mt4agents.exceptions.AgentRelationshipException;

@Controller
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AgentRelationshipService {

	private AgentRelationshipDAO agentRelationshipDAO;
	private AgentService agentService;
	private MessageSource messageSource;

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setAgentRelationshipDAO(
			AgentRelationshipDAO agentRelationshipDAO) {
		this.agentRelationshipDAO = agentRelationshipDAO;
	}

	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	public void saveRelationship(Integer parentAgentId, Integer childAgentId)
			throws AgentRelationshipException {

		if (parentAgentId == childAgentId) {
			throw new AgentRelationshipException(messageSource.getMessage(
					"mt4agents.exception.relationship.self", null, Locale.US));
		}

		/*
		 * Make sure that parent and child do not have a relationship before
		 * they are assigned to a relationship. Also make sure the inverse does
		 * not happen as well, that is the child as parent and parent as child.
		 */
		if (!hasRelationship(parentAgentId, childAgentId)
				&& !hasRelationship(childAgentId, parentAgentId)) {			
			Agent parentAgent = agentService.getAgentById(parentAgentId);
			Agent childAgent = agentService.getAgentById(childAgentId);
			AgentRelationship relationship;
			if (childAgent.getParentRelationship() != null) {
				relationship = childAgent.getParentRelationship();
			} else {
				relationship = new AgentRelationship();
			}
			relationship.setParentAgent(parentAgent);
			relationship.setChildAgent(childAgent);
			agentRelationshipDAO.save(relationship);
			childAgent.setParentRelationship(relationship);
		} /*
		 * else { throw new AgentRelationshipException(messageSource.getMessage(
		 * "mt4agents.exception.relationship.hasrelationship", new Object[] {
		 * parentAgentId, childAgentId }, Locale.US)); }
		 */
	}

	public boolean hasRelationship(Integer parentAgentId, Integer childAgentId) {
		return agentRelationshipDAO.exists(parentAgentId, childAgentId);
	}

	/**
	 * Returns an AgentRelationship where agent is acting as child.
	 * 
	 * @param agentId
	 * @return
	 */
	public AgentRelationship getParentRelationship(Integer agentId) {
		return agentRelationshipDAO.getParentRelationship(agentId);
	}

	/**
	 * Returns an AgentRelationship where agent is acting as parent.
	 * 
	 * @param agentId
	 * @return
	 */
	public List<AgentRelationship> getChildrenRelationships(Integer agentId) {
		return agentRelationshipDAO.getChildrenRelationships(agentId);
	}
}
