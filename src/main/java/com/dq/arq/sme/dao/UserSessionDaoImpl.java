package com.dq.arq.sme.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserSessionDo;

@Repository
public class UserSessionDaoImpl implements UserSessionDao{

	@Autowired
	SessionFactory sessionFactory;
	
	
	@Override
	public Integer saveUserSessionDo(UserSessionDo userDo) {
		sessionFactory.getCurrentSession().save(userDo);
		return userDo.getId();
	}

	@Override
	public List<UserSessionDo> getUserSessionDosList() {
	
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserSessionDo.class);
		return criteria.list();
		
	}

	@Override
	public UserSessionDo getUserSessionDoById(Integer id) {
		
		UserSessionDo userSessionDo= (UserSessionDo) sessionFactory.getCurrentSession().get(UserSessionDo.class, id);
		return userSessionDo;
		
	}
	
	@Override
	public UserSessionDo getUserSessionDoBySessionId(String sessionId) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserSessionDo.class)
				.add(Restrictions.eq("sessionId", sessionId));
		List<UserSessionDo> userSessionDos = criteria.list();
		
		if(userSessionDos!=null && userSessionDos.size()>0)
			return userSessionDos.get(0);
		else
			return null;
		
	}
	
	@Override
	public List<UserSessionDo> getUserSessionDosByUserDosandSessionIds(List<UserDo> userDos,List<String> sessionIds) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserSessionDo.class)
				.add(Restrictions.in("userDo",userDos))
				.add(Restrictions.in("sessionId",sessionIds));
		List<UserSessionDo> userSessionDos = criteria.list();
		
		return userSessionDos;
		
	}
	
	@Override
	public UserSessionDo checkIfUserLoggedIn(UserDo userDo) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserSessionDo.class)
				.add(Restrictions.eq("userDo", userDo))
				.add(Restrictions.eq("isLoggedIn", 1))
				.addOrder(Order.asc("loggedInTime"));
		List<UserSessionDo> userSessionDos = criteria.list();
		
		if(userSessionDos!=null && userSessionDos.size()>0)
			return userSessionDos.get(0);
		else
			return null;
		
	}

	@Override
	public void updateUserSessionDo(UserSessionDo userSessionDo) {
		
		sessionFactory.getCurrentSession().update(userSessionDo);
		
	}

	@Override
	public List<UserSessionDo> getUserSessionDosByUserDo(UserDo userDo) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserSessionDo.class)
							.add(Restrictions.eq("userDo", userDo))
							.addOrder(Order.desc("loggedInTime"));
		List<UserSessionDo> userSessionDos = criteria.list();
		
		return userSessionDos;
	}

	@Override
	public UserSessionDo getLastUserSessionDoByUserDo(UserDo userDo) {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserSessionDo.class)
							.add(Restrictions.eq("userDo", userDo))
							.addOrder(Order.desc("loggedInTime"))
							.setFirstResult(0)
							.setMaxResults(1);
		List<UserSessionDo> userSessionDos = criteria.list();
		if(userSessionDos!=null && userSessionDos.size()>0)
			return userSessionDos.get(0);
		else
			return null;
	}

	@Override
	public Long countUserSessionDosByUserDo(UserDo userDo) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserSessionDo.class)
				.add(Restrictions.eq("userDo", userDo))
				.addOrder(Order.desc("loggedInTime"))
				.setProjection(Projections.rowCount());
		if(criteria!=null)
			return (Long) criteria.uniqueResult();
		return null;
	}

}
