package com.mt4agents.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.mt4agents.dto.AgentDTO;
import com.mt4agents.services.AgentService;
import com.mt4agents.services.BaseTest;

@Transactional
public class DataGeneratorTest extends BaseTest {

	@Autowired
	private DataGenerator dataGenerator;

	@Autowired
	private AgentService agentService;

	@Test
	public void createRandomMT4Users_100() {
		dataGenerator.createRandomMT4Users(100);
	}

	@Test
	public void createRandomMT4Users_1000() {
		dataGenerator.createRandomMT4Users(1000);
	}

	@Test
	public void createRandomMT4Users_777() {
		dataGenerator.createRandomMT4Users(777);
	}

	@Test
	public void getLoginsNotAssignedAsAgents_TestOne() {
		Integer login1 = dataGenerator.createRandomMT4User();
		Integer login2 = dataGenerator.createRandomMT4User();

		dataGenerator.createRandomAgent(login1);

		List<Integer> unusedLogins = dataGenerator
				.getLoginsNotAssignedAsAgents(Arrays.asList(login1));

		Assert.assertEquals(1, unusedLogins.size());
		Assert.assertEquals(login2, unusedLogins.get(0));
	}

	@Test
	public void getLoginsNotAssignedAsAgents_TestTwo() {
		int noOfLogins = 10;
		int noOfAgents = 6;
		List<Integer> logins = new ArrayList<Integer>();
		List<Integer> agentLogins = new ArrayList<Integer>();
		for (int i = 0; i < noOfLogins; i++) {
			logins.add(dataGenerator.createRandomMT4User());
		}
		for (int j = 0; j < noOfAgents; j++) {
			Integer agentLogin = logins.get(j);
			dataGenerator.createRandomAgent(agentLogin);
			agentLogins.add(agentLogin);
		}

		List<Integer> unusedLogins = dataGenerator
				.getLoginsNotAssignedAsAgents(agentLogins);

		Assert.assertEquals(noOfLogins - noOfAgents, unusedLogins.size());
	}

	@Test
	public void constructAgentsTree_TestOne() throws Exception {
		int expectedNoOfAgents = 13;
		dataGenerator.constructAgentsTree(2, 3, 2, 2);
		List<Integer> agentLogins = agentService.getAllAgentsLogins();
		Assert.assertEquals(expectedNoOfAgents, agentLogins.size());
	}

	@Test
	public void constructAgentsTree_TestTwo() throws Exception {
		int expectedNoOfAgents = 40;
		dataGenerator.constructAgentsTree(3, 3, 2, 2);
		List<Integer> agentLogins = agentService.getAllAgentsLogins();
		Assert.assertEquals(expectedNoOfAgents, agentLogins.size());
	}

	@Test
	public void constructAgentsTree_TestThree() throws Exception {
		int expectedNoOfAgents = 121;
		dataGenerator.constructAgentsTree(4, 3, 2, 2);
		List<Integer> agentLogins = agentService.getAllAgentsLogins();
		Assert.assertEquals(expectedNoOfAgents, agentLogins.size());
	}

	@Test
	public void constructAgentsTree_findAgentDTOs() throws Exception {
		dataGenerator.constructAgentsTree(2, 2, 2, 2);
		Map<Integer, AgentDTO> agents = agentService.findAgentDTOs(null, 0, 10);
		for (AgentDTO agent : agents.values()) {
			// System.out.println("parentAgentId=" + agent.getParentAgentId());
			// System.out.println("parentAgentLabel=" +
			// agent.getParentAgentLabel());
			// Assert.assertTrue(StringUtils.hasLength(agent.getParentAgentLabel()));
		}
	}

	@Test
	public void test1() {
		int expectedNoOfAgents = 4;
		Assert.assertEquals(expectedNoOfAgents,
				dataGenerator.agentsCountInTree(1, 3));
	}

	@Test
	public void test2() {
		int expectedNoOfAgents = 13;
		Assert.assertEquals(expectedNoOfAgents,
				dataGenerator.agentsCountInTree(2, 3));
	}

	@Test
	public void test3() {
		int expectedNoOfAgents = 40;
		Assert.assertEquals(expectedNoOfAgents,
				dataGenerator.agentsCountInTree(3, 3));
	}

	@Test
	public void test4() {
		int expectedNoOfAgents = 3;
		Assert.assertEquals(expectedNoOfAgents,
				dataGenerator.agentsCountInTree(1, 2));
	}

	@Test
	public void test5() {
		int expectedNoOfAgents = 7;
		Assert.assertEquals(expectedNoOfAgents,
				dataGenerator.agentsCountInTree(2, 2));
	}
}
