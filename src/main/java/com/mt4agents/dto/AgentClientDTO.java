package com.mt4agents.dto;

import java.util.Date;

public class AgentClientDTO {
	private Integer agentClientId;
	private Integer login;
	private Integer agentId;
	private String agentName;
	private String agentLabel;
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
	private MT4UserDTO userDTO;
	private MT4CommissionDTO commissionDTO;
	private Date registrationDate;
	
	public Integer getAgentClientId() {
		return agentClientId;
	}
	public void setAgentClientId(Integer agentClientId) {
		this.agentClientId = agentClientId;
	}
	public Integer getLogin() {
		return login;
	}
	public void setLogin(Integer login) {
		this.login = login;
	}
	public Integer getAgentId() {
		return agentId;
	}
	public void setAgentId(Integer agentId) {
		this.agentId = agentId;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentLabel() {
		return agentLabel;
	}
	public void setAgentLabel(String agentLabel) {
		this.agentLabel = agentLabel;
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
	public MT4UserDTO getMT4UserDTO() {
		return userDTO;
	}
	public void setMT4UserDTO(MT4UserDTO userDTO) {
		this.userDTO = userDTO;
	}
	public MT4CommissionDTO getCommissionDTO() {
		return commissionDTO;
	}
	public void setCommissionDTO(MT4CommissionDTO commissionDTO) {
		this.commissionDTO = commissionDTO;
	}
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
}
