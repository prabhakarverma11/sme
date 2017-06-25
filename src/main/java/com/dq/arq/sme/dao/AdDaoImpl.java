package com.dq.arq.sme.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dq.arq.sme.domain.AdDo;
import com.dq.arq.sme.domain.AdgroupDo;

@Repository
public class AdDaoImpl implements AdDao{

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Integer saveAdDo(AdDo adDo) {
		
		sessionFactory.getCurrentSession().save(adDo);
		return adDo.getId();
	}

	@Override
	public List<AdDo> getAdDosList() {
		
		List<AdDo> adDos = sessionFactory.getCurrentSession().createCriteria(AdDo.class).list();
				return adDos;

	}

	@Override
	public AdDo getAdDoById(Integer id) {
		
		AdDo adDo = (AdDo) sessionFactory.getCurrentSession().get(AdDo.class, id);
		return adDo;
	}

	@Override
	public List<AdDo> getAdDosListByAdgroupDo(AdgroupDo adgroupDo) {
	
		List<AdDo> adDos = sessionFactory.getCurrentSession().createCriteria(AdDo.class)
						.add(Restrictions.eq("adgroupDo", adgroupDo))
						.addOrder(Order.asc("status"))
						.addOrder(Order.desc("createdOn"))
						.add(Restrictions.or(Restrictions.eq("status", AdDo.Status.Enabled.name()), Restrictions.eq("status", AdDo.Status.Paused.name())))
						.list();
		return adDos;
		
		
	}

	@Override
	public void updateAdDo(AdDo adDo) {
		sessionFactory.getCurrentSession().update(adDo);
		
	}

	@Override
	public Long countAdDosByAdgroupDo(AdgroupDo adgroupDo) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdDo.class)
				.add(Restrictions.or(Restrictions.eq("status", AdDo.Status.Enabled.name()), Restrictions.eq("status", AdDo.Status.Paused.name())))
				.add(Restrictions.eq("adgroupDo",adgroupDo))
				.setProjection(Projections.rowCount());
		Long count = (Long) criteria.uniqueResult();
				
		return count;
	}

	@Override
	public void deleteAdDo(AdDo adDo){
		sessionFactory.getCurrentSession().delete(adDo);
	}

	
	@Override
	public AdgroupDo getAdgroupDoByAdId(Integer adId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdDo.class,"adDo")
				.createCriteria("adgroupDo","adgroupDo")
				.add(Restrictions.eq("adDo.id",adId));
		List<Object> rows = criteria.list();
		if(rows.size()>0)
			return ((AdDo)rows.get(0)).getAdgroupDo();
		else
			return null;
	}
	
}
