package com.dq.arq.sme.services;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.dao.CampaignDao;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.UserDo;

@Service
@Transactional
public class CampaignServiceImpl implements CampaignService{

	@Autowired
	CampaignDao campaignDao;
	
	final static Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);
	
	@Override
	public Integer saveCampaignDo(CampaignDo campaignDo) {
		
		Integer id = campaignDao.saveCampaignDo(campaignDo);
		logger.info("Campaign saved with id: "+id);
		return id;
	}

	@Override
	public CampaignDo getCampaignDoById(Integer id) {
		
		return campaignDao.getCampaignDoById(id);
	}
	
	@Override
	public CampaignDo getCampaignDoByApiId(Long apiId){
		return campaignDao.getCampaignDoByApiId(apiId);
	}

	@Override
	public CampaignDo getCampaignDoByName(String name) {
		return campaignDao.getCampaignDoByName(name);
	}

	@Override
	public void updateCampaign(CampaignDo campaignDo) {
		campaignDao.updateCampaign(campaignDo);
		logger.info("Campaign with id: "+campaignDo.getId()+" updated successfully");
	}


	@Override
	public List<CampaignDo> getCampaignDosListByUserDo(UserDo userDo) {
		
		return campaignDao.getCampaignDosListByUserDo(userDo);
	}
	
	@Override
	public List<CampaignDo> getCampaignDosListByUserDoAndPage(UserDo userDo,int page,int rows) {
		
		return campaignDao.getCampaignDosListByUserDoAndPage(userDo,page,rows);
	}
	
	@Override
	public List<CampaignDo> getCampaignDosListForAdmin() {
		return campaignDao.getCampaignDosListForAdmin();
	}
	
	@Override
	public List<CampaignDo> getCampaignDosListForAdminByPage(int page,int rows) {
		return campaignDao.getCampaignDosListForAdminByPage(page,rows);
	}

	@Override
	public Long countCampaignDosByAdmin() {
		return campaignDao.countCampaignDosByAdmin();
	}

	@Override
	public Long countCampaignDosByUserDo(UserDo userDo) {

		return campaignDao.countCampaignDosByUserDo(userDo);
	}

	@Override
	public List<CampaignDo> getCampaignDosListForAdminBySearchKey(String key) {
		return campaignDao.getCampaignDosListForAdminBySearchKey(key);
	}

	@Override
	public List<CampaignDo> getCampaignDosListForAdminByPageAndSortedByColumn(Integer page, Integer campaignsPerPage,
			String columnName, int orderBy) {
		return campaignDao.getCampaignDosListForAdminByPageAndSortedByColumn(page,campaignsPerPage,columnName,orderBy);
	}

	@Override
	public List<CampaignDo> getCampaignDosListByUserDoAndPageAndSortedByColumn(UserDo userDo, Integer page,
			Integer campaignsPerPage, String columnName, int orderBy) {
		return campaignDao.getCampaignDosListByUserDoAndPageAndSortedByColumn(userDo,page,campaignsPerPage,columnName,orderBy);
	}

	
	@Override
	public List<CampaignDo> getCampaignDosListCreatedToday() {
		return campaignDao.getCampaignDosListCreatedToday();
	}

	@Override
	public List<CampaignDo> getCampaignDosListExpireToday() {
		return campaignDao.getCampaignDosListExpireToday();
	}

	
}
