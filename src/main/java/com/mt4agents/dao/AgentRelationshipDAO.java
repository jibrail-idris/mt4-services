package com.mt4agents.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mt4agents.entities.AgentRelationship;

public class AgentRelationshipDAO extends BaseDAO {

	public void save(AgentRelationship relationship) {
		Session session = sessionFactory.getCurrentSession();
		session.save(relationship);
		session.flush();
	}

	public boolean exists(Integer parentAgentId, Integer childAgentId) {
		Session session = sessionFactory.getCurrentSession();
		return session
				.createCriteria(AgentRelationship.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("id")))
				.add(parentAgentId == null ? Restrictions
						.isNull("parentAgent.id") : Restrictions.eq(
						"parentAgent.id", parentAgentId))
				.add(childAgentId == null ? Restrictions
						.isNull("childAgent.id") : Restrictions.eq(
						"childAgent.id", childAgentId)).uniqueResult() != null;
	}

	public AgentRelationship getParentRelationship(Integer agentId) {
		Session session = sessionFactory.getCurrentSession();
		return (AgentRelationship) session
				.createCriteria(AgentRelationship.class)
				.add(Restrictions.eq("childAgent.id", agentId)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<AgentRelationship> getChildrenRelationships(Integer agentId) {
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(AgentRelationship.class)
				.add(Restrictions.eq("parentAgent.id", agentId)).list();
	}
}
