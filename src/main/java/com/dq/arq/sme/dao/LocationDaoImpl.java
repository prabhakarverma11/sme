package com.dq.arq.sme.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dq.arq.sme.domain.LocationDo;

@Repository
public class LocationDaoImpl implements LocationDao{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Integer saveLocationDo(LocationDo locationDo) {


		sessionFactory.getCurrentSession().save(locationDo);
		return locationDo.getCriteriaId();


	}

	@Override
	public List<LocationDo> getLocationDosList() {

		String hql = "FROM LocationDo order by canonicalName DESC";
		List<LocationDo> locationDos = sessionFactory.getCurrentSession().createQuery(hql)
				.list();
		return locationDos;
	}

	@Override
	public LocationDo getLocationDoById(Integer locationId) {
		LocationDo locationDo = (LocationDo) sessionFactory.getCurrentSession().get(LocationDo.class, locationId);
		return locationDo;
	}

	@Override
	public LocationDo getLocationDoByName(String name) {
		String hql = "FROM LocationDo where name = '"+name+"'";
		List<LocationDo> locationDos = sessionFactory.getCurrentSession().createQuery(hql).list();
		return locationDos.get(0);
	}
	
	@Override
	public List<LocationDo> getLocationsInIndia()
	{
		String hql = "FROM LocationDo where countryCode = 'IN' order by canonicalName desc";
		List<LocationDo> locationDos = sessionFactory.getCurrentSession().createQuery(hql).list();
			
			  return locationDos;
	}
	
	@Override
	public List<LocationDo> getCountryList()
	{
		String hql = " from LocationDo where canonicalName=name order by name";
		List<LocationDo> locationDos = sessionFactory.getCurrentSession().createQuery(hql).list();
		
		return locationDos;
	}


}


