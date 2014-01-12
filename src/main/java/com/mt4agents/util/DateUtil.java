package com.mt4agents.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * http://stackoverflow.com/questions/428918/how-can-i-increment-a-date-by-one-day-in-java
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); // minus number would decrement the days
		return cal.getTime();
	}
}
