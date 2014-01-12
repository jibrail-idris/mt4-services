package com.mt4agents.transformers;

import java.util.ArrayList;
import java.util.List;

import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.dto.MT4CommissionDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentClient;
import com.mt4agents.formatters.LabelFormatter;

public class AgentClientToAgentClientDTO extends
		AbstractTransformer<AgentClient, AgentClientDTO> {

	public List<AgentClientDTO> transformMany(List<AgentClient> clients,
			List<MT4CommissionDTO> commissions) {
		List<AgentClientDTO> agentClientDTOs = new ArrayList<AgentClientDTO>();
		for (AgentClient client : clients) {
			Integer clientLogin = client.getMt4Login();
			AgentClientDTO agentClient = transform(client);
			for (MT4CommissionDTO comm : commissions) {
				if (comm.getLogin().intValue() == clientLogin.intValue()) {
					agentClient.setCommissionDTO(comm);
					break;
				}
			}
			agentClientDTOs.add(agentClient);
		}
		return agentClientDTOs;
	}

	public AgentClientDTO transform(AgentClient agentClient) {

		Agent agent = agentClient.getAgent();

		AgentClientDTO dto = new AgentClientDTO();
		dto.setAgentClientId(agentClient.getId());
		dto.setLogin(agentClient.getMt4Login());
		dto.setName(agentClient.getName());
		dto.setRegistrationDate(agentClient.getRegistrationDate());
		dto.setEmail(agentClient.getEmail());
		dto.setAgentId(agent.getId());
		dto.setAgentName(agent.getName());

		LabelFormatter.formatAgentLabel(agent, dto);

		return dto;
	}
}
