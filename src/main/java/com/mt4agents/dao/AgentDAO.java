package com.mt4agents.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import com.mt4agents.entities.Agent;

public class AgentDAO extends BaseDAO {

	public void save(Agent agent) {
		Session session = sessionFactory.getCurrentSession();
		session.save(agent);
		session.flush();
	}

	@SuppressWarnings("unchecked")
	public List<Agent> get() {
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(Agent.class).list();
	}

	public Integer count(String search) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Agent.class);
		criteria.setProjection(Projections.rowCount());
		criteria = searchCriteria(search, criteria);
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Agent> find(String search, int offset, int limit) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Agent.class);
		criteria = searchCriteria(search, criteria);
		return criteria.setFirstResult(offset).setMaxResults(limit).list();
	}

	@SuppressWarnings("unchecked")
	public List<Agent> get(String search) {
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(Agent.class)
				// TODO: Warning: SQL Injection. Properly parameterised this
				// code.
				.add(Restrictions
						.sqlRestriction("cast({alias}.mt4Login as char(11)) like '"
								+ new StringBuilder(search).append("%")
										.toString() + "'")).list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getAllAgentsLogins() {
		Session session = sessionFactory.getCurrentSession();
		return (List<Integer>) session
				.createCriteria(Agent.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("mt4Login"))).list();
	}

	@SuppressWarnings("unchecked")
	public List<Agent> getForCommission(List<Integer> agentIds) {
		Session session = sessionFactory.getCurrentSession();
		List<Object[]> agentsRows = session
				.createCriteria(Agent.class)
				.setProjection(
						Projections.projectionList()
								.add(Projections.property("id"))
								.add(Projections.property("mt4Login"))
								.add(Projections.property("commission")))
				.add(Restrictions.in("id", agentIds)).list();
		List<Agent> agents = new ArrayList<Agent>();
		for (Object[] row : agentsRows) {
			Agent agent = new Agent();
			agent.setId((Integer) row[0]);
			agent.setMt4Login((Integer) row[1]);
			agent.setCommission((Double) row[2]);
			agents.add(agent);
		}
		return agents;
	}

	public void delete(Integer agentId) {
		Session session = sessionFactory.getCurrentSession();
		Agent agent = (Agent) session.load(Agent.class, agentId);
		session.delete(agent);
		session.flush();
	}

	public Agent read(Integer agentId) {
		Session session = sessionFactory.getCurrentSession();
		Agent agent = (Agent) session.get(Agent.class, agentId);
		if (agent != null) {
			session.refresh(agent);
		}
		return agent;
	}

	public Agent readByMT4Login(Integer mt4Login) {
		Session session = sessionFactory.getCurrentSession();
		return (Agent) session.createCriteria(Agent.class)
				.add(Restrictions.eq("mt4Login", mt4Login)).uniqueResult();
	}

	public boolean checkById(Integer agentId) {
		Session session = sessionFactory.getCurrentSession();
		Integer id = (Integer) session
				.createCriteria(Agent.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("id")))
				.add(Restrictions.eq("id", agentId)).uniqueResult();
		return id != null && id > 0;
	}

	public boolean checkByMT4Login(Integer mt4Login) {
		Session session = sessionFactory.getCurrentSession();
		Integer id = (Integer) session
				.createCriteria(Agent.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("id")))
				.add(Restrictions.eq("mt4Login", mt4Login)).uniqueResult();
		return id != null && id > 0;
	}

	public Integer getAgentMT4Login(Long agentId) {
		Session session = sessionFactory.getCurrentSession();
		return (Integer) session
				.createCriteria(Agent.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("mt4Login")))
				.add(Restrictions.eq("id", agentId)).uniqueResult();
	}

	private Criteria searchCriteria(String search, Criteria criteria) {
		if (StringUtils.hasLength(search)) {
			search = search.replaceAll("(^')|('$)", "");
			criteria = criteria.add(Restrictions
					.sqlRestriction("cast({alias}.mt4Login as char(11)) like '"
							+ new StringBuilder(search).append("%").toString()
							+ "'"));
			
		}
		return criteria;
	}
}
