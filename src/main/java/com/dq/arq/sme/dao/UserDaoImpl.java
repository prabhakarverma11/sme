package com.dq.arq.sme.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserRoleDo;
import com.dq.arq.sme.util.UtilConstants;

@Repository
public class UserDaoImpl implements UserDao{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Integer saveUserDo(UserDo userDo) {
		
		sessionFactory.getCurrentSession().save(userDo);
		return userDo.getId();
		
	}

	@Override
	public List<UserDo> getUserDosList() {
		
		String hql = "FROM UserDo order by createdOn DESC";
		List<UserDo> userDos = sessionFactory.getCurrentSession().createQuery(hql)
				.list();
		return userDos;
	}
	
	
	@Override
	public Long countUserDos(){
		
		Criteria c = sessionFactory.getCurrentSession().createCriteria(UserDo.class);
    	c.setProjection(Projections.rowCount());
    	Long count = (Long) c.uniqueResult();
		return count;
		
	}
	
	@Override
	public List<UserDo> getUserDosListByPage(int page) {
		
		int maxrecords = UtilConstants.CAMPAIGNS_PER_PAGE;
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(UserDo.class);
		cr.addOrder(Order.desc("createdOn"))
		.setFirstResult((page-1)*maxrecords)
    			.setMaxResults(maxrecords);
		List<UserDo> userDos = cr.list();
		return userDos;
	}

	@Override
	public UserDo getUserDoById(Integer userId) {
		UserDo userDo = (UserDo) sessionFactory.getCurrentSession().get(UserDo.class, userId);
		return userDo;
	}

	@Override
	public UserDo getUserDoByName(String name) {
		String hql = "FROM UserDo where name = "+name;
		List<UserDo> userDos = sessionFactory.getCurrentSession().createQuery(hql).list();
		if(userDos.size() == 0) {
			return null;
		}else {
			return userDos.get(0);
		}
	}

	@Override
	public void updateUser(UserDo userDo) {
		sessionFactory.getCurrentSession().update(userDo);
		
	}

	@Override
	public UserDo getUserDoByEmail(String email) {
		String hql = "FROM UserDo where email = '"+email+"'";
		List<UserDo> userDos = sessionFactory.getCurrentSession().createQuery(hql).list();
		if(userDos.size() == 0) {
			return null;
		}else {
			return userDos.get(0);
		}
	}
	
	@Override
	public void createUserRole(UserRoleDo userRoleDo) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().save(userRoleDo);
	}

	@Override
	public UserRoleDo getUserRoleDoByUserDo(UserDo userDo) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRoleDo.class);
		criteria.add(Restrictions.eq("userDo", userDo));
		List<UserRoleDo> userRoleDos = criteria.list();
		if(!userRoleDos.isEmpty()) {
			return userRoleDos.get(0);
		}
		return null;
	}
	
	@Override
	public List<UserRoleDo> getClientsList() {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRoleDo.class);
		criteria.add(Restrictions.eq("role", "ROLE_USER"));
		List<UserRoleDo> userRoleDos = criteria.list();
		return userRoleDos;
	}

	@Override
	public List<Object[]> getUserRoleDosListBySearch(String key) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserRoleDo.class,"userRoleDo")
				.add(Restrictions.eq("role", "ROLE_USER"))
				.createCriteria("userDo","userDo")
				.add(Restrictions.like("name", "%"+key+"%"))
				.add(Restrictions.eq("isVerified", true));
		criteria.setProjection(Projections.projectionList().add(Projections.property("userDo.id")).add(Projections.property("userDo.name")));
		List<Object[]> rows = criteria.list();
		return rows;
	}

	@Override
	public List<UserDo> getUserDosListByPageAndSortedByColumn(Integer page, String columnName, Integer orderBy) {
		int maxrecords = UtilConstants.CAMPAIGNS_PER_PAGE;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(UserDo.class);
		switch (columnName) {
			case "na":
				if(orderBy==0)
					cr.addOrder(Order.asc("name"));
				else
					cr.addOrder(Order.desc("name"));
				break;
			case "em":
				if(orderBy==0)
					cr.addOrder(Order.asc("email"));
				else
					cr.addOrder(Order.desc("email"));
				break;
			default:
				break;
		}
		cr.setFirstResult((page-1)*maxrecords)
    			.setMaxResults(maxrecords);
		List<UserDo> userDos = cr.list();
		return userDos;
	}

}
