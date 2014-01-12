package com.mt4agents.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_DEFAULT)
public class MT4UserDTO {
	private Integer login;
	private String group;
	private Boolean enable;
	private Boolean enableChangePass;
	private Boolean enableReadOnly;
	private String passwordPhone;
	private String name;
	private String country;
	private String city;
	private String state;
	private String zipcode;
	private String address;
	private String phone;
	private String email;
	private String comment;
	private String id;
	private String status;
	private Date regDate;
	private Date lastDate;
	private Integer leverage;
	private Integer agentAccount;
	private Date timestamp;
	private Double balance;
	private Double prevMonthBalance;
	private Double prevBalance;
	private Double credit;
	private Double interestRate;
	private Double taxes;
	private Integer sendReports;
	private Integer userColor;
	private Double equity;
	private Double margin;
	private Double marginLevel;
	private Double marginFree;
	private Date modifyTime;
	private AgentDTO assignment;
	
	public Integer getLogin() {
		return login;
	}
	public void setLogin(Integer login) {
		this.login = login;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	public Boolean getEnableChangePass() {
		return enableChangePass;
	}
	public void setEnableChangePass(Boolean enableChangePass) {
		this.enableChangePass = enableChangePass;
	}
	public Boolean getEnableReadOnly() {
		return enableReadOnly;
	}
	public void setEnableReadOnly(Boolean enableReadOnly) {
		this.enableReadOnly = enableReadOnly;
	}
	public String getPasswordPhone() {
		return passwordPhone;
	}
	public void setPasswordPhone(String passwordPhone) {
		this.passwordPhone = passwordPhone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getRegDate() {
		return regDate;
	}
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	public Date getLastDate() {
		return lastDate;
	}
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	public Integer getLeverage() {
		return leverage;
	}
	public void setLeverage(Integer leverage) {
		this.leverage = leverage;
	}
	public Integer getAgentAccount() {
		return agentAccount;
	}
	public void setAgentAccount(Integer agentAccount) {
		this.agentAccount = agentAccount;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public Double getPrevMonthBalance() {
		return prevMonthBalance;
	}
	public void setPrevMonthBalance(Double prevMonthBalance) {
		this.prevMonthBalance = prevMonthBalance;
	}
	public Double getPrevBalance() {
		return prevBalance;
	}
	public void setPrevBalance(Double prevBalance) {
		this.prevBalance = prevBalance;
	}
	public Double getCredit() {
		return credit;
	}
	public void setCredit(Double credit) {
		this.credit = credit;
	}
	public Double getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}
	public Double getTaxes() {
		return taxes;
	}
	public void setTaxes(Double taxes) {
		this.taxes = taxes;
	}
	public Integer getSendReports() {
		return sendReports;
	}
	public void setSendReports(Integer sendReports) {
		this.sendReports = sendReports;
	}
	public Integer getUserColor() {
		return userColor;
	}
	public void setUserColor(Integer userColor) {
		this.userColor = userColor;
	}
	public Double getEquity() {
		return equity;
	}
	public void setEquity(Double equity) {
		this.equity = equity;
	}
	public Double getMargin() {
		return margin;
	}
	public void setMargin(Double margin) {
		this.margin = margin;
	}
	public Double getMarginLevel() {
		return marginLevel;
	}
	public void setMarginLevel(Double marginLevel) {
		this.marginLevel = marginLevel;
	}
	public Double getMarginFree() {
		return marginFree;
	}
	public void setMarginFree(Double marginFree) {
		this.marginFree = marginFree;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public AgentDTO getAssignment() {
		return assignment;
	}
	public void setAssignment(AgentDTO assignment) {
		this.assignment = assignment;
	}
}
