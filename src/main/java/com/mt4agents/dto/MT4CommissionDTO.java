package com.mt4agents.dto;

public class MT4CommissionDTO {
	private Integer login;
	private String name;
	private String email;
	private Double totalVolume;
	private Double commission;
	public Integer getLogin() {
		return login;
	}
	public void setLogin(Integer login) {
		this.login = login;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Double getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
	}
	public Double getCommission() {
		return commission;
	}
	public void setCommission(Double commission) {
		this.commission = commission;
	}
}
