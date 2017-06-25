package com.dq.arq.sme.dao;

import java.util.List;

import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserRoleDo;

public interface UserDao {
	Integer saveUserDo(UserDo userDo);

	List<UserDo> getUserDosList();

	UserDo getUserDoById(Integer userId);
	
	UserDo getUserDoByEmail(String email);
	
	UserDo getUserDoByName(String name);
	
	void updateUser(UserDo userDo);
	
	void createUserRole(UserRoleDo userRoleDo);

	UserRoleDo getUserRoleDoByUserDo(UserDo userDo);
	
	Long countUserDos();
	
	List<UserDo> getUserDosListByPage(int page);

	public List<UserRoleDo> getClientsList();

	List<Object[]> getUserRoleDosListBySearch(String key);

	List<UserDo> getUserDosListByPageAndSortedByColumn(Integer page, String columnName, Integer orderBy);
}
