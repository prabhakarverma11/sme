package com.dq.arq.sme.services;

import java.util.List;

import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserRoleDo;

public interface UserService {

	Integer saveUserDo(UserDo userDo);

	List<UserDo> getUserDosList();

	UserDo getUserDoById(Integer id);
	
	UserDo getUserDoByEmail(String email);
	
	UserDo getUserDoByName(String name);
	
	void updateUser(UserDo userDo);
	
	void createUserRole(UserDo userDo);

	UserRoleDo getUserRoleDoByUserDo(UserDo userDo);
	
	Long countUserDos();
	
	List<UserDo> getUserDosListByPage(int page);
	
	public List<UserRoleDo> getClientsList();

	List<Object[]> getUserDosListBySearch(String key);

	List<UserDo> getUserDosListByPageAndSortedByColumn(Integer page, String columnName, Integer orderBy);
	
}
