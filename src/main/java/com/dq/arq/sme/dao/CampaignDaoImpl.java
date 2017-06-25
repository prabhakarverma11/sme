package com.dq.arq.sme.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.util.UtilityMethod;


@Repository
public class CampaignDaoImpl implements CampaignDao{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Integer saveCampaignDo(CampaignDo campaignDo) {
		
		
		sessionFactory.getCurrentSession().save(campaignDo);
		return campaignDo.getId();
		
		
	}
	
	@Override
	public CampaignDo getCampaignDoByApiId(Long apiId) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class);
		cr.add(Restrictions.eq("apiId", apiId));
		List<CampaignDo> campaignDos = cr.list();
		if(campaignDos!=null && campaignDos.size()>0)
			return campaignDos.get(0);
		else
			return null;
	}


	@Override
	public CampaignDo getCampaignDoById(Integer campaignId) {
		CampaignDo campaignDo = (CampaignDo) sessionFactory.getCurrentSession().get(CampaignDo.class, campaignId);
		return campaignDo;
	}

	@Override
	public CampaignDo getCampaignDoByName(String name) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class);
		cr.add(Restrictions.eq("name", name));
		
		List<CampaignDo> campaignDos = cr.list();
		if(campaignDos!=null && campaignDos.size()>0)
			return campaignDos.get(0);
		else
			return null;
	}

	@Override
	public void updateCampaign(CampaignDo campaignDo) {
		sessionFactory.getCurrentSession().update(campaignDo);
		
	}
	

	@Override
	public List<CampaignDo> getCampaignDosListByUserDo(UserDo userDo) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class);
		cr.add(Restrictions.eq("userDo", userDo));
		cr.add(Restrictions.or(Restrictions.eq("status", CampaignDo.Status.Enabled.name()),Restrictions.eq("status", CampaignDo.Status.Paused.name())));
		cr.addOrder(Order.asc("status"));
		cr.addOrder(Order.desc("updatedOn"));
		List<CampaignDo> campaignDos = cr.list();
		return campaignDos;
	}
	
	@Override
	public List<CampaignDo> getCampaignDosListByUserDoAndPage(UserDo userDo,int page,int rows) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class);
		cr.add(Restrictions.eq("userDo", userDo));
		cr.add(Restrictions.or(Restrictions.eq("status", CampaignDo.Status.Enabled.name()),Restrictions.eq("status", CampaignDo.Status.Paused.name())));
		cr.addOrder(Order.asc("status"));
		cr.addOrder(Order.desc("updatedOn"))
		.setFirstResult((page-1)*rows)
    			.setMaxResults(rows);
		List<CampaignDo> campaignDos = cr.list();
		return campaignDos;
	}
	
	@Override
	public List<CampaignDo> getCampaignDosListForAdmin() {
		
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class);
		cr.add(Restrictions.or(Restrictions.eq("status", CampaignDo.Status.Enabled.name()),Restrictions.eq("status", CampaignDo.Status.Paused.name())));
		cr.addOrder(Order.asc("status"));
		cr.addOrder(Order.desc("updatedOn"));
		List<CampaignDo> campaignDos = cr.list();
		return campaignDos;
	}
	
	@Override
	public List<CampaignDo> getCampaignDosListForAdminByPage(int page,int rows) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class);
		cr.add(Restrictions.or(Restrictions.eq("status", CampaignDo.Status.Enabled.name()),Restrictions.eq("status", CampaignDo.Status.Paused.name())));
		cr.addOrder(Order.asc("status"));
		cr.addOrder(Order.desc("updatedOn"))
		
		.setFirstResult((page-1)*rows)
    			.setMaxResults(rows);
		List<CampaignDo> campaignDos = cr.list();
		return campaignDos;
	}
	
	@Override
	public Long countCampaignDosByAdmin(){
		
		Criteria c = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class);
		c.add(Restrictions.or(Restrictions.eq("status", CampaignDo.Status.Enabled.name()),Restrictions.eq("status", CampaignDo.Status.Paused.name())));
    	c.setProjection(Projections.rowCount());
    	if(c!=null)

			return (Long) c.uniqueResult();
		return null;
	}
	
	@Override
	public Long countCampaignDosByUserDo(UserDo userDo){
		
		Criteria c = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class);
		c.add(Restrictions.or(Restrictions.eq("status", CampaignDo.Status.Enabled.name()),Restrictions.eq("status", CampaignDo.Status.Paused.name())));
		c.add(Restrictions.eq("userDo", userDo));
		c.setProjection(Projections.rowCount());
		if(c!=null)
			return (Long) c.uniqueResult();
		return null;
	}

	@Override
	public List<CampaignDo> getCampaignDosListForAdminBySearchKey(String key) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class)
				.add(Restrictions.or(Restrictions.eq("status", CampaignDo.Status.Enabled.name()),Restrictions.eq("status", CampaignDo.Status.Paused.name())))
				.add(Restrictions.like("name", "%"+key+"%"));
		List<CampaignDo> rows = criteria.list();
		return rows;
	}

	@Override
	public List<CampaignDo> getCampaignDosListForAdminByPageAndSortedByColumn(Integer page, Integer rows,
			String columnName, int orderBy) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class,"campaignDo")
				.createCriteria("userDo","userDo");
		cr.add(Restrictions.or(Restrictions.eq("campaignDo.status", CampaignDo.Status.Enabled.name()),Restrictions.eq("campaignDo.status", CampaignDo.Status.Paused.name())));
		switch (columnName) {
			case "ad":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.name"));
				else
					cr.addOrder(Order.desc("campaignDo.name"));
				break;
			case "ac":
				if(orderBy==0)
					cr.addOrder(Order.asc("userDo.name"));
				else
					cr.addOrder(Order.desc("userDo.name"));
				break;
			case "st":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.status"));
				else
					cr.addOrder(Order.desc("campaignDo.status"));
				break;
			case "bu":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.budgetAmount"));
				else
					cr.addOrder(Order.desc("campaignDo.budgetAmount"));
				break;
			case "sd":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.startDate"));
				else
					cr.addOrder(Order.desc("campaignDo.startDate"));
				break;
			case "en":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.endDate"));
				else
					cr.addOrder(Order.desc("campaignDo.endDate"));
				break;
			default:
				break;
		}
		cr.setFirstResult((page-1)*rows)
    			.setMaxResults(rows);
		List<CampaignDo> campaignDos = cr.list();
		return campaignDos;
	}

	@Override
	public List<CampaignDo> getCampaignDosListByUserDoAndPageAndSortedByColumn(UserDo userDo, Integer page,
			Integer rows, String columnName, int orderBy) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class,"campaignDo")
				.createCriteria("userDo","userDo");
		cr.add(Restrictions.eq("campaignDo.userDo", userDo));
		cr.add(Restrictions.or(Restrictions.eq("campaignDo.status", CampaignDo.Status.Enabled.name()),Restrictions.eq("campaignDo.status", CampaignDo.Status.Paused.name())));
		switch (columnName) {
			case "ad":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.name"));
				else
					cr.addOrder(Order.desc("campaignDo.name"));
				break;
			case "ac":
				if(orderBy==0)
					cr.addOrder(Order.asc("userDo.name"));
				else
					cr.addOrder(Order.desc("userDo.name"));
				break;
			case "st":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.status"));
				else
					cr.addOrder(Order.desc("campaignDo.status"));
				break;
			case "bu":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.budgetAmount"));
				else
					cr.addOrder(Order.desc("campaignDo.budgetAmount"));
				break;
			case "sd":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.startDate"));
				else
					cr.addOrder(Order.desc("campaignDo.startDate"));
				break;
			case "en":
				if(orderBy==0)
					cr.addOrder(Order.asc("campaignDo.endDate"));
				else
					cr.addOrder(Order.desc("campaignDo.endDate"));
				break;
			default:
				break;
		}
		cr.setFirstResult((page-1)*rows)
    			.setMaxResults(rows);
		List<CampaignDo> campaignDos = cr.list();
		return campaignDos;
	}


	@Override
	public List<CampaignDo> getCampaignDosListCreatedToday() {
		Date today = new Date();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class,"campaignDo")
				.createCriteria("userDo","userDo")
				.add(Restrictions.le("campaignDo.createdOn", today))
				.add(Restrictions.or(Restrictions.eq("campaignDo.status", CampaignDo.Status.Enabled.name()),Restrictions.eq("campaignDo.status", CampaignDo.Status.Paused.name())))
				.add(Restrictions.gt("campaignDo.createdOn",
						UtilityMethod.convertStringYYYY_MM_DDTODateInJava(UtilityMethod.formatDateTOYYYY_MM_DD(today))
						)
				)
				.addOrder(Order.desc("campaignDo.createdOn"));
		return criteria.list();
	}

	@Override
	public List<CampaignDo> getCampaignDosListExpireToday() {
		Date today = new Date();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CampaignDo.class)
				.add(Restrictions.eq("endDate", UtilityMethod.convertStringYYYY_MM_DDTODateInJava(UtilityMethod.formatDateTOYYYY_MM_DD(today))))
				.add(Restrictions.or(Restrictions.eq("status", CampaignDo.Status.Enabled.name()),Restrictions.eq("status", CampaignDo.Status.Paused.name())))
				.addOrder(Order.desc("endDate"));
		return criteria.list();
	}

}
