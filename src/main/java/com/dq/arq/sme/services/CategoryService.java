package com.dq.arq.sme.services;

import java.util.List;

import com.dq.arq.sme.domain.CategoryDo;

public interface CategoryService {

	Integer saveCategoryDo(CategoryDo categoryDo);

	List<CategoryDo> getCategoryDosList();

	CategoryDo getCategoryDoById(Integer id);
	
	CategoryDo getCategoryDoByName(String name);
	
	List<String> getPrimeCategories();
}
