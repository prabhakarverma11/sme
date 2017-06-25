package com.dq.arq.sme.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.dao.UserDao;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserRoleDo;

@Service
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	UserDao userDao;
	
	@Override
	public Integer saveUserDo(UserDo userDo) {
		
		Integer id = userDao.saveUserDo(userDo);
		return id;
	}

	@Override
	public List<UserDo> getUserDosList() {
		
		return userDao.getUserDosList();
	}

	@Override
	public UserDo getUserDoById(Integer id) {
		
		return userDao.getUserDoById(id);
	}

	@Override
	public UserDo getUserDoByName(String name) {
		return userDao.getUserDoByName(name);
	}

	@Override
	public void updateUser(UserDo userDo) {
		userDao.updateUser(userDo);
	}

	@Override
	public UserDo getUserDoByEmail(String email) {
		return userDao.getUserDoByEmail(email);
	}
	
	@Override
	public void createUserRole(UserDo userDo) {
		UserRoleDo userRoleDo = new UserRoleDo();
		userRoleDo.setRole("ROLE_USER");
		userRoleDo.setUserDo(userDo);
		userRoleDo.setCreatedBy(userDo.getCreatedBy());
		userRoleDo.setCreatedOn(userDo.getCreatedOn());
		userRoleDo.setUpdatedBy(userDo.getUpdatedBy());
		userRoleDo.setUpdatedOn(userDo.getUpdatedOn());
		userDao.createUserRole(userRoleDo);
	}

	@Override
	public UserRoleDo getUserRoleDoByUserDo(UserDo userDo) {
		// TODO Auto-generated method stub
		return userDao.getUserRoleDoByUserDo(userDo);
	}

	@Override
	public Long countUserDos() {
		return userDao.countUserDos();
	}

	@Override
	public List<UserDo> getUserDosListByPage(int page) {
		return userDao.getUserDosListByPage(page);
	}
	
	@Override
	public List<UserRoleDo> getClientsList()
	{
		return userDao.getClientsList();
	}

	@Override
	public List<Object[]> getUserDosListBySearch(String key) {
		return userDao.getUserRoleDosListBySearch(key);
	}

	@Override
	public List<UserDo> getUserDosListByPageAndSortedByColumn(Integer page, String columnName, Integer orderBy) {
		return userDao.getUserDosListByPageAndSortedByColumn(page,columnName,orderBy);
	}
	
	
}
