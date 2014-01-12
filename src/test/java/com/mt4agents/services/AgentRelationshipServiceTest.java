package com.mt4agents.services;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.mt4agents.dto.AgentDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentRelationship;
import com.mt4agents.exceptions.AgentRelationshipException;
import com.mt4agents.util.DataGenerator;

public class AgentRelationshipServiceTest extends BaseTest {

	@Autowired
	private AgentRelationshipService agentRelationshipService;

	@Autowired
	private AgentService agentService;
	
	@Autowired
	private DataGenerator dataGenerator;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Transactional
	public void createRelationship_New() throws AgentRelationshipException {
		Agent parentAgent = dataGenerator.createRandomAgent();
		Agent childAgent = dataGenerator.createRandomAgent();

		Integer parentAgentId = parentAgent.getId();
		Integer childAgentId = childAgent.getId();
		agentRelationshipService.saveRelationship(parentAgentId, childAgentId);

		Assert.assertTrue(agentRelationshipService.hasRelationship(
				parentAgentId, childAgentId));

		AgentRelationship parentRelationship = agentRelationshipService
				.getParentRelationship(childAgentId);
		List<AgentRelationship> childRelationship = agentRelationshipService
				.getChildrenRelationships(parentAgentId);

		// noParentRelationship should return null because parentAgentId has no
		// parent.
		AgentRelationship noParentRelationship = agentRelationshipService
				.getParentRelationship(parentAgentId);

		Assert.assertEquals(1, childRelationship.size());
		Assert.assertSame(parentRelationship, childRelationship.get(0));
		Assert.assertSame(parentRelationship,
				childAgent.getParentRelationship());
		Assert.assertNull(noParentRelationship);
	}

	@Test
	@Transactional
	public void createRelationship_Three_Levels_All_New()
			throws AgentRelationshipException {
		Agent rootAgent = dataGenerator.createRandomAgent();

		Agent level2Agent1 = dataGenerator.createRandomAgent();
		Agent level2Agent2 = dataGenerator.createRandomAgent();

		Agent level3Agent_1_1 = dataGenerator.createRandomAgent();
		Agent level3Agent_1_2 = dataGenerator.createRandomAgent();
		Agent level3Agent_2_1 = dataGenerator.createRandomAgent();
		Agent level3Agent_2_2 = dataGenerator.createRandomAgent();
		Agent level3Agent_2_3 = dataGenerator.createRandomAgent();

		agentRelationshipService.saveRelationship(rootAgent.getId(),
				level2Agent1.getId());
		agentRelationshipService.saveRelationship(rootAgent.getId(),
				level2Agent2.getId());

		agentRelationshipService.saveRelationship(level2Agent1.getId(),
				level3Agent_1_1.getId());
		agentRelationshipService.saveRelationship(level2Agent1.getId(),
				level3Agent_1_2.getId());

		agentRelationshipService.saveRelationship(level2Agent2.getId(),
				level3Agent_2_1.getId());
		agentRelationshipService.saveRelationship(level2Agent2.getId(),
				level3Agent_2_2.getId());
		agentRelationshipService.saveRelationship(level2Agent2.getId(),
				level3Agent_2_3.getId());

		// rootAgent has no parent and should return null for parent
		// relationship.
		AgentRelationship rootNoParentRelationship = agentRelationshipService
				.getParentRelationship(rootAgent.getId());
		// rootAgent has two children and should return two AgentRelationships.
		List<AgentRelationship> rootChildrenRelationships = agentRelationshipService
				.getChildrenRelationships(rootAgent.getId());

		AgentRelationship level2Agent1_parentRelationship = agentRelationshipService
				.getParentRelationship(level2Agent1.getId());
		AgentRelationship level2Agent2_parentRelationship = agentRelationshipService
				.getParentRelationship(level2Agent2.getId());

		List<AgentRelationship> level2Agent1_childrenRelationships = agentRelationshipService
				.getChildrenRelationships(level2Agent1.getId());

		List<AgentRelationship> level2Agent2_childrenRelationships = agentRelationshipService
				.getChildrenRelationships(level2Agent2.getId());

		AgentRelationship level3_Agent_1_1_parentRelationship = agentRelationshipService
				.getParentRelationship(level3Agent_1_1.getId());
		AgentRelationship level3_Agent_1_2_parentRelationship = agentRelationshipService
				.getParentRelationship(level3Agent_1_2.getId());

		AgentRelationship level3_Agent_2_1_parentRelationship = agentRelationshipService
				.getParentRelationship(level3Agent_2_1.getId());
		AgentRelationship level3_Agent_2_2_parentRelationship = agentRelationshipService
				.getParentRelationship(level3Agent_2_2.getId());
		AgentRelationship level3_Agent_2_3_parentRelationship = agentRelationshipService
				.getParentRelationship(level3Agent_2_3.getId());

		Assert.assertNull(rootNoParentRelationship);
		Assert.assertEquals(2, rootChildrenRelationships.size());
		// Test to make sure the two children in the relationship between root
		// and level2Agent1 and level2Agent2 is root being the parent and
		// level2Agent1 and level2Agent2 are children.
		Assert.assertEquals(level2Agent1.getId(), rootChildrenRelationships
				.get(0).getChildAgent().getId());
		Assert.assertEquals(level2Agent2.getId(), rootChildrenRelationships
				.get(1).getChildAgent().getId());

		Assert.assertSame(level2Agent1_parentRelationship,
				rootChildrenRelationships.get(0));
		Assert.assertSame(level2Agent2_parentRelationship,
				rootChildrenRelationships.get(1));
		Assert.assertSame(level2Agent1_parentRelationship,
				level2Agent1.getParentRelationship());
		Assert.assertSame(level2Agent2_parentRelationship,
				level2Agent2.getParentRelationship());
		// Assert that both belong to the same root parent.
		Assert.assertEquals(level2Agent1.getParentRelationship()
				.getParentAgent().getId(), level2Agent2.getParentRelationship()
				.getParentAgent().getId());

		Assert.assertEquals(2, level2Agent1_childrenRelationships.size());
		Assert.assertEquals(3, level2Agent2_childrenRelationships.size());

		Assert.assertSame(level3_Agent_1_1_parentRelationship,
				level2Agent1_childrenRelationships.get(0));
		Assert.assertSame(level3_Agent_1_2_parentRelationship,
				level2Agent1_childrenRelationships.get(1));
		Assert.assertSame(level3_Agent_1_1_parentRelationship,
				level3Agent_1_1.getParentRelationship());
		Assert.assertSame(level3_Agent_1_2_parentRelationship,
				level3Agent_1_2.getParentRelationship());
		Assert.assertEquals(level3Agent_1_1.getParentRelationship()
				.getParentAgent().getId(), level3Agent_1_2
				.getParentRelationship().getParentAgent().getId());

		Assert.assertSame(level3_Agent_2_1_parentRelationship,
				level2Agent2_childrenRelationships.get(0));
		Assert.assertSame(level3_Agent_2_2_parentRelationship,
				level2Agent2_childrenRelationships.get(1));
		Assert.assertSame(level3_Agent_2_3_parentRelationship,
				level2Agent2_childrenRelationships.get(2));
		Assert.assertSame(level3_Agent_2_1_parentRelationship,
				level3Agent_2_1.getParentRelationship());
		Assert.assertSame(level3_Agent_2_2_parentRelationship,
				level3Agent_2_2.getParentRelationship());
		Assert.assertSame(level3_Agent_2_3_parentRelationship,
				level3Agent_2_3.getParentRelationship());
		Assert.assertEquals(level3Agent_2_1.getParentRelationship()
				.getParentAgent().getId(), level3Agent_2_2
				.getParentRelationship().getParentAgent().getId());
		Assert.assertEquals(level3Agent_2_2.getParentRelationship()
				.getParentAgent().getId(), level3Agent_2_3
				.getParentRelationship().getParentAgent().getId());
		Assert.assertEquals(level3Agent_2_1.getParentRelationship()
				.getParentAgent().getId(), level3Agent_2_3
				.getParentRelationship().getParentAgent().getId());

	}

	// @Test(expected = AgentRelationshipException.class)
	// @Transactional
	public void createRelationship_Already_Exist_Error()
			throws AgentRelationshipException {
		Agent parentAgent = dataGenerator.createRandomAgent();
		Agent childAgent = dataGenerator.createRandomAgent();

		Integer parentAgentId = parentAgent.getId();
		Integer childAgentId = childAgent.getId();
		agentRelationshipService.saveRelationship(parentAgentId, childAgentId);
		agentRelationshipService.saveRelationship(parentAgentId, childAgentId);
	}

	// @Test(expected = AgentRelationshipException.class)
	// @Transactional
	public void createRelationship_Reverse() throws AgentRelationshipException {
		Agent parentAgent = dataGenerator.createRandomAgent();
		Agent childAgent = dataGenerator.createRandomAgent();

		Integer parentAgentId = parentAgent.getId();
		Integer childAgentId = childAgent.getId();

		agentRelationshipService.saveRelationship(parentAgentId, childAgentId);
		agentRelationshipService.saveRelationship(childAgentId, parentAgentId);
	}

	@Test
	@Transactional
	public void updateAgent_With_Relationship() throws Exception {
		Agent parentAgent = dataGenerator.createRandomAgent(dataGenerator.createRandomMT4User(), 100.00);
		Agent childAgent = dataGenerator.createRandomAgent(dataGenerator.createRandomMT4User(), 10.00);

		Integer parentAgentId = parentAgent.getId();
		Integer childAgentId = childAgent.getId();

		agentRelationshipService.saveRelationship(parentAgentId, childAgentId);

		Double newCommission = 11.00;
		// Update comission only. The update should be successful.
		AgentDTO agentDTO = new AgentDTO();
		agentDTO.setAgentId(childAgentId);
		agentDTO.setCommission(newCommission);
		agentDTO.setParentAgentId(parentAgentId);

		agentService.saveAgent(agentDTO);
	}

	@Test(expected = AgentRelationshipException.class)
	@Transactional
	public void addSelfAsParent() throws AgentRelationshipException {
		Agent agent = dataGenerator.createRandomAgent();
		agentRelationshipService.saveRelationship(agent.getId(), agent.getId());
	}

}
