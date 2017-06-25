package com.dq.arq.sme.dao;

import java.util.List;

import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.UserDo;

public interface AdgroupDao {

	Integer saveAdgroupDo(AdgroupDo adgroupDo);

	List<AdgroupDo> getAdgroupDosList();

	CampaignDo getCampaignDoByAdgroupId(Integer adgroupId);
	
	AdgroupDo getAdgroupDoById(Integer adgroupId);

	List<AdgroupDo> getAdgroupDosListByCampaignDo(CampaignDo campaignDo);
	
	AdgroupDo getAdgroupDoByName(String name);
	
	void updateAdgroup(AdgroupDo adgroupDo);
	
	Long countAdGroupDosByCampaign(CampaignDo campaignDo);
	
	List<AdgroupDo> getAdgroupDosListByCampaignDoAndPage(CampaignDo campaignDo,int page,int rows);

	List<AdgroupDo> getAdgroupDosListByCampaignDoAndPageAndSortedByColumn(CampaignDo campaignDo, Integer page,
			Integer adgroupPerPage, String columnName, Integer orderBy);
	
	Long countAdGroupDosForAdmin();

	Long countAdGroupDosByUserDo(UserDo userDo);

	List<AdgroupDo> getAdgroupDosListForAdminByPage(Integer page, Integer adgroupPerPage);
	
	List<AdgroupDo> getAdgroupDosListForAdminByPageAndSortedByColumn(Integer page, Integer adgroupPerPage,
			String columnName, Integer orderBy);

	List<AdgroupDo> getAdgroupDosListByUserDoAndPage(UserDo userDo,Integer page, Integer adgroupPerPage);

	List<AdgroupDo> getAdgroupDosListByUserDoAndPageAndSortedByColumn(UserDo userDo, Integer page,
			Integer adgroupPerPage, String columnName, Integer orderBy);


}
