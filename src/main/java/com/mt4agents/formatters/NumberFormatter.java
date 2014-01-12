package com.mt4agents.formatters;

import java.text.DecimalFormat;

public class NumberFormatter {
	public enum Type {
		CURRENCY
	}

	private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

	public static DecimalFormat getNumberFormatter(Type type) {
		switch (type) {
		case CURRENCY:
			return decimalFormat;
		}
		return null;
	}

	public static Double parseDouble(Type type, Double doubleValue) {
		DecimalFormat formatter = getNumberFormatter(type);
		return (Double) Double.valueOf(formatter.format(doubleValue));

	}
}
