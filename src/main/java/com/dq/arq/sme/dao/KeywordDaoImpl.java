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

import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.KeywordDo;

@Repository
public class KeywordDaoImpl implements KeywordDao{
	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Integer saveKeywordDo(KeywordDo keywordDo) {

		sessionFactory.getCurrentSession().save(keywordDo);
		return keywordDo.getId();

	}

	@Override
	public List<KeywordDo> getKeywordDosList() {

		String hql = "FROM KeywordDo where order by createdOn DESC";
		List<KeywordDo> keywordDos = sessionFactory.getCurrentSession().createQuery(hql)
				.list();
		return keywordDos;
	}

	@Override
	public KeywordDo getKeywordDoById(Integer keywordId) {
		KeywordDo keywordDo = (KeywordDo) sessionFactory.getCurrentSession().get(KeywordDo.class, keywordId);
		return keywordDo;
	}

	@Override
	public KeywordDo getKeywordDoByName(String keywordName) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(KeywordDo.class);
		criteria.add(Restrictions.eq("text", keywordName));
		List<KeywordDo> keywordDos = criteria.list();
		if(keywordDos.size() !=0) {
			return keywordDos.get(0);
		}else {
			return null;
		}
	}
	
	@Override
	public List<KeywordDo> getKeywordDosListByAdgroupDo(AdgroupDo adgroupDo) {

		List<KeywordDo> keywordDos = sessionFactory.getCurrentSession().createCriteria(KeywordDo.class)
				.add(Restrictions.eq("adgroupDo", adgroupDo))
				.add(Restrictions.or(Restrictions.eq("status", KeywordDo.Status.Enabled.name()), Restrictions.eq("status", KeywordDo.Status.Paused.name())))
				.addOrder(Order.asc("status"))
				.addOrder(Order.desc("createdOn"))
				.list();

		return keywordDos;
	}


	@Override
	public void deleteKeywordDo(KeywordDo keywordDo) {
		sessionFactory.getCurrentSession().delete(keywordDo);

	}

	@Override
	public Long countKeywordDosByAdgroupDo(AdgroupDo adgroupDo){

		Criteria c = sessionFactory.getCurrentSession().createCriteria(KeywordDo.class);
		c.add(Restrictions.eq("adgroupDo", adgroupDo));
		c.setProjection(Projections.rowCount());
		Long count = (Long) c.uniqueResult();
		return count;

	}

	@Override
	public List<KeywordDo> getKeywordDosListByApiId(List<KeywordDo> keywordDos)
	{
		List<KeywordDo> newKeywordDos = new ArrayList<KeywordDo>();		
		for(KeywordDo keywordDo:keywordDos)
		{
			String hql = "FROM KeywordDo where apiId = "+ keywordDo.getApiId() + " and adgroupApiId = "+keywordDo.getAdgroupApiId();
			
			List<KeywordDo> getKeywordDos = sessionFactory.getCurrentSession().createQuery(hql).list();
			if(getKeywordDos.size()>0)
				newKeywordDos.add(getKeywordDos.get(0));
			else
				continue;

		}
		return newKeywordDos;
	}

	@Override
	public void updateKeywordDo(KeywordDo keywordDo){
		sessionFactory.getCurrentSession().update(keywordDo);
	}
}
