package com.mt4agents.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
@SequenceGenerator(name="AgentRelationshipGenerator")
public class AgentRelationship {
	@Id
	@GeneratedValue(generator="AgentRelationshipGenerator", strategy = GenerationType.AUTO)
	private Integer id;
	
	@Version
	private Integer version;
	
	@OneToOne
	private Agent parentAgent;
	
	@OneToOne
	private Agent childAgent;
	
	public Integer getId() {
		return this.id;
	}

	public Agent getParentAgent() {
		return parentAgent;
	}

	public void setParentAgent(Agent parentAgent) {
		this.parentAgent = parentAgent;
	}

	public Agent getChildAgent() {
		return childAgent;
	}

	public void setChildAgent(Agent childAgent) {
		this.childAgent = childAgent;
	}
}
