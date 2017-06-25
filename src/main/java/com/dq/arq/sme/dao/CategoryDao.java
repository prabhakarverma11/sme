package com.dq.arq.sme.dao;

import java.util.List;

import com.dq.arq.sme.domain.CategoryDo;

public interface CategoryDao {

	Integer saveCategoryDo(CategoryDo categoryDo);

	List<CategoryDo> getCategoryDosList();

	CategoryDo getCategoryDoById(Integer categoryId);
	
	CategoryDo getCategoryDoByName(String name);
	
	 List<String> getPrimeCategories();
	
}
