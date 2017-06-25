package com.dq.arq.sme.services;

import java.util.List;

import com.dq.arq.sme.domain.ProductCategoryDo;

public interface ProductCategoryService {
	
	Integer saveProductCategoryDo(ProductCategoryDo productCategoryDo);

	List<ProductCategoryDo> getProductCategoryDosList();

	ProductCategoryDo getProductCategoryDoById(Integer productCategoryId);
	
	ProductCategoryDo getProductCategoryDoByName(String name);
	
	
	
}
