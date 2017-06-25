package com.dq.arq.sme.services;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.adwordapi.AdAdwordApi;
import com.dq.arq.sme.adwordapi.AdgroupAdwordApi;
import com.dq.arq.sme.adwordapi.KeywordAdwordApi;
import com.dq.arq.sme.dao.AdgroupDao;
import com.dq.arq.sme.domain.AdDo;
import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.KeywordDo;
import com.dq.arq.sme.domain.UserDo;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;


@Service
@Transactional
public class AdgroupServiceImpl implements AdgroupService{

	final static Logger logger = LoggerFactory.getLogger(AdgroupServiceImpl.class);

	@Autowired
	AdgroupDao adgroupDao;

	@Autowired
	KeywordService keywordService;

	@Autowired
	AdService adService;

	@Override
	public Integer saveAdgroupDo(AdgroupDo adgroupDo) {

		Integer id = adgroupDao.saveAdgroupDo(adgroupDo);
		logger.info("Adgroup saved with id: "+id);
		return id;
	}

	@Override
	public List<AdgroupDo> getAdgroupDosList() {

		return adgroupDao.getAdgroupDosList();
	}

	@Override
	public AdgroupDo getAdgroupDoById(Integer id) {

		return adgroupDao.getAdgroupDoById(id);
	}
	
	@Override
	public CampaignDo getCampaignDoByAdgroupId(Integer adgroupId) {
		return adgroupDao.getCampaignDoByAdgroupId(adgroupId);
	}
	
	@Override
	public List<AdgroupDo> getAdgroupDosListByCampaignDo(CampaignDo campaignDo) {

		return adgroupDao.getAdgroupDosListByCampaignDo(campaignDo);
	}

	@Override
	public AdgroupDo getAdgroupDoByName(String name) {
		return adgroupDao.getAdgroupDoByName(name);
	}

	@Override
	public void updateAdgroup(AdgroupDo adgroupDo) {
		adgroupDao.updateAdgroup(adgroupDo);
		logger.info("Adgroup with id "+adgroupDo.getId()+" updated successfully");
	}

	@Override
	public Long countAdGroupDosByCampaign(CampaignDo campaignDo){
		return adgroupDao.countAdGroupDosByCampaign(campaignDo);
	}

	@Override
	public  List<AdgroupDo> getAdgroupDosListByCampaignDoAndPage(CampaignDo campaignDo,int page,int rows) {
		return adgroupDao.getAdgroupDosListByCampaignDoAndPage(campaignDo, page,rows);
	}

	@Override
	public List<AdgroupDo> getAdgroupDosListByCampaignDoAndPageAndSortedByColumn(CampaignDo campaignDo, Integer page,
			Integer adgroupPerPage, String columnName, Integer orderBy) {
		return adgroupDao.getAdgroupDosListByCampaignDoAndPageAndSortedByColumn(campaignDo,page,adgroupPerPage,columnName,orderBy);
	}
	
	@Override
	public void  syncAdgroupDetails(CampaignDo campaignDo,AdgroupDo adgroupDo,UserDo userDo) throws ApiException, RemoteException
	{

		AdgroupAdwordApi adgroupAdwordApi = new AdgroupAdwordApi();
		KeywordAdwordApi KeywordAdwordApi = new KeywordAdwordApi();
		AdAdwordApi adAdwordApi=new AdAdwordApi();
		if(adgroupAdwordApi.syncExistingAdgroupDetails(campaignDo,adgroupDo))
		{
			updateAdgroup(adgroupDo);
			//sync keywords

			List<KeywordDo> existingkeywordDos = keywordService.getKeywordDosListByAdgroupDo(adgroupDo);
			keywordService.deleteKeywordDos(existingkeywordDos);
			List<KeywordDo> newKeywordDos = KeywordAdwordApi.getKeywords(adgroupDo);
			for(KeywordDo keywordDo : newKeywordDos)
			{
				keywordDo.setAdgroupDo(adgroupDo);
				keywordDo.setAdgroupApiId(adgroupDo.getApiId());
				keywordDo.setCreatedBy(userDo.getName());
				keywordDo.setUpdatedBy(userDo.getName());
				keywordDo.setCreatedOn(new Date());
			}

			keywordService.saveKeywordDos(newKeywordDos);

			//sync ads
			List<AdDo> adDosFromLocalDb = adService.getAdDosListByAdgroupDo(adgroupDo);
			for(AdDo adDo:adDosFromLocalDb)
			{
				adService.deleteAdDo(adDo);
			}
			List<AdDo> adDosFromAdwords = adAdwordApi.getExistingAdDetails(adgroupDo);
			for(AdDo adDo : adDosFromAdwords)
			{
				adDo.setAdgroupDo(adgroupDo);
				adDo.setAdgroupApiId(adgroupDo.getApiId());
				adDo.setCreatedBy(userDo.getName());
				adDo.setCreatedOn(new Date());
				adDo.setUpdatedBy(userDo.getName());
				adDo.setUpdatedOn(new Date());
				Integer adId = adService.saveAdDo(adDo);
				logger.info("\n+++++++++++++++ SUCCESS:: Ad with id : "+adDo.getId()+" was added +++++++++++++++");
			}
		}
		else
		{
			//This means no such adgroup was ever added in adwords account, API ID was not found their db
			adgroupDo.setApiId(null);  
			adgroupDo.setStatus(AdgroupDo.Status.Unknown.name());  
		}

	}

	@Override
	public Long countAdGroupDosForAdmin() {
		return adgroupDao.countAdGroupDosForAdmin();
	}

	@Override
	public Long countAdGroupDosByUserDo(UserDo userDo) {
		return adgroupDao.countAdGroupDosByUserDo(userDo);
	}

	@Override
	public List<AdgroupDo> getAdgroupDosListForAdminByPage(Integer page, Integer adgroupPerPage) {
		return adgroupDao.getAdgroupDosListForAdminByPage(page,adgroupPerPage);
	}

	@Override
	public List<AdgroupDo> getAdgroupDosListForAdminByPageAndSortedByColumn(Integer page, Integer adgroupPerPage,String columnName,Integer orderBy) {
		return adgroupDao.getAdgroupDosListForAdminByPageAndSortedByColumn(page,adgroupPerPage,columnName,orderBy);
	}
	
	@Override
	public List<AdgroupDo> getAdgroupDosListByUserDoAndPage(UserDo userDo,Integer page, Integer adgroupPerPage) {
		return adgroupDao.getAdgroupDosListByUserDoAndPage(userDo,page,adgroupPerPage);
	}

	@Override
	public List<AdgroupDo> getAdgroupDosListByUserDoAndPageAndSortedByColumn(UserDo userDo,Integer page, Integer adgroupPerPage,String columnName,Integer orderBy) {
		return adgroupDao.getAdgroupDosListByUserDoAndPageAndSortedByColumn(userDo,page,adgroupPerPage,columnName,orderBy);
	}

	

}
