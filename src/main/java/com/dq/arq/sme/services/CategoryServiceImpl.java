package com.dq.arq.sme.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.dao.CategoryDao;
import com.dq.arq.sme.domain.CategoryDo;


@Service
@Transactional
public class CategoryServiceImpl implements CategoryService{

	@Autowired
	CategoryDao categoryDao;
	
	@Override
	public Integer saveCategoryDo(CategoryDo categoryDo) {
		
		Integer id = categoryDao.saveCategoryDo(categoryDo);
		return id;
	}

	@Override
	public List<CategoryDo> getCategoryDosList() {
		
		return categoryDao.getCategoryDosList();
	}

	@Override
	public CategoryDo getCategoryDoById(Integer id) {
		
		return categoryDao.getCategoryDoById(id);
	}

	@Override
	public CategoryDo getCategoryDoByName(String name) {
		return categoryDao.getCategoryDoByName(name);
	}
	
	@Override
	public List<String> getPrimeCategories(){
		return categoryDao.getPrimeCategories();
	}
	
}
