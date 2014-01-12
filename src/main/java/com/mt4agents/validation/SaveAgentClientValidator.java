package com.mt4agents.validation;

import java.util.Date;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.dto.MT4UserDTO;
import com.mt4agents.formatters.LabelFormatter;
import com.mt4agents.services.AgentClientService;
import com.mt4agents.services.AgentService;

public class SaveAgentClientValidator implements Validator {

	private AgentService agentService;
	private AgentClientService agentClientService;

	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	public void setAgentClientService(AgentClientService agentClientService) {
		this.agentClientService = agentClientService;
	}

	public boolean supports(Class<?> clazz) {
		return AgentClientDTO.class.equals(clazz);
	}

	public void validate(Object obj, Errors errors) {
		AgentClientDTO agentClient = (AgentClientDTO) obj;

		Integer agentClientId = agentClient.getAgentClientId();
		Integer mt4Login = agentClient.getLogin();
		Integer agentId = agentClient.getAgentId();
		Date registrationDate = agentClient.getRegistrationDate();

		MT4UserDTO userDTO = agentClient.getMT4UserDTO();
		if (userDTO == null) {
			errors.rejectValue("login",
					"mt4agents.exception.agentclient.invalidlogin",
					new Object[] { mt4Login }, "");
		} else {
			String name = userDTO.getName();
			agentClient.setName(name);
			agentClient.setLogin(userDTO.getLogin());
			LabelFormatter.formatLabel(agentClient);

			// Make sure the client to be added is not already an existing
			// client.
			boolean isAlreadyClient = agentClientService
					.checkExistsByMT4Login(mt4Login);
			boolean isClientAssignedToAgent = agentClientService
					.isClientLoginAssignedToAgent(agentId, mt4Login);
			// if mt4login is already in use as client, throw error.
			if (isAlreadyClient && !isClientAssignedToAgent) {
				errors.rejectValue("login",
						"mt4agents.exception.agentclient.logininuse",
						new Object[] { mt4Login }, "");
			}

			// Make sure the client to be added is not an agent in the system.
			boolean isAlreadyAgent = agentService
					.checkExistsByMT4Login(mt4Login);
			if (isAlreadyAgent) {
				errors.rejectValue("agentId",
						"mt4agents.exception.agentclient.alreadyagent",
						new Object[] { agentClient.getLabel() }, "");
			}

			// make sure the client has an agent.
			boolean agentValid = agentService.checkExistsById(agentId);
			if (!agentValid) {
				errors.rejectValue("agentId",
						"mt4agents.exception.agentclient.invalidagent",
						new Object[] { agentClient.getLabel() }, "");
			}

			// make sure the registration date is not null for new clients.
			if (agentClientId == null && registrationDate == null) {
				errors.rejectValue(
						"agentId",
						"mt4agents.exception.agentclient.invalidregistrationdate",
						new Object[] { agentId }, "");
			}
		}
	}
}
