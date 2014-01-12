package com.mt4agents.dao;

import org.hibernate.SessionFactory;

public abstract class BaseDAO {
	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
