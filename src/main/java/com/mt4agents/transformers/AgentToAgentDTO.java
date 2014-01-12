package com.mt4agents.transformers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.dto.AgentDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentClient;
import com.mt4agents.entities.AgentRelationship;
import com.mt4agents.formatters.LabelFormatter;

public class AgentToAgentDTO extends AbstractTransformer<Agent, AgentDTO> {

	private enum ClientMapType {
		ID, LOGIN
	};

	public AgentDTO transform(Agent agent) {

		AgentRelationship parentRelationship = agent.getParentRelationship();

		AgentDTO dto = new AgentDTO();
		dto.setAgentId(agent.getId());
		dto.setName(agent.getName());

		LabelFormatter.formatLabel(agent, dto);

		if (parentRelationship != null) {
			Agent parentAgent = parentRelationship.getParentAgent();
			if (parentAgent != null) {
				dto.setParentAgentId(parentAgent.getId());
				dto.setParentAgentName(parentAgent.getName());
				LabelFormatter.formatParentAgentLabel(parentAgent, dto);
			}
		}
		dto.setLogin(agent.getMt4Login());
		dto.setCommission(agent.getCommission());

		Map<Integer, AgentClient> clients = agent.getClients();
		populateAgentClientDTOs(dto, clients, ClientMapType.ID);

		Map<Integer, AgentClient> clientsByLogin = agent.getClientsByLogin();
		populateAgentClientDTOs(dto, clientsByLogin, ClientMapType.LOGIN);

		return dto;
	}

	public Map<Integer, AgentDTO> transformManyOrganisedByLogin(
			List<Agent> agents) {
		Map<Integer, AgentDTO> agentDTOs = new LinkedHashMap<Integer, AgentDTO>();
		for (Agent agent : agents) {
			agentDTOs.put(agent.getMt4Login(), transform(agent));
		}
		return agentDTOs;
	}

	private void populateAgentClientDTOs(AgentDTO dto,
			Map<Integer, AgentClient> clients, ClientMapType clientMapType) {
		Set<Integer> keys = clients.keySet();
		for (Integer key : keys) {
			AgentClient client = clients.get(key);
			AgentClientDTO clientDTO = new AgentClientDTO();
			clientDTO.setAgentClientId(client.getId());
			clientDTO.setName(client.getName());
			clientDTO.setEmail(client.getEmail());
			Agent agent = client.getAgent();
			if (agent != null) {
				clientDTO.setAgentId(agent.getId());
				clientDTO.setAgentName(agent.getName());
				LabelFormatter.formatAgentLabel(agent, clientDTO);
				if (clientMapType == ClientMapType.ID) {
					dto.getClients().put(key, clientDTO);
				} else if (clientMapType == ClientMapType.LOGIN) {
					dto.getClientsByLogin().put(key, clientDTO);
				}
			}
		}
	}
}
