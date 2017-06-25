package com.dq.arq.sme.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dq.arq.sme.domain.ProductCategoryDo;

@Repository
public class ProductCategoryDaoImpl implements ProductCategoryDao{

	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Integer saveProductCategoryDo(ProductCategoryDo productProductCategoryDo) {


		sessionFactory.getCurrentSession().save(productProductCategoryDo);
		return productProductCategoryDo.getId();


	}

	@Override
	public List<ProductCategoryDo> getProductCategoryDosList() {

		String hql = "FROM ProductCategoryDo  order by category";
		List<ProductCategoryDo> productProductCategoryDos = sessionFactory.getCurrentSession().createQuery(hql)
				.list();
		return productProductCategoryDos;
	}

	@Override
	public ProductCategoryDo getProductCategoryDoById(Integer productProductCategoryId) {
		ProductCategoryDo productProductCategoryDo = (ProductCategoryDo) sessionFactory.getCurrentSession().get(ProductCategoryDo.class, productProductCategoryId);
		return productProductCategoryDo;
	}

	@Override
	public ProductCategoryDo getProductCategoryDoByName(String name) {
		String hql = "FROM ProductCategoryDo where category = '"+name+"'";
		List<ProductCategoryDo> productProductCategoryDos = sessionFactory.getCurrentSession().createQuery(hql).list();
		return productProductCategoryDos.get(0);
	}
	
}
