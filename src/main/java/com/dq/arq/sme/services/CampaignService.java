package com.dq.arq.sme.services;

import java.util.List;

import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.UserDo;

public interface CampaignService {

	Integer saveCampaignDo(CampaignDo campaignDo);

	CampaignDo getCampaignDoById(Integer id);
	
	CampaignDo getCampaignDoByApiId(Long apiId);
	
	CampaignDo getCampaignDoByName(String name);
	
	void updateCampaign(CampaignDo campaignDo);
	
	List<CampaignDo> getCampaignDosListByUserDo(UserDo userDo);
	
	List<CampaignDo> getCampaignDosListByUserDoAndPage(UserDo userDo,int page,int rows);
	
	List<CampaignDo> getCampaignDosListForAdmin();
	
	List<CampaignDo> getCampaignDosListForAdminByPage(int page,int rows);
	
	Long countCampaignDosByAdmin();
	
	Long countCampaignDosByUserDo(UserDo userDo);

	List<CampaignDo> getCampaignDosListForAdminBySearchKey(String key);

	List<CampaignDo> getCampaignDosListForAdminByPageAndSortedByColumn(Integer page, Integer campaignsPerPage,
			String columnName, int orderBy);

	
	List<CampaignDo> getCampaignDosListByUserDoAndPageAndSortedByColumn(UserDo userDo, Integer page,
			Integer campaignsPerPage, String columnName, int orderBy);

	List<CampaignDo> getCampaignDosListCreatedToday();

	List<CampaignDo> getCampaignDosListExpireToday();
	
	
}
