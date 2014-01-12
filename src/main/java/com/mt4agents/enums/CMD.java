package com.mt4agents.enums;

public enum CMD {
	BUY, SELL, BUY_LIMIT, SELL_LIMIT, BUY_STOP, SELL_STOP, BALANCE, CREDIT;

	public static CMD parse(int val) {
		switch (val) {
		case 0:
			return BUY;
		case 1:
			return SELL;
		case 2:
			return BUY_LIMIT;
		case 3:
			return SELL_LIMIT;
		case 4:
			return BUY_STOP;
		case 5:
			return SELL_STOP;
		case 6:
			return BALANCE;
		case 7:
			return CREDIT;
		default:
			return null;
		}
	}
}
