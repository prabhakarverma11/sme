package com.dq.arq.sme.dao;

import java.util.List;

import com.dq.arq.sme.domain.ProductCategoryDo;

public interface ProductCategoryDao {
	
	Integer saveProductCategoryDo(ProductCategoryDo productCategoryDo);

	List<ProductCategoryDo> getProductCategoryDosList();

	ProductCategoryDo getProductCategoryDoById(Integer productCategoryId);
	
	ProductCategoryDo getProductCategoryDoByName(String name);
	
}
