package com.mt4agents.entities.users;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.mt4agents.entities.Agent;

@Entity
@DiscriminatorValue("agent")
public class AgentUser extends User {
	private static final long serialVersionUID = -2173740235138214265L;
	public static final String AGENT_USER_ROLE_PREFIX = "MT4_AGENT_";
	public static final String ROLE = "agent";

	@OneToOne
	private Agent agent;

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(ROLE));
		if (agent != null) {
			authorities.add(new SimpleGrantedAuthority(new StringBuilder(
					AGENT_USER_ROLE_PREFIX).append(agent.getId()).toString()));
		}
		return authorities;
	}
}
