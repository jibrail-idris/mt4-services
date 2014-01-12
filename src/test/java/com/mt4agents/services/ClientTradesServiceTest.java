package com.mt4agents.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.dto.MT4CommissionDTO;
import com.mt4agents.dto.MT4TradeDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentClient;
import com.mt4agents.exceptions.MT4RemoteServiceException;
import com.mt4agents.formatters.DateFormatter;
import com.mt4agents.formatters.DateFormatter.Type;
import com.mt4agents.services.MT4RemoteService.TradeType;
import com.mt4agents.util.DataGenerator;

public class ClientTradesServiceTest extends BaseTest {

	@Autowired
	private ClientTradesService agentTradesService;

	@Autowired
	private AgentClientService agentClientService;

	@Autowired
	private AgentRelationshipService agentRelationshipService;

	@Autowired
	private DataGenerator dataGenerator;

	@Test
	@Transactional
	public void getTrades_OneAgent_By_OpenTime() throws Exception {
		/*
		 * SELECT u.login, `t`.* from `mt4_trades` t inner join mt4_users u on
		 * t.LOGIN = u.LOGIN where u.LOGIN=6300083 and t.CLOSE_TIME >=
		 * '2012-10-01 00:00:00' and t.CLOSE_TIME < '2012-10-15 23:59:59';
		 */

		// Integer expectedNoOfTrades = 21;
		Integer expectedNoOfTrades = 30;

		Integer mt4Login = dataGenerator.createRandomMT4User();

		Double commission = 10.0;

		Agent agent = dataGenerator.createRandomAgent(1);

		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(mt4Login);
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		AgentClient client = agentClientService.saveClient(clientDTO);

		dataGenerator.createRandomMT4Trades(mt4Login, 0, TradeType.OPEN,
				expectedNoOfTrades, "2012-10-01");

		Date startOpenTime = getDate("01-10-2012 00:00:00");
		Date endOpenTime = getDate("15-10-2012 23:59:59");
		List<MT4TradeDTO> trades = agentTradesService.getClientTrades(
				client.getId(), commission, startOpenTime, endOpenTime, null,
				null);

		Assert.assertNotNull(trades);
		Assert.assertEquals(expectedNoOfTrades.intValue(), trades.size());
	}

	@Test
	@Transactional
	public void getTrades_OneAgent_By_CloseTime() throws Exception {

		/*
		 * SELECT u.login, `t`.* from `mt4_trades` t inner join mt4_users u on
		 * t.LOGIN = u.LOGIN where u.LOGIN=6300083 and t.CLOSE_TIME >=
		 * '2012-11-01 00:00:00' and t.CLOSE_TIME < '2012-11-15 23:59:59';
		 */
		// Integer expectedNoOfTrades = 21;
		Integer expectedNoOfTrades = 30;

		Integer mt4Login = dataGenerator.createRandomMT4User();

		Double commission = 10.0;

		Agent agent = dataGenerator.createRandomAgent(dataGenerator
				.createRandomMT4User());

		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(mt4Login);
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		AgentClient client = agentClientService.saveClient(clientDTO);

		dataGenerator.createRandomMT4Trades(clientDTO.getLogin(), 0,
				TradeType.CLOSE, expectedNoOfTrades, "2012-10-01");

		Date startCloseTime = getDate("01-10-2012 00:00:00");
		Date endCloseTime = getDate("15-10-2012 23:59:59");
		List<MT4TradeDTO> trades = agentTradesService.getClientTrades(
				client.getId(), commission, null, null, startCloseTime,
				endCloseTime);

		Assert.assertNotNull(trades);
		Assert.assertEquals(expectedNoOfTrades, (Integer) trades.size());
	}

	@Test
	@Transactional
	public void getClientTrades_One_Agent_Client_6307376() throws Exception {
		Integer mt4Login = 1;
		// Integer expectedNoOfTrades = 1;
		Integer expectedNoOfTrades = 9;
		Double commission = 10.0;
		Agent agent = dataGenerator.createRandomAgent(mt4Login);

		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		AgentClient client = agentClientService.saveClient(clientDTO);

		dataGenerator.createRandomMT4Trades(clientDTO.getLogin(), 0,
				TradeType.CLOSE, expectedNoOfTrades, "2013-02-06");

		Date startCloseTime = getDate("06-02-2013 00:00:00");
		Date endCloseTime = getDate("06-02-2013 23:59:59");

		List<MT4TradeDTO> clientTrades = agentTradesService.getClientTrades(
				client.getId(), commission, null, null, startCloseTime,
				endCloseTime);

		Assert.assertNotNull(clientTrades);
		Assert.assertEquals(expectedNoOfTrades, (Integer) clientTrades.size());
	}

	@Test
	@Transactional
	public void getClientsOpenTrades_FiveClients() throws Exception {
		Integer agentMT4Login = 1;
		Double agentCommission = 10.0;

		Integer expectedNoOfClients = 5;

		Agent agent = dataGenerator.createRandomAgent(agentMT4Login,
				agentCommission);
		List<Integer> clients = new ArrayList<Integer>();
		for (int i = 0; i < expectedNoOfClients; i++) {
			AgentClientDTO clientDTO = new AgentClientDTO();
			clientDTO.setLogin(dataGenerator.createRandomMT4User());
			clientDTO.setAgentId(agent.getId());
			clientDTO.setRegistrationDate(new Date());
			agentClientService.saveClient(clientDTO);
			clients.add(clientDTO.getLogin());
		}

		Date startDate = DateFormatter.parse(Type.GENERAL,
				"06-02-2013 00:00:00");
		Date endDate = DateFormatter.parse(Type.GENERAL, "06-02-2013 23:59:59");

		List<MT4TradeDTO> clientsTrades = agentTradesService
				.getClientsOpenTrades(agent.getId(), startDate, endDate);

		// loop through clients trades, get the logins and place them in a map
		Set<Integer> clientLogins = new HashSet<Integer>();
		for (MT4TradeDTO tradeDTO : clientsTrades) {
			clientLogins.add(tradeDTO.getLogin());
		}

		// There are no open trades in testing DB.
		Assert.assertEquals(0, clientLogins.size());
	}

	@Test
	@Transactional
	public void getClientsOpenTrade_6300759() throws Exception {
		Integer agentMT4Login = 1;
		Double agentCommission = 10.0;

		Integer clientMt4Login = dataGenerator.createRandomMT4User();

		Agent agent = dataGenerator.createRandomAgent(agentMT4Login,
				agentCommission);
		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(clientMt4Login);
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);
		dataGenerator.createRandomMT4Trades(clientDTO.getLogin(), 0,
				TradeType.OPEN, 1, "2013-01-01");

		Date startDate = DateFormatter.parse(Type.GENERAL,
				"01-01-2013 00:00:00");
		Date endDate = DateFormatter.parse(Type.GENERAL, "09-01-2013 23:59:59");

		List<MT4TradeDTO> clientsTrades = agentTradesService
				.getClientsOpenTrades(agent.getId(), startDate, endDate);

		Assert.assertEquals(1, clientsTrades.size());
		Assert.assertEquals(clientMt4Login, clientsTrades.get(0).getLogin());
	}

	@Test
	@Transactional
	public void getClientsTrades_Multiple_Agents_Downlines() throws Exception {
		Agent agent = dataGenerator.createRandomAgent(1);
		Agent level2Agent1 = dataGenerator.createRandomAgent(2);
		Agent level2Agent2 = dataGenerator.createRandomAgent(3);

		agentRelationshipService.saveRelationship(agent.getId(),
				level2Agent1.getId());
		agentRelationshipService.saveRelationship(agent.getId(),
				level2Agent2.getId());

		AgentClientDTO clientDTO1 = new AgentClientDTO();
		clientDTO1.setLogin(dataGenerator.createRandomMT4User());
		clientDTO1.setAgentId(level2Agent1.getId());
		clientDTO1.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO1);

		dataGenerator.createRandomMT4Trades(clientDTO1.getLogin(), 0,
				TradeType.CLOSE, 5, "2013-02-06");

		AgentClientDTO clientDTO2 = new AgentClientDTO();
		clientDTO2.setLogin(dataGenerator.createRandomMT4User());
		clientDTO2.setAgentId(level2Agent1.getId());
		clientDTO2.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO2);

		dataGenerator.createRandomMT4Trades(clientDTO2.getLogin(), 0,
				TradeType.CLOSE, 5, "2013-02-06");

		AgentClientDTO clientDTO3 = new AgentClientDTO();
		clientDTO3.setLogin(dataGenerator.createRandomMT4User());
		clientDTO3.setAgentId(level2Agent2.getId());
		clientDTO3.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO3);

		dataGenerator.createRandomMT4Trades(clientDTO2.getLogin(), 0,
				TradeType.CLOSE, 5, "2013-02-06");

		AgentClientDTO clientDTO4 = new AgentClientDTO();
		clientDTO4.setLogin(dataGenerator.createRandomMT4User());
		clientDTO4.setAgentId(level2Agent2.getId());
		clientDTO4.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO4);

		dataGenerator.createRandomMT4Trades(clientDTO2.getLogin(), 0,
				TradeType.CLOSE, 10, "2013-02-06");

		Date startDate = DateFormatter.parse(Type.GENERAL,
				"06-02-2013 00:00:00");
		Date endDate = DateFormatter.parse(Type.GENERAL, "06-02-2013 23:59:59");

		List<MT4TradeDTO> clientsTrades = agentTradesService
				.getDownlineClientsTrades(agent.getId(), null, null, startDate,
						endDate);

		Assert.assertEquals(25, clientsTrades.size());

		for (MT4TradeDTO trade : clientsTrades) {
			if (trade.getLogin() == clientDTO1.getLogin()) {
				Assert.assertEquals(level2Agent1.getId(), trade.getAgentId());
				Assert.assertEquals(level2Agent1.getId(), trade
						.getAgentClientDTO().getAgentId());
			} else if (trade.getLogin() == clientDTO2.getLogin()) {
				Assert.assertEquals(level2Agent1.getId(), trade.getAgentId());
				Assert.assertEquals(level2Agent1.getId(), trade
						.getAgentClientDTO().getAgentId());
			} else if (trade.getLogin() == clientDTO3.getLogin()) {
				Assert.assertEquals(level2Agent2.getId(), trade.getAgentId());
				Assert.assertEquals(level2Agent2.getId(), trade
						.getAgentClientDTO().getAgentId());
			} else if (trade.getLogin() == clientDTO4.getLogin()) {
				Assert.assertEquals(level2Agent2.getId(), trade.getAgentId());
				Assert.assertEquals(level2Agent2.getId(), trade
						.getAgentClientDTO().getAgentId());
			}
		}
	}

	@Test
	@Transactional
	public void getDownlineClientsTrades_GetNonBalanceTrades() throws Exception {

		String baseDate = "2013-06-06";

		Agent root = dataGenerator.createRandomAgent();
		Agent downline = dataGenerator.createRandomAgent();
		dataGenerator.setAgentAsDownline(root, downline);

		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(downline.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		dataGenerator.createRandomMT4Trades(clientDTO.getLogin(), 1,
				TradeType.CLOSE, 10, baseDate);

		List<MT4TradeDTO> trades = agentTradesService.getDownlineClientsTrades(
				root.getId(), null, null,
				DateFormatter.parse(Type.MYSQL, baseDate + " 00:00:00"),
				DateFormatter.parse(Type.MYSQL, baseDate + " 23:59:59"), 0, 10,
				null, 1, "asc");

		Assert.assertEquals(10, trades.size());
	}

	@Test
	@Transactional
	public void getCommissionsEarnedFromDownlines_Multiple_Agents_Downlines()
			throws Exception {
		Agent agent = dataGenerator.createRandomAgent(1);
		Agent level2Agent1 = dataGenerator.createRandomAgent(2);
		Agent level2Agent2 = dataGenerator.createRandomAgent(3);

		agentRelationshipService.saveRelationship(agent.getId(),
				level2Agent1.getId());
		agentRelationshipService.saveRelationship(agent.getId(),
				level2Agent2.getId());

		AgentClientDTO clientDTO1 = new AgentClientDTO();
		clientDTO1.setLogin(dataGenerator.createRandomMT4User());
		clientDTO1.setAgentId(level2Agent1.getId());
		clientDTO1.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO1);
		dataGenerator.createRandomMT4Trades(clientDTO1.getLogin(), 0,
				TradeType.CLOSE, 5, "2013-02-06");

		AgentClientDTO clientDTO2 = new AgentClientDTO();
		clientDTO2.setLogin(dataGenerator.createRandomMT4User());
		clientDTO2.setAgentId(level2Agent1.getId());
		clientDTO2.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO2);
		dataGenerator.createRandomMT4Trades(clientDTO2.getLogin(), 0,
				TradeType.CLOSE, 5, "2013-02-06");

		AgentClientDTO clientDTO3 = new AgentClientDTO();
		clientDTO3.setLogin(dataGenerator.createRandomMT4User());
		clientDTO3.setAgentId(level2Agent2.getId());
		clientDTO3.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO3);
		dataGenerator.createRandomMT4Trades(clientDTO3.getLogin(), 0,
				TradeType.CLOSE, 5, "2013-02-06");

		AgentClientDTO clientDTO4 = new AgentClientDTO();
		clientDTO4.setLogin(dataGenerator.createRandomMT4User());
		clientDTO4.setAgentId(level2Agent2.getId());
		clientDTO4.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO4);

		Date startDate = DateFormatter.parse(Type.GENERAL,
				"06-02-2013 00:00:00");
		Date endDate = DateFormatter.parse(Type.GENERAL, "06-02-2013 23:59:59");

		List<MT4CommissionDTO> commissions = agentTradesService
				.getCommissionsEarnedFromDownline(agent.getId(), null, null,
						startDate, endDate);

		Assert.assertEquals(3, commissions.size());
	}

	@Test
	@Transactional
	public void getAllDownlineClientsList_4LevelAgents() throws Exception {
		Agent root = dataGenerator.createRandomAgent(1, 10.0);
		Agent a1 = dataGenerator.createRandomAgent(2, 10.0);
		Agent a2 = dataGenerator.createRandomAgent(3, 10.0);
		Agent a11 = dataGenerator.createRandomAgent(4, 10.0);
		Agent a12 = dataGenerator.createRandomAgent(5, 10.0);
		Agent a13 = dataGenerator.createRandomAgent(6, 10.0);
		Agent a14 = dataGenerator.createRandomAgent(7, 10.0);
		Agent a21 = dataGenerator.createRandomAgent(8, 10.0);
		Agent a22 = dataGenerator.createRandomAgent(9, 10.0);
		Agent a23 = dataGenerator.createRandomAgent(10, 10.0);
		Agent a111 = dataGenerator.createRandomAgent(11, 10.0);
		Agent a112 = dataGenerator.createRandomAgent(12, 10.0);
		Agent a1111 = dataGenerator.createRandomAgent(13, 10.0);
		Agent a1112 = dataGenerator.createRandomAgent(14, 10.0);
		Agent a1113 = dataGenerator.createRandomAgent(15, 10.0);

		agentRelationshipService.saveRelationship(root.getId(), a1.getId());
		agentRelationshipService.saveRelationship(root.getId(), a2.getId());

		agentRelationshipService.saveRelationship(a1.getId(), a11.getId());
		agentRelationshipService.saveRelationship(a1.getId(), a12.getId());
		agentRelationshipService.saveRelationship(a1.getId(), a13.getId());
		agentRelationshipService.saveRelationship(a1.getId(), a14.getId());

		agentRelationshipService.saveRelationship(a2.getId(), a21.getId());
		agentRelationshipService.saveRelationship(a2.getId(), a22.getId());
		agentRelationshipService.saveRelationship(a2.getId(), a23.getId());

		agentRelationshipService.saveRelationship(a11.getId(), a111.getId());
		agentRelationshipService.saveRelationship(a11.getId(), a112.getId());

		agentRelationshipService.saveRelationship(a111.getId(), a1111.getId());
		agentRelationshipService.saveRelationship(a111.getId(), a1112.getId());
		agentRelationshipService.saveRelationship(a111.getId(), a1113.getId());

		AgentClientDTO clientDTO = new AgentClientDTO();

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a11.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		List<Integer> allDownlinesClients_a1 = agentTradesService
				.getAllDownlinesClientsList(a1.getId(), null);
		List<Integer> allDownlinesClients_a11 = agentTradesService
				.getAllDownlinesClientsList(a11.getId(), null);
		List<Integer> allDownlinesClients_a111 = agentTradesService
				.getAllDownlinesClientsList(a111.getId(), null);

		Assert.assertEquals(8, allDownlinesClients_a1.size()); // all clients
																// including his
																// own clients
		Assert.assertEquals(7, allDownlinesClients_a11.size());
		Assert.assertEquals(6, allDownlinesClients_a111.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	@Transactional
	public void getAllDownlineClientsMap_4LevelAgents() throws Exception {
		Agent root = dataGenerator.createRandomAgent(1, 10.0);
		Agent a1 = dataGenerator.createRandomAgent(2, 10.0);
		Agent a2 = dataGenerator.createRandomAgent(3, 10.0);
		Agent a11 = dataGenerator.createRandomAgent(4, 10.0);
		Agent a12 = dataGenerator.createRandomAgent(5, 10.0);
		Agent a13 = dataGenerator.createRandomAgent(6, 10.0);
		Agent a14 = dataGenerator.createRandomAgent(7, 10.0);
		Agent a21 = dataGenerator.createRandomAgent(8, 10.0);
		Agent a22 = dataGenerator.createRandomAgent(9, 10.0);
		Agent a23 = dataGenerator.createRandomAgent(10, 10.0);
		Agent a111 = dataGenerator.createRandomAgent(11, 10.0);
		Agent a112 = dataGenerator.createRandomAgent(12, 10.0);
		Agent a1111 = dataGenerator.createRandomAgent(13, 10.0);
		Agent a1112 = dataGenerator.createRandomAgent(14, 10.0);
		Agent a1113 = dataGenerator.createRandomAgent(15, 10.0);

		agentRelationshipService.saveRelationship(root.getId(), a1.getId());
		agentRelationshipService.saveRelationship(root.getId(), a2.getId());

		agentRelationshipService.saveRelationship(a1.getId(), a11.getId());
		agentRelationshipService.saveRelationship(a1.getId(), a12.getId());
		agentRelationshipService.saveRelationship(a1.getId(), a13.getId());
		agentRelationshipService.saveRelationship(a1.getId(), a14.getId());

		agentRelationshipService.saveRelationship(a2.getId(), a21.getId());
		agentRelationshipService.saveRelationship(a2.getId(), a22.getId());
		agentRelationshipService.saveRelationship(a2.getId(), a23.getId());

		agentRelationshipService.saveRelationship(a11.getId(), a111.getId());
		agentRelationshipService.saveRelationship(a11.getId(), a112.getId());

		agentRelationshipService.saveRelationship(a111.getId(), a1111.getId());
		agentRelationshipService.saveRelationship(a111.getId(), a1112.getId());
		agentRelationshipService.saveRelationship(a111.getId(), a1113.getId());

		AgentClientDTO clientDTO = new AgentClientDTO();

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a11.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		List<Map<String, Object>> allDownlinesClients_a1 = agentTradesService
				.getAllDownlinesClientsMap(a1.getId(), null);
		List<Map<String, Object>> allDownlinesClients_a11 = agentTradesService
				.getAllDownlinesClientsMap(a11.getId(), null);
		List<Map<String, Object>> allDownlinesClients_a111 = agentTradesService
				.getAllDownlinesClientsMap(a111.getId(), null);

		Assert.assertEquals(4, allDownlinesClients_a1.size()); // only 4 (3
																// downlines and
																// 1 self)
																// downline
																// agents(a11,
																// a111 and
																// a1111) under
																// a1 have
																// clients
		Assert.assertEquals(3, allDownlinesClients_a11.size()); // only 3 (2
																// downlines and
																// 1 self)
																// downline
																// agents(a111
																// and a1111)
																// under a11
																// have clients.
		Assert.assertEquals(2, allDownlinesClients_a111.size()); // only 2 (1
																	// downline
																	// and 1
																	// self)
																	// downline
																	// agent(a1111)
																	// has
																	// clients.

		Assert.assertEquals(
				8,
				((List<Integer>) allDownlinesClients_a1.get(0).get("clients"))
						.size()
						+ ((List<Integer>) allDownlinesClients_a1.get(1).get(
								"clients")).size()
						+ ((List<Integer>) allDownlinesClients_a1.get(2).get(
								"clients")).size()
						+ ((List<Integer>) allDownlinesClients_a1.get(3).get(
								"clients")).size());

		Assert.assertEquals(7, ((List<Integer>) allDownlinesClients_a11.get(0)
				.get("clients")).size()
				+ ((List<Integer>) allDownlinesClients_a11.get(1)
						.get("clients")).size()
				+ ((List<Integer>) allDownlinesClients_a11.get(2)
						.get("clients")).size());

		Assert.assertEquals(
				6,
				((List<Integer>) allDownlinesClients_a111.get(0).get("clients"))
						.size()
						+ ((List<Integer>) allDownlinesClients_a111.get(1).get(
								"clients")).size());
	}

	@Test
	@Transactional
	public void getAllDownlinesClients_4LevelAgents() throws Exception {
		Agent root = dataGenerator.createRandomAgent(1, 10.0);
		Agent a1 = dataGenerator.createRandomAgent(2, 10.0);
		Agent a2 = dataGenerator.createRandomAgent(3, 10.0);
		Agent a11 = dataGenerator.createRandomAgent(4, 10.0);
		Agent a12 = dataGenerator.createRandomAgent(5, 10.0);
		Agent a13 = dataGenerator.createRandomAgent(6, 10.0);
		Agent a14 = dataGenerator.createRandomAgent(7, 10.0);
		Agent a21 = dataGenerator.createRandomAgent(8, 10.0);
		Agent a22 = dataGenerator.createRandomAgent(9, 10.0);
		Agent a23 = dataGenerator.createRandomAgent(10, 10.0);
		Agent a111 = dataGenerator.createRandomAgent(11, 10.0);
		Agent a112 = dataGenerator.createRandomAgent(12, 10.0);
		Agent a1111 = dataGenerator.createRandomAgent(13, 10.0);
		Agent a1112 = dataGenerator.createRandomAgent(14, 10.0);
		Agent a1113 = dataGenerator.createRandomAgent(15, 10.0);

		agentRelationshipService.saveRelationship(root.getId(), a1.getId());
		agentRelationshipService.saveRelationship(root.getId(), a2.getId());

		agentRelationshipService.saveRelationship(a1.getId(), a11.getId());
		agentRelationshipService.saveRelationship(a1.getId(), a12.getId());
		agentRelationshipService.saveRelationship(a1.getId(), a13.getId());
		agentRelationshipService.saveRelationship(a1.getId(), a14.getId());

		agentRelationshipService.saveRelationship(a2.getId(), a21.getId());
		agentRelationshipService.saveRelationship(a2.getId(), a22.getId());
		agentRelationshipService.saveRelationship(a2.getId(), a23.getId());

		agentRelationshipService.saveRelationship(a11.getId(), a111.getId());
		agentRelationshipService.saveRelationship(a11.getId(), a112.getId());

		agentRelationshipService.saveRelationship(a111.getId(), a1111.getId());
		agentRelationshipService.saveRelationship(a111.getId(), a1112.getId());
		agentRelationshipService.saveRelationship(a111.getId(), a1113.getId());

		AgentClientDTO clientDTO = new AgentClientDTO();

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a11.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(a1111.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);

		List<AgentClient> allDownlinesClients_a1 = agentTradesService
				.getAllDownlinesClients(a1.getId(), null);
		List<AgentClient> allDownlinesClients_a11 = agentTradesService
				.getAllDownlinesClients(a11.getId(), null);
		List<AgentClient> allDownlinesClients_a111 = agentTradesService
				.getAllDownlinesClients(a111.getId(), null);

		Assert.assertEquals(8, allDownlinesClients_a1.size());
		Assert.assertEquals(7, allDownlinesClients_a11.size());
		Assert.assertEquals(6, allDownlinesClients_a111.size());
	}

	@Test
	@Transactional
	public void getClientBalanceTrades_6300986() throws ParseException,
			MT4RemoteServiceException {
		Integer expectedNoOfTrades = 9;
		Integer login = 6300986;

		dataGenerator.createRandomMT4User(login);

		dataGenerator.createRandomMT4Trades(login, 6, TradeType.BALANCE, 3,
				"2012-10-03");
		dataGenerator.createRandomMT4Trades(login, 6, TradeType.BALANCE, 3,
				"2012-10-06");
		dataGenerator.createRandomMT4Trades(login, 6, TradeType.BALANCE, 3,
				"2012-10-09");

		Date startDate = DateFormatter.parse(Type.GENERAL,
				"03-10-2012 00:00:00");
		Date endDate = DateFormatter.parse(Type.GENERAL, "10-10-2012 23:59:59");

		List<MT4TradeDTO> balance = agentTradesService.getClientBalanceTrades(
				login, startDate, endDate);

		Assert.assertEquals(expectedNoOfTrades.intValue(), balance.size());
	}

	@Test
	@Transactional
	public void getClientsBalanceTrades_OneAgent_By_CloseTime()
			throws Exception {
		Integer agentMT4Login = 1;
		Double agentCommission = 10.0;

		Agent agent = dataGenerator.createRandomAgent(agentMT4Login,
				agentCommission);
		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);
		dataGenerator.createRandomMT4Trades(clientDTO.getLogin(), 6,
				TradeType.BALANCE, 13, "2012-10-03");

		clientDTO = new AgentClientDTO();
		clientDTO.setLogin(dataGenerator.createRandomMT4User());
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO);
		dataGenerator.createRandomMT4Trades(clientDTO.getLogin(), 6,
				TradeType.BALANCE, 13, "2012-11-20");

		Date startDate = DateFormatter.parse(Type.MYSQL, "2012-10-03 00:00:00");
		Date endDate = DateFormatter.parse(Type.MYSQL, "2012-11-24 23:59:59");

		List<MT4TradeDTO> clientsTrades = agentTradesService
				.getClientsBalanceTrades(agent.getId(), startDate, endDate);

		Assert.assertEquals(26, clientsTrades.size());
		// Assert.assertEquals(clientMt4Login, clientsTrades.get(0).getLogin());
	}
}
