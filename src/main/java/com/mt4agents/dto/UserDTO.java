package com.mt4agents.dto;

import com.mt4agents.entities.users.AdminUser;
import com.mt4agents.entities.users.AgentUser;

public class UserDTO {
	private Integer userId;
	private String username;
	private String password;
	private String newPassword1;
	private String newPassword2;
	private String role;
	private AgentDTO agentDTO;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewPassword1() {
		return newPassword1;
	}
	public void setNewPassword1(String newPassword1) {
		this.newPassword1 = newPassword1;
	}
	public String getNewPassword2() {
		return newPassword2;
	}
	public void setNewPassword2(String newPassword2) {
		this.newPassword2 = newPassword2;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
	public AgentDTO getAgentDTO() {
		return agentDTO;
	}
	public void setAgentDTO(AgentDTO agentDTO) {
		this.agentDTO = agentDTO;
	}
	public void assignAdminRole() {
		setRole(AdminUser.ROLE);
	}
	public void assignAgentRole() {
		setRole(AgentUser.ROLE);
	}
}
