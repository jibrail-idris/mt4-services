package com.mt4agents.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mt4agents.dao.queries.MT4TradeCommissionQuery;
import com.mt4agents.dao.queries.MT4TradesByLoginQuery;
import com.mt4agents.dao.queries.MT4UsersQuery;
import com.mt4agents.dto.MT4CommissionDTO;
import com.mt4agents.dto.MT4TradeDTO;
import com.mt4agents.dto.MT4UserDTO;
import com.mt4agents.enums.CMD;
import com.mt4agents.exceptions.MT4RemoteServiceException;
import com.mt4agents.formatters.DateFormatter;
import com.mt4agents.formatters.DateFormatter.Type;
import com.mt4agents.formatters.NumberFormatter;
import com.mt4agents.paginate.PaginationSettings;
import com.mt4agents.util.UnicodeUtils;

@Service
public class MT4RemoteService {

	private final class MT4TUserDTORowMapper implements RowMapper<MT4UserDTO> {
		public MT4UserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
			MT4UserDTO dto = new MT4UserDTO();
			dto.setLogin(rs.getInt("LOGIN"));
			dto.setGroup(rs.getString("GROUP"));
			dto.setEnable(rs.getBoolean("ENABLE"));
			dto.setEnableChangePass(rs.getBoolean("ENABLE_CHANGE_PASS"));
			dto.setEnableReadOnly(rs.getBoolean("ENABLE_READONLY"));
			dto.setPasswordPhone(rs.getString("PASSWORD_PHONE"));
			dto.setName(UnicodeUtils.convertToString(rs.getString("NAME")));
			dto.setCountry(rs.getString("COUNTRY"));
			dto.setCity(rs.getString("CITY"));
			dto.setState(rs.getString("STATE"));
			dto.setZipcode(rs.getString("ZIPCODE"));
			dto.setAddress(rs.getString("ADDRESS"));
			dto.setPhone(rs.getString("PHONE"));
			dto.setEmail(UnicodeUtils.convertToString(rs.getString("EMAIL")));
			dto.setComment(rs.getString("COMMENT"));
			dto.setId(rs.getString("ID"));
			dto.setStatus(rs.getString("STATUS"));
			dto.setRegDate(rs.getTimestamp("REGDATE"));
			dto.setLastDate(rs.getTimestamp("LASTDATE"));
			dto.setLeverage(rs.getInt("LEVERAGE"));
			dto.setAgentAccount(rs.getInt("AGENT_ACCOUNT"));
			dto.setTimestamp(rs.getTimestamp("TIMESTAMP"));
			dto.setBalance(rs.getDouble("BALANCE"));
			dto.setPrevMonthBalance(rs.getDouble("PREVMONTHBALANCE"));
			dto.setPrevBalance(rs.getDouble("PREVBALANCE"));
			dto.setCredit(rs.getDouble("CREDIT"));
			dto.setInterestRate(rs.getDouble("INTERESTRATE"));
			dto.setTaxes(rs.getDouble("TAXES"));
			dto.setSendReports(rs.getInt("SEND_REPORTS"));
			dto.setUserColor(rs.getInt("USER_COLOR"));
			dto.setEquity(rs.getDouble("EQUITY"));
			dto.setMargin(rs.getDouble("MARGIN"));
			dto.setMarginLevel(rs.getDouble("MARGIN_LEVEL"));
			dto.setMarginFree(rs.getDouble("MARGIN_FREE"));
			dto.setModifyTime(rs.getTimestamp("MODIFY_TIME"));
			return dto;
		}
	}

	private final class MT4TradeDTORowMapper implements RowMapper<MT4TradeDTO> {
		public MT4TradeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
			MT4TradeDTO trade = new MT4TradeDTO();
			trade.setAgentId(rs.getInt("AGENTID"));
			trade.setTicket(rs.getInt("TICKET"));
			trade.setLogin(rs.getInt("LOGIN"));
			trade.setSymbol(rs.getString("SYMBOL"));
			trade.setDigits(rs.getInt("DIGITS"));
			trade.setCmd(rs.getInt("CMD"));
			trade.setCmdLabel(CMD.parse(trade.getCmd()).toString());
			trade.setVolume(rs.getDouble("VOLUME"));
			trade.setOpenTime(rs.getTimestamp("OPEN_TIME"));
			trade.setOpenPrice(rs.getDouble("OPEN_PRICE"));
			trade.setSl(rs.getDouble("SL"));
			trade.setTp(rs.getDouble("TP"));
			trade.setCloseTime(rs.getTimestamp("CLOSE_TIME"));
			trade.setClosePrice(rs.getDouble("CLOSE_PRICE"));
			trade.setExpiration(rs.getDate("EXPIRATION"));
			trade.setConvRate1(rs.getDouble("CONV_RATE1"));
			trade.setConvRate2(rs.getDouble("CONV_RATE2"));
			trade.setCommission(rs.getDouble("COMMISSION"));
			trade.setCommissionAgent(rs.getInt("COMMISSION_AGENT"));
			trade.setSwaps(rs.getDouble("SWAPS"));
			trade.setProfit(rs.getDouble("PROFIT"));
			trade.setTaxes(rs.getDouble("TAXES"));
			trade.setComment(rs.getString("COMMENT"));
			trade.setInternalID(rs.getInt("INTERNAL_ID"));
			trade.setMarginRate(rs.getDouble("MARGIN_RATE"));
			trade.setTimestamp(rs.getLong("TIMESTAMP"));
			trade.setModifyTime(rs.getTimestamp("MODIFY_TIME"));
			return trade;
		}
	}

	private final class MT4CommissionDTORowMapper implements
			RowMapper<MT4CommissionDTO> {

		@Override
		public MT4CommissionDTO mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			MT4CommissionDTO commission = new MT4CommissionDTO();
			commission.setCommission(rs.getDouble("COMMISSION"));
			commission.setTotalVolume(rs.getDouble("VOLUME"));
			return commission;
		}

	}

	public enum TradeType {
		OPEN, CLOSE, BALANCE
	};

	public static final String BLANK_DATE = "1970-01-01 00:00:00";

	private static final Logger logger = Logger
			.getLogger(MT4RemoteService.class);

	private NamedParameterJdbcTemplate jdbcTemplate;
	private MessageSource messageSource;

	private MT4UsersQuery mt4UsersQueryByName;
	private MT4UsersQuery mt4UsersQueryByLogin;
	private MT4UsersQuery mt4UsersQueryByLoginSimple;
	private MT4TradesByLoginQuery mt4TradesByLoginQuery;
	private MT4TradesByLoginQuery mt4TradesByLoginQueryOpenTime;
	private MT4TradesByLoginQuery mt4TradesByLoginQueryCloseTime;
	private MT4TradesByLoginQuery mt4TradesByLoginQueryOpenTimeCloseTime;
	private MT4TradeCommissionQuery mt4TradeCommissionQuery;

	public MT4RemoteService(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setMt4UsersQueryByName(MT4UsersQuery mt4UsersQueryByName) {
		this.mt4UsersQueryByName = mt4UsersQueryByName;
	}

	public void setMt4UsersQueryByLogin(MT4UsersQuery mt4UsersQueryByLogin) {
		this.mt4UsersQueryByLogin = mt4UsersQueryByLogin;
	}

	public void setMt4UsersQueryByLoginSimple(
			MT4UsersQuery mt4UsersQueryByLoginSimple) {
		this.mt4UsersQueryByLoginSimple = mt4UsersQueryByLoginSimple;
	}

	public void setMt4TradesByLoginQuery(
			MT4TradesByLoginQuery mt4TradesByLoginQuery) {
		this.mt4TradesByLoginQuery = mt4TradesByLoginQuery;
	}

	public void setMt4TradesByLoginQueryOpenTime(
			MT4TradesByLoginQuery mt4TradesByLoginQueryOpenTime) {
		this.mt4TradesByLoginQueryOpenTime = mt4TradesByLoginQueryOpenTime;
	}

	public void setMt4TradesByLoginQueryCloseTime(
			MT4TradesByLoginQuery mt4TradesByLoginQueryCloseTime) {
		this.mt4TradesByLoginQueryCloseTime = mt4TradesByLoginQueryCloseTime;
	}

	public void setMt4TradesByLoginQueryOpenTimeCloseTime(
			MT4TradesByLoginQuery mt4TradesByLoginQueryOpenTimeCloseTime) {
		this.mt4TradesByLoginQueryOpenTimeCloseTime = mt4TradesByLoginQueryOpenTimeCloseTime;
	}

	public MT4TradeCommissionQuery getMt4TradeCommissionQuery() {
		return mt4TradeCommissionQuery;
	}

	public void setMt4TradeCommissionQuery(
			MT4TradeCommissionQuery mt4UserCommissionQuery) {
		this.mt4TradeCommissionQuery = mt4UserCommissionQuery;
	}

	// TODO: Find all references of getClientTrades, remove them and deprecate
	// this method.
	public List<MT4TradeDTO> getClientTrades(Integer login, Double commission,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("commission", commission);
		paramMap.put("login", login);

		boolean openTimeFilter = false;
		boolean closeTimeFilter = false;
		boolean noFilter = true;

		if (startOpenTime != null && endOpenTime != null) {
			paramMap.put("start_open_time", startOpenTime);
			paramMap.put("end_open_time", endOpenTime);

			openTimeFilter = true;
			noFilter = false;
		}
		if (startCloseTime != null && endCloseTime != null) {
			paramMap.put("start_close_time", startCloseTime);
			paramMap.put("end_close_time", endCloseTime);

			closeTimeFilter = true;
			noFilter = false;
		}

		if (noFilter) {
			return mt4TradesByLoginQuery.executeByNamedParam(paramMap);
		} else if (openTimeFilter) {
			return mt4TradesByLoginQueryOpenTime.executeByNamedParam(paramMap);
		} else if (closeTimeFilter) {
			return mt4TradesByLoginQueryCloseTime.executeByNamedParam(paramMap);
		} else if (openTimeFilter && closeTimeFilter) {
			return mt4TradesByLoginQueryOpenTimeCloseTime
					.executeByNamedParam(paramMap);
		}
		return null;
	}

	/**
	 * <p>
	 * Returns all agent's clients trades information. This method may be moved
	 * to it's own MappingSqlQuery class if Spring supports variable
	 * placeholders in future versions.
	 * </p>
	 * <code>
	 * Quote from Spring documentation:
	 * "This variable list is not directly supported for prepared statements by the JDBC standard; 
	 * you cannot declare a variable number of placeholders"
	 * </code>
	 * 
	 * <p>
	 * This method may impact system's performance because of the dynamic
	 * construction of query on each call.
	 * </p>
	 * 
	 * @param clientLogins
	 * @param commission
	 * @param startOpenTime
	 * @param endOpenTime
	 * @param startCloseTime
	 * @param endCloseTime
	 * @return
	 * @throws MT4RemoteServiceException
	 */
	public List<MT4TradeDTO> getClientsTrades(List<Integer> clients,
			Double commission, TradeType tradeType, Date startOpenTime,
			Date endOpenTime, Date startCloseTime, Date endCloseTime)
			throws MT4RemoteServiceException {

		if (clients == null || clients.size() == 0) {
			return new ArrayList<MT4TradeDTO>();
		}

		SimpleDateFormat dateFormmater = DateFormatter
				.getDateFormatter(Type.MYSQL);

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("NULL AS `AGENTID`, ");
		sql.append("`t`.TICKET, ");
		sql.append("`t`.LOGIN, ");
		sql.append("`t`.SYMBOL, ");
		sql.append("`t`.DIGITS, ");
		sql.append("`t`.CMD, ");
		sql.append("`t`.VOLUME, ");
		sql.append("`t`.OPEN_TIME, ");
		sql.append("`t`.OPEN_PRICE, ");
		sql.append("`t`.SL, ");
		sql.append("`t`.TP, ");
		sql.append("`t`.CLOSE_TIME, ");
		sql.append("`t`.CLOSE_PRICE, ");
		sql.append("`t`.EXPIRATION, ");
		sql.append("`t`.CONV_RATE1, ");
		sql.append("`t`.CONV_RATE2, ");
		sql.append("`t`.COMMISSION_AGENT, ");
		sql.append("`t`.SWAPS, ");
		sql.append("`t`.PROFIT, ");
		sql.append("`t`.TAXES, ");
		sql.append("`t`.COMMENT, ");
		sql.append("`t`.INTERNAL_ID, ");
		sql.append("`t`.MARGIN_RATE, ");
		sql.append("`t`.TIMESTAMP, ");
		sql.append("`t`.MODIFY_TIME, ");
		sql.append("((`t`.VOLUME / 100) * :commission) AS `COMMISSION`  ");
		sql.append("from `mt4_trades` t ");
		sql.append("inner join `mt4_users` u on t.LOGIN = u.LOGIN ");
		sql.append("where 1=1 ");
		sql.append("and u.LOGIN in (:login) ");

		// sql.append("and `t`.cmd in (0,1) ");

		paramMap.put("commission", commission);
		paramMap.put("login", clients);

		if (tradeType == TradeType.OPEN && startOpenTime != null
				&& endOpenTime != null) {
			sql.append("and t.close_time = '").append(BLANK_DATE).append("' ");
			sql.append("and (t.open_time >= :start_open_time ");
			sql.append("and t.open_time <= :end_open_time) ");
			sql.append("and `t`.cmd not in (6) ");

			paramMap.put("start_open_time", dateFormmater.format(startOpenTime));
			paramMap.put("end_open_time", dateFormmater.format(endOpenTime));

		} else if (tradeType == TradeType.CLOSE && startCloseTime != null
				&& endCloseTime != null) {
			sql.append("and (t.open_time != '").append(BLANK_DATE)
					.append("' and t.close_time != '").append(BLANK_DATE)
					.append("') ");
			sql.append("and (t.close_time >= :start_close_time ");
			sql.append("and t.close_time <= :end_close_time) ");
			sql.append("and `t`.cmd not in (6) ");

			paramMap.put("start_close_time",
					dateFormmater.format(startCloseTime));
			paramMap.put("end_close_time", dateFormmater.format(endCloseTime));
		} else if (tradeType == TradeType.BALANCE && startCloseTime != null
				&& endCloseTime != null) {
			sql.append("and (t.close_time >= :start_close_time ");
			sql.append("and t.close_time <= :end_close_time) ");
			sql.append("and `t`.cmd in (6) ");

			paramMap.put("start_close_time",
					dateFormmater.format(startCloseTime));
			paramMap.put("end_close_time", dateFormmater.format(endCloseTime));

		} else {
			sql.append("and 1 = 2 "); // make the query return blank recordset.
		}

		sql.append("order by u.LOGIN ");

		logger.info(sql);
		logger.info(paramMap);

		return jdbcTemplate.query(sql.toString(), paramMap,
				new MT4TradeDTORowMapper());
	}

	public List<MT4TradeDTO> getClientsTrades(List<Integer> clients,
			Double commission, TradeType tradeType, Date startOpenTime,
			Date endOpenTime, Date startCloseTime, Date endCloseTime,
			Integer offset, Integer rowcount, String search,
			Integer sortColumn, String sortDirection) {

		if (clients == null || clients.size() == 0) {
			return new ArrayList<MT4TradeDTO>();
		}

		SimpleDateFormat dateFormmater = DateFormatter
				.getDateFormatter(Type.MYSQL);

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("NULL AS `AGENTID`, ");
		sql.append("`t`.TICKET, ");
		sql.append("`t`.LOGIN, ");
		sql.append("`t`.SYMBOL, ");
		sql.append("`t`.DIGITS, ");
		sql.append("`t`.CMD, ");
		sql.append("`t`.VOLUME, ");
		sql.append("`t`.OPEN_TIME, ");
		sql.append("`t`.OPEN_PRICE, ");
		sql.append("`t`.SL, ");
		sql.append("`t`.TP, ");
		sql.append("`t`.CLOSE_TIME, ");
		sql.append("`t`.CLOSE_PRICE, ");
		sql.append("`t`.EXPIRATION, ");
		sql.append("`t`.CONV_RATE1, ");
		sql.append("`t`.CONV_RATE2, ");
		sql.append("`t`.COMMISSION_AGENT, ");
		sql.append("`t`.SWAPS, ");
		sql.append("`t`.PROFIT, ");
		sql.append("`t`.TAXES, ");
		sql.append("`t`.COMMENT, ");
		sql.append("`t`.INTERNAL_ID, ");
		sql.append("`t`.MARGIN_RATE, ");
		sql.append("`t`.TIMESTAMP, ");
		sql.append("`t`.MODIFY_TIME, ");
		sql.append("((`t`.VOLUME / 100) * :commission) AS `COMMISSION`  ");
		sql.append("from `mt4_trades` t ");
		sql.append("inner join `mt4_users` u on t.LOGIN = u.LOGIN ");
		sql.append("where 1=1 ");
		sql.append("and u.LOGIN in (:login) ");

		// sql.append("and `t`.cmd in (0,1) ");

		paramMap.put("commission", commission);
		paramMap.put("login", clients);

		if (tradeType == TradeType.OPEN && startOpenTime != null
				&& endOpenTime != null) {
			sql.append("and t.close_time = '").append(BLANK_DATE).append("' ");
			sql.append("and (t.open_time >= :start_open_time ");
			sql.append("and t.open_time <= :end_open_time) ");
			sql.append("and `t`.cmd not in (6) ");

			paramMap.put("start_open_time", dateFormmater.format(startOpenTime));
			paramMap.put("end_open_time", dateFormmater.format(endOpenTime));

		} else if (tradeType == TradeType.CLOSE && startCloseTime != null
				&& endCloseTime != null) {
			sql.append("and (t.open_time != '").append(BLANK_DATE)
					.append("' and t.close_time != '").append(BLANK_DATE)
					.append("') ");
			sql.append("and (t.close_time >= :start_close_time ");
			sql.append("and t.close_time <= :end_close_time) ");
			sql.append("and `t`.cmd not in (6) ");

			paramMap.put("start_close_time",
					dateFormmater.format(startCloseTime));
			paramMap.put("end_close_time", dateFormmater.format(endCloseTime));
		} else if (tradeType == TradeType.BALANCE && startCloseTime != null
				&& endCloseTime != null) {
			sql.append("and (t.close_time >= :start_close_time ");
			sql.append("and t.close_time <= :end_close_time) ");
			sql.append("and `t`.cmd in (6) ");

			paramMap.put("start_close_time",
					dateFormmater.format(startCloseTime));
			paramMap.put("end_close_time", dateFormmater.format(endCloseTime));

		} else {
			sql.append("and 1 = 2 "); // make the query return blank recordset.
		}

		if (StringUtils.hasLength(search)) {
			sql.append("AND (");
			sql.append("`t`.`LOGIN` LIKE :search ");
			sql.append("OR `t`.`TICKET` LIKE :search ");
			sql.append(") ");

			paramMap.put("search", new StringBuilder("%").append(search)
					.append("%"));
		}

		// + 1 because first column agentid is always null.
		if (sortColumn == null) {
			sortColumn = 0;
		}

		paginateSql(offset, rowcount, sortColumn.intValue() + 2, sortDirection,
				paramMap, sql);

		logger.info(sql);
		logger.info(paramMap);

		return jdbcTemplate.query(sql.toString(), paramMap,
				new MT4TradeDTORowMapper());
	}

	public Integer getClientsTradesCount(List<Integer> clients,
			Double commission, TradeType tradeType, Date startOpenTime,
			Date endOpenTime, Date startCloseTime, Date endCloseTime,
			String search) {
		SimpleDateFormat dateFormmater = DateFormatter
				.getDateFormatter(Type.MYSQL);

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("COUNT(`t`.TICKET) AS `COUNT` ");
		sql.append("from `mt4_trades` t ");
		sql.append("inner join `mt4_users` u on t.LOGIN = u.LOGIN ");
		sql.append("where 1=1 ");
		sql.append("and u.LOGIN in (:login) ");

		paramMap.put("commission", commission);
		paramMap.put("login", clients);

		if (tradeType == TradeType.OPEN && startOpenTime != null
				&& endOpenTime != null) {
			sql.append("and t.close_time = '").append(BLANK_DATE).append("' ");
			sql.append("and (t.open_time >= :start_open_time ");
			sql.append("and t.open_time <= :end_open_time) ");
			sql.append("and `t`.cmd not in (6) ");

			paramMap.put("start_open_time", dateFormmater.format(startOpenTime));
			paramMap.put("end_open_time", dateFormmater.format(endOpenTime));

		} else if (tradeType == TradeType.CLOSE && startCloseTime != null
				&& endCloseTime != null) {
			sql.append("and (t.open_time != '").append(BLANK_DATE)
					.append("' and t.close_time != '").append(BLANK_DATE)
					.append("') ");
			sql.append("and (t.close_time >= :start_close_time ");
			sql.append("and t.close_time <= :end_close_time) ");
			sql.append("and `t`.cmd not in (6) ");

			paramMap.put("start_close_time",
					dateFormmater.format(startCloseTime));
			paramMap.put("end_close_time", dateFormmater.format(endCloseTime));
		} else if (tradeType == TradeType.BALANCE && startCloseTime != null
				&& endCloseTime != null) {
			sql.append("and (t.close_time >= :start_close_time ");
			sql.append("and t.close_time <= :end_close_time) ");
			sql.append("and `t`.cmd in (6) ");

			paramMap.put("start_close_time",
					dateFormmater.format(startCloseTime));
			paramMap.put("end_close_time", dateFormmater.format(endCloseTime));

		} else {
			sql.append("and 1 = 2 "); // make the query return blank recordset.
		}

		if (StringUtils.hasLength(search)) {
			sql.append("AND (");
			sql.append("`t`.`LOGIN` LIKE :search ");
			sql.append("OR `t`.`TICKET` LIKE :search ");
			sql.append(") ");

			paramMap.put("search", new StringBuilder("%").append(search)
					.append("%"));
		}

		logger.info(sql);
		logger.info(paramMap);

		return jdbcTemplate.queryForInt(sql.toString(), paramMap);
	}

	public MT4CommissionDTO getVolumeCommission(List<Integer> clients,
			Double commission, TradeType tradeType, Date startOpenTime,
			Date endOpenTime, Date startCloseTime, Date endCloseTime) {
		SimpleDateFormat dateFormmater = DateFormatter
				.getDateFormatter(Type.MYSQL);

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("SUM(`t`.VOLUME) AS `VOLUME`, ");
		sql.append("SUM((`t`.VOLUME / 100) * :commission) AS `COMMISSION`  ");
		sql.append("from `mt4_trades` t ");
		sql.append("inner join `mt4_users` u on t.LOGIN = u.LOGIN ");
		sql.append("where 1=1 ");
		sql.append("and u.LOGIN in (:login) ");

		// sql.append("and `t`.cmd in (0,1) ");

		paramMap.put("commission", commission);
		paramMap.put("login", clients);

		if (tradeType == TradeType.OPEN && startOpenTime != null
				&& endOpenTime != null) {
			sql.append("and t.close_time = '").append(BLANK_DATE).append("' ");
			sql.append("and (t.open_time >= :start_open_time ");
			sql.append("and t.open_time <= :end_open_time) ");
			sql.append("and `t`.cmd not in (6) ");

			paramMap.put("start_open_time", dateFormmater.format(startOpenTime));
			paramMap.put("end_open_time", dateFormmater.format(endOpenTime));

		} else if (tradeType == TradeType.CLOSE && startCloseTime != null
				&& endCloseTime != null) {
			sql.append("and (t.open_time != '").append(BLANK_DATE)
					.append("' and t.close_time != '").append(BLANK_DATE)
					.append("') ");
			sql.append("and (t.close_time >= :start_close_time ");
			sql.append("and t.close_time <= :end_close_time) ");
			sql.append("and `t`.cmd not in (6) ");

			paramMap.put("start_close_time",
					dateFormmater.format(startCloseTime));
			paramMap.put("end_close_time", dateFormmater.format(endCloseTime));
		} else if (tradeType == TradeType.BALANCE && startCloseTime != null
				&& endCloseTime != null) {
			sql.append("and (t.close_time >= :start_close_time ");
			sql.append("and t.close_time <= :end_close_time) ");
			sql.append("and `t`.cmd in (6) ");

			paramMap.put("start_close_time",
					dateFormmater.format(startCloseTime));
			paramMap.put("end_close_time", dateFormmater.format(endCloseTime));

		} else {
			sql.append("and 1 = 2 "); // make the query return blank recordset.
		}

		logger.info(sql);
		logger.info(paramMap);

		return jdbcTemplate.queryForObject(sql.toString(), paramMap,
				new MT4CommissionDTORowMapper());
	}

	/**
	 * <p>
	 * Returns all downlines's clients' trade. From root node agent to leaf node
	 * agent.
	 * </p>
	 * 
	 * @param downlines
	 * @param startOpenTime
	 * @param endOpenTime
	 * @param startCloseTime
	 * @param endCloseTime
	 * @return
	 */
	public List<MT4TradeDTO> getDownlinesClientsTrades(
			List<Map<String, Object>> downlines, List<Integer> cmds,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime) {

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();

		constructDownlinesClientsTradesSql(downlines, cmds, startOpenTime,
				endOpenTime, startCloseTime, endCloseTime, paramMap, sql, null);

		logger.info(sql);
		logger.info(paramMap);

		if (StringUtils.hasLength(sql.toString())) {
			return jdbcTemplate.query(sql.toString(), paramMap,
					new MT4TradeDTORowMapper());
		} else {
			return new ArrayList<MT4TradeDTO>();
		}
	}

	public List<MT4TradeDTO> getDownlinesClientsTrades(
			List<Map<String, Object>> downlines, List<Integer> cmds,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime, Integer offset, Integer rowcount, String search,
			Integer sortColumn, String sortDirection) {

		if (sortColumn == null) {
			sortColumn = 1;
		}

		PaginationSettings paginationSettings = new PaginationSettings();
		paginationSettings.setOffset(offset);
		paginationSettings.setRowcount(rowcount);
		paginationSettings.setSearch(search);
		paginationSettings.setSortColumn(sortColumn + 1);
		paginationSettings.setSortDirection(sortDirection);

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		constructDownlinesClientsTradesSql(downlines, cmds, startOpenTime,
				endOpenTime, startCloseTime, endCloseTime, paramMap, sql,
				paginationSettings);

		logger.info(sql);
		logger.info(paramMap);

		if (StringUtils.hasLength(sql.toString())) {
			return jdbcTemplate.query(sql.toString(), paramMap,
					new MT4TradeDTORowMapper());
		} else {
			return new ArrayList<MT4TradeDTO>();
		}
	}

	public Integer getDownlinesClientsTradesCount(
			List<Map<String, Object>> downlines, List<Integer> cmds,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime, String search) {

		PaginationSettings paginationSettings = new PaginationSettings();
		paginationSettings.setSearch(search);

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		constructDownlinesClientsTradesCountSql(downlines, cmds, startOpenTime,
				endOpenTime, startCloseTime, endCloseTime, paramMap, sql,
				paginationSettings);

		logger.info(sql);
		logger.info(paramMap);

		return jdbcTemplate.queryForInt(sql.toString(), paramMap);
	}

	public MT4CommissionDTO getDownlinesVolumeCommission(
			List<Map<String, Object>> downlines, List<Integer> cmds,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime, String search) {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		constructDownlinesClientsTradesVolumeCommissionSql(downlines, cmds,
				startOpenTime, endOpenTime, startCloseTime, endCloseTime,
				paramMap, sql);

		logger.info(sql);
		logger.info(paramMap);

		if (StringUtils.hasLength(sql.toString())) {
			return jdbcTemplate.queryForObject(sql.toString(), paramMap,
					new MT4CommissionDTORowMapper());
		} else {
			return null;
		}
	}

	public MT4UserDTO getUserByMT4Login(Integer mt4Login) {
		return mt4UsersQueryByLogin._execute(mt4Login, 0, 1);
	}

	public MT4UserDTO getUserByMT4LoginSimple(Integer mt4Login) {
		return mt4UsersQueryByLoginSimple._execute(mt4Login, 0, 1);
	}

	public List<MT4UserDTO> getUsersByMT4Logins(List<Integer> users) {

		if (users == null || users.size() == 0) {
			return new ArrayList<MT4UserDTO>();
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("`LOGIN`, `GROUP`, `ENABLE`, `ENABLE_CHANGE_PASS`, ");
		sql.append("`ENABLE_READONLY`, `PASSWORD_PHONE`,  `NAME`,  ");
		sql.append("`COUNTRY`, `CITY`, `STATE`, ");
		sql.append("`ZIPCODE`, `ADDRESS`, `PHONE`, `EMAIL`, ");
		sql.append("`COMMENT`, `ID`, `STATUS`, `REGDATE`, `LASTDATE`, ");
		sql.append("`LEVERAGE`, `AGENT_ACCOUNT`, ");
		sql.append("CASE `TIMESTAMP` WHEN 0 THEN NULL ELSE `TIMESTAMP` END `TIMESTAMP`,");
		sql.append("`BALANCE`, `PREVMONTHBALANCE`, `PREVBALANCE`, ");
		sql.append("`CREDIT`,`INTERESTRATE`, `TAXES`, `SEND_REPORTS`, ");
		sql.append("`USER_COLOR`, `EQUITY`, `MARGIN`, `MARGIN_LEVEL`, ");
		sql.append("`MARGIN_FREE`, `MODIFY_TIME`");
		sql.append("from `mt4_users` ");
		sql.append("where ");
		sql.append("`LOGIN` IN (");
		for (int i = 0; i < users.size(); i++) {
			Integer login = users.get(i);
			sql.append(":login_").append(login);
			paramMap.put("login_" + login, login);
			if (i != users.size() - 1) {
				sql.append(", ");
			}
		}
		sql.append(") ");

		logger.info(sql);
		logger.info(paramMap);

		return jdbcTemplate.query(sql.toString(), paramMap,
				new MT4TUserDTORowMapper());
	}

	public List<MT4UserDTO> getUsersByMT4Logins(String search) {
		if (search == null || !StringUtils.hasLength(search)) {
			return new ArrayList<MT4UserDTO>();
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("`LOGIN`, `GROUP`, `ENABLE`, `ENABLE_CHANGE_PASS`, ");
		sql.append("`ENABLE_READONLY`, `PASSWORD_PHONE`,  `NAME`,  ");
		sql.append("`COUNTRY`, `CITY`, `STATE`, ");
		sql.append("`ZIPCODE`, `ADDRESS`, `PHONE`, `EMAIL`, ");
		sql.append("`COMMENT`, `ID`, `STATUS`, `REGDATE`, `LASTDATE`, ");
		sql.append("`LEVERAGE`, `AGENT_ACCOUNT`, ");
		sql.append("CASE `TIMESTAMP` WHEN 0 THEN NULL ELSE `TIMESTAMP` END `TIMESTAMP`,");
		sql.append("`BALANCE`, `PREVMONTHBALANCE`, `PREVBALANCE`, ");
		sql.append("`CREDIT`,`INTERESTRATE`, `TAXES`, `SEND_REPORTS`, ");
		sql.append("`USER_COLOR`, `EQUITY`, `MARGIN`, `MARGIN_LEVEL`, ");
		sql.append("`MARGIN_FREE`, `MODIFY_TIME` ");
		sql.append("from `mt4_users` ");
		sql.append("where ");
		sql.append("`LOGIN` LIKE :search ");

		paramMap.put("search", new StringBuilder(search).append("%"));

		logger.info(sql);
		logger.info(paramMap);

		return jdbcTemplate.query(sql.toString(), paramMap,
				new MT4TUserDTORowMapper());
	}

	public List<MT4UserDTO> getUsersByName(String nameSearchTerms) {
		if (nameSearchTerms != null && nameSearchTerms.length() > 3) {
			return mt4UsersQueryByName._execute(
					new StringBuilder("%").append(nameSearchTerms).append("%")
							.toString(), 0, 10);
		} else {
			return new ArrayList<MT4UserDTO>();
		}
	}

	public List<MT4UserDTO> getUsers(Integer offset, Integer rowcount,
			String search, Integer sortColumn, String sortDirection) {

		boolean searchHasLength = search != null
				&& StringUtils.hasLength(search);

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("`LOGIN`, `GROUP`, `ENABLE`, `ENABLE_CHANGE_PASS`, ");
		sql.append("`ENABLE_READONLY`, `PASSWORD_PHONE`,  `NAME`,  ");
		sql.append("`COUNTRY`, `CITY`, `STATE`, ");
		sql.append("`ZIPCODE`, `ADDRESS`, `PHONE`, `EMAIL`, ");
		sql.append("`COMMENT`, `ID`, `STATUS`, `REGDATE`, `LASTDATE`, ");
		sql.append("`LEVERAGE`, `AGENT_ACCOUNT`, ");
		sql.append("CASE `TIMESTAMP` WHEN 0 THEN NULL ELSE `TIMESTAMP` END `TIMESTAMP`,");
		sql.append("`BALANCE`, `PREVMONTHBALANCE`, `PREVBALANCE`, ");
		sql.append("`CREDIT`,`INTERESTRATE`, `TAXES`, `SEND_REPORTS`, ");
		sql.append("`USER_COLOR`, `EQUITY`, `MARGIN`, `MARGIN_LEVEL`, `MARGIN_FREE`, `MODIFY_TIME` ");
		sql.append("from `mt4_users` ");
		sql.append("where 1=1 ");
		if (searchHasLength) {
			sql.append("AND (");
			sql.append("`LOGIN` LIKE :search ");
			sql.append("OR `GROUP` LIKE :search ");
			sql.append("OR `NAME` LIKE :search ");
			sql.append("OR `COUNTRY` LIKE :search ");
			sql.append("OR `CITY` LIKE :search ");
			sql.append("OR `STATE` LIKE :search ");
			sql.append("OR `ZIPCODE` LIKE :search ");
			sql.append("OR `ADDRESS` LIKE :search ");
			sql.append("OR `PHONE` LIKE :search ");
			sql.append("OR `EMAIL` LIKE :search ");
			sql.append("OR `COMMENT` LIKE :search ");
			sql.append(") ");

			paramMap.put("search", new StringBuilder("%").append(search)
					.append("%"));
		}

		paginateSql(offset, rowcount, sortColumn, sortDirection, paramMap, sql);

		logger.info(sql);
		logger.info(paramMap);

		return jdbcTemplate.query(sql.toString(), paramMap,
				new MT4TUserDTORowMapper());

	}

	public Integer getUsersCount(String search) {

		boolean searchHasLength = search != null
				&& StringUtils.hasLength(search);

		Map<String, Object> paramMap = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("COUNT(`LOGIN`) ");
		sql.append("from `mt4_users` ");
		sql.append("where 1=1 ");
		if (searchHasLength) {
			sql.append("AND (");
			sql.append("`LOGIN` LIKE :search ");
			sql.append("OR `GROUP` LIKE :search ");
			sql.append("OR `NAME` LIKE :search ");
			sql.append("OR `COUNTRY` LIKE :search ");
			sql.append("OR `CITY` LIKE :search ");
			sql.append("OR `STATE` LIKE :search ");
			sql.append("OR `ZIPCODE` LIKE :search ");
			sql.append("OR `ADDRESS` LIKE :search ");
			sql.append("OR `PHONE` LIKE :search ");
			sql.append("OR `EMAIL` LIKE :search ");
			sql.append("OR `COMMENT` LIKE :search ");
			sql.append(") ");
		}

		if (searchHasLength) {
			paramMap.put("search", new StringBuilder("%").append(search)
					.append("%"));
		}

		return jdbcTemplate.queryForInt(sql.toString(), paramMap);
	}

	public List<MT4TradeDTO> getTradesWithCommission(Integer login,
			Double commission, Date startRangeCloseTime, Date endRangeCloseTime) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("login", login);
		paramMap.put("commission", commission);
		paramMap.put("start_close_time", startRangeCloseTime);
		paramMap.put("end_close_time", endRangeCloseTime);
		return mt4TradeCommissionQuery.executeByNamedParam(paramMap);
	}

	public List<MT4CommissionDTO> getCommissions(List<Integer> clients,
			Double commission, Date startOpenTime, Date endOpenTime,
			Date startCloseTime, Date endCloseTime)
			throws MT4RemoteServiceException {

		if (clients == null || clients.size() == 0) {
			throw new MT4RemoteServiceException(messageSource.getMessage(
					"mt4agents.exception.remote.blankclientslist", null,
					Locale.US));
		}

		SimpleDateFormat dateFormatter = DateFormatter
				.getDateFormatter(Type.MYSQL);

		StringBuilder sql = new StringBuilder();
		sql.append("select t.login, u.name, u.email, ");
		sql.append("sum(t.volume) AS `totalVolume`, ");
		sql.append("sum(t.VOLUME / 100 * R.commission) as `commission` ");
		sql.append("from mt4_trades t ");
		sql.append("inner join (");
		for (int i = 0; i < clients.size(); i++) {
			Integer login = clients.get(i);
			sql.append("select ").append(login).append(" as `login`, ");
			sql.append(commission).append(" as `commission` ");
			if (i != clients.size() - 1) {
				sql.append("union ");
			}
		}
		sql.append(") as R on R.login = t.LOGIN ");
		sql.append("inner join mt4_users u on t.login = u.login ");
		sql.append("where 1 = 1 ");
		if (startCloseTime != null && endCloseTime != null) {
			sql.append("and t.close_time >= :start_close_time ");
			sql.append("and t.close_time <= :end_close_time ");
		}
		if (startOpenTime != null && endOpenTime != null) {
			sql.append("and t.open_time >= :start_open_time ");
			sql.append("and t.open_time <= :end_open_time ");
		}
		sql.append("and t.cmd in (0,1) ");
		sql.append("and t.login in (");
		for (int i = 0; i < clients.size(); i++) {
			Integer login = clients.get(i);
			sql.append(login);
			if (i != clients.size() - 1) {
				sql.append(", ");
			}
		}
		sql.append(") ");
		sql.append("group by t.login ");
		sql.append("order by commission ");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (startCloseTime != null && endCloseTime != null) {
			paramMap.put("start_close_time",
					dateFormatter.format(startCloseTime));
			paramMap.put("end_close_time", dateFormatter.format(endCloseTime));
		}
		if (startOpenTime != null && endOpenTime != null) {
			paramMap.put("start_open_time", dateFormatter.format(startOpenTime));
			paramMap.put("end_open_time", dateFormatter.format(endOpenTime));
		}

		logger.info(sql);
		logger.info(paramMap);

		return jdbcTemplate.query(sql.toString(), paramMap,
				new RowMapper<MT4CommissionDTO>() {
					public MT4CommissionDTO mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						MT4CommissionDTO commission = new MT4CommissionDTO();
						commission.setLogin(rs.getInt("login"));
						commission.setName(UnicodeUtils.convertToString(rs
								.getString("name")));
						commission.setEmail(rs.getString("email"));
						commission.setTotalVolume(rs.getDouble("totalVolume"));
						commission.setCommission(new Double(NumberFormatter
								.parseDouble(NumberFormatter.Type.CURRENCY,
										rs.getDouble("commission"))));
						return commission;
					}
				});
	}

	private void paginateSql(Integer offset, Integer rowcount,
			Integer sortColumn, String sortDirection,
			Map<String, Object> paramMap, StringBuilder sql) {
		sql.append("ORDER BY :sortColumn ");
		// somewhat prevent sql injection.
		// TODO: Research on better way to replace direction param.
		if (sortDirection == null || sortDirection.equals("asc")) {
			sql.append("asc ");
		} else if (sortDirection.equals("desc")) {
			sql.append("desc ");
		}
		sql.append("LIMIT :offset,:rowcount ");

		if (sortColumn == null || sortColumn == 0) {
			sortColumn = 1;
		}

		paramMap.put("offset", offset);
		paramMap.put("rowcount", rowcount);
		paramMap.put("sortColumn", sortColumn);
	}

	private void constructDownlinesClientsTradesVolumeCommissionSql(
			List<Map<String, Object>> downlines, List<Integer> cmds,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime, Map<String, Object> paramMap, StringBuilder sql) {

		SimpleDateFormat dateFormatter = DateFormatter
				.getDateFormatter(Type.MYSQL);

		sql.append("SELECT SUM(`T`.`VOLUME`) AS VOLUME, SUM(`T`.`COMMISSION`) AS COMMISSION FROM (");

		for (int i = 0; i < downlines.size(); i++) {

			Map<String, Object> agentsAndClients = downlines.get(i);

			Integer agentId = (Integer) agentsAndClients.get("agentId");
			Double commission = (Double) agentsAndClients.get("commission");
			@SuppressWarnings("unchecked")
			List<Integer> clients = (List<Integer>) agentsAndClients
					.get("clients");

			// if agent has no clients, don't bother. move to next.
			if (clients.size() == 0) {
				continue;
			}

			sql.append("select ");
			sql.append("`t`.VOLUME, ");
			sql.append("(`t`.VOLUME / 100) * :commission_").append(agentId)
					.append(" AS `COMMISSION`  ");
			sql.append("from `mt4_trades` t ");
			sql.append("inner join `mt4_users` u on t.LOGIN = u.LOGIN ");
			sql.append("where 1=1 ");
			sql.append("and u.LOGIN in (:login_").append(agentId).append(") ");
			// sql.append("and `t`.cmd in (0,1) ");
			// filter by cmds only if not null.
			if (cmds != null) {
				sql.append("and `t`.cmd in (:cmds) ");
				paramMap.put("cmds",
						StringUtils.collectionToCommaDelimitedString(cmds));
			} else {
				sql.append("and `t`.cmd not in (6) ");
			}

			paramMap.put("login_" + agentId, clients);
			paramMap.put("commission_" + agentId, commission);

			if (startOpenTime != null && endOpenTime != null) {
				sql.append("and (t.open_time >= :start_open_time_")
						.append(agentId).append(" ");
				sql.append("and t.open_time <= :end_open_time_")
						.append(agentId).append(") ");

				paramMap.put("start_open_time_" + agentId,
						dateFormatter.format(startOpenTime));
				paramMap.put("end_open_time_" + agentId,
						dateFormatter.format(endOpenTime));
			}
			if (startCloseTime != null && endCloseTime != null) {
				sql.append("and (t.close_time >= :start_close_time_")
						.append(agentId).append(" ");
				sql.append("and t.close_time <= :end_close_time_")
						.append(agentId).append(") ");

				paramMap.put("start_close_time_" + agentId,
						dateFormatter.format(startCloseTime));
				paramMap.put("end_close_time_" + agentId,
						dateFormatter.format(endCloseTime));
			}

			if (i != downlines.size() - 1) {
				sql.append("union all ");
			}
		}
		sql.append(") AS T");
	}

	private void constructDownlinesClientsTradesCountSql(
			List<Map<String, Object>> downlines, List<Integer> cmds,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime, Map<String, Object> paramMap, StringBuilder sql,
			PaginationSettings paginationSettings) {

		SimpleDateFormat dateFormatter = DateFormatter
				.getDateFormatter(Type.MYSQL);

		sql.append("SELECT SUM(`T`.`COUNT`) FROM (");

		for (int i = 0; i < downlines.size(); i++) {

			Map<String, Object> agentsAndClients = downlines.get(i);

			Integer agentId = (Integer) agentsAndClients.get("agentId");
			@SuppressWarnings("unchecked")
			List<Integer> clients = (List<Integer>) agentsAndClients
					.get("clients");

			// if agent has no clients, don't bother. move to next.
			if (clients.size() == 0) {
				continue;
			}

			sql.append("select ");
			sql.append("COUNT(`t`.`TICKET`) AS `COUNT` ");
			sql.append("from `mt4_trades` t ");
			sql.append("inner join `mt4_users` u on t.LOGIN = u.LOGIN ");
			sql.append("where 1=1 ");
			sql.append("and u.LOGIN in (:login_").append(agentId).append(") ");
			// sql.append("and `t`.cmd in (0,1) ");
			// filter by cmds only if not null.
			if (cmds != null) {
				sql.append("and `t`.cmd in (:cmds) ");
				paramMap.put("cmds",
						StringUtils.collectionToCommaDelimitedString(cmds));
			} else {
				sql.append("and `t`.cmd not in (6) ");
			}

			if (paginationSettings != null
					&& StringUtils.hasLength(paginationSettings.getSearch())) {
				sql.append("AND (");
				sql.append("`t`.`LOGIN` LIKE :search ");
				sql.append("OR `t`.`TICKET` LIKE :search ");
				sql.append(") ");

				paramMap.put(
						"search",
						new StringBuilder("%").append(
								paginationSettings.getSearch()).append("%"));
			}

			paramMap.put("login_" + agentId, clients);

			if (startOpenTime != null && endOpenTime != null) {
				sql.append("and (t.open_time >= :start_open_time_")
						.append(agentId).append(" ");
				sql.append("and t.open_time <= :end_open_time_")
						.append(agentId).append(") ");

				paramMap.put("start_open_time_" + agentId,
						dateFormatter.format(startOpenTime));
				paramMap.put("end_open_time_" + agentId,
						dateFormatter.format(endOpenTime));
			}
			if (startCloseTime != null && endCloseTime != null) {
				sql.append("and (t.close_time >= :start_close_time_")
						.append(agentId).append(" ");
				sql.append("and t.close_time <= :end_close_time_")
						.append(agentId).append(") ");

				paramMap.put("start_close_time_" + agentId,
						dateFormatter.format(startCloseTime));
				paramMap.put("end_close_time_" + agentId,
						dateFormatter.format(endCloseTime));
			}

			if (i != downlines.size() - 1) {
				sql.append("union all ");
			}
		}
		sql.append(") AS T");
	}

	private void constructDownlinesClientsTradesSql(
			List<Map<String, Object>> downlines, List<Integer> cmds,
			Date startOpenTime, Date endOpenTime, Date startCloseTime,
			Date endCloseTime, Map<String, Object> paramMap, StringBuilder sql,
			PaginationSettings paginationSettings) {

		SimpleDateFormat dateFormatter = DateFormatter
				.getDateFormatter(Type.MYSQL);

		for (int i = 0; i < downlines.size(); i++) {

			Map<String, Object> agentsAndClients = downlines.get(i);

			Integer agentId = (Integer) agentsAndClients.get("agentId");
			Double commission = (Double) agentsAndClients.get("commission");
			@SuppressWarnings("unchecked")
			List<Integer> clients = (List<Integer>) agentsAndClients
					.get("clients");

			// if agent has no clients, don't bother. move to next.
			if (clients.size() == 0) {
				continue;
			}

			sql.append("select ");
			sql.append(agentId).append(" AS `AGENTID`, ");
			sql.append("`t`.TICKET, ");
			sql.append("`t`.LOGIN, ");
			sql.append("`t`.SYMBOL, ");
			sql.append("`t`.DIGITS, ");
			sql.append("`t`.CMD, ");
			sql.append("`t`.VOLUME, ");
			sql.append("`t`.OPEN_TIME, ");
			sql.append("`t`.OPEN_PRICE, ");
			sql.append("`t`.SL, ");
			sql.append("`t`.TP, ");
			sql.append("`t`.CLOSE_TIME, ");
			sql.append("`t`.CLOSE_PRICE, ");
			sql.append("`t`.EXPIRATION, ");
			sql.append("`t`.CONV_RATE1, ");
			sql.append("`t`.CONV_RATE2, ");
			// sql.append("`t`.COMMISSION, ");
			sql.append("`t`.COMMISSION_AGENT, ");
			sql.append("`t`.SWAPS, ");
			sql.append("`t`.PROFIT, ");
			sql.append("`t`.TAXES, ");
			sql.append("`t`.COMMENT, ");
			sql.append("`t`.INTERNAL_ID, ");
			sql.append("`t`.MARGIN_RATE, ");
			sql.append("`t`.TIMESTAMP, ");
			sql.append("`t`.MODIFY_TIME, ");
			sql.append("(`t`.VOLUME / 100) * :commission_").append(agentId)
					.append(" AS `COMMISSION`  ");
			sql.append("from `mt4_trades` t ");
			sql.append("inner join `mt4_users` u on t.LOGIN = u.LOGIN ");
			sql.append("where 1=1 ");
			sql.append("and u.LOGIN in (:login_").append(agentId).append(") ");
			// sql.append("and `t`.cmd in (0,1) ");
			// filter by cmds only if not null.
			if (cmds != null) {
				sql.append("and `t`.cmd in (:cmds) ");
				paramMap.put("cmds",
						StringUtils.collectionToCommaDelimitedString(cmds));
			} else {
				sql.append("and `t`.cmd not in (6) ");
			}

			if (paginationSettings != null
					&& StringUtils.hasLength(paginationSettings.getSearch())) {
				sql.append("AND (");
				sql.append("`t`.`LOGIN` LIKE :search ");
				sql.append("OR `t`.`TICKET` LIKE :search ");
				sql.append(") ");

				paramMap.put(
						"search",
						new StringBuilder("%").append(
								paginationSettings.getSearch()).append("%"));
			}

			paramMap.put("commission_" + agentId, commission);
			paramMap.put("login_" + agentId, clients);

			if (startOpenTime != null && endOpenTime != null) {
				sql.append("and (t.open_time >= :start_open_time_")
						.append(agentId).append(" ");
				sql.append("and t.open_time <= :end_open_time_")
						.append(agentId).append(") ");

				paramMap.put("start_open_time_" + agentId,
						dateFormatter.format(startOpenTime));
				paramMap.put("end_open_time_" + agentId,
						dateFormatter.format(endOpenTime));
			}
			if (startCloseTime != null && endCloseTime != null) {
				sql.append("and (t.close_time >= :start_close_time_")
						.append(agentId).append(" ");
				sql.append("and t.close_time <= :end_close_time_")
						.append(agentId).append(") ");

				paramMap.put("start_close_time_" + agentId,
						dateFormatter.format(startCloseTime));
				paramMap.put("end_close_time_" + agentId,
						dateFormatter.format(endCloseTime));
			}

			if (i != downlines.size() - 1) {
				sql.append("union all ");
			}
		}

		if (downlines.size() > 0 && paginationSettings != null) {
			paginateSql(paginationSettings.getOffset(),
					paginationSettings.getRowcount(),
					paginationSettings.getSortColumn(),
					paginationSettings.getSortDirection(), paramMap, sql);
		}

	}
}
