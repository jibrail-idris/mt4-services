package com.mt4agents.transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.dto.AgentDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentClient;
import com.mt4agents.entities.AgentRelationship;

public class AgentToAgentDTOTest {

	private AgentToAgentDTO transformer = new AgentToAgentDTO();

	@Test
	public void transform_FiveAgents() {
		Integer parentId = 999;
		String parentAgentName = "ParentAgent1";
		Agent parentAgent = new Agent();
		parentAgent.setId(parentId);
		parentAgent.setName(parentAgentName);
		List<Agent> agents = new ArrayList<Agent>();
		for (int i = 0; i < 5; i++) {

			Agent agent = new Agent();
			agent.setId(i);
			agent.setMt4Login(i);
			agent.setName(new StringBuilder("name").append(i).toString());
			agent.setCommission(i * 1.0);

			for (int j = 0; j < 10; j++) {
				AgentClient client = new AgentClient();
				client.setId(j);
				client.setMt4Login(i * 100 + j);
				client.setName(new StringBuilder("Name").append(j).toString());
				client.setEmail(new StringBuilder("Email").append(j).toString());
				client.setAgent(agent);
				agent.getClients().put(j, client);
				agent.getClientsByLogin().put(client.getMt4Login(), client);
			}

			AgentRelationship agentRelationship = new AgentRelationship();
			agentRelationship.setParentAgent(parentAgent);
			agentRelationship.setChildAgent(agent);
			agent.setParentRelationship(agentRelationship);

			agents.add(agent);
		}

		List<AgentDTO> dtos = transformer.transformMany(agents);

		Assert.assertNotNull(dtos);
		Assert.assertEquals(5, dtos.size());

		for (int j = 0; j < 5; j++) {
			AgentDTO dto = dtos.get(j);
			Map<Integer, AgentClientDTO> clientDTOs = dto.getClients();
			Assert.assertEquals(j, dto.getLogin().intValue());
			Assert.assertEquals(new StringBuilder("name").append(j).toString(),
					dto.getName());
			Assert.assertEquals(new Double(j * 1.0), dto.getCommission());
			Assert.assertEquals(parentId, dto.getParentAgentId());
			Assert.assertEquals(parentAgentName, dto.getParentAgentName());

			Assert.assertEquals(10, clientDTOs.size());

			for (int k = 0; k < 10; k++) {
				AgentClientDTO clientDTO = clientDTOs.get(k);
				Assert.assertEquals((Integer) k, clientDTO.getAgentClientId());
				Assert.assertEquals("Name" + k, clientDTO.getName());
				Assert.assertEquals("Email" + k, clientDTO.getEmail());
				Assert.assertEquals((Integer) j, dto.getAgentId());
				Assert.assertEquals("name" + j, clientDTO.getAgentName());
			}

			Map<Integer, AgentClientDTO> clientsByLoginDTOs = dto
					.getClientsByLogin();
			Assert.assertEquals(10, clientsByLoginDTOs.size());
		}
	}

	/**
	 * This scenario is not supposed to happen. If it does, let's test it out.
	 */
	@Test
	public void transformAgentWithBlankRelationship() {
		Agent childAgent = new Agent();
		childAgent.setCommission(1.0);
		childAgent.setMt4Login(1);

		AgentRelationship agentRelationship = new AgentRelationship();

		childAgent.setParentRelationship(agentRelationship);

		AgentDTO agentDTO = transformer.transform(childAgent);

		Assert.assertNotNull(agentDTO);
		Assert.assertNull(agentDTO.getParentAgentId());
	}

	@Test
	public void transformManyOrganisedByLogin_FiveAgents() {
		Integer parentId = 999;
		String parentAgentName = "ParentAgent1";
		Agent parentAgent = new Agent();
		parentAgent.setId(parentId);
		parentAgent.setName(parentAgentName);
		List<Agent> agents = new ArrayList<Agent>();
		for (int i = 0; i < 5; i++) {

			Agent agent = new Agent();
			agent.setId(i);
			agent.setMt4Login(i);
			agent.setName(new StringBuilder("name").append(i).toString());
			agent.setCommission(i * 1.0);

			for (int j = 0; j < 10; j++) {
				AgentClient client = new AgentClient();
				client.setId(j);
				client.setMt4Login(i * 100 + j);
				client.setName(new StringBuilder("Name").append(j).toString());
				client.setEmail(new StringBuilder("Email").append(j).toString());
				client.setAgent(agent);
				agent.getClients().put(j, client);
				agent.getClientsByLogin().put(client.getMt4Login(), client);
			}

			AgentRelationship agentRelationship = new AgentRelationship();
			agentRelationship.setParentAgent(parentAgent);
			agentRelationship.setChildAgent(agent);
			agent.setParentRelationship(agentRelationship);

			agents.add(agent);
		}

		Map<Integer, AgentDTO> dtos = transformer
				.transformManyOrganisedByLogin(agents);

		Assert.assertNotNull(dtos);
		Assert.assertEquals(5, dtos.size());

		for (int j = 0; j < 5; j++) {
			AgentDTO dto = dtos.get(j);
			Assert.assertEquals(j, dto.getLogin().intValue());
		}
	}
}