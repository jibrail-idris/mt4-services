package com.mt4agents.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mt4agents.dao.AgentDAO;
import com.mt4agents.dto.AgentDTO;
import com.mt4agents.dto.MT4UserDTO;
import com.mt4agents.dto.UserDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentRelationship;
import com.mt4agents.entities.users.AgentUser;
import com.mt4agents.exceptions.AgentException;
import com.mt4agents.exceptions.AgentRelationshipException;
import com.mt4agents.exceptions.UserServiceException;
import com.mt4agents.transformers.AgentToAgentDTO;
import com.mt4agents.util.DataGenerator;

public class AgentServiceTest extends BaseTest {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AgentService agentService;

	@Autowired
	private AgentRelationshipService agentRelationshipService;

	@Autowired
	private UserService userService;

	@Autowired
	private AgentDAO agentDAO;

	@Autowired
	private AgentToAgentDTO agentToAgentDTO;
	
	@Autowired
	private DataGenerator dataGenerator;

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Transactional
	public void newAgent() throws Exception {
		Double commission = 0.9;
		Integer mt4Login = dataGenerator.createRandomMT4User();
		AgentDTO agentDTO = new AgentDTO();
		agentDTO.setCommission(commission);
		agentDTO.setLogin(mt4Login);
		Agent agent = agentService.saveAgent(agentDTO);
		Integer id = agent.getId();
		String name = agent.getName();
		Assert.assertNotNull(id);
		Assert.assertTrue(id > 0);
		Assert.assertTrue(StringUtils.hasLength(name));
	}

	@Test
	@Transactional
	public void updateAgent() throws Exception {
		Agent agent = new Agent();
		agent.setCommission(0.4);
		agent.setMt4Login(dataGenerator.createRandomMT4User());
		agentDAO.save(agent);

		Integer id = agent.getId();
		String name = agent.getName();
		Double newCommission = 0.6;

		Agent agent1 = agentService.getAgentById(id);
		agent1.setCommission(newCommission);

		AgentDTO agentDTO2 = new AgentDTO();
		agentDTO2.setAgentId(id);
		agentDTO2.setName("adasd");
		agentDTO2.setCommission(newCommission);

		Agent agent2 = agentService.saveAgent(agentDTO2);

		Assert.assertEquals(id, agent2.getId());
		Assert.assertEquals(newCommission, agent2.getCommission());
		Assert.assertEquals(agent.getMt4Login(), agent2.getMt4Login());
		// assert that name does not change on update.
		Assert.assertEquals(name, agent2.getName());
	}

	@Test
	@Transactional
	public void deleteAgent() throws Exception {

		String username = "user1";
		String password = "password";

		Agent agent = dataGenerator.createRandomAgent();
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(password);
		userDTO.setNewPassword1(password);
		userDTO.setNewPassword2(password);
		userDTO.setAgentDTO(agentToAgentDTO.transform(agent));
		userDTO.assignAgentRole();
		userService.saveUser(userDTO);

		agentService.deleteAgent(agent.getId());

	}

	@Test(expected = AgentException.class)
	@Transactional
	public void deleteAgent_WithDownlines() throws Exception {
		try {
			Agent parentAgent = dataGenerator.createRandomAgent();
			AgentDTO agentDTO1 = new AgentDTO();
			agentDTO1.setLogin(dataGenerator.createRandomMT4User());
			agentDTO1.setCommission(1.0);
			agentDTO1.setParentAgentId(parentAgent.getId());
			agentService.saveAgent(agentDTO1);

			agentService.deleteAgent(parentAgent.getId());
		} catch (AgentException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.agent.delete.hasdownlines",
							null, Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	@Test
	@Transactional
	public void deleteAgent_deleteDownline() throws Exception {
		String username = "user1";
		String password = "password";

		Agent parentAgent = dataGenerator.createRandomAgent();
		AgentDTO agentDTO1 = new AgentDTO();
		agentDTO1.setLogin(dataGenerator.createRandomMT4User());
		agentDTO1.setCommission(1.0);
		agentDTO1.setParentAgentId(parentAgent.getId());
		Agent downline = agentService.saveAgent(agentDTO1);
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(password);
		userDTO.setNewPassword1(password);
		userDTO.setNewPassword2(password);
		userDTO.setAgentDTO(agentToAgentDTO.transform(downline));
		userDTO.assignAgentRole();
		userService.saveUser(userDTO);
		agentService.deleteAgent(downline.getId());
	}

	@Test
	@Transactional
	public void deleteAgents_WithClients() {

	}

	@Test(expected = AgentException.class)
	@Transactional
	public void saveAgent_InvalidParent() throws AgentException,
			AgentRelationshipException {
		Integer invalidParentId = -1;
		Double commission = 2.0;

		try {
			AgentDTO agentDTO = new AgentDTO();
			agentDTO.setCommission(commission);
			agentDTO.setParentAgentId(invalidParentId);

			agentService.saveAgent(agentDTO);
		} catch (AgentException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.agent.invalidparent",
							new Object[] { invalidParentId }, Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	@Test(expected = AgentException.class)
	@Transactional
	public void newAgent_MT4Login_Absent() throws AgentException,
			AgentRelationshipException {
		AgentDTO agentDTO = new AgentDTO();
		agentDTO.setCommission(1.0);
		try {
			agentService.saveAgent(agentDTO);
		} catch (AgentException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.agent.mt4login.notassigned",
							null, Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	@Test(expected = AgentException.class)
	@Transactional
	public void newAgent_Two_Same_MT4Login() throws AgentException,
			AgentRelationshipException {

		Integer mt4Login = dataGenerator.createRandomMT4User();

		try {

			AgentDTO agentDTO1 = new AgentDTO();
			AgentDTO agentDTO2 = new AgentDTO();

			agentDTO1.setCommission(1.0);
			agentDTO1.setLogin(mt4Login);

			agentDTO2.setCommission(0.5);
			agentDTO2.setLogin(mt4Login);

			agentService.saveAgent(agentDTO1);
			agentService.saveAgent(agentDTO2);
		} catch (AgentException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.agent.mt4login.inuse",
							new Object[] { mt4Login }, Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	@Test(expected = AgentException.class)
	@Transactional
	public void newAgent_Invalid_MT4Login() throws Exception {

		Integer invalidMT4Login = 100;

		Double commission = 0.9;
		AgentDTO agentDTO = new AgentDTO();
		agentDTO.setCommission(commission);
		agentDTO.setLogin(invalidMT4Login);
		try {
			agentService.saveAgent(agentDTO);
		} catch (AgentException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.agent.mt4login.invalidlogin",
							new Object[] { invalidMT4Login }, Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	// @Test(expected = AgentException.class)
	// @Transactional
	public void newAgent_Commission_Set_Exceed_Parent_Commission_Balance()
			throws Exception {
		/*
		 * One parent agent with 4 downline agents. Parent agent's commission is
		 * set to 100. Downline Agent 1 is set to 25. Downline Agent 2 is set to
		 * 25. Downline Agent 3 is set to 40 Downline Agent 4 is set to 10.
		 * 
		 * If a new agent is to be added under the parent agent, the system will
		 * disallow because the parent agent has exceeded the commission limit.
		 */

		Integer parentAgentLogin = 1;
		Double parentAgentCommission = 100.0;

		Double agent1Commission = 25.0;
		Double agent2Commission = 25.0;
		Double agent3Commission = 40.0;
		Double agent4Commission = 10.0;

		Agent parentAgent = dataGenerator.createRandomAgent(parentAgentLogin,
				parentAgentCommission);

		try {
			AgentDTO agentDTO1 = new AgentDTO();
			agentDTO1.setLogin(2);
			agentDTO1.setCommission(agent1Commission);
			agentDTO1.setParentAgentId(parentAgent.getId());
			agentService.saveAgent(agentDTO1);

			AgentDTO agentDTO2 = new AgentDTO();
			agentDTO2.setLogin(3);
			agentDTO2.setCommission(agent2Commission);
			agentDTO2.setParentAgentId(parentAgent.getId());
			agentService.saveAgent(agentDTO2);

			AgentDTO agentDTO3 = new AgentDTO();
			agentDTO3.setLogin(4);
			agentDTO3.setCommission(agent3Commission);
			agentDTO3.setParentAgentId(parentAgent.getId());
			agentService.saveAgent(agentDTO3);

			AgentDTO agentDTO4 = new AgentDTO();
			agentDTO4.setLogin(5);
			agentDTO4.setCommission(agent4Commission);
			agentDTO4.setParentAgentId(parentAgent.getId());
			agentService.saveAgent(agentDTO4);

			// This agent cannot be added as a downline of parent agent.
			AgentDTO agentDTO5 = new AgentDTO();
			agentDTO5.setLogin(6);
			agentDTO5.setCommission(agent1Commission);
			agentDTO5.setParentAgentId(parentAgent.getId());
			agentService.saveAgent(agentDTO5);
		} catch (AgentException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.agent.commission.exceed",
							null, Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	@Test
	@Transactional
	public void newAgent_Commission_Set_Not_Exceed_Parent_Commission_Balance()
			throws Exception {
		Integer parentAgentLogin = 1;
		Double parentAgentCommission = 125.0;

		Double agent1Commission = 25.0;
		Double agent2Commission = 25.0;
		Double agent3Commission = 40.0;
		Double agent4Commission = 10.0;

		Agent parentAgent = dataGenerator.createRandomAgent(parentAgentLogin,
				parentAgentCommission);

		AgentDTO agentDTO1 = new AgentDTO();
		agentDTO1.setLogin(dataGenerator.createRandomMT4User());
		agentDTO1.setCommission(agent1Commission);
		agentDTO1.setParentAgentId(parentAgent.getId());
		agentService.saveAgent(agentDTO1);

		AgentDTO agentDTO2 = new AgentDTO();
		agentDTO2.setLogin(dataGenerator.createRandomMT4User());
		agentDTO2.setCommission(agent2Commission);
		agentDTO2.setParentAgentId(parentAgent.getId());
		agentService.saveAgent(agentDTO2);

		AgentDTO agentDTO3 = new AgentDTO();
		agentDTO3.setLogin(dataGenerator.createRandomMT4User());
		agentDTO3.setCommission(agent3Commission);
		agentDTO3.setParentAgentId(parentAgent.getId());
		agentService.saveAgent(agentDTO3);

		AgentDTO agentDTO4 = new AgentDTO();
		agentDTO4.setLogin(dataGenerator.createRandomMT4User());
		agentDTO4.setCommission(agent4Commission);
		agentDTO4.setParentAgentId(parentAgent.getId());
		agentService.saveAgent(agentDTO4);

		// This agent cannot be added as a downline of parent agent.
		AgentDTO agentDTO5 = new AgentDTO();
		agentDTO5.setLogin(dataGenerator.createRandomMT4User());
		agentDTO5.setCommission(agent1Commission);
		agentDTO5.setParentAgentId(parentAgent.getId());
		agentService.saveAgent(agentDTO5);
	}

	@Test
	@Transactional
	public void newAgent_WithNewAdminUserAccountCreated() throws Exception {
		Double commission = 0.9;
		Integer mt4Login = dataGenerator.createRandomMT4User();
		String username = "Agent" + mt4Login;
		String password = "!@#!@";
		AgentDTO agentDTO = new AgentDTO();
		agentDTO.setCommission(commission);
		agentDTO.setLogin(mt4Login);
		agentService.saveAgentWithNewAgentUser(agentDTO, username, password,
				password);
		AgentUser user = (AgentUser) userService.getUserByUsername(username);
		Assert.assertNotNull(user);
	}

	@Test(expected = UserServiceException.class)
	@Transactional
	public void newAgent_BlankPassword() throws Exception {
		try {
			Double commission = 0.9;
			Integer mt4Login = dataGenerator.createRandomMT4User();
			String username = "Agent" + mt4Login;
			AgentDTO agentDTO = new AgentDTO();
			agentDTO.setCommission(commission);
			agentDTO.setLogin(mt4Login);
			agentService.saveAgentWithNewAgentUser(agentDTO, username, "", "");
		} catch (UserServiceException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.user.newpasswordblank", null,
							Locale.US))) {
				throw ex;
			} else {
				ex.printStackTrace();
				Assert.fail("Expected exception message failure.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail("Incorrect exception thrown");
		}
	}

	@Test
	@Transactional
	public void newAgent_WithUpline() throws Exception {
		Integer parentAgentLogin = 1;
		Double parentAgentCommission = 125.0;

		Double agent1Commission = 25.0;

		Agent parentAgent = dataGenerator.createRandomAgent(parentAgentLogin,
				parentAgentCommission);

		AgentDTO agentDTO1 = new AgentDTO();
		agentDTO1.setLogin(dataGenerator.createRandomMT4User());
		agentDTO1.setCommission(agent1Commission);
		agentDTO1.setParentAgentId(parentAgent.getId());
		Agent childAgent = agentService.saveAgent(agentDTO1);

		AgentRelationship parentRelationship2 = agentRelationshipService
				.getParentRelationship(childAgent.getId());

		Agent agent = agentService.getAgentById(childAgent.getId());

		Assert.assertNotNull(parentRelationship2);
		Assert.assertNotNull(agent.getParentRelationship());
		Assert.assertSame(parentRelationship2, agent.getParentRelationship());
	}

	@Test
	@Transactional
	public void getAgents_Two() {

		Integer beforeSize = agentService.getAgents().size();

		Agent agent1 = new Agent();
		Agent agent2 = new Agent();

		agent1.setCommission(0.2);
		agent1.setMt4Login(dataGenerator.createRandomMT4User());
		agent2.setCommission(0.4);
		agent2.setMt4Login(dataGenerator.createRandomMT4User());

		Agent agents[] = new Agent[2];
		agents[0] = agent1;
		agents[1] = agent2;

		for (int i = 0; i < agents.length; i++) {
			agentDAO.save(agents[i]);
		}

		List<Agent> agentsList = agentService.getAgents();

		Assert.assertEquals(agents.length, agentsList.size() - beforeSize);
	}

	@Test
	@Transactional
	public void getAgentByLogin_One() {
		Integer mt4Login = 5;
		Agent agent = dataGenerator.createRandomAgent(mt4Login);
		Agent agent2 = agentService.getAgentByLogin(mt4Login);
		Assert.assertSame(agent, agent2);
	}

	@Test
	@Transactional
	public void getAgentByUsername_Valid() throws UserServiceException,
			AgentException {
		AgentUser agentUser = dataGenerator.createRandomAgentUser();
		Agent agent1 = agentUser.getAgent();
		Agent agent2 = agentService.getAgentByUsername(agentUser.getUsername());
		Assert.assertSame(agent1, agent2);
	}

	@Test
	@Transactional
	public void getAgentDTOs_BySearch() {

		Integer agent1Login = 987658765;
		Integer agent2Login = 987678981;

		dataGenerator.createRandomAgent(agent1Login);
		dataGenerator.createRandomAgent(agent2Login);

		List<AgentDTO> agentDTOs = agentService.getAgentDTOs(new Integer(
				agent1Login).toString());
		Assert.assertEquals(1, agentDTOs.size());
		Assert.assertEquals(1,
				agentService.getAgentDTOs(new Integer(agent2Login).toString())
						.size());

		Assert.assertNotNull(agentDTOs.get(0).getAgentId());
	}

	@Test
	@Transactional
	public void getAgentDTOs_ByWildcardSearch() {
		Integer agent1Login = 63056111;
		Integer agent2Login = 63056222;
		Integer agent3Login = 63056333;
		Integer agent4Login = 63056122;

		dataGenerator.createRandomAgent(agent1Login);
		dataGenerator.createRandomAgent(agent2Login);
		dataGenerator.createRandomAgent(agent3Login);
		dataGenerator.createRandomAgent(agent4Login);

		Assert.assertEquals(4, agentService.getAgentDTOs("6305").size());
		Assert.assertEquals(2, agentService.getAgentDTOs("630561").size());
		Assert.assertEquals(1, agentService.getAgentDTOs("6305611").size());
		Assert.assertEquals(1, agentService.getAgentDTOs("6305612").size());
		Assert.assertEquals(1, agentService.getAgentDTOs("6305633").size());
	}

	@Test
	@Transactional
	public void findAgents_Paginate20In2PagesOf10() {

		Integer agentCountBefore = agentService.getAgentsCount(null);

		Integer expectedNumberOfAgents = 20;
		for (int i = 0; i < expectedNumberOfAgents; i++) {
			dataGenerator.createRandomAgent(dataGenerator.createRandomMT4User());
		}

		Map<Integer, AgentDTO> dPage1 = agentService.findAgentDTOs(null, 0, 10);
		Map<Integer, AgentDTO> dPage2 = agentService
				.findAgentDTOs(null, 10, 10);

		Assert.assertEquals(10, dPage1.size());
		Assert.assertEquals(10, dPage2.size());

		Integer agentCountAfter = agentService.getAgentsCount(null);
		Assert.assertEquals(expectedNumberOfAgents.intValue(), agentCountAfter
				- agentCountBefore);

		for (AgentDTO agentDTO : dPage1.values()) {
			MT4UserDTO userDTO = agentDTO.getMt4User();
			Assert.assertNotNull(userDTO);
		}

		for (AgentDTO agentDTO : dPage2.values()) {
			MT4UserDTO userDTO = agentDTO.getMt4User();
			Assert.assertNotNull(userDTO);
		}
	}

	@Test
	@Transactional
	public void findAgents_findBySearch() {
		dataGenerator.createRandomAgent(9999);
		dataGenerator.createRandomAgent(8888);
		dataGenerator.createRandomAgent(9988);

		Map<Integer, AgentDTO> results = agentService.findAgentDTOs("9999", 0,
				10);

		Assert.assertEquals(1, results.size());

		Map<Integer, AgentDTO> results1 = agentService.findAgentDTOs("99", 0,
				10);

		Assert.assertEquals(2, results1.size());
	}
}
