package com.dq.arq.sme.services;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dq.arq.sme.adwordapi.KeywordAdwordApi;
import com.dq.arq.sme.dao.KeywordDao;
import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.CampaignDo;
import com.dq.arq.sme.domain.KeywordDo;
import com.dq.arq.sme.domain.ProductCategoryDo;
import com.dq.arq.sme.domain.UserDo;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;

@Service
@Transactional
public class KeywordServiceImpl implements KeywordService{

final static Logger logger = LoggerFactory.getLogger(KeywordServiceImpl.class);
	
	@Autowired
	KeywordDao keywordDao;
	
	@Autowired
	CampaignService campaignService;
	
	@Override
	public Integer saveKeywordDo(KeywordDo keywordDo) {
		
		Integer id = keywordDao.saveKeywordDo(keywordDo);
		logger.info("Keyword saved with id: "+id);
		return id;
	}
	

	@Override
	public void saveKeywordDos(List<KeywordDo> keywordDos) {
		
		Integer id=0;
		for(KeywordDo keywordDo:keywordDos)
		{
			id = saveKeywordDo(keywordDo);
			logger.info("Keyword saved with id: "+id);
		}
		
	}
	
	
	@Override
	public List<KeywordDo> getKeywordDosList() {
		
		return keywordDao.getKeywordDosList();
	}

	@Override
	public KeywordDo getKeywordDoById(Integer id) {
		
		return keywordDao.getKeywordDoById(id);
	}
	
	@Override
	public KeywordDo getKeywordDoByName(String keywordName) {
		return keywordDao.getKeywordDoByName(keywordName);
	}
	
	@Override
	public List<KeywordDo> getKeywordDosListByAdgroupDo(AdgroupDo adgroupDo) {
		
		return keywordDao.getKeywordDosListByAdgroupDo(adgroupDo);
	}


	@Override
	public void deleteKeywordDo(KeywordDo keywordDo) {
		keywordDao.deleteKeywordDo(keywordDo);
		logger.info("Keyword with id "+keywordDo.getId()+" deleted successfully");
	}
	
	@Override
	public void deleteKeywordDos(List<KeywordDo> keywordDos) {
		
		for(KeywordDo keywordDo:keywordDos)
		{
			keywordDao.deleteKeywordDo(keywordDo);
			logger.info("Keyword with id "+keywordDo.getId()+" deleted successfully");
		}
	}
	
	@Override
	public Long countKeywordDosByAdgroupDo(AdgroupDo adgroupDo){
		return keywordDao.countKeywordDosByAdgroupDo(adgroupDo);
	}

	@Override
	public List<KeywordDo> getKeywordDosListByApiId(List<KeywordDo> keywordDos){
		return keywordDao.getKeywordDosListByApiId(keywordDos);
	}

	
	@Override
	public void updateKeywordDo(KeywordDo keywordDo){
		keywordDao.updateKeywordDo(keywordDo);
	}
	
	public void updateKeywordDos(List<KeywordDo> keywordDos){
		for(KeywordDo keywordDo:keywordDos)
		{
			keywordDao.updateKeywordDo(keywordDo);
		}
	}
	
	@Override
	public int addNumberOfKeywords(AdgroupDo adgroupDo,UserDo userDo,ProductCategoryDo productCategoryDo,int count) throws ApiException, UnsupportedEncodingException, RemoteException
	{
		KeywordAdwordApi keywordAdwordApi = new KeywordAdwordApi();
		CampaignDo campaignDo = campaignService.getCampaignDoById(adgroupDo.getCampaignDo().getId());
		List<String> locationApiIds = new ArrayList<String>();
		if(campaignDo.getLocationInclude()!=null && !campaignDo.getLocationInclude().equals(""))
		{
			for(String locationId : campaignDo.getLocationInclude().split(","))
			{
				if(!locationId.equals(""))
				{
					if(campaignDo.getLocationIncludeCriteria()==4)
					{
						locationApiIds.add(locationId.split(":")[0]);
					}
					else	
						locationApiIds.add(locationId);
				}
			}
		}
		List<KeywordDo> moreKeywordDosToAdd = keywordAdwordApi.getKeywordIdeas(adgroupDo.getProductName(),productCategoryDo.getId(),count,locationApiIds);
		for(KeywordDo keywordDo : moreKeywordDosToAdd)
		{
			keywordDo.setMatchType("PHRASE");
			keywordDo.setStatus(KeywordDo.Status.Enabled.name());//?? set bid also
		}
		keywordAdwordApi.addKeywords(adgroupDo,moreKeywordDosToAdd);
		for(KeywordDo keywordDo : moreKeywordDosToAdd)
		{
			keywordDo.setAdgroupDo(adgroupDo);
			keywordDo.setAdgroupApiId(adgroupDo.getApiId());
			keywordDo.setCreatedBy(userDo.getName());
			keywordDo.setCreatedOn(new Date());
			keywordDo.setUpdatedBy(userDo.getName());
			keywordDo.setUpdatedOn(new Date());
		}

		saveKeywordDos(moreKeywordDosToAdd);
		return moreKeywordDosToAdd.size();
	}
}
