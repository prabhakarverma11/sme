package com.dq.arq.sme.services;

import java.rmi.RemoteException;
import java.util.List;

import com.dq.arq.sme.adwordapi.AdAdwordApi;
import com.dq.arq.sme.adwordapi.AdgroupAdwordApi;
import com.dq.arq.sme.adwordapi.KeywordAdwordApi;
import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.UserDo;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;

public interface AdgroupService {


	Integer saveAdgroupDo(AdgroupDo adgroupDo);
	
	List<AdgroupDo> getAdgroupDosList();

	AdgroupDo getAdgroupDoById(Integer id);
	
	CampaignDo getCampaignDoByAdgroupId(Integer adgroupId);

	List<AdgroupDo> getAdgroupDosListByCampaignDo(CampaignDo campaignDo);

	AdgroupDo getAdgroupDoByName(String name);

	void updateAdgroup(AdgroupDo adgroupDo);

	Long countAdGroupDosByCampaign(CampaignDo campaignDo);

	List<AdgroupDo> getAdgroupDosListByCampaignDoAndPage(CampaignDo campaignDo,int page,int rows);

	List<AdgroupDo> getAdgroupDosListByCampaignDoAndPageAndSortedByColumn(CampaignDo campaignDo, Integer page,
			Integer adgroupPerPage, String columnName, Integer orderBy);

	public void  syncAdgroupDetails(CampaignDo campaignDo,AdgroupDo adgroupDo,UserDo userDo) throws ApiException, RemoteException;

	Long countAdGroupDosForAdmin();

	Long countAdGroupDosByUserDo(UserDo userDo);

	List<AdgroupDo> getAdgroupDosListForAdminByPage(Integer page, Integer adgroupPerPage);

	List<AdgroupDo> getAdgroupDosListForAdminByPageAndSortedByColumn(Integer page, Integer adgroupPerPage,String columnName,Integer orderBy);

	List<AdgroupDo> getAdgroupDosListByUserDoAndPage(UserDo userDo,Integer page, Integer adgroupPerPage);

	List<AdgroupDo> getAdgroupDosListByUserDoAndPageAndSortedByColumn(UserDo userDo,Integer page, Integer adgroupPerPage,String columnName,Integer orderBy);


}
