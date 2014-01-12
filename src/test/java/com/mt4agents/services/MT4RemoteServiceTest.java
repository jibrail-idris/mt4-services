package com.mt4agents.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.dto.MT4CommissionDTO;
import com.mt4agents.dto.MT4TradeDTO;
import com.mt4agents.dto.MT4UserDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.users.AgentUser;
import com.mt4agents.exceptions.MT4RemoteServiceException;
import com.mt4agents.formatters.DateFormatter;
import com.mt4agents.formatters.DateFormatter.Type;
import com.mt4agents.services.MT4RemoteService.TradeType;
import com.mt4agents.util.DataGenerator;
import com.mt4agents.util.DateUtil;

@Transactional
public class MT4RemoteServiceTest extends BaseTest {
	@Autowired
	private MT4RemoteService mt4RemoteService;

	@Autowired
	private AgentService agentService;

	@Autowired
	private AgentClientService agentClientService;

	@Autowired
	private AgentRelationshipService agentRelationshipService;

	@Autowired
	private ClientTradesService clientTradesService;

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
	public void getUserByMT4Login_OneUser() {

		Integer mt4Login = 4444;
		dataGenerator.createRandomMT4User(mt4Login);

		MT4UserDTO userDTO = mt4RemoteService.getUserByMT4Login(mt4Login);

		Assert.assertNotNull(userDTO);
		Assert.assertNotNull(userDTO.getLogin());
		Assert.assertTrue(userDTO.getLogin() > 0);
		Assert.assertTrue(StringUtils.hasLength(userDTO.getName()));
	}

	@Test
	public void getUserByMT4LoginSimple_OneUser() {

		Integer mt4Login = 3333;
		dataGenerator.createRandomMT4User(mt4Login);

		MT4UserDTO userDTO = mt4RemoteService.getUserByMT4LoginSimple(mt4Login);

		Assert.assertNotNull(userDTO);
		Assert.assertNotNull(userDTO.getLogin());
		Assert.assertTrue(userDTO.getLogin() > 0);
		Assert.assertTrue(StringUtils.hasLength(userDTO.getName()));
		Assert.assertNull(userDTO.getCountry());
		Assert.assertNull(userDTO.getCity());
		Assert.assertNull(userDTO.getState());
		Assert.assertNull(userDTO.getZipcode());
	}

	@Test
	public void getMT4Users_SortByLogin_OrderAsc() {

		dataGenerator.createRandomMT4Users(40);

		int pageSize1 = 10;
		int pageSize2 = 20;

		List<MT4UserDTO> page1 = mt4RemoteService.getUsers(0, pageSize1, null,
				null, null);
		List<MT4UserDTO> page2 = mt4RemoteService.getUsers(10, pageSize1, null,
				null, null);

		Assert.assertEquals(pageSize1, page1.size());
		Assert.assertEquals(pageSize1, page2.size());

		List<MT4UserDTO> _page1 = mt4RemoteService.getUsers(0, pageSize2, null,
				null, null);

		Assert.assertEquals(pageSize2, _page1.size());

		List<MT4UserDTO> split1 = _page1.subList(0, 10);
		List<MT4UserDTO> split2 = _page1.subList(10, 20);

		for (int i = 0; i < 10; i++) {
			Assert.assertEquals(split1.get(i).getLogin(), page1.get(i)
					.getLogin());
			Assert.assertEquals(split2.get(i).getLogin(), page2.get(i)
					.getLogin());
		}
	}

	@Test
	public void getMT4Users_SortByLogin_OrderDesc() {

		dataGenerator.createRandomMT4Users(40);

		int pageSize1 = 10;

		List<MT4UserDTO> page1 = mt4RemoteService.getUsers(0, pageSize1, null,
				null, "desc");
		List<MT4UserDTO> page2 = mt4RemoteService.getUsers(10, pageSize1, null,
				null, "desc");

		Assert.assertEquals(pageSize1, page1.size());
		Assert.assertEquals(pageSize1, page2.size());

		int previousLogin = 1000000;
		for (MT4UserDTO user : page1) {
			Assert.assertFalse("previous login is greater than current",
					user.getLogin() >= previousLogin);
			previousLogin = user.getLogin();
		}

		previousLogin = 1000000;
		for (MT4UserDTO user : page2) {
			Assert.assertFalse("previous login is greater than current",
					user.getLogin() >= previousLogin);
			previousLogin = user.getLogin();
		}
	}

	@Test
	public void getMT4Users_SearchByLogin() {

		Integer mt4Login = 7777;
		dataGenerator.createRandomMT4User(mt4Login);

		List<MT4UserDTO> users = mt4RemoteService.getUsers(0, 10,
				mt4Login.toString(), null, null);

		Assert.assertEquals(1, users.size());
	}

	@Test
	public void getMT4Users_SearchByText() {

		Integer mt4Login = 7777;
		String country = "China";
		dataGenerator.createRandomMT4User(mt4Login, country);

		List<MT4UserDTO> users = mt4RemoteService.getUsers(0, 10, country,
				null, null);
		Assert.assertTrue(users.size() > 0);
	}

	@Test
	public void getMT4UsersCount_SearchByText() {

		int noOfUsers = 15;
		String country = "China";
		for (int i = 0; i < noOfUsers; i++) {
			dataGenerator.createRandomMT4User(i, country);
		}

		Assert.assertEquals(noOfUsers,
				(int) mt4RemoteService.getUsersCount("China"));
	}

	@Test
	public void getMT4UsersByName() {
		Integer mt4Login = 7777;
		String name = "boss kwan";
		String country = "Singapore";

		int noOfDealers = 10;
		for (int i = 0; i < noOfDealers; i++) {
			dataGenerator.createRandomMT4User(i, "Dealer" + i, country);
		}

		dataGenerator.createRandomMT4User(mt4Login, name, country);

		List<MT4UserDTO> users = mt4RemoteService.getUsersByName(name);
		Assert.assertEquals(1, users.size());

		// now search by dealer
		List<MT4UserDTO> dealers = mt4RemoteService.getUsersByName("dealer");
		Assert.assertEquals(noOfDealers, dealers.size());

		// make no results
		// make sure a search with less than four characters return an empty
		// list.
		List<MT4UserDTO> noResults = mt4RemoteService.getUsersByName("dea");
		Assert.assertEquals(0, noResults.size());
	}

	@Test
	public void getClientsTrades_Close() throws ParseException,
			MT4RemoteServiceException {

		int noOfTrades = 33;

		Double commissionPerLot = 13.0;

		Integer mt4Login = 7777;
		dataGenerator.createRandomMT4User(mt4Login);

		Date startDate = DateFormatter.getDateFormatter(Type.MYSQL).parse(
				"2013-01-01 00:00:00");
		Date endDate = DateFormatter.getDateFormatter(Type.MYSQL).parse(
				"2013-01-01 23:59:59");

		dataGenerator.createRandomMT4Trades(mt4Login, 0, TradeType.CLOSE,
				noOfTrades, "2013-01-01");
		dataGenerator.createRandomMT4Trades(mt4Login, 6, TradeType.BALANCE, 1,
				"2013-01-01"); // one
		// balance

		List<MT4TradeDTO> trades = mt4RemoteService.getClientsTrades(
				Arrays.asList(mt4Login), commissionPerLot, TradeType.CLOSE,
				null, null, startDate, endDate);

		// if number of trades is equals to noOfTrades, then we know
		// getClientTrades is
		// correctly filtering out balance trades (6);
		Assert.assertEquals(noOfTrades, trades.size());

		double totalCommission = 0.00;
		double totalVolume = 0.00;

		for (MT4TradeDTO trade : trades) {
			double commission = trade.getCommission().doubleValue();
			Assert.assertEquals(commission, (trade.getVolume() / 100)
					* commissionPerLot, 0.00001);
			totalCommission += commission;
			totalVolume += trade.getVolume().doubleValue();
		}

		List<MT4CommissionDTO> commissions = mt4RemoteService.getCommissions(
				Arrays.asList(mt4Login), commissionPerLot, null, null,
				startDate, endDate);

		Assert.assertEquals(1, commissions.size());

		MT4CommissionDTO commission = commissions.get(0);

		Assert.assertEquals(totalCommission, commission.getCommission()
				.doubleValue(), 0.00001);
		Assert.assertEquals(totalVolume, commission.getTotalVolume()
				.doubleValue(), 0.00001);
	}

	@Test
	public void getClientsTrades_Open() throws ParseException,
			MT4RemoteServiceException {
		int noOfTrades = 3;

		Integer mt4Login = 7777;
		dataGenerator.createRandomMT4User(mt4Login);

		Date startDate = DateFormatter.getDateFormatter(Type.MYSQL).parse(
				"2013-01-01 00:00:00");
		Date endDate = DateFormatter.getDateFormatter(Type.MYSQL).parse(
				"2013-01-01 23:59:59");

		dataGenerator.createRandomMT4Trades(mt4Login, 0, TradeType.OPEN,
				noOfTrades, "2013-01-01");

		List<MT4TradeDTO> trades = mt4RemoteService.getClientsTrades(
				Arrays.asList(mt4Login), 0.00, TradeType.OPEN, startDate,
				endDate, null, null);

		Assert.assertEquals(noOfTrades, trades.size());
	}

	@Test
	public void getClientsTrades_Balance() throws ParseException,
			MT4RemoteServiceException {

		int noOfTrades = 11;

		Integer mt4Login = 7777;
		dataGenerator.createRandomMT4User(mt4Login);

		dataGenerator.createRandomMT4Trades(mt4Login, 6, TradeType.BALANCE,
				noOfTrades, "2013-01-01");
		dataGenerator.createRandomMT4Trades(mt4Login, 6, TradeType.BALANCE,
				noOfTrades, "2013-01-02");
		dataGenerator.createRandomMT4Trades(mt4Login, 6, TradeType.BALANCE,
				noOfTrades, "2013-01-03");

		Date startDate = DateFormatter.parse(Type.MYSQL, "2013-01-01 00:00:00");
		Date endDate = DateFormatter.parse(Type.MYSQL, "2013-01-03 23:59:59");
		List<MT4TradeDTO> trades = mt4RemoteService.getClientsTrades(
				Arrays.asList(mt4Login), 0.00, TradeType.BALANCE, null, null,
				startDate, endDate);

		Assert.assertEquals(noOfTrades * 3, trades.size());
	}

	@Test
	public void getClientsCloseTrades_FiveClients() throws Exception {

		Integer agentMT4Login = 1;
		Double agentCommission = 10.0;

		Integer expectedNoOfClients = 5;

		dataGenerator.createRandomMT4User(agentMT4Login);

		Agent agent = dataGenerator.createRandomAgent(agentMT4Login,
				agentCommission);
		List<Integer> clients = new ArrayList<Integer>();
		for (int i = 0; i < expectedNoOfClients; i++) {
			AgentClientDTO clientDTO = new AgentClientDTO();
			int clientMT4Login = i + 10;
			dataGenerator.createRandomMT4User(clientMT4Login);
			clientDTO.setLogin(clientMT4Login);
			clientDTO.setAgentId(agent.getId());
			clientDTO.setRegistrationDate(new Date());
			agentClientService.saveClient(clientDTO);
			clients.add(clientDTO.getLogin());

			dataGenerator.createRandomMT4Trades(clientMT4Login, 0,
					TradeType.CLOSE, 2, "2013-01-01"); // close trades each
		}

		Date startDate = DateFormatter.parse(Type.MYSQL, "2013-01-01 00:00:00");
		Date endDate = DateFormatter.parse(Type.MYSQL, "2013-01-01 23:59:59");

		List<MT4TradeDTO> clientsTrades = mt4RemoteService.getClientsTrades(
				clients, agent.getCommission(), TradeType.CLOSE, null, null,
				startDate, endDate);

		// loop through clients trades, get the logins and place them in a map
		Set<Integer> clientLogins = new HashSet<Integer>();
		for (MT4TradeDTO tradeDTO : clientsTrades) {
			clientLogins.add(tradeDTO.getLogin());
		}
		Assert.assertEquals(expectedNoOfClients.intValue(), clientLogins.size());

		for (Integer login : clients) {
			List<MT4TradeDTO> trades = mt4RemoteService.getClientsTrades(
					Arrays.asList(login), agent.getCommission(),
					TradeType.CLOSE, null, null, startDate, endDate);
			Assert.assertEquals(2, trades.size());
		}
	}

	// @Test(expected = MT4RemoteServiceException.class)
	// @Transactional
	public void getClientsCloseTrades_NoClientsGiven()
			throws MT4RemoteServiceException, ParseException {
		try {
			Agent agent = dataGenerator.createRandomAgent();
			List<Integer> emptyList = new ArrayList<Integer>();
			Date startDate = DateFormatter.parse(Type.GENERAL,
					"06-02-2013 00:00:00");
			Date endDate = DateFormatter.parse(Type.GENERAL,
					"06-02-2013 23:59:59");

			mt4RemoteService.getClientsTrades(emptyList, agent.getCommission(),
					TradeType.CLOSE, null, null, startDate, endDate);
		} catch (MT4RemoteServiceException ex) {
			if (ex.getMessage().equals(
					messageSource.getMessage(
							"mt4agents.exception.remote.blankclientslist",
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
	// @Transactional
	public void getClientsTrades_MultipleAgentsDownlines() throws Exception {
		/*
		 * Agent has two downlines. Each downline has two clients.
		 */
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
				TradeType.CLOSE, 2, "2013-01-01");

		AgentClientDTO clientDTO2 = new AgentClientDTO();
		clientDTO2.setLogin(dataGenerator.createRandomMT4User());
		clientDTO2.setAgentId(level2Agent1.getId());
		clientDTO2.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO2);

		dataGenerator.createRandomMT4Trades(clientDTO2.getLogin(), 0,
				TradeType.CLOSE, 2, "2013-01-01");

		AgentClientDTO clientDTO3 = new AgentClientDTO();
		clientDTO3.setLogin(dataGenerator.createRandomMT4User());
		clientDTO3.setAgentId(level2Agent2.getId());
		clientDTO3.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO3);

		dataGenerator.createRandomMT4Trades(clientDTO3.getLogin(), 0,
				TradeType.CLOSE, 2, "2013-01-01");

		AgentClientDTO clientDTO4 = new AgentClientDTO();
		clientDTO4.setLogin(dataGenerator.createRandomMT4User());
		clientDTO4.setAgentId(level2Agent2.getId());
		clientDTO4.setRegistrationDate(new Date());
		agentClientService.saveClient(clientDTO4);

		dataGenerator.createRandomMT4Trades(clientDTO4.getLogin(), 0,
				TradeType.CLOSE, 2, "2013-01-01");

		List<Map<String, Object>> downlines = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();

		map1.put("agentId", level2Agent1.getId());
		map2.put("agentId", level2Agent2.getId());

		map1.put("commission", level2Agent1.getCommission());
		map2.put("commission", level2Agent2.getCommission());

		Agent agent1 = agentService.getAgentById(level2Agent1.getId());
		Agent agent2 = agentService.getAgentById(level2Agent2.getId());

		map1.put("clients", new ArrayList<Integer>(agent1.getClientsByLogin()
				.keySet()));
		map2.put("clients", new ArrayList<Integer>(agent2.getClientsByLogin()
				.keySet()));

		downlines.add(map1);
		downlines.add(map2);

		Date startDate = DateFormatter.parse(Type.MYSQL, "2013-01-01 00:00:00");
		Date endDate = DateFormatter.parse(Type.MYSQL, "2013-01-01 23:59:59");

		List<MT4TradeDTO> clientsTrades = mt4RemoteService
				.getDownlinesClientsTrades(downlines, null, null, null,
						startDate, endDate);

		// 4 clients, 2 close trades each.
		Assert.assertEquals(8, clientsTrades.size());
	}

	@Test
	public void getUsersByMT4Logins() throws NoSuchMessageException,
			MT4RemoteServiceException {
		List<Integer> users = new ArrayList<Integer>();
		users.add(dataGenerator.createRandomMT4User());
		users.add(dataGenerator.createRandomMT4User());
		users.add(dataGenerator.createRandomMT4User());
		users.add(dataGenerator.createRandomMT4User());

		List<MT4UserDTO> usersByMT4Logins = mt4RemoteService
				.getUsersByMT4Logins(users);

		Assert.assertEquals(users.size(), usersByMT4Logins.size());
	}

	@Test
	public void getUsersByMT4Logins_Search() {
		Assert.assertEquals(
				1,
				mt4RemoteService.getUsersByMT4Logins(
						dataGenerator.createRandomMT4User().toString()).size());
		Assert.assertEquals(
				1,
				mt4RemoteService.getUsersByMT4Logins(
						dataGenerator.createRandomMT4User().toString()).size());
		Assert.assertEquals(
				1,
				mt4RemoteService.getUsersByMT4Logins(
						dataGenerator.createRandomMT4User().toString()).size());
	}

	@Test
	public void getUsersByMT4Logins_WildcardSearch() {

		int[] logins = new int[] { 1000, 1001, 1002, 2000, 2001, 2002 };
		for (int login : logins) {
			dataGenerator.createRandomMT4User(login);
		}

		Assert.assertEquals(3, mt4RemoteService.getUsersByMT4Logins("100")
				.size());
		Assert.assertEquals(3, mt4RemoteService.getUsersByMT4Logins("200")
				.size());
	}

	@Test
	public void getDownlineClientsTrades_Paginate_TestOne() throws Exception {

		int noOfExpectedRows = 10;
		int expectedNoOfAgentsInTree = 40;
		int expectedNoOfPages = expectedNoOfAgentsInTree / noOfExpectedRows;

		AgentUser agentUser = dataGenerator.createRandomAgentUser();
		Agent agent = agentUser.getAgent();
		dataGenerator.constructAgentsTree(agent.getMt4Login(), 2, 2, 1, 100);

		List<Map<String, Object>> downlinesClients = clientTradesService
				.getAllDownlinesClientsMap(agent.getId(),
						agent.getCommission(), null);

		Date endCloseTime = new Date();
		Date startCloseTime = DateUtil.addDays(endCloseTime, -7);

		Integer noOfTrades = mt4RemoteService.getDownlinesClientsTradesCount(
				downlinesClients, null, null, null, startCloseTime,
				endCloseTime, null);

		System.out.println("noOfTrades=" + noOfTrades);

		for (int i = 0; i < expectedNoOfPages; i++) {
			List<MT4TradeDTO> downlinesClientsTrades = mt4RemoteService
					.getDownlinesClientsTrades(downlinesClients, null, null,
							null, startCloseTime, endCloseTime,
							(i * noOfExpectedRows), noOfExpectedRows, null, 1,
							"ASC");

			Assert.assertEquals(noOfExpectedRows, downlinesClientsTrades.size());
			for (MT4TradeDTO trade : downlinesClientsTrades) {
				System.out.println(trade.getTicket());
			}
		}
	}

	@Test
	public void getDownlineClientsTrades_SearchByLogin() throws Exception {
		Integer noOfTrades = 5;
		Integer login = dataGenerator.createRandomMT4User();
		String baseDate = "2013-02-02";
		dataGenerator.createRandomMT4Trades(login, 1, TradeType.CLOSE,
				noOfTrades, baseDate);

		Date startCloseTime = DateFormatter.parse(Type.MYSQL, baseDate
				+ " 00:00:00");
		Date endCloseTime = DateFormatter.parse(Type.MYSQL, baseDate
				+ " 23:59:59");

		String search = new StringBuilder(login).toString();

		List<Map<String, Object>> downlines = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> mockAgentCommission = new HashMap<String, Object>();
		mockAgentCommission.put("agentId", 1);
		mockAgentCommission.put("commission", 10.0);
		mockAgentCommission.put("clients", Arrays.asList(login));
		downlines.add(mockAgentCommission);

		Integer tradesCount = mt4RemoteService.getDownlinesClientsTradesCount(
				downlines, null, null, null, startCloseTime, endCloseTime,
				search);

		List<MT4TradeDTO> trades = mt4RemoteService.getDownlinesClientsTrades(
				downlines, null, null, null, startCloseTime, endCloseTime, 0,
				10, search, null, null);

		Assert.assertEquals(noOfTrades.intValue(), trades.size());
		Assert.assertEquals(noOfTrades, tradesCount);
	}

	@Test
	public void getDownlinesVolumeCommission() throws ParseException {
		Integer noOfTrades = 5;
		Integer login = dataGenerator.createRandomMT4User();
		String baseDate = "2013-02-02";
		dataGenerator.createRandomMT4Trades(login, 1, TradeType.CLOSE,
				noOfTrades, baseDate);

		Date startCloseTime = DateFormatter.parse(Type.MYSQL, baseDate
				+ " 00:00:00");
		Date endCloseTime = DateFormatter.parse(Type.MYSQL, baseDate
				+ " 23:59:59");

		List<Map<String, Object>> downlines = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> mockAgentCommission = new HashMap<String, Object>();
		mockAgentCommission.put("agentId", 1);
		mockAgentCommission.put("commission", 10.0);
		mockAgentCommission.put("clients", Arrays.asList(login));
		downlines.add(mockAgentCommission);

		MT4CommissionDTO commission = mt4RemoteService
				.getDownlinesVolumeCommission(downlines, null, null, null,
						startCloseTime, endCloseTime, null);

		System.out.println(commission.getTotalVolume());
	}

	@Test
	public void getClientsTrades_Paginate_TestOne() throws Exception {

		Integer noOfTradesPerClient = 10;

		String baseDate = "2013-02-02";

		Double openPrice = 1000.0;
		Double closePrice = 1200.0;

		Date openTime = DateFormatter.parse(Type.MYSQL, baseDate + " 00:00:00");
		Date closeTime = DateFormatter
				.parse(Type.MYSQL, baseDate + " 11:11:11");

		Date startCloseTime = DateFormatter.parse(Type.MYSQL, baseDate
				+ " 00:00:00");
		Date endCloseTime = DateFormatter.parse(Type.MYSQL, baseDate
				+ " 23:59:59");

		AgentUser agentUser = dataGenerator.createRandomAgentUser();
		Agent agent = agentUser.getAgent();

		Integer client1 = dataGenerator.createRandomMT4User();
		Integer client2 = dataGenerator.createRandomMT4User();
		Integer client3 = dataGenerator.createRandomMT4User();

		dataGenerator.assignUserAsAgentClient(agent, client1);
		dataGenerator.assignUserAsAgentClient(agent, client2);
		dataGenerator.assignUserAsAgentClient(agent, client3);

		for (int i = 0; i < noOfTradesPerClient; i++) {
			dataGenerator.createRandomMT4Trade(client1, 1, 10, openTime,
					closeTime, openPrice, closePrice);
			dataGenerator.createRandomMT4Trade(client2, 1, 10, openTime,
					closeTime, openPrice, closePrice);
			dataGenerator.createRandomMT4Trade(client3, 1, 10, openTime,
					closeTime, openPrice, closePrice);
		}

		List<Integer> clients = Arrays.asList(client1, client2, client3);

		Integer tradesCount = mt4RemoteService.getClientsTradesCount(clients,
				agent.getCommission(), TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime, null);

		List<MT4TradeDTO> page1 = mt4RemoteService.getClientsTrades(clients,
				agent.getCommission(), TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime, 0, 10, null, null, null);

		List<MT4TradeDTO> page2 = mt4RemoteService.getClientsTrades(clients,
				agent.getCommission(), TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime, 10, 10, null, null, null);

		List<MT4TradeDTO> page3 = mt4RemoteService.getClientsTrades(clients,
				agent.getCommission(), TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime, 20, 10, null, null, null);

		Assert.assertEquals(30, tradesCount.intValue());
		Assert.assertEquals(10, page1.size());
		Assert.assertEquals(10, page2.size());
		Assert.assertEquals(10, page3.size());
	}

	@Test
	public void getClientsTrades_SearchByLogin() throws Exception {
		Integer noOfTradesPerClient = 10;

		String baseDate = "2013-02-02";

		Double openPrice = 1000.0;
		Double closePrice = 1200.0;

		Date openTime = DateFormatter.parse(Type.MYSQL, baseDate + " 00:00:00");
		Date closeTime = DateFormatter
				.parse(Type.MYSQL, baseDate + " 11:11:11");

		Date startCloseTime = DateFormatter.parse(Type.MYSQL, baseDate
				+ " 00:00:00");
		Date endCloseTime = DateFormatter.parse(Type.MYSQL, baseDate
				+ " 23:59:59");

		AgentUser agentUser = dataGenerator.createRandomAgentUser();
		Agent agent = agentUser.getAgent();

		Integer client1 = dataGenerator.createRandomMT4User();
		Integer client2 = dataGenerator.createRandomMT4User();
		Integer client3 = dataGenerator.createRandomMT4User();

		dataGenerator.assignUserAsAgentClient(agent, client1);
		dataGenerator.assignUserAsAgentClient(agent, client2);
		dataGenerator.assignUserAsAgentClient(agent, client3);

		for (int i = 0; i < noOfTradesPerClient; i++) {
			dataGenerator.createRandomMT4Trade(client1, 1, 10, openTime,
					closeTime, openPrice, closePrice);
			dataGenerator.createRandomMT4Trade(client2, 1, 10, openTime,
					closeTime, openPrice, closePrice);
			dataGenerator.createRandomMT4Trade(client3, 1, 10, openTime,
					closeTime, openPrice, closePrice);
		}

		List<Integer> clients = Arrays.asList(client1, client2, client3);

		List<MT4TradeDTO> clientsTrades = mt4RemoteService.getClientsTrades(
				clients, agent.getCommission(), TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime, 0, 10, client1 + "", null, null);
		Integer clientTradesCount = mt4RemoteService.getClientsTradesCount(
				clients, agent.getCommission(), TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime, client1 + "");

		Assert.assertEquals(10, clientsTrades.size());
		Assert.assertEquals(10, clientTradesCount.intValue());

		List<MT4TradeDTO> noMatch = mt4RemoteService.getClientsTrades(clients,
				agent.getCommission(), TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime, 0, 10, "ASDASD", null, null);

		Assert.assertEquals(0, noMatch.size());
	}

	@Test
	public void getVolumeCommission() throws ParseException {

		Double commission = 13.0;

		Integer noOfTrades = 5;
		Integer login = dataGenerator.createRandomMT4User();
		String baseDate = "2013-02-02";
		dataGenerator.createRandomMT4Trades(login, 1, TradeType.CLOSE,
				noOfTrades, baseDate);

		Date startCloseTime = DateFormatter.parse(Type.MYSQL, baseDate
				+ " 00:00:00");
		Date endCloseTime = DateFormatter.parse(Type.MYSQL, baseDate
				+ " 23:59:59");

		MT4CommissionDTO commissionVol = mt4RemoteService.getVolumeCommission(
				Arrays.asList(login), commission, TradeType.CLOSE, null, null,
				startCloseTime, endCloseTime);

		Assert.assertTrue(commissionVol.getTotalVolume() > 0.00);
		Assert.assertTrue(commissionVol.getCommission() > 0.00);
	}
}