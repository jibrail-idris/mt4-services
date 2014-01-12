package com.mt4agents.transformers;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.dto.MT4CommissionDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentClient;
import com.mt4agents.formatters.LabelFormatter;

public class AgentClientToAgentClientDTOTest {

	private AgentClientToAgentClientDTO transformer = new AgentClientToAgentClientDTO();

	@Test
	public void transformFiveClients() {

		Integer agentId = 99;
		Integer agentLogin = 1;
		String agentName = "Agent1";

		Agent agent = new Agent();
		agent.setId(agentId);
		agent.setMt4Login(agentLogin);
		agent.setName(agentName);

		List<AgentClient> clients = new ArrayList<AgentClient>();
		for (int i = 0; i < 5; i++) {
			AgentClient client = new AgentClient();
			client.setId(i);
			client.setMt4Login(i);
			client.setName("client" + i);
			client.setEmail("client" + i + "@mail.com");
			client.setAgent(agent);
			clients.add(client);
		}

		List<AgentClientDTO> dtos = transformer.transformMany(clients);

		Assert.assertEquals(5, dtos.size());
		for (int j = 0; j < 5; j++) {
			AgentClientDTO dto = dtos.get(j);
			Assert.assertEquals((Integer) j, dto.getAgentClientId());
			Assert.assertEquals((Integer) j, dto.getLogin());
			Assert.assertEquals("client" + j, dto.getName());
			Assert.assertEquals("client" + j + "@mail.com", dto.getEmail());
			Assert.assertEquals(LabelFormatter.formatLabelToString(agent),
					dto.getAgentLabel());
		}
	}

	@Test
	public void transformAgentClientsWithCommissions() {
		Integer agentId = 99;
		Integer agentLogin = 1;
		String agentName = "Agent1";

		Agent agent = new Agent();
		agent.setId(agentId);
		agent.setMt4Login(agentLogin);
		agent.setName(agentName);

		List<MT4CommissionDTO> commissions = new ArrayList<MT4CommissionDTO>();

		List<AgentClient> clients = new ArrayList<AgentClient>();
		for (int i = 0; i < 5; i++) {
			AgentClient client = new AgentClient();
			client.setId(i);
			client.setMt4Login(i);
			client.setName("client" + i);
			client.setEmail("client" + i + "@mail.com");
			client.setAgent(agent);
			clients.add(client);

			MT4CommissionDTO commission = new MT4CommissionDTO();
			commission.setLogin(client.getMt4Login());

			commissions.add(commission);
		}

		List<AgentClientDTO> dtos = transformer.transformMany(clients,
				commissions);

		Assert.assertEquals(5, dtos.size());
		for (int j = 0; j < 5; j++) {
			AgentClientDTO dto = dtos.get(j);
			MT4CommissionDTO commission = dto.getCommissionDTO();
			Assert.assertNotNull(commission);
		}
	}
}
