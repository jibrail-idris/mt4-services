package com.mt4agents.services;

import java.util.ArrayList;
import java.util.Date;
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

import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentClient;
import com.mt4agents.exceptions.AgentClientException;
import com.mt4agents.formatters.LabelFormatter;
import com.mt4agents.services.MT4RemoteService.TradeType;
import com.mt4agents.util.DataGenerator;

public class AgentClientServiceTest extends BaseTest {

	@Autowired
	private AgentService agentService;

	@Autowired
	private AgentClientService agentClientService;

	@Autowired
	private AgentRelationshipService agentRelationshipService;

	@Autowired
	private MessageSource messageSource;
	
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
	public void addOneClientToOneAgent() throws Exception {
		Agent agent = dataGenerator.createRandomAgent();
		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		AgentClient client = agentClientService.saveClient(clientDTO);
		Integer id = client.getId();
		Agent agent2 = client.getAgent();
		Assert.assertNotNull(id);
		Assert.assertTrue(id > 0);
		Assert.assertSame(agent, agent2);

		// Test the Map OneToMany relationship
		Agent agent3 = agentService.getAgentById(agent.getId());
		Map<Integer, AgentClient> clients = agent3.getClients();

		Assert.assertEquals(1, clients.size());
		Assert.assertEquals(clientDTO.getLogin(), clients.get(client.getId())
				.getMt4Login());
		Assert.assertSame(clients.get(client.getId()), client);

		Map<Integer, AgentClient> clientsByLogin = agent3.getClientsByLogin();

		Assert.assertEquals(1, clientsByLogin.size());
		AgentClient agentClient = clientsByLogin.get(clientDTO.getLogin());
		Assert.assertEquals(clientDTO.getLogin(), agentClient.getMt4Login());
		Assert.assertSame(agentClient, client);
		Assert.assertTrue(StringUtils.hasLength(agentClient.getName()));
	}

	@Test
	@Transactional
	public void addSameClientToAgentTwice() throws Exception {
		Agent agent = dataGenerator.createRandomAgent();
		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);
		agentClientService.saveClient(clientDTO);

		Agent agent1 = agentService.getAgentById(agent.getId());
		Assert.assertEquals(1, agent1.getClients().size());

	}

	@Test(expected = AgentClientException.class)
	@Transactional
	public void addSameClientToDifferentAgents() throws Exception {
		Integer clientMt4Login = dataGenerator.createRandomMT4User();
		try {
			Agent agent1 = dataGenerator.createRandomAgent();
			Agent agent2 = dataGenerator.createRandomAgent();

			AgentClientDTO clientDTO1 = new AgentClientDTO();
			clientDTO1.setLogin(clientMt4Login);
			clientDTO1.setAgentId(agent1.getId());
			clientDTO1.setRegistrationDate(new Date());
			agentClientService.saveClient(clientDTO1);

			AgentClientDTO clientDTO2 = new AgentClientDTO();
			clientDTO2.setLogin(clientMt4Login);
			clientDTO2.setAgentId(agent2.getId());
			clientDTO2.setRegistrationDate(new Date());
			agentClientService.saveClient(clientDTO2);
		} catch (AgentClientException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.agentclient.logininuse",
							new Object[] { clientMt4Login }, Locale.US))) {
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

	@Test(expected = AgentClientException.class)
	@Transactional
	public void addClientWithoutRegistrationDate() throws Exception {
		try {
			Agent agent1 = dataGenerator.createRandomAgent();

			Integer clientMt4Login = dataGenerator.createRandomMT4User();

			AgentClientDTO clientDTO1 = new AgentClientDTO();
			clientDTO1.setLogin(clientMt4Login);
			clientDTO1.setAgentId(agent1.getId());
			agentClientService.saveClient(clientDTO1);
		} catch (AgentClientException ex) {
			if (ex.getMessage()
					.equals(messageSource
							.getMessage(
									"mt4agents.exception.agentclient.invalidregistrationdate",
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

	/**
	 * Starts off with an agent with three clients. Use batch saveClients(). In
	 * the login list, include two existing clients and two new clients. I would
	 * expect four clients at the end of this test.
	 * 
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void removeClientFromAgent() throws Exception {
		Agent agent = dataGenerator.createRandomAgent();

		List<AgentClientDTO> clients = new ArrayList<AgentClientDTO>();

		for (int i = 0; i < 3; i++) {
			AgentClientDTO clientDTO = new AgentClientDTO();
			Integer clientLogin = dataGenerator.createRandomMT4User();
			clientDTO.setLogin(clientLogin);
			clientDTO.setAgentId(agent.getId());
			clientDTO.setRegistrationDate(new Date());
			clients.add(clientDTO);
		}

		// Start off with three clients for this single agent.
		agentClientService.saveClients(agent.getId(), clients);

		// Now remove the second client.
		clients.remove(1);

		// add two new clients.
		for (int k = 0; k < 2; k++) {
			AgentClientDTO clientDTO = new AgentClientDTO();
			Integer clientLogin = dataGenerator.createRandomMT4User();
			clientDTO.setLogin(clientLogin);
			clientDTO.setAgentId(agent.getId());
			clientDTO.setRegistrationDate(new Date());
			clients.add(clientDTO);
		}

		// Update list of clients.
		agentClientService.saveClients(agent.getId(), clients);

		// Assert that agent has now four clients.
		Agent agent1 = agentService.getAgentById(agent.getId());
		Assert.assertEquals(4, agent1.getClients().size());
		for (AgentClientDTO clientDTO : clients) {
			Assert.assertNotNull(agent1.getClientByLogin(clientDTO.getLogin()));
		}
	}

	@Test(expected = AgentClientException.class)
	@Transactional
	public void addAgentAsClient() throws Exception {
		Agent agent1 = dataGenerator.createRandomAgent();
		Agent agent2 = dataGenerator.createRandomAgent();

		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(agent2.getMt4Login());
		// setting agent2 up as a client with agent1 as it's managing agent.
		clientDTO.setAgentId(agent1.getId());
		LabelFormatter.formatLabel(clientDTO);

		try {
			agentClientService.saveClient(clientDTO);
		} catch (AgentClientException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.agentclient.alreadyagent",
							new Object[] { clientDTO.getLabel() }, Locale.US))) {
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

	@Test(expected = AgentClientException.class)
	@Transactional
	public void addClientWithoutAgent() throws Exception {
		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		agentClientService.saveClient(clientDTO);
	}

	@Test
	@Transactional
	public void getAgentClients() throws Exception {

		Integer expectedNoOfClients = 5;

		Agent agent = dataGenerator.createRandomAgent();
		for (int i = 0; i < expectedNoOfClients; i++) {
			AgentClientDTO clientDTO = new AgentClientDTO();
			clientDTO.setLogin(dataGenerator.createRandomMT4User());
			clientDTO.setAgentId(agent.getId());
			clientDTO.setRegistrationDate(new Date());
			agentClientService.saveClient(clientDTO);
		}

		List<AgentClient> clients = agentClientService
				.getClients(agent.getId());

		Assert.assertEquals(expectedNoOfClients, (Integer) clients.size());
	}

	@Test
	@Transactional
	public void getAgentClientsWithCommission() throws Exception {
		Integer expectedNoOfClients = 5;

		Agent agent = dataGenerator.createRandomAgent();
		for (int i = 0; i < expectedNoOfClients; i++) {
			AgentClientDTO clientDTO = new AgentClientDTO();
			clientDTO.setLogin(dataGenerator.createRandomMT4User());
			clientDTO.setAgentId(agent.getId());
			clientDTO.setRegistrationDate(new Date());
			agentClientService.saveClient(clientDTO);
			
			dataGenerator.createRandomMT4Trades(clientDTO.getLogin(), 0, TradeType.CLOSE, 1,
					"2013-02-01");
		}

		Date startDate = getDate("01-02-2013 00:00:00");
		Date endDate = getDate("31-02-2013 23:59:59");

		List<AgentClientDTO> clientsWithCommission = agentClientService
				.getClientsWithCommission(agent.getId(), null, null, startDate,
						endDate);

		Assert.assertEquals(expectedNoOfClients,
				(Integer) clientsWithCommission.size());
		for (AgentClientDTO client : clientsWithCommission) {
			Assert.assertNotNull(client.getCommissionDTO());
			Assert.assertEquals(agent.getId(), client.getAgentId());
		}
	}

	@Test
	@Transactional
	public void getAgentClientsWithCommission1() throws Exception {
		Agent agent = dataGenerator.createRandomAgent();
		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);
		dataGenerator.createRandomMT4Trades(clientDTO.getLogin(), 0, TradeType.CLOSE, 1,
				"2013-02-01");

		Date startDate = getDate("01-02-2013 00:00:00");
		Date endDate = getDate("31-02-2013 23:59:59");

		List<AgentClientDTO> clientsWithCommission = agentClientService
				.getClientsWithCommission(agent.getId(), null, null, startDate,
						endDate);

		Assert.assertEquals(1, clientsWithCommission.size());
		Assert.assertNotNull(clientsWithCommission.get(0).getCommissionDTO());
	}

	/**
	 * <p>
	 * This premise of this test is that the agent has his own clients and he
	 * has two downlines under him, with each having his own set of clients. The
	 * test calculates the commission based on the agent's own clients and his
	 * downlines's based on the commission assigned to him.
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void getAgentAndDownlineClientsWithCommission() throws Exception {

		Integer agentMt4Login = 1;
		Double agentCommission = 11.0;

		Agent agent = dataGenerator.createRandomAgent(agentMt4Login, agentCommission);

		// Assign agent 2 clients.
		AgentClientDTO clientDTO = null;

		Integer expectedNoOfClients = 0;

		for (int i = 0; i < 2; i++) {
			clientDTO = new AgentClientDTO();
			clientDTO.setLogin(dataGenerator.createRandomMT4User());
			clientDTO.setAgentId(agent.getId());
			clientDTO.setRegistrationDate(new Date());
			agentClientService.saveClient(clientDTO);
			expectedNoOfClients++;
		}

		// Create two downlines
		Integer downline1Mt4Login = 2;
		Integer downline2Mt4Login = 3;

		Double downline1Commission = 10.0;
		Double downline2Commission = 13.0;

		Agent downline1 = dataGenerator.createRandomAgent(downline1Mt4Login,
				downline1Commission);
		Agent downline2 = dataGenerator.createRandomAgent(downline2Mt4Login,
				downline2Commission);
		// Assign downlines some clients
		// Assign downline1 three clients
		for (int i = 0; i < 3; i++) {
			clientDTO = new AgentClientDTO();
			clientDTO.setLogin(dataGenerator.createRandomMT4User());
			clientDTO.setAgentId(downline1.getId());
			clientDTO.setRegistrationDate(new Date());
			agentClientService.saveClient(clientDTO);
			expectedNoOfClients++;
		}
		// Assign downline2 two clients
		for (int i = 0; i < 2; i++) {
			clientDTO = new AgentClientDTO();
			clientDTO.setLogin(dataGenerator.createRandomMT4User());
			clientDTO.setAgentId(downline2.getId());
			clientDTO.setRegistrationDate(new Date());
			agentClientService.saveClient(clientDTO);
			expectedNoOfClients++;
		}

		// establish the relationship between agent and downlines.
		dataGenerator.setAgentAsDownline(agent, downline1);
		dataGenerator.setAgentAsDownline(agent, downline2);

		Date startCloseDate = getMySqlDate("2012-10-01 00:00:00");
		Date endCloseDate = getMySqlDate("2012-10-02 23:59:59");

		List<AgentClientDTO> clients = agentClientService
				.getClientsWithCommission(agent.getId(), null, null,
						startCloseDate, endCloseDate);

		// I am expecting seven clients to be returned, two from agent, three
		// from downline1 and two from downline2
		Assert.assertEquals(expectedNoOfClients, (Integer) clients.size());
	}

	@Test
	@Transactional
	public void bug_AgentReturningThriceAfterAddingThreeClients()
			throws Exception {

		Integer beforeSize = agentService.getAgents().size();

		Agent agent = dataGenerator.createRandomAgent();

		AgentClientDTO clientDTO1 = new AgentClientDTO();
		clientDTO1.setLogin(dataGenerator.createRandomMT4User());
		clientDTO1.setAgentId(agent.getId());
		clientDTO1.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO1);

		AgentClientDTO clientDTO2 = new AgentClientDTO();
		clientDTO2.setLogin(dataGenerator.createRandomMT4User());
		clientDTO2.setAgentId(agent.getId());
		clientDTO2.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO2);

		AgentClientDTO clientDTO3 = new AgentClientDTO();
		clientDTO3.setLogin(dataGenerator.createRandomMT4User());
		clientDTO3.setAgentId(agent.getId());
		clientDTO3.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO3);

		List<Agent> agents = agentService.getAgents();

		Assert.assertEquals(1, agents.size() - beforeSize);
	}

	@Test
	@Transactional
	public void isClientAlreadyAssignedToAgent_IsAssigned() throws Exception {
		Agent agent = dataGenerator.createRandomAgent();

		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		AgentClient client = agentClientService.saveClient(clientDTO);

		Assert.assertTrue(agentClientService.isClientAssignedToAgent(
				agent.getId(), client.getId()));
	}

	@Test
	@Transactional
	public void isClientAlreadyAssignedToAgent_IsNotAssigned() throws Exception {
		Agent agent = dataGenerator.createRandomAgent();
		Agent agent2 = dataGenerator.createRandomAgent();

		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		AgentClientDTO clientDTO2 = new AgentClientDTO();
		clientDTO2.setLogin(dataGenerator.createRandomMT4User());
		clientDTO2.setAgentId(agent2.getId());
		clientDTO2.setRegistrationDate(new Date());
		AgentClient client2 = agentClientService.saveClient(clientDTO2);

		Assert.assertFalse(agentClientService.isClientAssignedToAgent(
				agent.getId(), client2.getId()));
	}

	@Test
	@Transactional
	public void isClientLoginAlreadyAssignedToAgent_IsAssigned()
			throws Exception {
		Agent agent = dataGenerator.createRandomAgent();

		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		AgentClient client = agentClientService.saveClient(clientDTO);

		Assert.assertTrue(agentClientService.isClientLoginAssignedToAgent(
				agent.getId(), client.getMt4Login()));
	}

	@Test
	@Transactional
	public void isClientLoginAlreadyAssignedToAgent_IsNotAssigned()
			throws Exception {
		Agent agent = dataGenerator.createRandomAgent();
		Agent agent2 = dataGenerator.createRandomAgent();

		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		AgentClientDTO clientDTO2 = new AgentClientDTO();
		clientDTO2.setLogin(dataGenerator.createRandomMT4User());
		clientDTO2.setAgentId(agent2.getId());
		clientDTO2.setRegistrationDate(new Date());
		AgentClient client2 = agentClientService.saveClient(clientDTO2);

		Assert.assertFalse(agentClientService.isClientLoginAssignedToAgent(
				agent.getId(), client2.getMt4Login()));
	}
}
