package com.dq.arq.sme.dao;

import java.util.List;

import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserSessionDo;

public interface UserSessionDao {

	Integer saveUserSessionDo(UserSessionDo userDo);

	List<UserSessionDo> getUserSessionDosList();

	UserSessionDo getUserSessionDoById(Integer id);
	
	UserSessionDo getUserSessionDoBySessionId(String id);
	
	List<UserSessionDo> getUserSessionDosByUserDosandSessionIds(List<UserDo> userDos,List<String> sessionIds);
	
	UserSessionDo checkIfUserLoggedIn(UserDo userDo);
	
	void updateUserSessionDo(UserSessionDo userSessionDo);
	
	List<UserSessionDo> getUserSessionDosByUserDo(UserDo userDo);
	
	UserSessionDo getLastUserSessionDoByUserDo(UserDo userDo);
	
	Long countUserSessionDosByUserDo(UserDo userDo);
}
