package com.mt4agents.dao;

import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.mt4agents.entities.users.AgentUser;
import com.mt4agents.entities.users.User;

public class UserDAO extends BaseDAO {

	public void save(User user) {
		Session session = sessionFactory.getCurrentSession();
		session.save(user);
		session.flush();
	}

	public User readByUsername(String username) {
		Session session = sessionFactory.getCurrentSession();
		return (User) session.createCriteria(User.class)
				.add(Restrictions.eq("username", username)).uniqueResult();
	}

	public Boolean isUsernameInUse(String username) {
		Session session = sessionFactory.getCurrentSession();
		Integer id = (Integer) session
				.createCriteria(User.class)
				.setProjection(
						Projections.projectionList().add(
								Projections.property("id")))
				.add(Restrictions.eq("username", username)).uniqueResult();
		return id != null && id > 0;
	}

	public AgentUser readAgentUserById(Integer agentId) {
		Session session = sessionFactory.getCurrentSession();
		return (AgentUser) session.createCriteria(AgentUser.class)
				.createAlias("agent", "a")
				.add(Restrictions.eq("a.id", agentId)).uniqueResult();
	}

	public void delete(Integer userId) {
		Session session = sessionFactory.getCurrentSession();
		User user = (User) session.load(User.class, userId);
		session.delete(user);
		session.flush();
	}
}
