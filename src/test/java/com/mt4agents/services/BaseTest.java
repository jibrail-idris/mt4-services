package com.mt4agents.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class BaseTest {

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss");
	private SimpleDateFormat mysqlSimpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Before
	public void setUp() throws Exception {
	}

	protected Date getDate(String dateString) throws ParseException {
		return simpleDateFormat.parse(dateString);
	}

	protected Date getMySqlDate(String dateString) throws ParseException {
		return mysqlSimpleDateFormat.parse(dateString);
	}

	protected Date getMysqlDateFromDate(Date date) throws ParseException {
		return mysqlSimpleDateFormat.parse(mysqlSimpleDateFormat.format(date));
	}

	protected Date getMysqlDateFromDateString(String dateString)
			throws ParseException {
		return mysqlSimpleDateFormat.parse(mysqlSimpleDateFormat
				.format(getDate(dateString)));
	}

	@Test
	public void dummy() {
	}
}
