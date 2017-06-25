package com.dq.arq.sme.services;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.List;

import com.dq.arq.sme.domain.KeywordDo;
import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.ProductCategoryDo;
import com.dq.arq.sme.domain.UserDo;
import com.google.api.ads.adwords.axis.v201607.cm.ApiException;

public interface KeywordService {

	Integer saveKeywordDo(KeywordDo keywordDo);

	void saveKeywordDos(List<KeywordDo> keywordDos);

	List<KeywordDo> getKeywordDosList();

	KeywordDo getKeywordDoById(Integer id);
	
	KeywordDo getKeywordDoByName(String keywordName);

	List<KeywordDo> getKeywordDosListByAdgroupDo(AdgroupDo adgroupDo);

	void deleteKeywordDo(KeywordDo keywordDo);
	
	void deleteKeywordDos(List<KeywordDo> keywordDos);

	Long countKeywordDosByAdgroupDo(AdgroupDo adgroupDo);
	
	List<KeywordDo> getKeywordDosListByApiId(List<KeywordDo> keywordDos);
	
	void updateKeywordDo(KeywordDo keywordDo);
	
	void updateKeywordDos(List<KeywordDo> keywordDos);
	
	int addNumberOfKeywords(AdgroupDo adgroupDo,UserDo userDo,ProductCategoryDo productCategoryDo,int count) throws ApiException, UnsupportedEncodingException, RemoteException;

}
