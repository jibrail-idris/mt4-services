package com.mt4agents.formatters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

public class DateFormatter {

	public enum Type {
		MYSQL, GENERAL, YEARFIRSTNODASHDAY, MYSQLNOTIME, _12HOUR, _24HOUR
	};

	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"dd-MM-yyyy HH:mm:ss");
	private static final SimpleDateFormat mysqlSimpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat simpleDayYearFirstNoDashFormat = new SimpleDateFormat(
			"yyyyMMdd");
	public static final SimpleDateFormat mysqlSimpleNoTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final SimpleDateFormat _12Timeformat = new SimpleDateFormat(
			"hh:mm:ss aa");
	public static final SimpleDateFormat _24TimeForamt = new SimpleDateFormat(
			"HH:mm:ss");

	public static SimpleDateFormat getDateFormatter(Type type) {
		switch (type) {
		case GENERAL:
			return simpleDateFormat;
		case MYSQL:
			return mysqlSimpleDateFormat;
		case YEARFIRSTNODASHDAY:
			return simpleDayYearFirstNoDashFormat;
		case MYSQLNOTIME:
			return mysqlSimpleNoTimeFormat;
		case _12HOUR:
			return _12Timeformat;
		case _24HOUR:
			return _24TimeForamt;
		}

		return null;
	}

	public static String parse(Type type, Date date) {
		SimpleDateFormat dateFormatter = getDateFormatter(type);
		if (dateFormatter != null) {
			return dateFormatter.format(date);
		} else {
			return "";
		}
	}

	public static Date format(Type type, Date date) throws ParseException {
		SimpleDateFormat dateFormatter = getDateFormatter(type);
		return dateFormatter.parse(dateFormatter.format(date));
	}

	public static Date parse(Type type, String dateString)
			throws ParseException {
		if (StringUtils.hasLength(dateString)) {
			SimpleDateFormat dateFormatter = getDateFormatter(type);
			return dateFormatter.parse(dateString);
		} else {
			return null;
		}
	}
}
