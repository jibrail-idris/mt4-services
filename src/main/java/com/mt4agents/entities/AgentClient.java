package com.mt4agents.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

@Entity
@SequenceGenerator(name = "AgentClientGenerator")
public class AgentClient {
	@Id
	@GeneratedValue(generator = "AgentClientGenerator", strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false)
	@Type(type = "integer")
	@NaturalId
	private Integer mt4Login;

	@Column(nullable = true)
	@Type(type = "string")
	private String name;

	@Column(nullable = true)
	@Type(type = "string")
	private String email;

	@Column(nullable = false)
	@Type(type = "timestamp")
	private Date registrationDate;

	@ManyToOne
	private Agent agent;

	@Version
	private Integer version;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

}
