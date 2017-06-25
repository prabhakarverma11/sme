package com.dq.arq.sme.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.dao.UserSessionDao;
import com.dq.arq.sme.domain.UserDo;
import com.dq.arq.sme.domain.UserSessionDo;

@Service
@Transactional
public class UserSessionServiceImpl implements UserSessionService{

	@Autowired
	UserSessionDao userSessionDao;
	
	@Override
	public Integer saveUserSessionDo(UserSessionDo userSessionDo) {
		return userSessionDao.saveUserSessionDo(userSessionDo);
	}

	@Override
	public List<UserSessionDo> getUserSessionDosList() {
		return userSessionDao.getUserSessionDosList();
	}

	@Override
	public UserSessionDo getUserSessionDoById(Integer id) {
		return userSessionDao.getUserSessionDoById(id);
	}
	
	@Override
	public UserSessionDo getUserSessionDoBySessionId(String sessionId) {
		return userSessionDao.getUserSessionDoBySessionId(sessionId);
	}
	
	@Override
	public List<UserSessionDo> getUserSessionDosByUserDosandSessionIds(List<UserDo> userDos,List<String> sessionIds) {
		return userSessionDao.getUserSessionDosByUserDosandSessionIds(userDos,sessionIds);
	}
	
	@Override
	public UserSessionDo checkIfUserLoggedIn(UserDo userDo) {
		return userSessionDao.checkIfUserLoggedIn(userDo);
	}

	@Override
	public void updateUserSessionDo(UserSessionDo userSessionDo) {
		userSessionDao.updateUserSessionDo(userSessionDo);
		
	}

	@Override
	public List<UserSessionDo> getUserSessionDosByUserDo(UserDo userDo) {

		return userSessionDao.getUserSessionDosByUserDo(userDo);
	}

	@Override
	public UserSessionDo getLastUserSessionDoByUserDo(UserDo userDo) {

		return userSessionDao.getLastUserSessionDoByUserDo(userDo);
	}

	@Override
	public Long countUserSessionDosByUserDo(UserDo userDo) {

		return userSessionDao.countUserSessionDosByUserDo(userDo);
	}

}
