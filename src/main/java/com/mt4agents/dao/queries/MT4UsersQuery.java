package com.mt4agents.dao.queries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import com.mt4agents.dto.MT4UserDTO;
import com.mt4agents.util.UnicodeUtils;

public class MT4UsersQuery extends MappingSqlQuery<MT4UserDTO> {

	private boolean simple;

	public MT4UsersQuery(DataSource dataSource, List<String> filters,
			boolean simple) {
		this.simple = simple;
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		if (simple) {
			sql.append("`LOGIN`, `GROUP`, `NAME`, `EMAIL` ");
		} else {
			sql.append("`LOGIN`, `GROUP`, `ENABLE`, `ENABLE_CHANGE_PASS`, ");
			sql.append("`ENABLE_READONLY`, `PASSWORD_PHONE`,  `NAME`,  ");
			sql.append("`COUNTRY`, `CITY`, `STATE`, ");
			sql.append("`ZIPCODE`, `ADDRESS`, `PHONE`, `EMAIL`, ");
			sql.append("`COMMENT`, `ID`, `STATUS`, `REGDATE`, `LASTDATE`, ");
			sql.append("`LEVERAGE`, `AGENT_ACCOUNT`, CASE `TIMESTAMP` WHEN 0 THEN NULL ELSE `TIMESTAMP` END `TIMESTAMP`, ");
			sql.append("`BALANCE`, `PREVMONTHBALANCE`, `PREVBALANCE`, ");
			sql.append("`CREDIT`,`INTERESTRATE`, `TAXES`, `SEND_REPORTS`, ");
			sql.append("`USER_COLOR`, `EQUITY`, `MARGIN`, `MARGIN_LEVEL`, `MARGIN_FREE`, `MODIFY_TIME` ");
		}
		sql.append("from `mt4_users` ");

		if (!filters.isEmpty()) {
			sql.append("where ");
			boolean filterAdded = false;
			if (filters.contains("login")) {
				sql.append("`LOGIN` = :login ");
				declareParameter(new SqlParameter("login", Types.INTEGER));
				filterAdded = true;
			}
			if (filters.contains("name")) {
				if (filterAdded) {
					sql.append("OR ");
				}
				sql.append("`NAME` like :name ");
				declareParameter(new SqlParameter("name", Types.VARCHAR));
				filterAdded = true;
			}
		}

		sql.append("LIMIT :offset,:rowcount ");
		declareParameter(new SqlParameter("offset", Types.INTEGER));
		declareParameter(new SqlParameter("rowcount", Types.INTEGER));

		setDataSource(dataSource);
		setSql(sql.toString());
		compile();
	}

	public MT4UserDTO _execute(Integer mt4Login, Integer offset,
			Integer rowcount) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("login", mt4Login);
		paramMap.put("offset", offset);
		paramMap.put("rowcount", rowcount);
		List<MT4UserDTO> userDTOList = executeByNamedParam(paramMap);
		if (!userDTOList.isEmpty()) {
			return userDTOList.get(0);
		} else {
			return null;
		}
	}

	public List<MT4UserDTO> _execute(String name, Integer offset,
			Integer rowcount) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", name);
		paramMap.put("offset", offset);
		paramMap.put("rowcount", rowcount);
		return executeByNamedParam(paramMap);
	}

	public List<MT4UserDTO> _execute(Integer offset, Integer rowcount) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("offset", offset);
		paramMap.put("rowcount", rowcount);
		return executeByNamedParam(paramMap);
	}

	@Override
	protected MT4UserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		MT4UserDTO dto = new MT4UserDTO();
		if (simple) {
			dto.setLogin(rs.getInt("LOGIN"));
			dto.setGroup(rs.getString("GROUP"));
			dto.setName(UnicodeUtils.convertToString(rs.getString("NAME")));
			dto.setEmail(UnicodeUtils.convertToString(rs.getString("EMAIL")));
		} else {
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
		}
		return dto;
	}

}
