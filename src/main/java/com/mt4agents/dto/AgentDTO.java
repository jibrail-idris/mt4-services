package com.mt4agents.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgentDTO {
	private Integer agentId;
	private Integer login;
	private Double commission;
	private Integer parentAgentId;
	private String parentAgentName;
	private String parentAgentLabel;
	private String group;
	private String name;
	private String label;
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
	private Map<Integer, AgentClientDTO> clients = new HashMap<Integer, AgentClientDTO>();
	private Map<Integer, AgentClientDTO> clientsByLogin = new HashMap<Integer, AgentClientDTO>();
	private MT4UserDTO mt4User;

	public Integer getAgentId() {
		return agentId;
	}

	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}

	public Integer getLogin() {
		return login;
	}

	public void setLogin(Integer login) {
		this.login = login;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public Integer getParentAgentId() {
		return parentAgentId;
	}

	public void setParentAgentId(Integer parentAgentId) {
		this.parentAgentId = parentAgentId;
	}

	public String getParentAgentName() {
		return parentAgentName;
	}

	public void setParentAgentName(String parentAgentName) {
		this.parentAgentName = parentAgentName;
	}

	public String getParentAgentLabel() {
		return parentAgentLabel;
	}

	public void setParentAgentLabel(String parentAgentLabel) {
		this.parentAgentLabel = parentAgentLabel;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public Map<Integer, AgentClientDTO> getClients() {
		return clients;
	}

	public void setClients(Map<Integer, AgentClientDTO> clients) {
		this.clients = clients;
	}

	public Map<Integer, AgentClientDTO> getClientsByLogin() {
		return clientsByLogin;
	}

	public void setClientsByLogin(Map<Integer, AgentClientDTO> clientsByLogin) {
		this.clientsByLogin = clientsByLogin;
	}

	public MT4UserDTO getMt4User() {
		return mt4User;
	}

	public void setMt4User(MT4UserDTO mt4User) {
		this.mt4User = mt4User;
	}
}
