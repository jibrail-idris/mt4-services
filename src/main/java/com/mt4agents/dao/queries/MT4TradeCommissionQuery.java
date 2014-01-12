package com.mt4agents.dao.queries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import com.mt4agents.dto.MT4TradeDTO;

public class MT4TradeCommissionQuery extends MappingSqlQuery<MT4TradeDTO> {

	public MT4TradeCommissionQuery(DataSource dataSource) {
		StringBuilder sql = new StringBuilder();
		sql.append("select VOLUME, ");
		sql.append("SUM((VOLUME / 100) * :commission) AS `commission` ");
		sql.append("from mt4_trades ");
		sql.append("where login = :login ");
		sql.append("and close_time >= :start_close_time and close_time <= :end_close_time ");
		sql.append("order by close_time desc ");

		declareParameter(new SqlParameter("commission", Types.DOUBLE));
		declareParameter(new SqlParameter("login", Types.INTEGER));
		declareParameter(new SqlParameter("start_close_time", Types.TIMESTAMP));
		declareParameter(new SqlParameter("end_close_time", Types.TIMESTAMP));

		setDataSource(dataSource);
		setSql(sql.toString());
		compile();
	}

	public List<MT4TradeDTO> execute(Integer login, Double commission,
			Date startRangeCloseTime, Date endRangeCloseTime) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("login", login);
		paramMap.put("commission", commission);
		paramMap.put("start_close_time", startRangeCloseTime);
		paramMap.put("end_close_time", endRangeCloseTime);
		return executeByNamedParam(paramMap);
	}

	@Override
	protected MT4TradeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		MT4TradeDTO tradeDTO = new MT4TradeDTO();
		return tradeDTO;
	}
}
