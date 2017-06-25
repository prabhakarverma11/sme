package com.dq.arq.sme.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dq.arq.sme.domain.CategoryDo;
import com.dq.arq.sme.domain.LocationDo;

@Repository
public class CategoryDaoImpl implements CategoryDao{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Integer saveCategoryDo(CategoryDo categoryDo) {


		sessionFactory.getCurrentSession().save(categoryDo);
		return categoryDo.getId();


	}

	@Override
	public List<CategoryDo> getCategoryDosList() {

		String hql = "FROM CategoryDo order by canonicalName DESC";
		List<CategoryDo> categoryDos = sessionFactory.getCurrentSession().createQuery(hql)
				.list();
		return categoryDos;
	}

	@Override
	public CategoryDo getCategoryDoById(Integer categoryId) {
		CategoryDo categoryDo = (CategoryDo) sessionFactory.getCurrentSession().get(CategoryDo.class, categoryId);
		return categoryDo;
	}

	@Override
	public CategoryDo getCategoryDoByName(String name) {
		String hql = "FROM CategoryDo where name = "+name;
		List<CategoryDo> categoryDos = sessionFactory.getCurrentSession().createQuery(hql).list();
		return categoryDos.get(0);
	}
	
	@Override
	public List<String> getPrimeCategories()
	{
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(CategoryDo.class)
			    .setProjection(Projections.distinct(Projections.projectionList()
			      .add(Projections.property("primeCategory"))))
			      .addOrder(Order.asc("primeCategory"));
			
			  return (List<String>)cr.list();
		
	}
	
}
