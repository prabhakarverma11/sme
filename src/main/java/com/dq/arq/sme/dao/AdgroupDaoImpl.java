package com.dq.arq.sme.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.util.UtilConstants;

@Repository
public class AdgroupDaoImpl implements AdgroupDao{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Integer saveAdgroupDo(AdgroupDo adgroupDo) {
		
		sessionFactory.getCurrentSession().save(adgroupDo);
		return adgroupDo.getId();
		
	}

	@Override
	public List<AdgroupDo> getAdgroupDosList() {

		List<AdgroupDo> adgroupDos = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class)
    			.add(Restrictions.or(Restrictions.eq("status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("status", AdgroupDo.Status.Paused.name())))
    			.addOrder(Order.desc("updatedOn"))
    			.list();
		return adgroupDos;
	}

	@Override
	public AdgroupDo getAdgroupDoById(Integer adgroupId) {
		AdgroupDo adgroupDo = (AdgroupDo) sessionFactory.getCurrentSession().get(AdgroupDo.class, adgroupId);
		return adgroupDo;
	}
	
	@Override
	public CampaignDo getCampaignDoByAdgroupId(Integer adgroupId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class,"adgroupDo")
				.add(Restrictions.eq("adgroupDo.id", adgroupId))
				.createCriteria("campaignDo","campaignDo");
		List<AdgroupDo> adgroupDos = criteria.list();
		if(adgroupDos.size()>0) {
			return adgroupDos.get(0).getCampaignDo();
		}
		return null;
	}
	
	@Override
	public List<AdgroupDo> getAdgroupDosListByCampaignDo(CampaignDo campaignDo) {
		List<AdgroupDo> adgroupDos = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class)
    			.add(Restrictions.eq("campaignDo", campaignDo))
    			.add(Restrictions.or(Restrictions.eq("status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("status", AdgroupDo.Status.Paused.name())))
    			.addOrder(Order.asc("status"))
    			.addOrder(Order.desc("updatedOn"))
    			.list();
		
		return adgroupDos;
	}

	@Override
	public AdgroupDo getAdgroupDoByName(String name) {
//		String hql = "FROM AdgroupDo where productName = '"+name+"' and status < 3";
//		List<AdgroupDo> adgroupDos = sessionFactory.getCurrentSession().createQuery(hql).list();
		List<AdgroupDo> adgroupDos = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class)
    			.add(Restrictions.eq("name", name))
    			.add(Restrictions.or(Restrictions.eq("status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("status", AdgroupDo.Status.Paused.name())))
    			.addOrder(Order.desc("updatedOn"))
    			.list();
		if(adgroupDos.size()>0)
			return adgroupDos.get(0);
		else
			return null;
	}

	@Override
	public void updateAdgroup(AdgroupDo adgroupDo) {
		sessionFactory.getCurrentSession().update(adgroupDo);
		
	}
	
	@Override
	public Long countAdGroupDosByCampaign(CampaignDo campaignDo){
		
		Criteria c = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class);
		c.add(Restrictions.eq("campaignDo", campaignDo));
		c.add(Restrictions.or(Restrictions.eq("status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("status", AdgroupDo.Status.Paused.name())));
		c.setProjection(Projections.rowCount());
		Long count = (Long) c.uniqueResult();
		return count;
		
	}
	
	@Override
	public List<AdgroupDo> getAdgroupDosListByCampaignDoAndPage(CampaignDo campaignDo,int page,int rows) {
		
		List<AdgroupDo> adgroupDos = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class)
    			.add(Restrictions.eq("campaignDo", campaignDo))
    			.add(Restrictions.or(Restrictions.eq("status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("status", AdgroupDo.Status.Paused.name())))
    			.addOrder(Order.asc("status"))
    			.addOrder(Order.desc("updatedOn"))
    			.setFirstResult((page-1)*rows)
    			.setMaxResults(rows)
    			.list();
		
		return adgroupDos;
	}

	@Override
	public List<AdgroupDo> getAdgroupDosListByCampaignDoAndPageAndSortedByColumn(CampaignDo campaignDo, Integer page,
			Integer rows, String columnName, Integer orderBy) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class)
    			.add(Restrictions.eq("campaignDo", campaignDo))
    			.add(Restrictions.or(Restrictions.eq("status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("status", AdgroupDo.Status.Paused.name())));
		switch (columnName) {
			case "st":
				if(orderBy==0)
					cr.addOrder(Order.asc("status"));
				else
					cr.addOrder(Order.desc("status"));
				break;
			case "pr":
				if(orderBy==0)
					cr.addOrder(Order.asc("productName"));
				else
					cr.addOrder(Order.desc("productName"));
				break;
			case "ca":
				if(orderBy==0)
					cr.addOrder(Order.asc("categoryName"));
				else
					cr.addOrder(Order.desc("categoryName"));
				break;
			default:
				break;
		}
	List<AdgroupDo> adgroupDos = cr.setFirstResult((page-1)*rows)
		.setMaxResults(rows)
		.list();
		return adgroupDos;
	}

	@Override
	public Long countAdGroupDosForAdmin() {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class);
		c.add(Restrictions.or(Restrictions.eq("status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("status", AdgroupDo.Status.Paused.name())));
		c.setProjection(Projections.rowCount());
		Long count = (Long) c.uniqueResult();
		return count;
	}

	@Override
	public Long countAdGroupDosByUserDo(UserDo userDo) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class,"adgroupDo")
				.createCriteria("campaignDo","campaignDo")
				.add(Restrictions.eq("campaignDo.userDo", userDo));
		c.add(Restrictions.or(Restrictions.eq("adgroupDo.status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("adgroupDo.status", AdgroupDo.Status.Paused.name())));
		c.setProjection(Projections.rowCount());
		Long count = (Long) c.uniqueResult();
		return count;
	}

	@Override
	public List<AdgroupDo> getAdgroupDosListForAdminByPage(Integer page, Integer adgroupPerPage) {
		List<AdgroupDo> adgroupDos = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class,"adgroupDo")
				.createCriteria("campaignDo","campaignDo")
				.createCriteria("campaignDo.userDo","userDo")
    			.add(Restrictions.or(Restrictions.eq("adgroupDo.status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("adgroupDo.status", AdgroupDo.Status.Paused.name())))
    			.addOrder(Order.asc("adgroupDo.status"))
    			.addOrder(Order.desc("adgroupDo.updatedOn"))
    			.setFirstResult((page-1)*adgroupPerPage)
    			.setMaxResults(adgroupPerPage)
    			.list();
		
		return adgroupDos;
	}

	@Override
	public List<AdgroupDo> getAdgroupDosListForAdminByPageAndSortedByColumn(Integer page, Integer adgroupPerPage,String columnName, Integer orderBy) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class,"adgroupDo")
				.createCriteria("campaignDo","campaignDo")
				.createCriteria("campaignDo.userDo","userDo")
				.add(Restrictions.or(Restrictions.eq("adgroupDo.status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("adgroupDo.status", AdgroupDo.Status.Paused.name())));
		switch (columnName) {
			case "st":
				if(orderBy==0)
					cr.addOrder(Order.asc("adgroupDo.status"));
				else
					cr.addOrder(Order.desc("adgroupDo.status"));
				break;
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
			case "pr":
				if(orderBy==0)
					cr.addOrder(Order.asc("adgroupDo.productName"));
				else
					cr.addOrder(Order.desc("adgroupDo.productName"));
				break;
			case "ca":
				if(orderBy==0)
					cr.addOrder(Order.asc("adgroupDo.categoryName"));
				else
					cr.addOrder(Order.desc("adgroupDo.categoryName"));
				break;
			default:
				break;
		}
		List<AdgroupDo> adgroupDos = cr.setFirstResult((page-1)*adgroupPerPage).setMaxResults(adgroupPerPage).list();
		return adgroupDos;
	}
	
	@Override
	public List<AdgroupDo> getAdgroupDosListByUserDoAndPage(UserDo userDo,Integer page, Integer adgroupPerPage) {
		List<AdgroupDo> adgroupDos = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class,"adgroupDo")
				.createCriteria("campaignDo","campaignDo")
				.createCriteria("campaignDo.userDo","userDo")
				.add(Restrictions.eq("campaignDo.userDo", userDo))
				.add(Restrictions.or(Restrictions.eq("adgroupDo.status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("adgroupDo.status", AdgroupDo.Status.Paused.name())))
				.addOrder(Order.asc("adgroupDo.status"))
				.addOrder(Order.desc("adgroupDo.updatedOn"))
				.setFirstResult((page-1)*adgroupPerPage)
				.setMaxResults(adgroupPerPage)
				.list();
		
		return adgroupDos;
	}
	
	@Override
	public List<AdgroupDo> getAdgroupDosListByUserDoAndPageAndSortedByColumn(UserDo userDo,Integer page, Integer adgroupPerPage, String columnName, Integer orderBy) {
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(AdgroupDo.class,"adgroupDo")
				.createCriteria("campaignDo","campaignDo")
				.createCriteria("campaignDo.userDo","userDo")
				.add(Restrictions.eq("campaignDo.userDo", userDo))
				.add(Restrictions.or(Restrictions.eq("adgroupDo.status", AdgroupDo.Status.Enabled.name()),Restrictions.eq("adgroupDo.status", AdgroupDo.Status.Paused.name())));
		switch (columnName) {
			case "st":
				if(orderBy==0)
					cr.addOrder(Order.asc("adgroupDo.status"));
				else
					cr.addOrder(Order.desc("adgroupDo.status"));
				break;
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
			case "pr":
				if(orderBy==0)
					cr.addOrder(Order.asc("adgroupDo.productName"));
				else
					cr.addOrder(Order.desc("adgroupDo.productName"));
				break;
			case "ca":
				if(orderBy==0)
					cr.addOrder(Order.asc("adgroupDo.categoryName"));
				else
					cr.addOrder(Order.desc("adgroupDo.categoryName"));
				break;
			default:
				break;
		}
		List<AdgroupDo> adgroupDos = cr.setFirstResult((page-1)*adgroupPerPage).setMaxResults(adgroupPerPage).list();
		return adgroupDos;
	}

	
}
