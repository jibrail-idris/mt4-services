package com.mt4agents.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mt4agents.dao.AgentDAO;
import com.mt4agents.dto.AgentClientDTO;
import com.mt4agents.dto.AgentDTO;
import com.mt4agents.dto.MT4UserDTO;
import com.mt4agents.dto.UserDTO;
import com.mt4agents.entities.Agent;
import com.mt4agents.entities.AgentClient;
import com.mt4agents.entities.AgentRelationship;
import com.mt4agents.entities.users.AgentUser;
import com.mt4agents.exceptions.AgentRelationshipException;
import com.mt4agents.exceptions.UserServiceException;
import com.mt4agents.formatters.DateFormatter;
import com.mt4agents.formatters.DateFormatter.Type;
import com.mt4agents.services.AgentClientService;
import com.mt4agents.services.AgentRelationshipService;
import com.mt4agents.services.MT4RemoteService;
import com.mt4agents.services.MT4RemoteService.TradeType;
import com.mt4agents.services.UserService;

public class DataGenerator {

	private int ticketCounter = 0;
	private int agentNodeCounter;

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedJdbcTemplate;

	private AgentDAO agentDAO;
	private UserService userService;
	private AgentClientService agentClientService;
	private AgentRelationshipService agentRelationshipService;
	private MT4RemoteService mt4RemoteService;

	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public void setAgentDAO(AgentDAO agentDAO) {
		this.agentDAO = agentDAO;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setAgentClientService(AgentClientService agentClientService) {
		this.agentClientService = agentClientService;
	}

	public void setAgentRelationshipService(
			AgentRelationshipService agentRelationshipService) {
		this.agentRelationshipService = agentRelationshipService;
	}

	public void setMt4RemoteService(MT4RemoteService mt4RemoteService) {
		this.mt4RemoteService = mt4RemoteService;
	}

	public void createRandomMT4User(Integer login, String name, String country) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO `mt4_users` VALUES( ");
		sql.append("?, "); // `LOGIN`
		sql.append("'manager', "); // `GROUP`
		sql.append("'1',  "); // `ENABLE`
		sql.append("'1',  "); // `ENABLE_CHANGE_PASS`
		sql.append("'0',  "); // `ENABLE_READONLY`
		sql.append("'',  "); // `PASSWORD_PHONE`
		sql.append("?,  "); // `NAME`
		sql.append("?,  "); // `COUNTRY`
		sql.append("'',  "); // `CITY`
		sql.append("'',  "); // `STATE`
		sql.append("'',  "); // `ZIPCODE`
		sql.append("'',  "); // `ADDRESS`
		sql.append("'',  "); // `PHONE`
		sql.append("'',  "); // `EMAIL`
		sql.append("'automaticaly generated on startup', "); // `COMMENT`
		sql.append("'',  "); // `ID`
		sql.append("'',  "); // `STATUS`
		sql.append("'2011-08-26 10:54:49', "); // `REGDATE`
		sql.append("'2012-12-28 23:20:34',  "); // `LASTDATE`
		sql.append("'0',  "); // `LEVERAGE`
		sql.append("'0',  "); // `AGENT_ACCOUNT`
		sql.append("'1352377242', "); // `TIMESTAMP`
		sql.append("'0',  "); // `BALANCE`
		sql.append("'0',  "); // `PREVMONTHBALANCE`
		sql.append("'0',  "); // `PREVBALANCE`
		sql.append("'0',  "); // `CREDIT`
		sql.append("'0',  "); // `INTERESTRATE`
		sql.append("'0',  "); // `TAXES`
		sql.append("'0',  "); // `SEND_REPORTS`
		sql.append("'-16777216', "); // `USER_COLOR`
		sql.append("'0',  "); // `EQUITY`
		sql.append("'0',  "); // `MARGIN`
		sql.append("'0',  "); // `MARGIN_LEVEL`
		sql.append("'0',  "); // `MARGIN_FREE`
		sql.append("'2013-02-25 14:21:36')"); // `MODIFY_TIME`

		jdbcTemplate.update(sql.toString(), login, name, country);
	}

	public Integer createRandomMT4User(Integer login, String country) {
		StringBuilder name = new StringBuilder("NAME");
		name.append(randomiseLogin());
		createRandomMT4User(login, name.toString(), country);
		return login;
	}

	public Integer createRandomMT4User(Integer login) {
		StringBuilder name = new StringBuilder("NAME");
		name.append(randomiseLogin());
		createRandomMT4User(login, name.toString(), "");
		return login;
	}

	public Integer createRandomMT4User() {
		Integer login = randomiseLogin();
		createRandomMT4User(login.intValue());
		return login;
	}

	public void createRandomMT4Users(int count) {
		for (int i = 0; i < count; i++) {
			createRandomMT4User();
		}
	}

	public void createRandomMT4Trade(Integer login, Integer cmd,
			Integer volume, Date openTime, Date closeTime, Double openPrice,
			Double closePrice) {

		if (ticketCounter == 0) {
			Random rnd = new Random();
			ticketCounter = rnd.nextInt(203) * rnd.nextInt(41);
		}
		Integer ticket = ticketCounter++;

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO `mt4_trades` VALUES( ");
		sql.append("?,"); // `TICKET`
		sql.append("?,"); // `LOGIN`
		sql.append("'',"); // `SYMBOL`
		sql.append("'0',"); // `DIGITS`
		sql.append("?,"); // `CMD`
		sql.append("?,"); // `VOLUME`
		sql.append("?,"); // `OPEN_TIME`
		sql.append("?,"); // `OPEN_PRICE`
		sql.append("'0',"); // `SL`
		sql.append("'0',"); // `TP`
		sql.append("?,"); // `CLOSE_TIME`
		sql.append("'1970-01-01 00:00:00',"); // `EXPIRATION`
		sql.append("'0',"); // `CONV_RATE1`
		sql.append("'0',"); // `CONV_RATE2`
		sql.append("'0',"); // `COMMISSION`
		sql.append("'0',"); // `COMMISSION_AGENT`
		sql.append("'0',"); // `SWAPS`
		sql.append("?,"); // `CLOSE_PRICE`
		sql.append("'115524.48',"); // `PROFIT`
		sql.append("'0',"); // `TAXES`
		sql.append("'B/F as at 2012-09-28 388888',"); // `COMMENT`
		sql.append("'0',"); // `INTERNAL_ID`
		sql.append("'0',"); // `MARGIN_RATE``MARGIN_RATE`
		sql.append("'1351320224',"); // `TIMESTAMP`
		sql.append("'2013-02-25 14:21:49'"); // `MODIFY_TIME`
		sql.append(")");

		jdbcTemplate.update(sql.toString(), ticket, login, cmd, volume,
				openTime, openPrice, closeTime, closePrice);
	}

	public void createRandomMT4Trades(Integer login, Integer cmd,
			TradeType tradeType, int noOfTrades, String baseDate)
			throws ParseException {
		Random rnd = new Random();
		for (int i = 0; i < noOfTrades; i++) {
			int rndHour = rnd.nextInt(24);
			int rndMin = rnd.nextInt(60);

			if (rndHour < 10) {
				rndHour += 10;
			}

			if (rndMin < 10) {
				rndMin += 10;
			}

			Date randomMySQLDate = DateFormatter.parse(Type.MYSQL, baseDate
					+ " " + rndHour + ":" + rndMin + ":00");
			if (tradeType == TradeType.CLOSE) {
				createRandomMT4Trade(login, cmd, rnd.nextInt(500),
						randomMySQLDate, randomMySQLDate,
						rnd.nextInt(1400) * 1.00, rnd.nextInt(1400) * 1.00);
			} else if (tradeType == TradeType.OPEN) {
				createRandomMT4Trade(login, cmd, rnd.nextInt(500),
						randomMySQLDate, DateFormatter.parse(Type.MYSQL,
								MT4RemoteService.BLANK_DATE),
						rnd.nextInt(1400) * 1.00, rnd.nextInt(1400) * 1.00);
			} else {
				createRandomMT4Trade(login, 6, rnd.nextInt(500),
						randomMySQLDate, randomMySQLDate,
						rnd.nextInt(1400) * 1.00, rnd.nextInt(1400) * 1.00);
			}
		}
	}

	public Agent createRandomAgent() {
		StringBuilder name = new StringBuilder("NAME");
		Integer login = createRandomMT4User();
		name.append(login);
		Random rnd = new Random();
		Agent randomAgent = new Agent();
		randomAgent.setCommission(rnd.nextDouble() * 100);
		randomAgent.setName(name.toString());
		randomAgent.setMt4Login(login);
		agentDAO.save(randomAgent);
		return randomAgent;
	}

	public Agent createRandomAgent(Integer mt4Login) {
		Random rnd = new Random();
		Agent randomAgent = new Agent();
		randomAgent.setCommission(rnd.nextDouble() * 100);
		randomAgent.setMt4Login(mt4Login);
		agentDAO.save(randomAgent);
		return randomAgent;
	}

	public Agent createRandomAgent(Integer mt4Login, Double commission) {
		Agent randomAgent = new Agent();
		randomAgent.setCommission(commission);
		randomAgent.setMt4Login(mt4Login);
		agentDAO.save(randomAgent);
		return randomAgent;
	}

	public AgentUser createRandomAgentUser() throws UserServiceException {
		Agent agent = createRandomAgent();
		AgentDTO agentDTO = new AgentDTO();
		agentDTO.setAgentId(agent.getId());
		UserDTO agentUserDTO = new UserDTO();
		agentUserDTO.setAgentDTO(agentDTO);
		String mt4Login = agent.getMt4Login().toString();
		agentUserDTO.setUsername(mt4Login);
		agentUserDTO.setPassword(mt4Login);
		agentUserDTO.setNewPassword1(mt4Login);
		agentUserDTO.setNewPassword2(mt4Login);
		agentUserDTO.setRole(AgentUser.ROLE);
		return (AgentUser) userService.saveUser(agentUserDTO);
	}

	public AgentRelationship setAgentAsDownline(Agent parentAgent,
			Agent childAgent) throws AgentRelationshipException {
		agentRelationshipService.saveRelationship(parentAgent.getId(),
				childAgent.getId());
		return agentRelationshipService.getParentRelationship(parentAgent
				.getId());
	}

	public AgentClient assignUserAsAgentClient(Agent agent, Integer clientLogin)
			throws Exception {
		AgentClientDTO clientDTO = new AgentClientDTO();
		clientDTO.setLogin(clientLogin);
		clientDTO.setAgentId(agent.getId());
		clientDTO.setRegistrationDate(new Date());
		return agentClientService.saveClient(clientDTO);
	}

	public int agentsCountInTree(int depth, int noOfNodes) {
		if (depth > 0) {
			return (int) (Math.pow(noOfNodes, depth) + agentsCountInTree(
					depth - 1, noOfNodes));
		}
		return 1;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void constructAgentsTree(int depth, int childrenCountPerNode,
			int noOfClients, int noOfTradesPerClient) throws Exception {
		AgentUser agentUser = createRandomAgentUser();
		agentNodeCounter = 1;
		int noOfAgentsToAdd = agentsCountInTree(depth, childrenCountPerNode);

		// for (int d = 0; d < depth; d++) {
		//
		// int power = childrenCountPerNode;
		// for (int e = 0; e < d; e++) {
		// power *= childrenCountPerNode;
		// }
		// noOfAgentsToAdd += power;
		// }

		assignAgentNode(noOfAgentsToAdd, childrenCountPerNode, noOfClients,
				noOfTradesPerClient, depth, 0, agentUser.getAgent());
	}

	public void constructAgentsTree(Integer rootLogin, int depth,
			int childrenCountPerNode, int noOfClients, int noOfTradesPerClient)
			throws Exception {

		Agent agent = agentDAO.readByMT4Login(rootLogin);
		agentNodeCounter = 1;
		int noOfAgentsToAdd = agentsCountInTree(depth, childrenCountPerNode);

		assignAgentNode(noOfAgentsToAdd, childrenCountPerNode, noOfClients,
				noOfTradesPerClient, depth, 0, agent);
	}

	private Date today = new Date();

	private void assignAgentNode(int noOfAgents, int childrenCountPerNode,
			int noOfClients, int noOfTradesPerClient, int depth,
			int currentDepth, Agent parentAgent) throws Exception {

		Random rnd = new Random();

		currentDepth++;

		List<Agent> children = new ArrayList<Agent>();

		if (currentDepth <= depth) {
			for (int i = 0; i < childrenCountPerNode; i++) {
				agentNodeCounter++;
				if (agentNodeCounter > noOfAgents) {
					return;
				}
				AgentUser agentUser = createRandomAgentUser();
				setAgentAsDownline(parentAgent, agentUser.getAgent());

				for (int j = 0; j < noOfClients; j++) {
					AgentClientDTO clientDTO = new AgentClientDTO();
					clientDTO.setLogin(createRandomMT4User());
					clientDTO.setAgentId(agentUser.getAgent().getId());
					clientDTO.setRegistrationDate(new Date());
					agentClientService.saveClient(clientDTO);

					// generate trades for clients here.
					Date baseDate = DateUtil.addDays(today,
							Math.abs(rnd.nextInt() * 30 % 7) * -1);

					createRandomMT4Trades(clientDTO.getLogin(), 1,
							TradeType.OPEN, noOfTradesPerClient,
							DateFormatter.getDateFormatter(Type.MYSQLNOTIME)
									.format(baseDate));

					createRandomMT4Trades(clientDTO.getLogin(), 1,
							TradeType.CLOSE, noOfTradesPerClient,
							DateFormatter.getDateFormatter(Type.MYSQLNOTIME)
									.format(baseDate));

					createRandomMT4Trades(clientDTO.getLogin(), 1,
							TradeType.BALANCE, noOfTradesPerClient,
							DateFormatter.getDateFormatter(Type.MYSQLNOTIME)
									.format(baseDate));
				}

				children.add(agentUser.getAgent());
			}

			for (Agent agent : children) {

				assignAgentNode(noOfAgents, childrenCountPerNode, noOfClients,
						noOfTradesPerClient, depth, currentDepth, agent);

			}
		}

	}

	public List<Integer> getLoginsNotAssignedAsAgents(List<Integer> agentLogins) {
		if (agentLogins == null || agentLogins.size() == 0) {
			return new ArrayList<Integer>();
		} else {

			Map<String, Object> paramMap = new HashMap<String, Object>();

			StringBuilder sql = new StringBuilder();

			sql.append("select ");
			sql.append("`LOGIN` ");
			sql.append("from `mt4_users` ");
			sql.append("where ");
			sql.append("`LOGIN` NOT IN (");
			for (int i = 0; i < agentLogins.size(); i++) {
				Integer login = agentLogins.get(i);
				sql.append(":login_").append(login);
				paramMap.put("login_" + login, login);
				if (i != agentLogins.size() - 1) {
					sql.append(", ");
				}
			}
			sql.append(") ");

			return namedJdbcTemplate.query(sql.toString(), paramMap,
					new RowMapper<Integer>() {

						@Override
						public Integer mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return rs.getInt("LOGIN");
						}
					});
		}

	}

	private int randomiseLogin() {
		Random rnd = new Random();
		int login = rnd.nextInt(20000 + rnd.nextInt(100)) + rnd.nextInt(99999);
		MT4UserDTO user = mt4RemoteService.getUserByMT4LoginSimple(login);
		while (user != null) {
			login = rnd.nextInt(20000 + rnd.nextInt(100)) + rnd.nextInt(99999);
			user = mt4RemoteService.getUserByMT4LoginSimple(login);
		}
		return login;
	}
}
