package com.mt4agents.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.mt4agents.dto.AgentDTO;
import com.mt4agents.services.AgentRelationshipService;
import com.mt4agents.services.AgentService;

public class SaveAgentValidator implements Validator {

	@Autowired
	private AgentService agentService;

	@Autowired
	private AgentRelationshipService agentRelationshipService;

	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	public void setAgentRelationshipService(
			AgentRelationshipService agentRelationshipService) {
		this.agentRelationshipService = agentRelationshipService;
	}

	public boolean supports(Class<?> clazz) {
		return AgentDTO.class.equals(clazz);
	}

	public void validate(Object obj, Errors errors) {
		AgentDTO agent = (AgentDTO) obj;

		Integer agentId = agent.getAgentId();
		if (agentId == null && agent.getCommission() == null) {
			errors.reject("mt4agents.exception.agent.commission.notset");
		}

		Integer parentAgentId = agent.getParentAgentId();
		boolean parentAgentExists = agentService.checkExistsById(parentAgentId);
		if (parentAgentId != null && !parentAgentExists) {
			errors.rejectValue("parentAgentId",
					"mt4agents.exception.agent.invalidparent",
					new Object[] { parentAgentId }, "");
		}

		Integer mt4Login = agent.getLogin();

		if (agentId == null && agent.getLogin() == null) {
			errors.reject("mt4agents.exception.agent.mt4login.notassigned");
		}

		/*
		 * Check if the mt4Login has already been assigned to another agent. If
		 * it is, throw AgentException.
		 */
		if (mt4Login != null && agentService.checkExistsByMT4Login(mt4Login)) {
			errors.rejectValue("login",
					"mt4agents.exception.agent.mt4login.inuse",
					new Object[] { mt4Login }, "");
		}

//		validateCommission(parentAgentId, agent.getCommission(), errors);
	}

//	private void validateCommission(Integer parentAgentId, Double commission,
//			Errors errors) {
//		Agent parentAgent = agentService.getAgentById(parentAgentId);
//		if (parentAgent != null) {
//			Double parentCommission = parentAgent.getCommission();
//			// Get all the downline agents' commissions and sum them up.
//			List<AgentRelationship> downlines = agentRelationshipService
//					.getChildrenRelationships(parentAgent.getId());
//			Double totalDownlineCommission = 0.00;
//			for (AgentRelationship downline : downlines) {
//				Agent downlineAgent = downline.getChildAgent();
//				Double downlineAgentCommission = downlineAgent.getCommission();
//				totalDownlineCommission += downlineAgentCommission;
//			}
//			// If the commission to be added exceeds the summed downline
//			// commission, throw error.
//			if ((totalDownlineCommission + commission) > parentCommission) {
//				errors.rejectValue("login",
//						"mt4agents.exception.agent.commission.exceed", null, "");
//			}
//		}
//	}

}
