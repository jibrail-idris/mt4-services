package com.mt4agents.entities;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

@Entity
@DynamicUpdate
@SequenceGenerator(name = "AgentGenerator")
public class Agent {
	@Id
	@GeneratedValue(generator = "AgentGenerator", strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	@Type(type = "double")
	private Double commission;

	@Column(nullable = false)
	@Type(type = "integer")
	@NaturalId
	private Integer mt4Login;

	@Column(nullable = true, length = 50)
	@Type(type = "string")
	private String name;

	@OneToOne(orphanRemoval = true)
	private AgentRelationship parentRelationship;

	@OneToMany(mappedBy = "agent")
	@MapKeyColumn(name = "id")
	private Map<Integer, AgentClient> clients = new HashMap<Integer, AgentClient>();

	@OneToMany(mappedBy = "agent")
	@MapKeyColumn(name = "mt4Login")
	private Map<Integer, AgentClient> clientsByLogin = new HashMap<Integer, AgentClient>();

	@Version
	private Integer version;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}

	public Integer getMt4Login() {
		return mt4Login;
	}

	public void setMt4Login(Integer mt4Login) {
		this.mt4Login = mt4Login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentRelationship getParentRelationship() {
		return parentRelationship;
	}

	public void setParentRelationship(AgentRelationship parentRelationship) {
		this.parentRelationship = parentRelationship;
	}

	public Map<Integer, AgentClient> getClients() {
		return clients;
	}

	public AgentClient getClientById(Integer id) {
		return clients.get(id);
	}

	public Map<Integer, AgentClient> getClientsByLogin() {
		return clientsByLogin;
	}

	public AgentClient getClientByLogin(Integer mt4Login) {
		return clientsByLogin.get(mt4Login);
	}
}
