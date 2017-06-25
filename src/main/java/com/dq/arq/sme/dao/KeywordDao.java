package com.dq.arq.sme.dao;

import java.util.List;

import com.dq.arq.sme.domain.AdgroupDo;
import com.dq.arq.sme.domain.KeywordDo;

public interface KeywordDao {


	Integer saveKeywordDo(KeywordDo keywordDo);

	List<KeywordDo> getKeywordDosList();

	KeywordDo getKeywordDoById(Integer id);

	List<KeywordDo> getKeywordDosListByAdgroupDo(AdgroupDo adgroupDo);

	void deleteKeywordDo(KeywordDo keywordDo);

	Long countKeywordDosByAdgroupDo(AdgroupDo adgroupDo);

	List<KeywordDo> getKeywordDosListByApiId(List<KeywordDo> keywordDos);
	
	void updateKeywordDo(KeywordDo keywordDo);

	KeywordDo getKeywordDoByName(String keywordName);
}
