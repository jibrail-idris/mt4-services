package com.mt4agents.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mt4agents.entities.AgentClient;

public class AgentClientDAO extends BaseDAO {
	public void save(AgentClient agentClient) {
		Session session = sessionFactory.getCurrentSession();
		session.save(agentClient);
		session.flush();
	}

	public AgentClient read(Integer agentClientId) {
		Session session = sessionFactory.getCurrentSession();
		return (AgentClient) session.get(AgentClient.class, agentClientId);
	}

	public void delete(Integer agentClientId) {
		Session session = sessionFactory.getCurrentSession();
		AgentClient agentClient = (AgentClient) session.load(AgentClient.class,
				agentClientId);
		session.delete(agentClient);
	}

	public void deleteByAgent(Integer agentId) {

	}

	@SuppressWarnings("unchecked")
	public List<AgentClient> getClientsByAgent(Integer agentId) {
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(AgentClient.class)
				.createAlias("agent", "a")
				.add(Restrictions.eq("a.id", agentId)).list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getClientIdsByAgent(Integer agentId) {
		Session session = sessionFactory.getCurrentSession();
		return session
				.createCriteria(AgentClient.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("id")))
				.createAlias("agent", "a")
				.add(Restrictions.eq("a.id", agentId)).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getClientLoginsByAgent(Integer agentId) {
		Session session = sessionFactory.getCurrentSession();
		return session
				.createCriteria(AgentClient.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("mt4Login")))
				.createAlias("agent", "a")
				.add(Restrictions.eq("a.id", agentId)).list();
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getAllClientsLogins() {
		Session session = sessionFactory.getCurrentSession();
		return (List<Integer>) session
				.createCriteria(AgentClient.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("mt4Login"))).list();
	}

	public void deleteNotInLoginList(Integer agentId, List<Integer> logins) {
		Session session = sessionFactory.getCurrentSession();
		String hql = "delete from AgentClient where agent_id = :agentId and mt4Login not in (:logins)";
		Query query = session.createQuery(hql);
		query.setParameter("agentId", agentId);
		query.setParameterList("logins", logins);
		query.executeUpdate();
	}

	public boolean checkByMT4Login(Integer mt4Login) {
		Session session = sessionFactory.getCurrentSession();
		Integer id = (Integer) session
				.createCriteria(AgentClient.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("id")))
				.add(Restrictions.eq("mt4Login", mt4Login)).uniqueResult();
		return id != null && id > 0;
	}

	public AgentClient readByLogin(Integer mt4Login) {
		Session session = sessionFactory.getCurrentSession();
		return (AgentClient) session.createCriteria(AgentClient.class)
				.add(Restrictions.eq("mt4Login", mt4Login)).uniqueResult();
	}

	public boolean checkIfAssigned(Integer agentId, Integer agentClientId) {
		Session session = sessionFactory.getCurrentSession();
		Integer id = (Integer) session
				.createCriteria(AgentClient.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("id")))
				.createAlias("agent", "a")
				.add(Restrictions.eq("id", agentClientId))
				.add(Restrictions.eq("a.id", agentId)).uniqueResult();
		return id != null && id > 0;
	}

	public boolean checkIfLoginAssigned(Integer agentId,
			Integer agentClientLogin) {
		Session session = sessionFactory.getCurrentSession();
		Integer id = (Integer) session
				.createCriteria(AgentClient.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("id")))
				.createAlias("agent", "a")
				.add(Restrictions.eq("mt4Login", agentClientLogin))
				.add(Restrictions.eq("a.id", agentId)).uniqueResult();
		return id != null && id > 0;
	}
}
