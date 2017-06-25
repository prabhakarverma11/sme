package com.dq.arq.sme.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.dao.ProductCategoryDao;
import com.dq.arq.sme.domain.ProductCategoryDo;

@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService{

	@Autowired
	ProductCategoryDao productCategoryDao;
	
	@Override
	public Integer saveProductCategoryDo(ProductCategoryDo productCategoryDo) {
		
		Integer id = productCategoryDao.saveProductCategoryDo(productCategoryDo);
		return id;
	}

	@Override
	public List<ProductCategoryDo> getProductCategoryDosList() {
		
		return productCategoryDao.getProductCategoryDosList();
	}

	@Override
	public ProductCategoryDo getProductCategoryDoById(Integer id) {
		
		return productCategoryDao.getProductCategoryDoById(id);
	}

	@Override
	public ProductCategoryDo getProductCategoryDoByName(String name) {
		return productCategoryDao.getProductCategoryDoByName(name);
	}
	
}
