package com.mt4agents.dao.queries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

import com.mt4agents.dto.MT4CommissionDTO;

public class MT4MultipleLoginsCommissionQuery extends MappingSqlQuery<MT4CommissionDTO> {
	
	public MT4MultipleLoginsCommissionQuery(DataSource dataSource) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		sql.append("t.login, u.name, u.email, ");
		sql.append("sum(t.volume) AS `totalVolume`, ");
		sql.append("sum(t.volume * :commission) AS `commission` ");
		sql.append("from mt4_trades t ");
		sql.append("inner join mt4_users u on t.login = u.login ");
		sql.append("where ");
		sql.append("t.close_time >= :start_close_time and t.close_time <= :end_close_time ");
		sql.append("and t.login in :logins ");
		sql.append("group by t.login ");
		sql.append("order by commissions");
		
		declareParameter(new SqlParameter("commission", Types.DOUBLE));
		declareParameter(new SqlParameter("start_close_time", Types.TIMESTAMP));
		declareParameter(new SqlParameter("end_close_time", Types.TIMESTAMP));
		declareParameter(new SqlParameter("logins", Types.ARRAY));
		
		setDataSource(dataSource);
		setSql(sql.toString());
		compile();
	}

	@Override
	protected MT4CommissionDTO mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		MT4CommissionDTO commission = new MT4CommissionDTO();
		commission.setLogin(rs.getInt("login"));
		commission.setName(rs.getString("name"));
		commission.setEmail(rs.getString("email"));
		commission.setTotalVolume(rs.getDouble("totalVolume"));
		commission.setCommission(rs.getDouble("commission"));
		return commission;
	}
}
