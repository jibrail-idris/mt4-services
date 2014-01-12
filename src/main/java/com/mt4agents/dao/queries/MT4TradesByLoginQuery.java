package com.mt4agents.dao.queries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import com.mt4agents.dto.MT4TradeDTO;

public class MT4TradesByLoginQuery extends MappingSqlQuery<MT4TradeDTO> {

	public MT4TradesByLoginQuery(DataSource dataSource, List<String> filters) {
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("`t`.TICKET, ");
		sql.append("`t`.LOGIN, ");
		sql.append("`t`.SYMBOL, ");
		sql.append("`t`.VOLUME, ");
		sql.append("`t`.OPEN_TIME, ");
		sql.append("`t`.OPEN_PRICE, ");
		sql.append("`t`.CLOSE_TIME, ");
		sql.append("`t`.CLOSE_PRICE, ");
		sql.append("`t`.SL, ");
		sql.append("`t`.TP, ");
		sql.append("`t`.SWAPS, ");
		sql.append("`t`.PROFIT, ");
		sql.append("`t`.COMMENT, ");
		sql.append("((`t`.VOLUME / 100) * :commission) AS `COMMISSION`  ");
		sql.append("from `mt4_trades` t ");
		sql.append("inner join `mt4_users` u on t.LOGIN = u.LOGIN ");
		sql.append("where u.LOGIN = :login ");
		//sql.append("and `t`.cmd in (0,1) ");

		declareParameter(new SqlParameter("commission", Types.DOUBLE));
		declareParameter(new SqlParameter("login", Types.INTEGER));

		if (filters.contains("open_time")) {
			sql.append("and (t.open_time >= :start_open_time ");
			sql.append("and t.open_time <= :end_open_time) ");
			declareParameter(new SqlParameter("start_open_time",
					Types.TIMESTAMP));
			declareParameter(new SqlParameter("end_open_time", Types.TIMESTAMP));
		}
		if (filters.contains("close_time")) {
			sql.append("and (t.close_time >= :start_close_time ");
			sql.append("and t.close_time <= :end_close_time)");
			declareParameter(new SqlParameter("start_close_time",
					Types.TIMESTAMP));
			declareParameter(new SqlParameter("end_close_time", Types.TIMESTAMP));
		}

		setDataSource(dataSource);
		setSql(sql.toString());
		compile();
	}

	@Override
	protected MT4TradeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		MT4TradeDTO trade = new MT4TradeDTO();
		trade.setTicket(rs.getInt("TICKET"));
		trade.setLogin(rs.getInt("LOGIN"));
		trade.setSymbol(rs.getString("SYMBOL"));
		trade.setVolume(rs.getDouble("VOLUME"));
		trade.setOpenTime(rs.getDate("OPEN_TIME"));
		trade.setOpenPrice(rs.getDouble("OPEN_PRICE"));
		trade.setCloseTime(rs.getDate("CLOSE_TIME"));
		trade.setClosePrice(rs.getDouble("CLOSE_PRICE"));
		trade.setSl(rs.getDouble("SL"));
		trade.setTp(rs.getDouble("TP"));
		trade.setSwaps(rs.getDouble("SWAPS"));
		trade.setProfit(rs.getDouble("PROFIT"));
		trade.setComment(rs.getString("COMMENT"));
		trade.setCommission(rs.getDouble("COMMISSION"));
		return trade;
	}
}
