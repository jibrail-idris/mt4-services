package com.mt4agents.formatters;

import org.junit.Test;

import com.mt4agents.formatters.NumberFormatter.Type;

public class NumberFormatterTest {
	@Test
	public void test1() {
		System.out.println(NumberFormatter.parseDouble(Type.CURRENCY, 1.18845));
	}
}
